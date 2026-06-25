package csh.back.domain.tem.entity;

import csh.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Template extends BaseEntity {
    private String tem;

    @Builder
    private Template (String tem) {
        this.tem = tem;
    }
}
