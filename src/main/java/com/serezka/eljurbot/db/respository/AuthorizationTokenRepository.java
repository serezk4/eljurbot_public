package com.serezka.eljurbot.db.respository;

import com.serezka.eljurbot.db.model.AuthorizationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface AuthorizationTokenRepository extends CrudRepository<AuthorizationToken, Long> {
    AuthorizationToken findByUserId(long userId);

    AuthorizationToken findAuthorizationTokenByUserIdAndExpiresInIsAfter(long userId, Date date);

    boolean existsByUserId(long userId);

    void removeAllByUserId(long userId);
}
