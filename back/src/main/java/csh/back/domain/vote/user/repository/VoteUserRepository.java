package csh.back.domain.vote.user.repository;

import csh.back.domain.vote.user.entity.VoteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteUserRepository extends JpaRepository<VoteUser, Long> {
//    @Query("select count(v) from VoteUser v where v.voteItem.id = :voteItemId")
    Long countByVoteItemId(Long voteItemId);
}
