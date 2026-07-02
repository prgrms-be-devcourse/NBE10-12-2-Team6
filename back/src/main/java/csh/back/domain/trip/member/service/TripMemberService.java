package csh.back.domain.trip.member.service;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.exception.NotFoundException;
import csh.back.domain.trip.group.repository.TripGroupRepository;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripMemberService {

	private final TripGroupRepository tripGroupRepository;
	private final TripMemberRepository tripMemberRepository;
	private final MemberRepository memberRepository;

	// joinCode 조회 후 멤버 추가
	@Transactional
	public void createJoinMember(String joinCode, Long memberId) {
		TripGroup tripGroup = tripGroupRepository.findByJoinCode(joinCode)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 모임방"));

		Member owner = memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));

		tripMemberRepository.save(
				TripMember.builder()
						.member(owner)
						.tripGroup(tripGroup)
						.isAdmin(false)
						.build()
		);
	}
}
