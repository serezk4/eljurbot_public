package com.serezka.eljurbot.db.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Table(name = "auth_tokens")
@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long authId;

    Long userId;

    @ToString.Exclude String authToken;

    Date expiresIn;

    public AuthorizationToken(long userId,
                              String authToken,
                              Date expiresIn) {
        this.userId = userId;
        this.authToken = authToken;
        this.expiresIn = expiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuthorizationToken that = (AuthorizationToken) o;
        return authId != null && Objects.equals(authId, that.authId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
