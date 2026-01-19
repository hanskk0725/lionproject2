package com.example.lionproject2backend.mentor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PutAvailabilityRequest {

    @NotNull(message = "시작 시간을 입력해주세요")
    private LocalTime startTime;

    @NotNull(message = "종료 시간을 입력해주세요")
    private LocalTime endTime;
}
