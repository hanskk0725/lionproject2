package com.example.lionproject2backend.mentor.service;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import com.example.lionproject2backend.mentor.domain.DayOfWeekCode;
import com.example.lionproject2backend.mentor.domain.Mentor;
import com.example.lionproject2backend.mentor.domain.MentorAvailability;
import com.example.lionproject2backend.mentor.dto.*;
import com.example.lionproject2backend.mentor.repository.MentorAvailabilityRepository;
import com.example.lionproject2backend.mentor.repository.MentorRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.tutorial.repository.TutorialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorScheduleService {

    private final MentorRepository mentorRepository;
    private final MentorAvailabilityRepository availabilityRepository;
    private final TutorialRepository tutorialRepository;
    private final LessonRepository lessonRepository;

    /**
     * 멘토 가용 시간 등록
     */
    @Transactional
    public PostAvailabilityResponse addAvailability(Long userId, PostAvailabilityRequest request) {
        Mentor mentor = findMentorByUserId(userId);

        // 중복 요일 체크
        if (availabilityRepository.existsByMentorAndDayOfWeek(mentor, request.getDayOfWeek())) {
            throw new IllegalStateException("이미 해당 요일에 가용 시간이 설정되어 있습니다");
        }

        MentorAvailability availability = MentorAvailability.create(
                mentor,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime()
        );

        MentorAvailability saved = availabilityRepository.save(availability);
        return PostAvailabilityResponse.from(saved);
    }

    /**
     * 멘토 가용 시간 수정
     */
    @Transactional
    public PostAvailabilityResponse updateAvailability(Long userId, Long availabilityId, PutAvailabilityRequest request) {
        Mentor mentor = findMentorByUserId(userId);

        MentorAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new EntityNotFoundException("가용 시간을 찾을 수 없습니다"));

        // 본인 소유 확인
        if (!availability.getMentor().getId().equals(mentor.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        availability.update(request.getStartTime(), request.getEndTime());
        return PostAvailabilityResponse.from(availability);
    }

    /**
     * 멘토 가용 시간 삭제
     */
    @Transactional
    public void deleteAvailability(Long userId, Long availabilityId) {
        Mentor mentor = findMentorByUserId(userId);

        MentorAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new EntityNotFoundException("가용 시간을 찾을 수 없습니다"));

        // 본인 소유 확인
        if (!availability.getMentor().getId().equals(mentor.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        availabilityRepository.delete(availability);
    }

    /**
     * 내 가용 시간 조회
     */
    public GetAvailabilityResponse getMyAvailability(Long userId) {
        Mentor mentor = findMentorByUserId(userId);
        List<MentorAvailability> availabilities = availabilityRepository.findByMentorOrderByDayOfWeek(mentor);

        return GetAvailabilityResponse.of(
                mentor.getId(),
                mentor.getUser().getNickname(),
                availabilities
        );
    }

    /**
     * 특정 멘토의 가용 시간 조회 (공개)
     */
    public GetAvailabilityResponse getMentorAvailability(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("멘토를 찾을 수 없습니다"));

        List<MentorAvailability> availabilities = availabilityRepository.findByMentorAndIsActiveTrueOrderByDayOfWeek(mentor);

        return GetAvailabilityResponse.of(
                mentor.getId(),
                mentor.getUser().getNickname(),
                availabilities
        );
    }

    /**
     * 특정 날짜의 예약 가능 슬롯 조회
     */
    public GetAvailableSlotsResponse getAvailableSlots(Long tutorialId, LocalDate date) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new EntityNotFoundException("튜토리얼을 찾을 수 없습니다"));

        Mentor mentor = tutorial.getMentor();
        DayOfWeekCode dayOfWeekCode = DayOfWeekCode.from(date.getDayOfWeek());

        // 1. 해당 요일의 가용 시간 조회
        MentorAvailability availability = availabilityRepository
                .findByMentorAndDayOfWeekAndIsActiveTrue(mentor, dayOfWeekCode)
                .orElse(null);

        if (availability == null) {
            // 해당 요일 가용 시간 없음
            return GetAvailableSlotsResponse.builder()
                    .tutorialId(tutorialId)
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek())
                    .duration(tutorial.getDuration())
                    .slots(List.of())
                    .build();
        }

        // 2. 슬롯 생성 (duration 단위)
        List<LocalTime> slotTimes = generateSlotTimes(
                availability.getStartTime(),
                availability.getEndTime(),
                tutorial.getDuration()
        );

        // 3. 이미 예약된 시간 조회
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<Lesson> bookedLessons = lessonRepository.findByTutorialIdAndScheduledAtBetweenAndStatusIn(
                tutorialId,
                startOfDay,
                endOfDay,
                List.of(LessonStatus.REQUESTED, LessonStatus.CONFIRMED, LessonStatus.SCHEDULED)
        );

        Set<LocalTime> bookedTimes = bookedLessons.stream()
                .map(lesson -> lesson.getScheduledAt().toLocalTime())
                .collect(Collectors.toSet());

        // 4. 슬롯에 예약 여부 표시
        List<GetAvailableSlotsResponse.TimeSlot> slots = slotTimes.stream()
                .map(time -> {
                    if (bookedTimes.contains(time)) {
                        return GetAvailableSlotsResponse.TimeSlot.unavailable(time, "이미 예약됨");
                    }
                    // 과거 시간 체크
                    if (date.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
                        return GetAvailableSlotsResponse.TimeSlot.unavailable(time, "지난 시간");
                    }
                    return GetAvailableSlotsResponse.TimeSlot.available(time);
                })
                .toList();

        return GetAvailableSlotsResponse.builder()
                .tutorialId(tutorialId)
                .date(date)
                .dayOfWeek(date.getDayOfWeek())
                .duration(tutorial.getDuration())
                .slots(slots)
                .build();
    }

    /**
     * 시간 슬롯 생성
     */
    private List<LocalTime> generateSlotTimes(LocalTime startTime, LocalTime endTime, int durationMinutes) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = startTime;

        while (current.plusMinutes(durationMinutes).compareTo(endTime) <= 0) {
            slots.add(current);
            current = current.plusMinutes(durationMinutes);
        }

        return slots;
    }

    /**
     * 멘토의 특정 시간이 가용한지 확인
     */
    public boolean isAvailable(Long mentorId, LocalDateTime scheduledAt, int durationMinutes) {
        DayOfWeekCode dayOfWeek = DayOfWeekCode.from(scheduledAt.getDayOfWeek());
        LocalTime time = scheduledAt.toLocalTime();

        return availabilityRepository.findActiveByMentorIdAndDayOfWeek(mentorId, dayOfWeek)
                .map(availability -> availability.isTimeWithinRange(time, durationMinutes))
                .orElse(false);
    }

    private Mentor findMentorByUserId(Long userId) {
        return mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("멘토 정보를 찾을 수 없습니다"));
    }
}
