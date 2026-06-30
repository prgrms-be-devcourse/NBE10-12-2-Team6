package csh.back.domain.vote.item.repository;

import csh.back.domain.vote.item.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
}
