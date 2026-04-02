package dev.simstoe.ranks.models;

import dev.simstoe.ranks.models.enums.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public final class RankPlayer {
    private final UUID uuid;
    private Rank rank;
}
