package csh.back.domain.trip.timeline.dto.response;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.timeline.entity.TimeLine;

import java.time.LocalDateTime;

public record TimeLineWithConfirmedPlaceResponse(
        Long voteId,
        String confirmedPlaceName,
        LocalDateTime startTime
) {
    public static TimeLineWithConfirmedPlaceResponse of(TimeLine timeLine, Long voteId) {
        TripPlace tripPlace = timeLine.getConfirmedPlace();
        String confirmedPlaceName = tripPlace == null ? "" : tripPlace.getName();
        return new TimeLineWithConfirmedPlaceResponse(
                voteId,
                confirmedPlaceName,
                timeLine.getStartTime()
        );
    }
}
