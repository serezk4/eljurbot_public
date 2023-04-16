package com.serezka.eljurbot.db.services;

import com.serezka.eljurbot.db.model.Mark;
import com.serezka.eljurbot.db.respository.MarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Log @RequiredArgsConstructor
public class MarkService {
    final MarkRepository markRepository;

    @Transactional
    public List<Mark> findAllByUserId(long userId) {
        try {
            return markRepository.findAllByUserId(userId);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public List<Mark> findAllByUserIdAndSubject(long userId, String subject) {
        try {
            return markRepository.findAllByUserIdAndSubject(userId, subject);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public boolean saveMarks(List<Mark> marks) {
        try {
            markRepository.saveAll(marks);
            return true;
        } catch (Exception e) {
            log.warning(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void deleteMarks(List<Mark> marks) {
        try {
            markRepository.deleteAll(marks);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    @Transactional
    public void deleteAllByUserId(long userId) {
        try {
            markRepository.deleteAllByUserId(userId);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

}
