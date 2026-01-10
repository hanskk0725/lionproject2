package com.example.lionproject2backend.lesson.controller;

import com.example.lionproject2backend.lesson.dto.*;
import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.lesson.domain.LessonStatus;
import com.example.lionproject2backend.lesson.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


/**
 * 수업 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * 수업 신청 - 인증 필요
     * Post /api/tutorials/{tutorialId}/lessons
     */
    @PostMapping("/tutorials/{tutorialId}/lessons")
    public ResponseEntity<ApiResponse<PostLessonRegisterResponse>> registerLesson(
            @PathVariable Long tutorialId,
            @Valid @RequestBody PostLessonRegisterRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        PostLessonRegisterResponse response = lessonService.register(tutorialId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내가 신청한 수업 목록 조회 (멘티) - 인증 필요
     * Get /api/lessons/my(?status=PENDING)
     * status - PENDING, COMPLETE, IN_PROCESS
     */
    @GetMapping("/lessons/my")
    public ResponseEntity<ApiResponse<GetLessonListResponse>> getMyLessons(
            @RequestParam(required = false) LessonStatus status,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonListResponse myLessons = lessonService.getMyLessons(userId, status);
        return ResponseEntity.ok(ApiResponse.success(myLessons));
    }

    /**
     * 수업 신청 목록 조회 (멘토) - 인증 필요
     * Get /api/lessons/requests(?status=PENDING)
     */
    public ResponseEntity<ApiResponse<GetLessonRequestListResponse>> getLessonRequests(
            @RequestParam(required = false) LessonStatus status,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonRequestListResponse response = lessonService.getMyLessonRequests(userId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 수업 상세 조회
     * Get /api/lessons/{lessonsId}
     */
    public ResponseEntity<ApiResponse<GetLessonDetailResponse>> getLessonDetail(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Long userId
    ) {
        GetLessonDetailResponse response = lessonService.getLessonDetail(lessonId, userId);
    }


}
