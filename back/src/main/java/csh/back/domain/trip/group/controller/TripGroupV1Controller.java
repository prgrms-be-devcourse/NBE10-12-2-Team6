package csh.back.domain.trip.group.controller;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.dto.request.TripGroupRequestDto;
import csh.back.domain.trip.group.dto.response.TripGroupResponseDto;
import csh.back.domain.trip.group.exception.NotFoundException;
import csh.back.domain.trip.group.service.TripGroupService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripGroupV1Controller {

	private final TripGroupService tripGroupService;
	private final MemberRepository memberRepository;

	//모임방 조회
	@GetMapping()
	public ResponseData<List<TripGroupResponseDto>> getAllGroups(
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {

//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, tripGroupService.getGroups(ownerId));
	}

	//모임방 생성
	@PostMapping()
	public ResponseData<TripGroupResponseDto> saveGroup(
			@RequestParam Long ownerId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@RequestBody TripGroupRequestDto request
	) {
		Member owner = memberRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));
		return new ResponseData<>(201, tripGroupService.writeGroup(request, owner));
	}

	//모임방 상세페이지 조회
	@GetMapping("/{groupId}")
	public ResponseData<TripGroupResponseDto> getGroupDetail(
			@PathVariable Long groupId,
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {
//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, tripGroupService.getGroupDetail(groupId, ownerId));
	}
//
//	//모임방 상세 수정 - name
//	@PatchMapping("/{groupId}")
//	public ResponseData<>
}
