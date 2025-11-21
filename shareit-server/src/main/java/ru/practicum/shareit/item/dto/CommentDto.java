package ru.practicum.shareit.item.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD:shareit-server/src/main/java/ru/practicum/shareit/item/dto/CommentDto.java
=======
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
>>>>>>> 724b90a (feat: implement complete booking system with database integration (#2)):src/main/java/ru/practicum/shareit/request/ItemRequest.java

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

<<<<<<< HEAD:shareit-server/src/main/java/ru/practicum/shareit/item/dto/CommentDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
=======
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(nullable = false)
    private LocalDateTime created;

    @Transient
    private List<Item> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", requestorId=" + (requestor != null ? requestor.getId() : null) +
                ", created=" + created +
                ", items=" + (items != null ? items.size() : 0) +
                '}';
    }
>>>>>>> 724b90a (feat: implement complete booking system with database integration (#2)):src/main/java/ru/practicum/shareit/request/ItemRequest.java
}