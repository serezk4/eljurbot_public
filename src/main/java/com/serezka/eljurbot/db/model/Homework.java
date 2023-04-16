package com.serezka.eljurbot.db.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "homework")
@Data @FieldDefaults(level = AccessLevel.PRIVATE)
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long homeworkId;


}
