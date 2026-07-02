package csh.back.domain.trip.post.repository;

import csh.back.domain.trip.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTimeLineTripGroupId(Long tripId);

}
