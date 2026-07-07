package csh.back.domain.trip.post.dto.response;

import csh.back.domain.trip.post.entity.Post;

import java.time.LocalDateTime;

public record PostTimeLineResponse(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String confirmedPlaceName,
        boolean isTaken

) {
    public static PostTimeLineResponse of(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String confirmedPlaceName,
            boolean isTaken) {
        return new PostTimeLineResponse(startTime, endTime, confirmedPlaceName, isTaken);
    }
}
