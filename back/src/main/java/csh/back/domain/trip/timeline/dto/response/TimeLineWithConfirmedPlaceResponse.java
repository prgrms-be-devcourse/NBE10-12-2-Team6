package csh.back.domain.trip.timeline.dto.response;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.timeline.entity.TimeLine;

import java.time.LocalDateTime;

public record TimeLineWithConfirmedPlaceResponse(
        Long timelineId,
        String confirmedPlaceName,
        LocalDateTime startTime
) {
    public static TimeLineWithConfirmedPlaceResponse from(TimeLine timeLine) {
        TripPlace tripPlace = timeLine.getConfirmedPlace();
        String confirmedPlaceName = tripPlace == null ? "" : tripPlace.getName();
        return new TimeLineWithConfirmedPlaceResponse(
                timeLine.getId(),
                confirmedPlaceName,
                timeLine.getStartTime()
        );
    }
}
