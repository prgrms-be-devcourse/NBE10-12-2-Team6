package csh.back.domain.vote.item.repository;

import csh.back.domain.vote.item.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    Optional<VoteItem> findByTripPlace_Id(Long placeId);
}
