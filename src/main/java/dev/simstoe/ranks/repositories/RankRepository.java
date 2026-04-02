package dev.simstoe.ranks.repositories;

import dev.simstoe.ranks.models.RankPlayer;

import java.util.UUID;

public interface RankRepository {
    void savePlayer(RankPlayer player);
    RankPlayer loadPlayer(UUID uuid);
}
