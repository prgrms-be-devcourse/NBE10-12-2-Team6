package csh.back.domain.vote.item.repository;

import csh.back.domain.vote.item.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    VoteItem findByTripPlaceId(Long placeId);
    List<VoteItem> findByVoteId(Long voteId);
}
