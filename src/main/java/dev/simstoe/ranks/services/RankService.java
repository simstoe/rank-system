package dev.simstoe.ranks.services;

import dev.simstoe.ranks.models.RankPlayer;
import dev.simstoe.ranks.models.enums.Rank;
import dev.simstoe.ranks.repositories.RankRepository;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public final class RankService {
    private final RankRepository rankRepository;
    private final Map<UUID, RankPlayer> cache = new HashMap<>();

    public void handleJoin(UUID uuid) {
        var player = this.rankRepository.loadPlayer();

        if (player == null) player = new RankPlayer(uuid, Rank.DEFAULT);

        this.cache.put(uuid, player);
        this.applyPermissions(player);
    }

    public void updateRank(UUID uuid, Rank newRank) {
        var player = this.cache.get(uuid);

        if (player != null) {
            player.rank(newRank);
            this.rankRepository.savePlayer(player);
            this.applyPermissions(player);
        }
    }

    private void applyPermissions(RankPlayer rankPlayer) {

    }
}
