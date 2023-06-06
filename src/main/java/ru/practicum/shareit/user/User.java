package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/** TODO Sprint add-controllers. */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "email", unique = true)
  private String email;

  public User() {}

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
}
