package csh.back.domain.trip.group.controller;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.dto.request.GroupRequestDto;
import csh.back.domain.trip.group.dto.response.GroupResponseDto;
import csh.back.domain.trip.group.service.GroupService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class GroupV1Controller {

	private final GroupService groupService;
	private final MemberRepository memberRepository;

	//모임방 조회
	@GetMapping()
	public ResponseData<List<GroupResponseDto>> getAllGroups(
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {

//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, groupService.getGroups(ownerId));
	}

	//모임방 생성
	@PostMapping()
	public ResponseData<GroupResponseDto> saveGroup(
			@RequestParam Long ownerId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@RequestBody GroupRequestDto request
	) {
		Member owner = memberRepository.findById(ownerId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));
		return new ResponseData<>(201, groupService.writeGroup(request, owner));
	}

//	@GetMapping("/{groupId}")
//	public void getGroupDetail() {
//
//	}
}
