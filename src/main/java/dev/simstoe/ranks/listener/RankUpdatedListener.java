package dev.simstoe.ranks.listener;

import dev.simstoe.ranks.events.RankUpdatedEvent;
import dev.simstoe.ranks.services.RankService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class RankUpdatedListener implements Listener {
    private final RankService rankService;

    @EventHandler
    public void handle(RankUpdatedEvent event) {
        this.rankService.scheduleTablistRefresh();
    }
}

