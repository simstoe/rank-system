package dev.simstoe.ranks.commands;

import dev.simstoe.ranks.models.enums.Rank;
import dev.simstoe.ranks.services.RankService;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class RankCommand implements BasicCommand {
    private final RankService rankService;

    @Override
    public void execute(@NotNull CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be run by players.");
            return;
        }

        var playerRank = this.rankService.playerRank(player.getUniqueId());

        if (args.length == 0) {
            player.sendRichMessage("<yellow>Rank<dark_gray>: <gray>You have the <yellow>" + playerRank.rank().name() + " <gray>rank.");
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                player.sendRichMessage("<red>Usage<dark_gray>: <gray>/rank set <yellow><player> <rank>");
                return;
            }

            var target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendRichMessage("<red>Error<dark_gray>: <gray>Player not found.");
                return;
            }

            try {
                var targetRank = Rank.valueOf(args[2].toUpperCase());

                this.rankService.updateRank(target.getUniqueId(), targetRank);

                player.sendRichMessage("<green>Success<dark_gray>: <gray>Set <yellow>" + target.getName() + "'s <gray>rank to <yellow>" + targetRank.name());
                target.sendRichMessage("<yellow>Rank<dark_gray>: <gray>Your rank has been updated to <yellow>" + targetRank.name());
            } catch (IllegalArgumentException exception) {
                player.sendRichMessage("<red>Error<dark_gray>: <gray>Invalid rank. Available: <yellow>" +
                        Arrays.stream(Rank.values()).map(Enum::name).collect(Collectors.joining(", ")));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            if (!player.hasPermission("ranks.debug")) {
                player.sendRichMessage("<red>Error<dark_gray>: <gray>You do not have permission <yellow>ranks.debug<gray>.");
                return;
            }

            if (args.length < 2) {
                player.sendRichMessage("<red>Usage<dark_gray>: <gray>/rank debug <yellow><player>");
                return;
            }

            var target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendRichMessage("<red>Error<dark_gray>: <gray>Player not found or offline.");
                return;
            }

            var targetRank = this.rankService.playerRank(target.getUniqueId());
            if (targetRank == null) {
                player.sendRichMessage("<red>Error<dark_gray>: <gray>No rank data found for <yellow>" + target.getName() + "<gray>.");
                return;
            }

            var configuredPermissions = this.rankService.configuredPermissions(target.getUniqueId());
            var effectivePermissions = this.rankService.effectivePermissions(target.getUniqueId());

            player.sendRichMessage("<gold>Debug<dark_gray>: <gray>Permissions for <yellow>" + target.getName());
            player.sendRichMessage("<yellow>Rank<dark_gray>: <gray>" + targetRank.rank().name());
            player.sendRichMessage("<yellow>Configured<dark_gray>: <gray>" + String.join(", ", configuredPermissions));
            player.sendRichMessage("<yellow>Effective (<gold>" + effectivePermissions.size() + "<yellow>)<dark_gray>: <gray>" + String.join(", ", effectivePermissions));
            return;
        }

        player.sendRichMessage("<red>Usage<dark_gray>: <gray>/rank [set|debug]");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, String[] args) {
        if (args.length == 1) {
            return List.of("set", "debug");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return Arrays.stream(Rank.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .toList();
        }

        return List.of();
    }
}