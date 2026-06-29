package csh.back.domain.member.entity;

import csh.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//멤버 엔티티
@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    //회원 이메일
    private String email;
    //회원 비밀번호
    private String password;
    //회원 이름 혹은 닉네임
    private String name;

    //생성자
    //빌드 사용
    @Builder
    private Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
