package com.example.lionproject2backend.mentor.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "mentor_availability",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_mentor_day",
                columnNames = {"mentor_id", "day_of_week"}
        ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorAvailability extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeekCode dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_active")
    private boolean isActive = true;

    // =============== 생성 메서드 =============== //

    public static MentorAvailability create(
            Mentor mentor,
            DayOfWeekCode dayOfWeek,
            LocalTime startTime,
            LocalTime endTime) {
        validateTimeRange(startTime, endTime);

        MentorAvailability availability = new MentorAvailability();
        availability.mentor = mentor;
        availability.dayOfWeek = dayOfWeek;
        availability.startTime = startTime;
        availability.endTime = endTime;
        availability.isActive = true;
        return availability;
    }

    // =============== 비즈니스 로직 =============== //

    /**
     * 시간 범위 검증
     */
    private static void validateTimeRange(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다");
        }
    }

    /**
     * 특정 시간이 가용 범위 내인지 확인
     */
    public boolean isTimeWithinRange(LocalTime time) {
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    /**
     * 특정 시간이 가용 범위 내인지 확인 (duration 고려)
     */
    public boolean isTimeWithinRange(LocalTime time, int durationMinutes) {
        LocalTime lessonEndTime = time.plusMinutes(durationMinutes);
        return !time.isBefore(startTime) && !lessonEndTime.isAfter(endTime);
    }

    /**
     * 수정
     */
    public void update(LocalTime startTime, LocalTime endTime) {
        validateTimeRange(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }
}
