package com.serezka.eljurbot.db.services;

import com.serezka.eljurbot.db.model.ApplicationParameter;
import com.serezka.eljurbot.db.respository.ApplicationParameterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service @Log @RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationParameterService {
    final ApplicationParameterRepository applicationParameterRepository;

    @Transactional
    public void saveApplicationParameter(ApplicationParameter applicationParameter) {
        try {
            applicationParameterRepository.save(applicationParameter);
        }catch (Exception e ) {
            log.warning(e.getMessage());
        }
    }

    @Transactional
    public ApplicationParameter findApplicationParameterByName(String name) {
        try {
            return applicationParameterRepository.findApplicationParameterByName(name);
        } catch (Exception e) {
            log.warning(e.getMessage());
            return null;
        }
    }

    @Transactional
    public List<ApplicationParameter> findAll() {
        try {
            return applicationParameterRepository.findAll();
        } catch (Exception e) {
            log.warning(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public boolean applicationParameterExistsByName(String name) {
        try {
            return applicationParameterRepository.existsByName(name);
        } catch (Exception e) {
            return false;
        }
    }
}
