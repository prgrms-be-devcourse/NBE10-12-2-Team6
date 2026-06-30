package csh.back.domain.vote.user.repository;

import csh.back.domain.vote.user.entity.VoteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteUserRepository extends JpaRepository<VoteUser, Long> {
}
