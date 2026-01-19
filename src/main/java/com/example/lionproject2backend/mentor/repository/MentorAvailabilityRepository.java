package com.example.lionproject2backend.mentor.repository;

import com.example.lionproject2backend.mentor.domain.DayOfWeekCode;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.domain.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {

    /**
     * 멘토의 모든 가용 시간 조회
     */
    List<MentorAvailability> findByMentorOrderByDayOfWeek(Mentor mentor);

    /**
     * 멘토의 활성화된 가용 시간만 조회
     */
    List<MentorAvailability> findByMentorAndIsActiveTrueOrderByDayOfWeek(Mentor mentor);

    /**
     * 멘토 ID로 활성화된 가용 시간 조회
     */
    @Query("SELECT ma FROM MentorAvailability ma WHERE ma.mentor.id = :mentorId AND ma.isActive = true ORDER BY ma.dayOfWeek")
    List<MentorAvailability> findActiveByMentorId(@Param("mentorId") Long mentorId);

    /**
     * 특정 요일의 가용 시간 조회
     */
    Optional<MentorAvailability> findByMentorAndDayOfWeek(Mentor mentor, DayOfWeekCode dayOfWeek);

    /**
     * 특정 요일의 활성화된 가용 시간 조회
     */
    Optional<MentorAvailability> findByMentorAndDayOfWeekAndIsActiveTrue(Mentor mentor, DayOfWeekCode dayOfWeek);

    /**
     * 멘토 ID와 요일로 활성화된 가용 시간 조회
     */
    @Query("SELECT ma FROM MentorAvailability ma " +
            "WHERE ma.mentor.id = :mentorId " +
            "AND ma.dayOfWeek = :dayOfWeek " +
            "AND ma.isActive = true")
    Optional<MentorAvailability> findActiveByMentorIdAndDayOfWeek(
            @Param("mentorId") Long mentorId,
            @Param("dayOfWeek") DayOfWeekCode dayOfWeek);

    /**
     * 해당 요일에 가용 시간이 이미 설정되어 있는지 확인
     */
    boolean existsByMentorAndDayOfWeek(Mentor mentor, DayOfWeekCode dayOfWeek);

    /**
     * 멘토 ID와 요일로 가용 시간 존재 여부 확인
     */
    boolean existsByMentorIdAndDayOfWeek(Long mentorId, DayOfWeekCode dayOfWeek);

    /**
     * 특정 시간이 멘토의 가용 시간 내인지 확인
     */
    @Query("SELECT CASE WHEN COUNT(ma) > 0 THEN true ELSE false END " +
            "FROM MentorAvailability ma " +
            "WHERE ma.mentor.id = :mentorId " +
            "AND ma.dayOfWeek = :dayOfWeek " +
            "AND ma.isActive = true " +
            "AND ma.startTime <= :time " +
            "AND ma.endTime > :time")
    boolean existsByMentorIdAndDayOfWeekAndTimeInRange(
            @Param("mentorId") Long mentorId,
            @Param("dayOfWeek") DayOfWeekCode dayOfWeek,
            @Param("time") LocalTime time);

    /**
     * 멘토의 가용 시간 삭제
     */
    void deleteByMentorAndId(Mentor mentor, Long id);
}
