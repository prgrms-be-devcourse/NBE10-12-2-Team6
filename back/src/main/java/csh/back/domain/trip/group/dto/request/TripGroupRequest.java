package csh.back.domain.trip.group.dto.request;

public record TripGroupRequest(
		String name,
		String region,
		String startDate,
		String endDate
){
}
