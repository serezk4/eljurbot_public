package com.serezka.eljurbot.db.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "app_params")
@Entity
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ApplicationParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long parameter_id;

    String name;
    String value;

    public ApplicationParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationParameter that = (ApplicationParameter) o;
        return parameter_id != null && Objects.equals(parameter_id, that.parameter_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
