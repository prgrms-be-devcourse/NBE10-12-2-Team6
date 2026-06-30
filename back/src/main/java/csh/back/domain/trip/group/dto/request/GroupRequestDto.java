package csh.back.domain.trip.group.dto.request;

public record GroupRequestDto (
		String name,
		String region,
		String startDate,
		String endDate
){
}
