package com.serezka.eljurbot.db.respository;

import com.serezka.eljurbot.db.model.Mark;
import org.springframework.data.repository.CrudRepository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findAllByUserId(long userId);

    List<Mark> findAllByUserIdAndSubject(long userId, String subject);

    void deleteAllByUserId(long userId);
}
