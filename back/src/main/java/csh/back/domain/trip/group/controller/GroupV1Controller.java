package csh.back.domain.trip.group.controller;

import csh.back.domain.member.entity.Member;
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

	@GetMapping()
	public ResponseData getAllGroups(
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {

//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		List<GroupResponseDto> response = groupService.getGroups(ownerId);
		return new ResponseData(200, response);
	}

	@PostMapping()
	public ResponseData saveGroup(
			@RequestParam Long ownerId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@RequestBody GroupRequestDto request
	) {
		Member owner = memberRepository.findById(ownerId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저"));
		GroupResponseDto response = groupService.writeGroup(request, owner); // owner는 나중에 Security에서 주입
		return new ResponseData(200, response);
	}

//	@GetMapping("/{groupId}")
//	public void getGroupDetail() {
//
//	}
}
