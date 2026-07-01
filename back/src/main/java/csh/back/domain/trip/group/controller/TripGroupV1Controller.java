package csh.back.domain.trip.group.controller;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.dto.request.TripGroupModifyRequest;
import csh.back.domain.trip.group.dto.request.TripGroupRequest;
import csh.back.domain.trip.group.dto.response.TripGroupResponse;
import csh.back.domain.trip.group.exception.NotFoundException;
import csh.back.domain.trip.group.service.TripGroupService;
import csh.back.global.dto.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "여행 모임방", description = "여행 모임 관리 API")
@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripGroupV1Controller {

	private final TripGroupService tripGroupService;
	private final MemberRepository memberRepository;

	//Swagger 문서 표시
	@Operation(summary = "모임방 목록 조회(로그인한 사용자 기준)")
	//모임방 조회
	@GetMapping()
	public ResponseData<List<TripGroupResponse>> getAllGroups(
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {

//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, tripGroupService.getGroups(ownerId));
	}

	//Swagger 문서 표시
	@Operation(summary = "모임방 생성")
	//모임방 생성
	@PostMapping()
	public ResponseData<TripGroupResponse> saveGroup(
			@RequestParam Long ownerId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@Valid @RequestBody TripGroupRequest request
	) {
		Member owner = memberRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 유저"));
		return new ResponseData<>(201, tripGroupService.writeGroup(request, owner));
	}

	//Swagger 문서 표시
	@Operation(summary = "상세 모임방 조회")
	//모임방 상세페이지 조회
	@GetMapping("/{groupId}")
	public ResponseData<TripGroupResponse> getGroupDetail(
			@PathVariable Long groupId,
			@RequestParam Long ownerId //FIXME 나중에 @AuthenticationPrincipal 수정예정
	) {
//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, tripGroupService.getGroupDetail(groupId, ownerId));
	}

	//Swagger 문서 표시
	@Operation(summary = "상세 모임방 수")
	//모임방 상세 수정 - name
	@PatchMapping("/{groupId}")
	public ResponseData<TripGroupResponse> modifyGroupName(
			@PathVariable Long groupId,
			@RequestParam Long ownerId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@RequestBody TripGroupModifyRequest request
			) {
		//		Long ownerId = userDetails.getMember().getId(); // id만 추출
		return new ResponseData<>(200, tripGroupService.modifyGroupDetail(groupId, ownerId, request));
	}
}
