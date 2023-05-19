package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query(" select b from Booking as b" +
            " where b.booker.id = ?1 and" +
            " ?2 between b.start and b.end" +
            " order by b.start desc")
    List<Booking> getBookerCurrentBooking(Long bookerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.booker.id = ?1 and" +
            " b.start >= ?2 and" +
            " b.status = 'WAITING' " +
            " order by b.start desc")
    List<Booking> getBookerWaitingBooking(Long bookerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.booker.id = ?1 and" +
            " (b.status = 'REJECTED' OR b.status = 'CANCELED') " +
            " order by b.start desc")
    List<Booking> getBookerRejectedBooking(Long bookerId);

    @Query(" select b from Booking as b" +
            " where b.booker.id = ?1 and" +
            " b.start > ?2" +
            " order by b.start desc")
    List<Booking> getBookerFutureBooking(Long bookerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.booker.id = ?1 and" +
            " b.end < ?2" +
            " order by b.start desc")
    List<Booking> getBookerPastBooking(Long bookerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1" +
            " order by b.start desc")
    List<Booking> getOwnerBooking(Long ownerId);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1 and" +
            " b.start > ?2" +
            " order by b.start desc")
    List<Booking> getOwnerFutureBooking(Long ownerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1 and" +
            " ?2 between b.start and b.end" +
            " order by b.start desc")
    List<Booking> getOwnerCurrentBooking(Long ownerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1 and" +
            " b.start >= ?2 and" +
            " b.status = 'WAITING' " +
            " order by b.start desc")
    List<Booking> getOwnerWaitingBooking(Long ownerId, LocalDateTime localDateTime);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1 and" +
            " (b.status = 'REJECTED' OR b.status = 'CANCELED') " +
            " order by b.start desc")
    List<Booking> getOwnerRejectedBooking(Long ownerId);

    @Query(" select b from Booking as b" +
            " where b.item.owner.id = ?1 and" +
            " b.end < ?2" +
            " order by b.start desc")
    List<Booking> getOwnerPastBooking(Long ownerId, LocalDateTime localDateTime);

    @Query(value = "SELECT * from bookings as b " +
            " where b.item_id = ?1 " +
            " and b.start_date < ?2 " +
            " and b.status = 'APPROVED'" +
            " order by b.start_date desc " +
            " limit 1", nativeQuery = true)
    Optional<Booking> getItemLastBooking(Long itemId, LocalDateTime localDateTime);

    @Query(value = "SELECT * from bookings as b " +
            " where b.item_id = ?1 " +
            " and b.start_date > ?2 " +
            " and b.status = 'APPROVED'" +
            " order by b.start_date asc " +
            " limit 1", nativeQuery = true)
    Optional<Booking> getItemNextBooking(Long itemId, LocalDateTime localDateTime);

    @Query(value = "select * from bookings as b" +
            " where b.booker_id = ?1 and" +
            " b.status = 'APPROVED' and" +
            " b.item_id = ?2 and" +
            " b.end_date < ?3 " +
            " limit 1 ", nativeQuery = true)
    Optional<Booking> getByBookerAndItemPastApprovedBooking(Long bookerId, Long itemId, LocalDateTime localDateTime);
}
