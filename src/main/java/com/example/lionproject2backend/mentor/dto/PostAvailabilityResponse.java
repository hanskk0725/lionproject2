package com.example.lionproject2backend.mentor.dto;

import com.example.lionproject2backend.mentor.domain.DayOfWeekCode;
import com.example.lionproject2backend.mentor.domain.MentorAvailability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class PostAvailabilityResponse {

    private Long id;
    private DayOfWeekCode dayOfWeek;
    private String dayOfWeekKr;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isActive;

    public static PostAvailabilityResponse from(MentorAvailability availability) {
        return PostAvailabilityResponse.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .dayOfWeekKr(availability.getDayOfWeek().getDescription())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .isActive(availability.isActive())
                .build();
    }
}
