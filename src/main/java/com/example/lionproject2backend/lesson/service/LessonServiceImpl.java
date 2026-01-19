package com.example.lionproject2backend.lesson.service;

import com.example.lionproject2backend.lesson.dto.*;
import com.example.lionproject2backend.lesson.domain.Lesson;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.repository.LessonRepository;
import com.example.lionproject2backend.mentor.service.MentorScheduleService;
import com.example.lionproject2backend.ticket.domain.Ticket;
import com.example.lionproject2backend.ticket.repository.TicketRepository;
import com.example.lionproject2backend.tutorial.domain.Tutorial;
import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final TicketRepository ticketRepository;
    private final MentorScheduleService mentorScheduleService;

    /**
     * 수업 신청 (이용권 기반)
     * POST /api/tickets/{ticketId}/lessons
     */
    @Override
    @Transactional
    public PostLessonRegisterResponse register(Long ticketId, Long userId, PostLessonRegisterRequest request) {
        // 이용권 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        // 이용권 소유자 검증
        if (!ticket.getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.TICKET_FORBIDDEN);
        }

        // 이용권 유효성 검증
        if (!ticket.hasRemaining()) {
            throw new CustomException(ErrorCode.TICKET_EXHAUSTED);
        }

        Tutorial tutorial = ticket.getTutorial();
        LocalDateTime scheduledAt = LocalDateTime.of(request.getLessonDate(), request.getLessonTime());

        // 멘토 가용 시간 검증
        validateMentorAvailability(tutorial.getMentor().getId(), scheduledAt, tutorial.getDuration());

        // 중복 예약 검증
        validateNoConflict(tutorial.getId(), scheduledAt);

        // Lesson 생성 (내부에서 ticket.use() 호출)
        Lesson lesson = Lesson.register(
                ticket,
                request.getRequestMessage(),
                scheduledAt
        );

        Lesson savedLesson = lessonRepository.save(lesson);

        return PostLessonRegisterResponse.from(savedLesson);
    }

    /**
     * 내가 신청한 수업 목록 조회 (멘티)
     */
    @Override
    public GetLessonListResponse getMyLessons(Long menteeId, LessonStatus status) {
        List<Lesson> lessons;

        if (status == null) {
            lessons = lessonRepository.findByMenteeIdWithDetails(menteeId);
        } else {
            lessons = lessonRepository.findByMenteeIdAndStatusWithDetails(menteeId, status);
        }

        return GetLessonListResponse.from(lessons);
    }

    /**
     * 수업 신청 목록 조회 (멘토)
     */
    @Override
    public GetLessonRequestListResponse getMyLessonRequests(Long mentorId, LessonStatus status) {
        List<Lesson> lessons;

        if (status == null) {
            lessons = lessonRepository.findByMentorUserIdWithDetails(mentorId);
        } else {
            lessons = lessonRepository.findByMentorUserIdAndStatusWithDetails(mentorId, status);
        }

        return GetLessonRequestListResponse.from(lessons);
    }

    /**
     * 수업 상세 조회
     */
    @Override
    public GetLessonDetailResponse getLessonDetail(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        if (!lesson.isParticipant(userId)) {
            throw new CustomException(ErrorCode.LESSON_FORBIDDEN);
        }

        return GetLessonDetailResponse.from(lesson, userId);
    }

    /**
     * 수업 확정 (멘토) - 기존 approve
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse confirm(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.confirm(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 거절 (멘토) - 이용권 복구
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse reject(Long lessonId, Long mentorId, PutLessonRejectRequest request) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        // 도메인 로직 실행 (내부에서 ticket.restore() 호출)
        lesson.reject(mentorId, request.getRejectReason());

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 시작 (멘토)
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse start(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.start(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 수업 완료 (멘토)
     */
    @Override
    @Transactional
    public PutLessonStatusUpdateResponse complete(Long lessonId, Long mentorId) {
        Lesson lesson = lessonRepository.findByIdWithDetails(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        lesson.complete(mentorId);

        return PutLessonStatusUpdateResponse.from(lesson);
    }

    /**
     * 달력 예약 현황 조회 (공개)
     * GET /api/tutorials/{tutorialId}/calendar?year=&month=
     */
    @Override
    public GetCalendarLessonsResponse getCalendarLessons(Long tutorialId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Lesson> lessons = lessonRepository.findByTutorialIdAndDateRange(
                tutorialId, startDate, endDate
        );

        return GetCalendarLessonsResponse.from(lessons);
    }

    // =============== 검증 메서드 =============== //

    /**
     * 멘토 가용 시간 검증
     */
    private void validateMentorAvailability(Long mentorId, LocalDateTime scheduledAt, int durationMinutes) {
        boolean available = mentorScheduleService.isAvailable(mentorId, scheduledAt, durationMinutes);

        if (!available) {
            throw new IllegalStateException("멘토의 가용 시간이 아닙니다. 다른 시간을 선택해주세요.");
        }
    }

    /**
     * 중복 예약 검증
     */
    private void validateNoConflict(Long tutorialId, LocalDateTime scheduledAt) {
        List<LessonStatus> activeStatuses = List.of(
                LessonStatus.REQUESTED,
                LessonStatus.CONFIRMED,
                LessonStatus.SCHEDULED
        );

        boolean hasConflict = lessonRepository.existsConflictingLesson(
                tutorialId,
                scheduledAt,
                activeStatuses
        );

        if (hasConflict) {
            throw new IllegalStateException("해당 시간에 이미 예약된 수업이 있습니다.");
        }
    }
}
