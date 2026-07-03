package csh.back.domain.vote.vote.service;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.repository.TripGroupRepository;
import csh.back.domain.trip.member.validator.TripMemberValidator;
import csh.back.domain.trip.timeline.dto.response.TimeLineWithConfirmedPlaceResponse;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import csh.back.domain.vote.vote.dto.response.VoteFindListResponse;
import csh.back.domain.vote.vote.dto.response.VoteFindResponse;
import csh.back.domain.vote.vote.dto.response.VoteFindUserResponse;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.domain.vote.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteUserRepository voteUserRepository;
    private final VoteItemRepository voteItemRepository;
    private final TripGroupRepository tripGroupRepository;
    private final TripMemberValidator tripMemberValidator;
    private final VoteRepository voteRepository;

    public List<VoteFindListResponse> findVoteList(Long tripId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        TripGroup tripGroup = tripGroupRepository.findById(tripId).orElseThrow(RuntimeException::new);

        Integer totalDays = tripGroup.getNights() + 1;
        List<Vote> votes = voteRepository.findVotesWithTimeLineByTripId(tripId);
        List<TimeLine> timeLines = votes.stream().map(vote -> vote.getTimeLine()).toList();

        Map<Integer, List<TimeLine>> byDay = timeLines.stream()
                .collect(Collectors.groupingBy(TimeLine::getDayNumber));
        for(Vote vote : votes){
            vote.getTimeLine().getId();
        }

        Map<Long, Vote> byTimeLindId = votes.stream()
                .collect(Collectors.toMap(
                        vote -> vote.getTimeLine().getId(),
                        vote -> vote
                ));


        List<VoteFindListResponse> voteFindListResponses = new ArrayList<>();
        for (int day = 1; day <= totalDays; day++) {
            List<TimeLineWithConfirmedPlaceResponse> timeLineResponses =
                    byDay.getOrDefault(day, List.of()).stream()
                            .map(timeLine -> createVoteAndTimeLineResponse(timeLine, byTimeLindId))
                            .toList();

            voteFindListResponses.add(
                    VoteFindListResponse.of(tripGroup.getStartDate().plusDays(day - 1), timeLineResponses)
            );
        }

        return voteFindListResponses;
    }
    private TimeLineWithConfirmedPlaceResponse createVoteAndTimeLineResponse(TimeLine timeLine, Map<Long, Vote> byTimeLindId) {
        Vote vote = byTimeLindId.getOrDefault(timeLine.getId(), null);
        if(vote == null){
            //create vote해야한다
        }
        return TimeLineWithConfirmedPlaceResponse.of(timeLine, vote.getId());
    }

    public List<VoteFindResponse> findVoteItemAndCount(Long tripId, Long voteId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        //투표된 장소 목록을 조회해 옴
        List<VoteItem> voteItemList = voteItemRepository.findByVoteIdWithTripPlace(voteId);

        //각 장소에 몇포가 투표 되었는지 카운팅
        Map<Long, Long> countMap = voteUserRepository.countGroupByVoteId(voteId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        //장소의 아이디를 키로 하여 위의 맵에서 횟수를 매핑하여 반환
        return voteItemList.stream()
                .map(vi -> VoteFindResponse.from(
                        vi.getTripPlace().getId(),
                        vi.getTripPlace().getName(),
                        countMap.getOrDefault(vi.getId(), 0L)
                ))
                .toList();
    }

    public List<VoteFindUserResponse> findUserVoteThisPlace(Long tripId, Long voteId, Long placeId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        VoteItem voteItem = voteItemRepository.findByVoteIdAndTripPlaceId(voteId, placeId).orElseThrow(RuntimeException::new);
        List<VoteUser> voteUsers = voteUserRepository.findByVoteItemId(voteItem.getId());
        List<VoteFindUserResponse> responses = voteUsers.stream().map(VoteFindUserResponse::from).toList();
        return responses;
    }
}
