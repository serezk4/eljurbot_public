package com.serezka.eljurbot.db.services;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.api.school.EljurApi;
import com.serezka.eljurbot.db.model.AuthorizationToken;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.respository.AuthorizationTokenRepository;
import com.serezka.eljurbot.db.respository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@Log
@PropertySource("classpath:eljur.properties")
public class AuthorizationTokenService {
    final AuthorizationTokenRepository authorizationTokenRepository;
    final UserRepository userRepository;

    final String eljurDeveloperKey;

    public AuthorizationTokenService(AuthorizationTokenRepository authorizationTokenRepository, UserRepository userRepository,
                                     @Value("${eljur.api.devkey}") String eljurDeveloperKey) {
        this.authorizationTokenRepository = authorizationTokenRepository;
        this.userRepository = userRepository;
        this.eljurDeveloperKey = eljurDeveloperKey;
    }

    @Transactional
    public void saveAuthorizationToken(AuthorizationToken authorizationToken) {
        try {
            authorizationTokenRepository.save(authorizationToken);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    @Transactional
    public AuthorizationToken findAuthorizationTokenByUserId(long userId) {
        try {
            AuthorizationToken authorizationToken = authorizationTokenRepository.findAuthorizationTokenByUserIdAndExpiresInIsAfter(userId, new Date());
            if (authorizationToken == null) {
                Optional<User> user = userRepository.findById(userId);
                if (user.isEmpty()) return null;


                EljurApi eljurApi = EljurApi.getInstance();
                EljurApi.Authorization.TokenInfo tokenInfo = eljurApi.requestAuthorizationToken(user.get().getEljurUsername(), user.get().getEljurPassword(), user.get().getEljurVendor());

                if (tokenInfo.getErrorText() != null) return null;
                authorizationToken = new AuthorizationToken(userId, tokenInfo.getToken(), Application.AUTHORIZATION_TOKEN_DATE_FORMAT.parse(tokenInfo.getExpires()));

                saveAuthorizationToken(authorizationToken);
            }

            return authorizationToken;
        } catch (Exception e) {
            log.warning(e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean authorizationTokenExistsByUserId(long userId) {
        try {
            return authorizationTokenRepository.existsByUserId(userId);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void removeAllAuthorizationTokensByUserId(long userId) {
        try {
            authorizationTokenRepository.removeAllByUserId(userId);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }
}
