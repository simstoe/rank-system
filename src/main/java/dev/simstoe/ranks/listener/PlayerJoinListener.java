package dev.simstoe.ranks.listener;

import dev.simstoe.ranks.Plugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class PlayerJoinListener implements Listener {
    private final Plugin plugin;

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        this.plugin.rankService().handleJoin(event.getPlayer().getUniqueId());
        this.plugin.rankService().scheduleTablistRefresh(2L);
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        this.plugin.rankService().removeFromCache(event.getPlayer().getUniqueId());
    }
}
