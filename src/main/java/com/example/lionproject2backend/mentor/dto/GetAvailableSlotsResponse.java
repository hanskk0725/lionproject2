package com.example.lionproject2backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetAvailableSlotsResponse {

    private Long tutorialId;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private int duration;
    private List<TimeSlot> slots;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TimeSlot {
        private LocalTime time;
        private boolean available;
        private String reason;

        public static TimeSlot available(LocalTime time) {
            return TimeSlot.builder()
                    .time(time)
                    .available(true)
                    .reason(null)
                    .build();
        }

        public static TimeSlot unavailable(LocalTime time, String reason) {
            return TimeSlot.builder()
                    .time(time)
                    .available(false)
                    .reason(reason)
                    .build();
        }
    }
}
