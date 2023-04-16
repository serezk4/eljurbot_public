package com.serezka.eljurbot.db.respository;

import com.serezka.eljurbot.db.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();

    boolean existsByChatId(String chatId);

    User findByChatId(String chatId);
}
