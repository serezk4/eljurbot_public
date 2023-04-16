package com.serezka.eljurbot.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor @Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserRoles {
    ADMIN("admin"),USER("user");

    String name;
}
