package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByBookerId(Long bookerId, Pageable pageable);

  List<Booking> findByBookerIdAndStartLessThanAndEndGreaterThan(
      Long bookerId, LocalDateTime localDateTime, LocalDateTime localDateTime2, Pageable pageable);

  List<Booking> findByBookerIdAndStartGreaterThanEqualAndStatusIs(
      Long bookerId, LocalDateTime localDateTime, BookingStatus status, Pageable pageable);

  @Query(
      " select b from Booking as b"
          + " where b.booker.id = ?1 and"
          + " (b.status = 'REJECTED' OR b.status = 'CANCELED') ")
  List<Booking> getBookerRejectedBooking(Long bookerId, Pageable pageable);

  List<Booking> findByBookerIdAndStartGreaterThan(
      Long bookerId, LocalDateTime localDateTime, Pageable pageable);

  List<Booking> findByBookerIdAndEndLessThan(
      Long bookerId, LocalDateTime localDateTime, Pageable pageable);

  List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStartGreaterThan(
      Long ownerId, LocalDateTime localDateTime, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStartLessThanAndEndGreaterThan(
      Long ownerId, LocalDateTime localDateTime, LocalDateTime localDateTime2, Pageable pageable);

  List<Booking> findByItemOwnerIdAndStartGreaterThanEqualAndStatus(
      Long ownerId, LocalDateTime localDateTime, BookingStatus status, Pageable pageable);

  @Query(
      " select b from Booking as b"
          + " where b.item.owner.id = ?1 and"
          + " (b.status = 'REJECTED' OR b.status = 'CANCELED')")
  List<Booking> getOwnerRejectedBooking(Long ownerId, Pageable pageable);

  List<Booking> findByItemOwnerIdAndEndLessThan(
      Long ownerId, LocalDateTime localDateTime, Pageable pageable);

  @Query(
      value =
          "SELECT * from bookings as b "
              + " where b.item_id = ?1 "
              + " and b.start_date < ?2 "
              + " and b.status = 'APPROVED'"
              + " order by b.start_date desc "
              + " limit 1",
      nativeQuery = true)
  Optional<Booking> getItemLastBooking(Long itemId, LocalDateTime localDateTime);

  @Query(
      value =
          "SELECT * from bookings as b "
              + " where b.item_id = ?1 "
              + " and b.start_date > ?2 "
              + " and b.status = 'APPROVED'"
              + " order by b.start_date asc "
              + " limit 1",
      nativeQuery = true)
  Optional<Booking> getItemNextBooking(Long itemId, LocalDateTime localDateTime);

  @Query(
      value =
          "select * from bookings as b"
              + " where b.booker_id = ?1 and"
              + " b.status = 'APPROVED' and"
              + " b.item_id = ?2 and"
              + " b.end_date < ?3 "
              + " limit 1 ",
      nativeQuery = true)
  Optional<Booking> getByBookerAndItemPastApprovedBooking(
      Long bookerId, Long itemId, LocalDateTime localDateTime);
}
