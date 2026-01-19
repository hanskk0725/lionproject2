package com.example.lionproject2backend.mentor.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.mentor.dto.*;
import com.example.lionproject2backend.mentor.service.MentorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MentorScheduleController {

    private final MentorScheduleService mentorScheduleService;

    /**
     * 내 가용 시간 조회 (멘토 본인)
     */
    @GetMapping("/mentors/me/availability")
    public ResponseEntity<ApiResponse<GetAvailabilityResponse>> getMyAvailability(
            @AuthenticationPrincipal Long userId) {
        GetAvailabilityResponse response = mentorScheduleService.getMyAvailability(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 가용 시간 등록 (멘토 본인)
     */
    @PostMapping("/mentors/me/availability")
    public ResponseEntity<ApiResponse<PostAvailabilityResponse>> addAvailability(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PostAvailabilityRequest request) {
        PostAvailabilityResponse response = mentorScheduleService.addAvailability(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("가용 시간이 등록되었습니다", response));
    }

    /**
     * 가용 시간 수정 (멘토 본인)
     */
    @PutMapping("/mentors/me/availability/{availabilityId}")
    public ResponseEntity<ApiResponse<PostAvailabilityResponse>> updateAvailability(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long availabilityId,
            @RequestBody @Valid PutAvailabilityRequest request) {
        PostAvailabilityResponse response = mentorScheduleService.updateAvailability(userId, availabilityId, request);
        return ResponseEntity.ok(ApiResponse.success("가용 시간이 수정되었습니다", response));
    }

    /**
     * 가용 시간 삭제 (멘토 본인)
     */
    @DeleteMapping("/mentors/me/availability/{availabilityId}")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long availabilityId) {
        mentorScheduleService.deleteAvailability(userId, availabilityId);
        return ResponseEntity.ok(ApiResponse.success("가용 시간이 삭제되었습니다", null));
    }

    /**
     * 특정 멘토의 가용 시간 조회 (공개)
     */
    @GetMapping("/mentors/{mentorId}/availability")
    public ResponseEntity<ApiResponse<GetAvailabilityResponse>> getMentorAvailability(
            @PathVariable Long mentorId) {
        GetAvailabilityResponse response = mentorScheduleService.getMentorAvailability(mentorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 특정 날짜의 예약 가능 슬롯 조회 (공개)
     */
    @GetMapping("/tutorials/{tutorialId}/available-slots")
    public ResponseEntity<ApiResponse<GetAvailableSlotsResponse>> getAvailableSlots(
            @PathVariable Long tutorialId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GetAvailableSlotsResponse response = mentorScheduleService.getAvailableSlots(tutorialId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
