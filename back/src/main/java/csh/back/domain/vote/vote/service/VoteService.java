package csh.back.domain.vote.vote.service;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.repository.TripGroupRepository;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.member.validator.TripMemberValidator;
import csh.back.domain.trip.timeline.dto.response.TimeLineWithConfirmedPlaceResponse;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import csh.back.domain.vote.vote.dto.response.*;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.domain.vote.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteUserRepository voteUserRepository;
    private final TripGroupRepository tripGroupRepository;
    private final TimeLineRepository timeLineRepository;
    private final TripMemberValidator tripMemberValidator;
    private final TripMemberRepository tripMemberRepository;

    private final int DEFAULT_UPDATE_COUNT = 0;

    public List<VoteFindListResponse> findVoteList(Long tripId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        TripGroup tripGroup = tripGroupRepository.findById(tripId).orElseThrow(RuntimeException::new);

        Integer totalDays = tripGroup.getNights() + 1;
        Map<Integer, List<TimeLine>> byDay = timeLineRepository.findAllByTripGroupId(tripId).stream()
                .collect(Collectors.groupingBy(TimeLine::getDayNumber));

        Map<Long, Vote> byTimeLineId = voteRepository.findVotesWithTimeLineByTripId(tripId).stream()
                .collect(Collectors.toMap(
                        vote -> vote.getTimeLine().getId(),
                        vote -> vote
                ));

        List<VoteFindListResponse> voteFindListResponses = new ArrayList<>();
        for (int day = 1; day <= totalDays; day++) {
            List<TimeLineWithConfirmedPlaceResponse> timeLineResponses =
                    byDay.getOrDefault(day, List.of()).stream()
                            .map(timeLine -> createVoteAndTimeLineResponse(timeLine, byTimeLineId))
                            .toList();

            voteFindListResponses.add(
                    VoteFindListResponse.of(tripGroup.getStartDate().plusDays(day - 1), timeLineResponses)
            );
        }

        return voteFindListResponses;
    }

    public VoteFindWithUpdateCountResponse findVoteItemAndCount(Long tripId, Long voteId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        TripMember tripMember = tripMemberRepository.findByMemberId(memberId).orElseThrow(RuntimeException::new); // 이게 없으면 에러가 맞지
        VoteUser voteUser = voteUserRepository.findByVoteIdAndTripMemberId(voteId ,tripMember.getId()).orElse(null); // 이건 없을수있지

        //투표된 장소 목록을 조회해 옴
        List<VoteItem> voteItemList = voteItemRepository.findAllByVoteIdWithTripPlace(voteId);
        //각 장소에 몇포가 투표 되었는지 카운팅
        Map<Long, Long> countMap = voteUserRepository.countGroupByVoteId(voteId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        VoteItem voteItem = voteUser != null ? voteUser.getVoteItem() : null;
        int updateCount = voteUser != null ? voteUser.getUpdateCount() : DEFAULT_UPDATE_COUNT;

        List<VoteFindResponse> voteFindResponses = voteItemList.stream()
                .map(vi -> VoteFindResponse.of(
                        vi.getTripPlace().getId(),
                        vi.getTripPlace().getName(),
                        countMap.getOrDefault(vi.getId(), 0L),
                        vi.equals(voteItem)
                ))
                .toList();

        //장소의 아이디를 키로 하여 위의 맵에서 횟수를 매핑하여 반환
        return VoteFindWithUpdateCountResponse.of(voteFindResponses, updateCount);
    }

    public List<VoteFindUserResponse> findUserVoteThisPlace(Long tripId, Long voteId, Long placeId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        VoteItem voteItem = voteItemRepository.findByVoteIdAndTripPlaceId(voteId, placeId).orElseThrow(RuntimeException::new);
        List<VoteUser> voteUsers = voteUserRepository.findByVoteItemId(voteItem.getId());
        List<VoteFindUserResponse> responses = voteUsers.stream().map(VoteFindUserResponse::from).toList();
        return responses;
    }

    @Transactional
    public VoteCreateResponse wrapperCreateVote(Long tripId, Long memberId, Long timeLineId) {
        TimeLine timeLine = timeLineRepository.findById(timeLineId).orElseThrow(RuntimeException::new);
        return createVote(tripId, memberId, timeLine);
    }

    @Transactional
    public VoteCreateResponse createVote(Long tripId, Long memberId, TimeLine timeLine) {
        tripMemberValidator.validMember(tripId, memberId);
        TripGroup tripGroup = tripGroupRepository.findById(tripId).orElseThrow(RuntimeException::new);
        TripMember tripMember = tripMemberRepository.findByMemberIdAndTripGroupId(memberId, tripId).orElseThrow(RuntimeException::new);
        Vote vote = Vote
                .builder()
                .tripGroup(tripGroup)
                .timeLine(timeLine)
                .tripMember(tripMember)
                .penddingDays(3)
                .build();
        Vote saved = voteRepository.save(vote);
        return VoteCreateResponse.from(saved);
    }

    @Transactional
    public void createVoteBatch(Long tripId, Long memberId, List<TimeLine> timeLines) {
        tripMemberValidator.validMember(tripId, memberId);
        TripGroup tripGroup = tripGroupRepository.findById(tripId).orElseThrow(RuntimeException::new);
        TripMember tripMember = tripMemberRepository.findByMemberId(memberId).orElseThrow(RuntimeException::new);
        List<Vote> votes = timeLines.stream()
                .map(timeLine -> Vote
                        .builder()
                        .tripGroup(tripGroup)
                        .timeLine(timeLine)
                        .tripMember(tripMember)
                        .penddingDays(3)
                        .build())
                .toList();
        voteRepository.saveAll(votes);
    }

    private TimeLineWithConfirmedPlaceResponse createVoteAndTimeLineResponse(
            TimeLine timeLine,
            Map<Long, Vote> byTimeLindId
    ) {
        Long timeLineId = timeLine.getId();
        Vote vote = byTimeLindId.get(timeLineId);
        if(vote == null) {
            log.error("TimeLine {}과 연결된 Vote가 존재 하지 않습니다! 확인 해주세요!", timeLineId);
            return TimeLineWithConfirmedPlaceResponse.of(timeLine, null);
        }
        return TimeLineWithConfirmedPlaceResponse.of(timeLine, vote.getId());
    }
}
