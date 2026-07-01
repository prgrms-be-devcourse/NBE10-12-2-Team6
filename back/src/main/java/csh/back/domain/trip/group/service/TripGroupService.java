package csh.back.domain.trip.group.service;

import csh.back.domain.member.entity.Member;
import csh.back.domain.trip.group.dto.request.TripGroupModifyRequest;
import csh.back.domain.trip.group.dto.request.TripGroupRequest;
import csh.back.domain.trip.group.dto.response.TripGroupResponse;
import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.exception.NotFoundException;
import csh.back.domain.trip.group.repository.TripGroupRepository;
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
public class TripGroupService {

	private final TripGroupRepository tripGroupRepository;
	private final TripMemberRepository tripMemberRepository;

	// 모임방 조회
	@Transactional(readOnly = true)
	public List<TripGroupResponse> getGroups(Long ownerId) {
		List<TripGroup> tripGroups = tripGroupRepository.findAllByOwnerIdOrderByStartDateDesc(ownerId);
		return tripGroups
				.stream()
				.map(TripGroupResponse::from)
				.toList();
	}

	// 모임방 생성
	@Transactional
	public TripGroupResponse writeGroup(TripGroupRequest request, Member owner) {
		LocalDate startDate = LocalDate.parse(request.startDate());
		LocalDate endDate = LocalDate.parse(request.endDate());

		if (endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
		}

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
		TripGroup savedGroup = tripGroupRepository.save(group);

		tripMemberRepository.save(
				TripMember.builder()
						.tripGroup(savedGroup)
						.member(owner)
						.isAdmin(true)
						.build()
		);

		return TripGroupResponse.from(savedGroup);
	}

	//모임 상세 조회
	@Transactional(readOnly = true)
	public TripGroupResponse getGroupDetail(Long groupId, Long ownerId) {
		boolean isMember = tripMemberRepository.existsByTripGroupIdAndMemberId(groupId, ownerId);
		if (!isMember) {
			throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
		}
		TripGroup group = tripGroupRepository.findById(groupId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 모임입니다."));

		return TripGroupResponse.from(group);
	}

	//모임 상세 수정
	//TODO 1차 mvp에서는 name만 수정, 혹시몰라 patch로 진행
	@Transactional
	public TripGroupResponse modifyGroupDetail(Long groupId, Long ownerId, TripGroupModifyRequest request) {
		TripGroup group = tripGroupRepository.findById(groupId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 모임입니다."));

		if (!group.getOwner().getId().equals(ownerId)) {
			throw new IllegalArgumentException("해당 모임의 소유자가 아닙니다.");
		}
		group.modify(request);
		return TripGroupResponse.from(group);
	}
}
