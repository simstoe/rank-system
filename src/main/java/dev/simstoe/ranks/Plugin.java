package dev.simstoe.ranks;

import dev.simstoe.ranks.commands.RankCommand;
import dev.simstoe.ranks.listener.PlayerJoinListener;
import dev.simstoe.ranks.listener.RankUpdatedListener;
import dev.simstoe.ranks.repositories.RankRepository;
import dev.simstoe.ranks.repositories.impl.MySQLRankRepository;
import dev.simstoe.ranks.services.RankService;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public final class Plugin extends JavaPlugin {
    private RankRepository rankRepository;
    private RankService rankService;

    @Override
    public void onEnable() {
        this.rankRepository = new MySQLRankRepository("localhost", 3306, "network", "root", "test123");
        this.rankService = new RankService(this, this.rankRepository);

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new RankUpdatedListener(this.rankService), this);

        this.registerCommand("rank", new RankCommand(this.rankService));

        this.getServer().getOnlinePlayers().forEach(player -> this.rankService.handleJoin(player.getUniqueId()));

        this.rankService.updateTablistForAllOnlinePlayers();
    }

    @Override
    public void onDisable() {
        this.rankService.shutdown();

        if (this.rankRepository instanceof MySQLRankRepository mySQLRankRepository) {
            mySQLRankRepository.close();
        }
    }
}
