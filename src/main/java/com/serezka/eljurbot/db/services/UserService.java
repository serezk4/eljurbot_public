package com.serezka.eljurbot.db.services;

import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.warning(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public boolean saveUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.warning(e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean userExistsByChatId(String chatId) {
        try  {
            return userRepository.existsByChatId(chatId);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return false;
        }
    }

    @Transactional
    public User findUserByChatId(String chatId) {
        try {
            return userRepository.findByChatId(chatId);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return null;
        }
    }
}
