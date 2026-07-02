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

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));

		if (tripMemberRepository.existsByTripGroupIdAndMemberId(tripGroup.getId(), member.getId())) {
			//이미 가입되었으면 성공으로 save 없이 통과
			// 혹시 모를 서버 중단으로 save가 2번 이상 될 수 있는 점을 예외처리 하기 위해 추가함
			return;
		}

		tripMemberRepository.save(
				TripMember.builder()
						.member(member)
						.tripGroup(tripGroup)
						.isAdmin(false)
						.build()
		);
	}
}
