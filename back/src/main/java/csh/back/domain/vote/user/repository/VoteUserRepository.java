package csh.back.domain.vote.user.repository;

import csh.back.domain.vote.user.entity.VoteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteUserRepository extends JpaRepository<VoteUser, Long> {
    @Query("SELECT vi.id, COUNT(vu) FROM VoteUser vu JOIN vu.voteItem vi WHERE vi.vote.id = :voteId GROUP BY vi.id")
    List<Object[]> countGroupByVoteId(Long voteId);


}
