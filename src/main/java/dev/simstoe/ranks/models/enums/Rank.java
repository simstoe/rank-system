package dev.simstoe.ranks.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum Rank {
    ADMIN("<red>Admin", List.of("*")),
    DEFAULT("<gray>Default", List.of("players"));

    private final String prefix;
    private final List<String> permissions;
}
