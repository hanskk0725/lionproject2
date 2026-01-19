package com.example.lionproject2backend.lesson.scheduler;

import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수업 상태 자동 전이 스케줄러
 *
 * 상태 전이 로직:
 * - CONFIRMED → SCHEDULED: scheduledAt <= 현재 시간 (수업 시작)
 * - SCHEDULED → COMPLETED: scheduledAt + duration <= 현재 시간 (수업 완료)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LessonScheduler {

    private final LessonRepository lessonRepository;

    /**
     * 1분마다 수업 상태를 자동 업데이트
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateLessonStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // 1. CONFIRMED → SCHEDULED (수업 시작 시간 도달)
        int startedCount = startLessons(now);

        // 2. SCHEDULED → COMPLETED (수업 종료 시간 경과)
        int completedCount = completeLessons(now);

        if (startedCount > 0 || completedCount > 0) {
            log.info("[LessonScheduler] 수업 상태 자동 업데이트 - 시작: {}건, 완료: {}건",
                    startedCount, completedCount);
        }
    }

    /**
     * CONFIRMED 상태이고 수업 시작 시간이 지난 수업을 SCHEDULED로 변경
     */
    private int startLessons(LocalDateTime now) {
        List<Lesson> lessonsToStart = lessonRepository
                .findByStatusAndScheduledAtBefore(LessonStatus.CONFIRMED, now);

        for (Lesson lesson : lessonsToStart) {
            lesson.startByScheduler();
            log.debug("[LessonScheduler] 수업 시작 - lessonId: {}, scheduledAt: {}",
                    lesson.getId(), lesson.getScheduledAt());
        }

        return lessonsToStart.size();
    }

    /**
     * SCHEDULED 상태이고 수업 종료 시간(scheduledAt-예약된수업시간 + duration-멘토가 등록한 수업진행 시간)이 지난 수업을 COMPLETED로 변경
     */
    private int completeLessons(LocalDateTime now) {
        List<Lesson> scheduledLessons = lessonRepository.findByStatus(LessonStatus.SCHEDULED);
        int completedCount = 0;

        for (Lesson lesson : scheduledLessons) {
            int durationMinutes = lesson.getTutorial().getDuration();
            LocalDateTime endTime = lesson.getScheduledAt().plusMinutes(durationMinutes);

            if (!now.isBefore(endTime)) {
                lesson.completeByScheduler();
                completedCount++;
                log.debug("[LessonScheduler] 수업 완료 - lessonId: {}, endTime: {}",
                        lesson.getId(), endTime);
            }
        }

        return completedCount;
    }
}
