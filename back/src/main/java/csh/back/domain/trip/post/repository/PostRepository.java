package csh.back.domain.trip.post.repository;

import csh.back.domain.trip.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    //trip의 하위 개념으로 url를 수정했기 때문에 post는 tripid를 참조해야 하므로 추가
    List<Post> findByTimeLine_TripGroup_Id(Long tripId);

}