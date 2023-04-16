package com.serezka.eljurbot.db.respository;

import com.serezka.eljurbot.db.model.ApplicationParameter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationParameterRepository extends CrudRepository<ApplicationParameter, Long> {
    ApplicationParameter findApplicationParameterByName(String name);

    List<ApplicationParameter> findAll();

    boolean existsByName(String name);
}
