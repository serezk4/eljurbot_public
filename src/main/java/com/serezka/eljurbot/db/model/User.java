package com.serezka.eljurbot.db.model;

import com.serezka.eljurbot.db.UserRoles;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)

    String chatId;

    // eljur data
    String eljurUsername;
    String eljurSchoolClass;
    String eljurId;
    String eljurVendor;

    @ToString.Exclude
    private String eljurPassword;

    // nikasoft data

    String nikasoftUrl;
    String role = UserRoles.USER.getName();

    // base constructor
    public User(String chatId,
                String eljurUsername, String eljurPassword,
                String eljurVendor, String eljurId, String eljurSchoolClass) {

        this.chatId = chatId;
        this.eljurUsername = eljurUsername;
        this.eljurPassword = eljurPassword;
        this.eljurVendor = eljurVendor;
        this.eljurId = eljurId;
        this.eljurSchoolClass = eljurSchoolClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}