package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/** TODO Sprint add-bookings. */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_date")
  private LocalDateTime start;

  @Column(name = "end_date")
  private LocalDateTime end;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Item item;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private User booker;

  @Enumerated(EnumType.STRING)
  private BookingStatus status;
}
