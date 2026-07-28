package csh.back.domain.member.controller;

import csh.back.domain.member.support.WithMockMember;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    MockMvc mvc;

    private static final String BASE_URL = "/api/v1/auth";

    // ── 회원가입 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("회원가입 성공")
    void t1() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "newuser@test.com",
                                    "password": "password123",
                                    "name": "테스트유저"
                                }
                                """)
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("signUp"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.data.name").value("테스트유저"));
    }

    @Test
    @DisplayName("회원가입 - 이미 존재하는 이메일")
    void t2() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "admin@admin.com",
                                    "password": "1234",
                                    "name": "admin2"
                                }
                                """)
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("signUp"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    @Test
    @DisplayName("회원가입 - 이메일 형식 오류")
    void t3() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "notanemail",
                                    "password": "1234",
                                    "name": "유저"
                                }
                                """)
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("signUp"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: must be a well-formed email address"));
    }

    @Test
    @DisplayName("회원가입 - 빈 name")
    void t4() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@test.com",
                                    "password": "1234",
                                    "name": ""
                                }
                                """)
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("signUp"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: must not be blank"));
    }

    // ── 로그인 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("로그인 성공")
    void t5() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "admin@admin.com",
                                    "password": "1234"
                                }
                                """)
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("admin@admin.com"))
                .andExpect(jsonPath("$.data.name").value("admin"))
                .andExpect(header().exists("Authorization"))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일")
    void t6() {
        Exception ex = assertThrows(Exception.class, () ->
                mvc.perform(
                        post(BASE_URL + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "notexist@test.com",
                                            "password": "1234"
                                        }
                                        """)
                )
        );
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        assertInstanceOf(RuntimeException.class, root);
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치")
    void t7() {
        Exception ex = assertThrows(Exception.class, () ->
                mvc.perform(
                        post(BASE_URL + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "admin@admin.com",
                                            "password": "wrongpassword"
                                        }
                                        """)
                )
        );
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        assertInstanceOf(RuntimeException.class, root);
    }

    // ── 로그아웃 ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("로그아웃 성공")
    @WithMockMember
    void t8() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/logout")
        ).andDo(print());

        result
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    @Test
    @DisplayName("로그아웃 - 미인증 상태")
    void t9() throws Exception {
        ResultActions result = mvc.perform(
                post(BASE_URL + "/logout")
        ).andDo(print());

        result
                .andExpect(status().isForbidden());
    }
}