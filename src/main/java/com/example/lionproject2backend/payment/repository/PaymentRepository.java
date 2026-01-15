package com.example.lionproject2backend.payment.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentCustomRepository {

    @Query("select p "
            + "from Payment p "
            + "join fetch p.tutorial t "
            + "join fetch t.mentor m "
            + "join fetch m.user u "
            + "where p.id = :paymentId "
            + "and p.mentee.id = :menteeId")
    Optional<Payment> findByIdAndMenteeId(
            @Param("paymentId") Long paymentId,
            @Param("menteeId") Long menteeId
    );

    @Query("select coalesce(sum(p.amount), 0) "
            + "from Payment p "
            + "where p.mentee.id = :menteeId "
            + "and p.status = 'PAID'")
    int sumPaidAmountByMentee(Long menteeId);

    @Query("select coalesce(sum(p.amount), 0) "
            + "from Payment p "
            + "where p.mentee.id = :menteeId "
            + "and p.status = 'PAID' "
            + "and p.paidAt between :start and :end")
    int sumPaidAmountByMenteeAndPaidAtBetween(
            Long menteeId,
            LocalDateTime start,
            LocalDateTime end
    );

}
