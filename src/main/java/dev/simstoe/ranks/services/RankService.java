package dev.simstoe.ranks.services;

import dev.simstoe.ranks.events.RankUpdatedEvent;
import dev.simstoe.ranks.models.RankPlayer;
import dev.simstoe.ranks.models.enums.Rank;
import dev.simstoe.ranks.repositories.RankRepository;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.*;

@AllArgsConstructor
public final class RankService {
    private final Plugin plugin;
    private final RankRepository rankRepository;
    private final Map<UUID, RankPlayer> cache = new HashMap<>();
    private final Map<UUID, PermissionAttachment> permissionAttachments = new HashMap<>();
    private final Set<UUID> forcedOperators = new HashSet<>();

    public void handleJoin(UUID uuid) {
        var player = this.playerRank(uuid);

        if (player == null) {
            player = new RankPlayer(uuid, Rank.DEFAULT);

            this.rankRepository.savePlayer(player);
            this.cache.put(uuid, player);
        }

        this.applyPermissions(uuid);
    }

    public RankPlayer playerRank(UUID uuid) {
        var cachedPlayer = this.cache.get(uuid);

        if (cachedPlayer != null) {
            return cachedPlayer;
        }

        var loadedPlayer = this.rankRepository.loadPlayer(uuid);
        if (loadedPlayer != null) {
            this.cache.put(uuid, loadedPlayer);
        }

        return loadedPlayer;
    }

    public List<String> configuredPermissions(UUID uuid) {
        var rankPlayer = this.playerRank(uuid);
        if (rankPlayer == null) {
            return List.of();
        }

        return List.copyOf(rankPlayer.rank().permissions());
    }

    public List<String> effectivePermissions(UUID uuid) {
        var onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer == null) {
            return List.of();
        }

        return onlinePlayer.getEffectivePermissions().stream()
                .filter(PermissionAttachmentInfo::getValue)
                .map(PermissionAttachmentInfo::getPermission)
                .distinct()
                .sorted()
                .toList();
    }

    public void updateRank(UUID uuid, Rank newRank) {
        var player = this.playerRank(uuid);

        if (player == null || player.rank() == newRank) {
            return;
        }

        var oldRank = player.rank();
        player.rank(newRank);

        this.rankRepository.savePlayer(player);
        this.cache.put(uuid, player);
        this.applyPermissions(uuid);

        this.plugin.getServer().getPluginManager().callEvent(new RankUpdatedEvent(uuid, oldRank, newRank));
    }

    public void updateTablist(Player player) {
        var rankPlayer = this.playerRank(player.getUniqueId());
        if (rankPlayer == null) {
            return;
        }

        player.playerListName(this.rankDisplayName(player.getUniqueId(), Component.text(player.getName())));
    }

    public Component chatDisplayName(UUID uuid, Component playerName) {
        return this.rankDisplayName(uuid, playerName);
    }

    public void updateTablistForAllOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(this::updateTablist);
    }

    public void scheduleTablistRefresh() {
        this.scheduleTablistRefresh(1L);
    }

    public void scheduleTablistRefresh(long delayTicks) {
        Bukkit.getScheduler().runTaskLater(this.plugin, this::updateTablistForAllOnlinePlayers, Math.max(delayTicks, 0L));
    }

    public void removeFromCache(UUID uuid) {
        this.removePermissionAttachment(uuid);
        this.cache.remove(uuid);
    }

    public void shutdown() {
        for (var uuid : this.permissionAttachments.keySet().toArray(new UUID[0])) {
            this.removePermissionAttachment(uuid);
        }
    }

    private void applyPermissions(UUID uuid) {
        var onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer == null) {
            return;
        }

        var rankPlayer = this.playerRank(uuid);
        if (rankPlayer == null) {
            return;
        }

        this.removePermissionAttachment(uuid);

        var attachment = onlinePlayer.addAttachment(this.plugin);
        for (var permission : rankPlayer.rank().permissions()) {
            if (permission.equals("*")) {
                Bukkit.getPluginManager().getPermissions().forEach(registeredPermission -> attachment.setPermission(registeredPermission.getName(), true));
                attachment.setPermission("*", true);
                continue;
            }

            attachment.setPermission(permission, true);
        }

        this.permissionAttachments.put(uuid, attachment);
        this.syncOperatorState(uuid, onlinePlayer, rankPlayer.rank().permissions().contains("*"));
        onlinePlayer.recalculatePermissions();
        Bukkit.getScheduler().runTask(this.plugin, onlinePlayer::updateCommands);
    }

    private void removePermissionAttachment(UUID uuid) {
        var attachment = this.permissionAttachments.remove(uuid);
        if (attachment == null) {
            return;
        }

        var onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer == null) {
            this.forcedOperators.remove(uuid);
            return;
        }

        onlinePlayer.removeAttachment(attachment);
        this.syncOperatorState(uuid, onlinePlayer, false);
        onlinePlayer.recalculatePermissions();
        Bukkit.getScheduler().runTask(this.plugin, onlinePlayer::updateCommands);
    }

    private void syncOperatorState(UUID uuid, Player player, boolean shouldBeOperator) {
        if (shouldBeOperator) {
            if (!player.isOp()) {
                player.setOp(true);
                this.forcedOperators.add(uuid);
            }
            return;
        }

        if (this.forcedOperators.remove(uuid) && player.isOp()) {
            player.setOp(false);
        }
    }

    private Component rankDisplayName(UUID uuid, Component playerName) {
        var rankPlayer = this.playerRank(uuid);
        if (rankPlayer == null) {
            return playerName;
        }

        var rawPrefix = rankPlayer.rank().prefix();
        var prefix = (rawPrefix == null || rawPrefix.isBlank())
                ? rankPlayer.rank().name().toLowerCase()
                : rawPrefix;

        return MiniMessage.miniMessage().deserialize(prefix + " ").append(playerName);
    }
}
