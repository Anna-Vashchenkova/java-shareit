package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotEmpty
    private String text;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    private LocalDateTime created;
}