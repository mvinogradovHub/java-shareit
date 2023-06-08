package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User author;

    private LocalDateTime created;
}
