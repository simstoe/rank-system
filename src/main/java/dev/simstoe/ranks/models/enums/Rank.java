package dev.simstoe.ranks.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum Rank {
    ADMIN("§4Admin", List.of("*")),
    DEFAULT("§7", List.of("players"));

    private final String prefix;
    private final List<String> permissions;
}
