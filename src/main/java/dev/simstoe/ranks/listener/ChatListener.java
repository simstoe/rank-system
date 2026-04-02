package dev.simstoe.ranks.listener;

import dev.simstoe.ranks.services.RankService;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class ChatListener implements Listener {
    private final RankService rankService;

    @EventHandler
    public void handle(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                this.rankService.chatDisplayName(source.getUniqueId(), sourceDisplayName)
                        .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                        .append(message)));
    }
}
