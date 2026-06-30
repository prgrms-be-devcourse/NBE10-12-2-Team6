package csh.back.domain.trip.group.dto.request;

public record TripGroupModifyRequest(
		String name,
		String region,
		String startDate,
		String endDate
) {
}
