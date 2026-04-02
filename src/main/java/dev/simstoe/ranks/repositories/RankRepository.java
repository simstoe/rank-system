package dev.simstoe.ranks.repositories;

import dev.simstoe.ranks.models.RankPlayer;

public interface RankRepository {
    void savePlayer(RankPlayer player);
    RankPlayer loadPlayer();
}
