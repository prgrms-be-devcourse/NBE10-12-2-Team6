package csh.back.domain.trip.group.controller;

import csh.back.domain.trip.group.dto.response.TripGroupResponse;
import csh.back.domain.trip.group.service.TripGroupService;
import csh.back.global.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
//테스트를 쉽게 하기 위해 제공하는 에노테이션 ->컨트롤러 계층을 별도로 실행하지 않고도 요청과 응답을 모킹하여 테스트 가능함
/*
 * addFilters : spring security와 같은 필터를 추가할지에 대한 여부, 기본값 true
 * */
@AutoConfigureMockMvc // 톰캣만 가짜 → MockMvc로 HTTP 요청 흉내
public class TripGroupV1ControllerTest {
	@Autowired
	MockMvc mvc;  // 실제 서버 안 띄우고 요청/응답 테스트성
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	private TripGroupService tripGroupService;

	String token;
	@BeforeEach //테스트 마다 매번 호출됨
	void setUserToken() {
		token = jwtUtil.generateAccessToken(1L, "admin@admin.com");
	}

	private String BASE_URL = "/api/v1";

	회
//	@WithMockLoginUser // jwt 인증 없이 테스트 진행하고 싶으면
	void t1() throws Exception {
		ResultActions resultActions = mvc
				.perform(
						get(BASE_URL+"/trips")
								.header("Authorization", "Bearer " + token + " " + token))
				.andDo(print());

		Long memberId = jwtUtil.getMemberId(token);
		List<TripGroupResponse> tripGroups = tripGroupService.getGroups(memberId);

		resultActions
				.andExpect(handler().handlerType(TripGroupV1Controller.class))
				.andExpect(handler().methodName("getAllGroups"))
				.andExpect(status().isOk());

		for (int i= 0; i<tripGroups.size(); i++) {
			TripGroupResponse trip = tripGroups.get(i);
			resultActions.andExpect(jsonPath("$.data[%d].ownerId".formatted(i)).value(trip.ownerId()));
		}
	}

	@Test
	@DisplayName("user 정보 없이 모임방 조회")
	void t2() throws Exception {
		ResultActions resultActions = mvc
				.perform(
						get(BASE_URL+"/trips")
				)
				.andDo(print());

		resultActions
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("존재하지 않는 유저"));
	}

	@Test
	@DisplayName("모임방 생성")
	void t3() throws Exception {
		
	}
}
