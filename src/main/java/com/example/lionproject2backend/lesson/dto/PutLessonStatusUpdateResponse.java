package com.example.lionproject2backend.lesson.dto;

import com.example.lionproject2backend.lesson.domain.LessonStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Put /api/lessons/{lessonId}/accept - 수업 수락
 *
 * query
 * UPDATE lessons
 * SET status = 'APPROVED',
 * updated_at = NOW(),
 * WHERE id = ?
 * AND status = 'PENDING'
 */
@Getter
@Builder
public class PutLessonStatusUpdateResponse {

    private Long lessonId;
    private LessonStatus status;  // "APPROVED"
    private LocalDateTime updatedAt;
    private String message;  // "수업이 승인되었습니다"
}
