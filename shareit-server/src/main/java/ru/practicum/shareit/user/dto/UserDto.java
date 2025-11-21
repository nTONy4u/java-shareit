package ru.practicum.shareit.user.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<<<<<<< HEAD:shareit-server/src/main/java/ru/practicum/shareit/user/dto/UserDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
=======
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
>>>>>>> 724b90a (feat: implement complete booking system with database integration (#2)):src/main/java/ru/practicum/shareit/user/User.java
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 512)
    private String email;
<<<<<<< HEAD:shareit-server/src/main/java/ru/practicum/shareit/user/dto/UserDto.java
=======

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
>>>>>>> 724b90a (feat: implement complete booking system with database integration (#2)):src/main/java/ru/practicum/shareit/user/User.java
}