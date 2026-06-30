package csh.back.domain.trip.group.dto.response;

import csh.back.domain.trip.group.entity.TripGroup;

import java.time.LocalDate;

public record TripGroupResponse(
		Long id,
		String name,
		Long ownerId,
		String region,
		String joinUrl,
		int nights,
		LocalDate startDate,
		LocalDate endDate
){
	public static TripGroupResponse from(TripGroup tripGroup) {
		return new TripGroupResponse(
				tripGroup.getId(),
				tripGroup.getName(),
				tripGroup.getOwner().getId(),
				tripGroup.getRegion(),
				tripGroup.getJoinUrl(),
				tripGroup.getNights(),
				tripGroup.getStartDate(),
				tripGroup.getEndDate()
		);
	}
}
