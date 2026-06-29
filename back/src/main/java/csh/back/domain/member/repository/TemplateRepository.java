package csh.back.domain.member.repository;

import csh.back.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Member, Long> {
}
