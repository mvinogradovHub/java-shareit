package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/** TODO Sprint add-controllers. */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;

  @Column(name = "is_available")
  private Boolean available;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private User owner;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @ToString.Exclude
  private ItemRequest request;

}
