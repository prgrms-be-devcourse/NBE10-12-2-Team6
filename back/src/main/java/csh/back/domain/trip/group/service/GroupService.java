package csh.back.domain.trip.group.service;

import csh.back.domain.member.entity.Member;
import csh.back.domain.trip.group.dto.request.GroupRequestDto;
import csh.back.domain.trip.group.dto.response.GroupResponseDto;
import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.exception.GroupNotFoundException;
import csh.back.domain.trip.group.repository.GroupRepository;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final TripMemberRepository tripMemberRepository;

	// 모임방 조회
	@Transactional(readOnly = true)
	public List<GroupResponseDto> getGroups(Long ownerId) {
		List<TripGroup> tripGroups = groupRepository.findAllByOwnerIdOrderByStartDateDesc(ownerId);
		return tripGroups
				.stream()
				.map(GroupResponseDto::from)
				.toList();
	}

	// 모임방 생성
	@Transactional
	public GroupResponseDto writeGroup(GroupRequestDto request, Member owner) {
		LocalDate startDate = LocalDate.parse(request.startDate());
		LocalDate endDate = LocalDate.parse(request.endDate());
		int nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
		// FIXME joinUrl 생성함수를 넣어서 수정예정-윤선
		TripGroup group = TripGroup.builder()
				.owner(owner)
				.name(request.name())
				.region(request.region())
				.nights(nights)
				.joinUrl("welcomeTripGroup")
				.startDate(startDate)
				.endDate(endDate)
				.build();
		TripGroup savedGroup = groupRepository.save(group);

		tripMemberRepository.save(
				TripMember.builder()
						.tripGroup(savedGroup)
						.owner(owner)
						.isAdmin(true)
						.build()
		);

		return GroupResponseDto.from(savedGroup);
	}

	//모임 상세 조회
	@Transactional(readOnly = true)
	public GroupResponseDto getGroupDetail(Long groupId, Long ownerId) {
		//TODO 멤버가 아닐경우에 대해서 조회 안된다는 로직 필요

		TripGroup group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("존재하지 않는 모임입니다."));

		return GroupResponseDto.from(group);
	}
}
