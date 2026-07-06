package csh.back.domain.vote.vote.dto.response;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.timeline.entity.TimeLine;

import java.time.LocalDateTime;

public record VoteWithTimeLineResponse(
        Long voteId,
        Long timeLineId,
        LocalDateTime startTime,
        String confirmedPlaceName
) {
    public static VoteWithTimeLineResponse of(TimeLine timeLine, Long voteId) {
        TripPlace tripPlace = timeLine.getConfirmedPlace();
        String confirmedPlaceName = tripPlace == null ? "" : tripPlace.getName();
        return new VoteWithTimeLineResponse(
                voteId,
                timeLine.getId(),
                timeLine.getStartTime(),
                confirmedPlaceName
        );
    }
}
