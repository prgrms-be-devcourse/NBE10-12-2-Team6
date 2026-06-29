package csh.back.domain.tem.entity;

import csh.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Template extends BaseEntity {
    private String tem;


    //생성자
    //빌드 사용
    @Builder
    private Template (String tem) {
        this.tem = tem;
    }
}
