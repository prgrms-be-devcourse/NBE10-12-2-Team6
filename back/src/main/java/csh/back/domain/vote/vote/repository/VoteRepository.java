package csh.back.domain.vote.vote.repository;

import csh.back.domain.vote.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v JOIN FETCH v.timeLine tl WHERE tl.tripGroup.id = :tripId")
    List<Vote> findVotesWithTimeLineByTripId(Long tripId);
}
