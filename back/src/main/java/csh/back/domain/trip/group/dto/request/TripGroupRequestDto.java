package csh.back.domain.trip.group.dto.request;

public record TripGroupRequestDto(
		String name,
		String region,
		String startDate,
		String endDate
){
}
