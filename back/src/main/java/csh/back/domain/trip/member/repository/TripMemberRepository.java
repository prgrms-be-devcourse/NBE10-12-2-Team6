package csh.back.domain.trip.member.repository;

import csh.back.domain.trip.member.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {

    //해당 사용자가 특정 여행 모임의 멤버인지 확인
    boolean existsByTripGroupIdAndOwnerId(Long tripId, Long memberId);

    //해당 사용자가 특정 여행 모임의 방장인지 확인
    boolean existsByTripGroupIdAndOwnerIdAndIsAdminTrue(Long tripId, Long memberId);
}