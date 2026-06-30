package csh.back.domain.trip.member.repository;

import csh.back.domain.trip.member.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
}