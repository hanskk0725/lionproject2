package com.example.lionproject2backend.mentor.dto;

import com.example.lionproject2backend.mentor.domain.DayOfWeekCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostAvailabilityRequest {

    @NotNull(message = "요일을 선택해주세요")
    private DayOfWeekCode dayOfWeek;

    @NotNull(message = "시작 시간을 입력해주세요")
    private LocalTime startTime;

    @NotNull(message = "종료 시간을 입력해주세요")
    private LocalTime endTime;
}
