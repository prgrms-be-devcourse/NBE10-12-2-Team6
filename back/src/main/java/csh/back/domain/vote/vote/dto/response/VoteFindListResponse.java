package csh.back.domain.vote.vote.dto.response;

import csh.back.domain.trip.timeline.dto.response.TimeLineWithConfirmedPlaceResponse;

import java.time.LocalDate;
import java.util.List;

public record VoteFindListResponse(
        LocalDate date,
        List<TimeLineWithConfirmedPlaceResponse> timeLines
) {
        public static VoteFindListResponse of(LocalDate date, List<TimeLineWithConfirmedPlaceResponse> timeLines) {
            return new VoteFindListResponse(date, timeLines);
        }
}
