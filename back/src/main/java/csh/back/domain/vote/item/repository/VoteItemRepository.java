package csh.back.domain.vote.item.repository;

import csh.back.domain.vote.item.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    Optional<VoteItem> findByTripPlaceId(Long placeId);

    @Query("SELECT vi FROM VoteItem vi JOIN FETCH vi.tripPlace WHERE vi.vote.id = :voteId")
    List<VoteItem> findByVoteIdWithTripPlace(Long voteId);

    Optional<VoteItem> findByVoteIdAndTripPlaceId(Long voteId, Long placeId);
}
