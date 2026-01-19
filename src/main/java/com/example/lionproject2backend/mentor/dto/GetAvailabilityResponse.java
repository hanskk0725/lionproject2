package com.example.lionproject2backend.mentor.dto;

import com.example.lionproject2backend.mentor.domain.DayOfWeekCode;
import com.example.lionproject2backend.mentor.domain.MentorAvailability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetAvailabilityResponse {

    private Long mentorId;
    private String mentorNickname;
    private List<AvailabilityInfo> availability;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AvailabilityInfo {
        private Long id;
        private DayOfWeekCode dayOfWeek;
        private String dayOfWeekKr;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean isActive;

        public static AvailabilityInfo from(MentorAvailability availability) {
            return AvailabilityInfo.builder()
                    .id(availability.getId())
                    .dayOfWeek(availability.getDayOfWeek())
                    .dayOfWeekKr(availability.getDayOfWeek().getDescription())
                    .startTime(availability.getStartTime())
                    .endTime(availability.getEndTime())
                    .isActive(availability.isActive())
                    .build();
        }
    }

    public static GetAvailabilityResponse of(Long mentorId, String nickname, List<MentorAvailability> availabilities) {
        List<AvailabilityInfo> availabilityInfos = availabilities.stream()
                .map(AvailabilityInfo::from)
                .toList();

        return GetAvailabilityResponse.builder()
                .mentorId(mentorId)
                .mentorNickname(nickname)
                .availability(availabilityInfos)
                .build();
    }
}
