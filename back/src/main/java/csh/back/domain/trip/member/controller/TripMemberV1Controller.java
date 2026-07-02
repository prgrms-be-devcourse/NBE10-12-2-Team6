package csh.back.domain.trip.member.controller;

import csh.back.domain.trip.group.service.TripGroupService;
import csh.back.global.dto.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "여행 맴버", description = "여행 멤버 관리 API")
@RestController
@RequestMapping("/trip_member")
@RequiredArgsConstructor
public class TripMemberV1Controller {
	private final TripGroupService tripGroupService;

	//Swagger 문서 표시
	@Operation(summary = "초대 코드를 통한 여행 멤버 등록")
	@PostMapping("/join/{joinCode}")
	public ResponseData createJoinMember(
			@RequestParam Long memberId, //FIXME 나중에 @AuthenticationPrincipal 수정예정
			@PathVariable String joinCode
	) {
		//Long memberId = userDetails.getMember().getId();
		return new ResponseData<>(200, "");
	}
}
