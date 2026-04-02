package dev.simstoe.ranks.events;

import dev.simstoe.ranks.models.enums.Rank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class RankUpdatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID uuid;
    private final Rank oldRank;
    private final Rank newRank;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

