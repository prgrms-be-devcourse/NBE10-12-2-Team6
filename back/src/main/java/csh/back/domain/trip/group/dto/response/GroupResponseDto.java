package csh.back.domain.trip.group.dto.response;

import csh.back.domain.member.entity.Member;
import csh.back.domain.trip.group.entity.TripGroup;

import java.time.LocalDate;

public record GroupResponseDto (
		Long id,
		String name,
		Long ownerId,
		String region,
		String joinUrl,
		int nights,
		LocalDate startDate,
		LocalDate endDate
){
	public static GroupResponseDto from(TripGroup tripGroup) {
		return new GroupResponseDto(
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
