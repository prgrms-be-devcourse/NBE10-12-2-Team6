"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import { useStore, TripDay, PlanCandidate } from "../../../../../../store";
import { timeText } from "../../../../../../lib";

// ── Tie random pick sheet ─────────────────────────────────────────────────────

function TieRandomSheet({
  candidates, onPick, onClose,
}: { candidates: PlanCandidate[]; onPick: (c: PlanCandidate) => void; onClose: () => void }) {
  const [picked, setPicked] = useState<PlanCandidate | null>(null);
  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative w-full max-w-md bg-white rounded-t-3xl p-6">
        <p className="text-lg font-bold mb-1">동점 후보 랜덤 뽑기</p>
        <p className="text-sm text-gray-500 mb-4">동점으로 나온 후보들끼리 랜덤 뽑기를 진행합니다.</p>
        <div className="flex flex-col gap-2 mb-4">
          {candidates.map(c => (
            <div key={c.id} className="p-3 bg-gray-50 rounded-xl">
              <p className="font-semibold text-sm">{c.placeName}</p>
            </div>
          ))}
        </div>
        {picked && <p className="text-center font-semibold mb-3" style={{ color: "#9333ea" }}>뽑힌 후보: {picked.placeName}</p>}
        <button
          onClick={() => setPicked(candidates[Math.floor(Math.random() * candidates.length)])}
          className="w-full py-3.5 rounded-2xl font-semibold text-white mb-2"
          style={{ background: "#9333ea" }}
        >
          랜덤 뽑기
        </button>
        {picked && (
          <button
            onClick={() => { onPick(picked); onClose(); }}
            className="w-full py-3.5 rounded-2xl bg-blue-500 text-white font-semibold"
          >
            이 후보로 확정
          </button>
        )}
      </div>
    </div>
  );
}

// ── Main ──────────────────────────────────────────────────────────────────────

export default function BlockDetailPage() {
  const router = useRouter();
  const { id, dayNumber, blockId } = useParams<{ id: string; dayNumber: string; blockId: string }>();
  const searchParams = useSearchParams();
  const goBack = () => {
    if (searchParams.get("from") === "vote") router.push(`/trip/${id}?tab=vote`);
    else router.back();
  };
  const { trips, updateTrip, currentUser } = useStore();


  const [showTie, setShowTie] = useState(false);
  const [tieCandidates, setTieCandidates] = useState<PlanCandidate[]>([]);
  const [activeCategory, setActiveCategory] = useState<string | null>(null);
  const [pendingVote, setPendingVote] = useState<string | null>(null);
  const [showHostMenu, setShowHostMenu] = useState(false);

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;
  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];
  const block = day.blocks.find(b => b.id === blockId);
  if (!block) return null;

  const selectedId = day.selectedCandidateByBlock[blockId];
  const selected = trip.candidates.find(c => c.id === selectedId);

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const voteCount = (candidateId: string): number =>
    day.votedUserIDsByBlockAndCandidate[blockId]?.[candidateId]?.length ?? 0;

  const isVoted = (candidateId: string): boolean =>
    day.votedUserIDsByBlockAndCandidate[blockId]?.[candidateId]?.includes(currentUser.id) ?? false;

  const vote = (candidateId: string) => {
    const blockVotes = { ...(day.votedUserIDsByBlockAndCandidate[blockId] ?? {}) };
    for (const cid of Object.keys(blockVotes)) {
      blockVotes[cid] = (blockVotes[cid] ?? []).filter(uid => uid !== currentUser.id);
    }
    blockVotes[candidateId] = [...(blockVotes[candidateId] ?? []), currentUser.id];
    setDay({ ...day, votedUserIDsByBlockAndCandidate: { ...day.votedUserIDsByBlockAndCandidate, [blockId]: blockVotes } });
  };

  const randomVote = () => {
    if (trip.candidates.length === 0) return;
    const picked = trip.candidates[Math.floor(Math.random() * trip.candidates.length)];
    vote(picked.id);
  };

  const randomConfirm = () => {
    if (trip.candidates.length === 0) return;
    const picked = trip.candidates[Math.floor(Math.random() * trip.candidates.length)];
    setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: picked.id } });
    setShowHostMenu(false);
  };

  const isHost = currentUser.id === trip.members[0]?.id;

  const decideByVote = () => {
    if (trip.candidates.length === 0) return;
    const maxVote = Math.max(...trip.candidates.map(c => voteCount(c.id)));
    const winners = trip.candidates.filter(c => voteCount(c.id) === maxVote);
    if (winners.length === 1) {
      setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: winners[0].id } });
    } else {
      setTieCandidates(winners);
      setShowTie(true);
    }
    setShowHostMenu(false);
  };

  const pickFromTie = (candidate: PlanCandidate) => {
    setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: candidate.id } });
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={goBack} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{block.order}번째 구간</h1>
        {isHost ? (
          <div className="relative">
            <button
              onClick={() => setShowHostMenu(v => !v)}
              className="text-xs font-bold px-2.5 py-1.5 rounded-full"
              style={{ background: showHostMenu ? "#fef08a" : "#fef9c3", color: "#92400e" }}
            >
              방장
            </button>
            {showHostMenu && (
              <>
                <div className="fixed inset-0 z-40" onClick={() => setShowHostMenu(false)} />
                <div className="absolute right-0 top-9 z-50 bg-white rounded-2xl shadow-xl border border-gray-100 p-2 flex flex-col gap-1 w-36">
                  <button
                    onClick={randomConfirm}
                    disabled={trip.candidates.length === 0}
                    className="w-full px-3 py-2.5 rounded-xl text-sm font-semibold text-left disabled:opacity-40"
                    style={{ background: "#f3e8ff", color: "#9333ea" }}
                  >
                    🔀 랜덤 확정
                  </button>
                  <button
                    onClick={decideByVote}
                    disabled={trip.candidates.length === 0}
                    className="w-full px-3 py-2.5 rounded-xl text-sm font-semibold text-left disabled:opacity-40"
                    style={{ background: "#dcfce7", color: "#16a34a" }}
                  >
                    📊 투표 확정
                  </button>
                </div>
              </>
            )}
          </div>
        ) : (
          <div className="w-8" />
        )}
      </div>

      <div className="flex-1 overflow-y-scroll px-4 pt-2 pb-4 flex flex-col gap-5">
        {/* Header */}
        <div>
          <p className="text-xs text-gray-400 font-bold">{timeText(block.startMinute)} ~ {timeText(block.endMinute)}</p>
          <p className="text-2xl font-bold mt-1">{block.order}번째 구간 후보 보기</p>
        </div>

        {/* Selected */}
        {selected ? (
          <div className="p-4 rounded-2xl" style={{ background: "#dcfce7" }}>
            <p className="text-sm font-semibold text-green-700 mb-2">이 구간에 확정된 후보</p>
            <div className="flex items-start gap-3">
              <span className="text-green-500 text-xl mt-0.5">✓</span>
              <div>
                <p className="font-bold">{selected.placeName}</p>
                <p className="text-xs text-gray-500 mt-0.5">{selected.address}</p>
                <p className="text-xs text-gray-400 mt-0.5">등록자 {selected.authorName}</p>
              </div>
            </div>
          </div>
        ) : (
          <div className="p-3 rounded-xl flex items-start gap-2" style={{ background: "#fff7ed" }}>
            <span className="shrink-0">💡</span>
            <div>
              <p className="text-sm font-semibold">아직 확정된 후보가 없습니다.</p>
              <p className="text-xs text-gray-500">전체 후보 중 하나를 투표 또는 랜덤으로 확정하세요.</p>
            </div>
          </div>
        )}

        {/* All candidates */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <p className="font-semibold">전체 후보 목록</p>
            <span className="text-xs text-gray-400 font-bold">{trip.candidates.length}개</span>
          </div>

          {/* 동적 카테고리 필터 */}
          {trip.candidates.length > 0 && (() => {
            const namedCategories = Array.from(new Set(trip.candidates.map(c => c.category).filter(Boolean))) as string[];
            const hasUncategorized = trip.candidates.some(c => !c.category);
            const categories = [...namedCategories, ...(hasUncategorized ? ["기타"] : [])];
            if (categories.length === 0) return null;
            return (
              <div className="flex gap-2 overflow-x-auto pb-2 mb-3">
                <button
                  onClick={() => setActiveCategory(null)}
                  className="shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold border transition-all"
                  style={activeCategory === null ? { background: "#3b82f6", color: "white", borderColor: "#3b82f6" } : { background: "white", color: "#6b7280", borderColor: "#e5e7eb" }}
                >
                  전체
                </button>
                {categories.map(cat => (
                  <button
                    key={cat}
                    onClick={() => setActiveCategory(activeCategory === cat ? null : cat)}
                    className="shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold border transition-all"
                    style={activeCategory === cat ? { background: "#3b82f6", color: "white", borderColor: "#3b82f6" } : { background: "white", color: "#6b7280", borderColor: "#e5e7eb" }}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            );
          })()}

          {trip.candidates.length === 0 ? (
            <div className="p-4 bg-gray-50 rounded-2xl">
              <p className="text-sm text-gray-400">아직 후보가 없습니다. 여행 모임 상세 화면에서 후보를 먼저 올려주세요.</p>
            </div>
          ) : (
            <div className="flex flex-col gap-3">
              {trip.candidates.filter(c => {
                if (!activeCategory) return true;
                if (activeCategory === "기타") return !c.category;
                return c.category === activeCategory;
              }).map(c => {
                const isSelected = c.id === selectedId;
                const voted = isVoted(c.id);
                return (
                  <div
                    key={c.id}
                    onClick={() => setPendingVote(c.id)}
                    className="p-4 rounded-2xl border cursor-pointer active:scale-[0.98] transition-transform"
                    style={{
                      background: isSelected ? "#dcfce7" : pendingVote === c.id ? "#fefce8" : voted ? "#eff6ff" : "white",
                      borderColor: isSelected ? "#4ade80" : pendingVote === c.id ? "#facc15" : voted ? "#93c5fd" : "#e5e7eb",
                    }}
                  >
                    <div className="flex items-start gap-2 mb-2">
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <p className="font-semibold text-sm">{c.placeName}</p>
                          {isSelected && <span className="text-green-500">✓</span>}
                        </div>
                        <p className="text-xs text-gray-400 mt-0.5">{c.address}</p>
                      </div>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-xs text-gray-400">등록자 {c.authorName}</span>
                      <div className="flex items-center gap-2">
                        <span className="text-xs font-bold text-gray-600">{voteCount(c.id)}표</span>
                        {voted && (
                          <span className="text-xs font-semibold px-2 py-0.5 rounded-full" style={{ background: "#dbeafe", color: "#2563eb" }}>
                            내 투표
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

      </div>

      {/* 하단 고정 버튼 */}
      <div className="px-4 py-4 border-t border-gray-100 flex gap-2 bg-white">
        <button
          onClick={randomVote}
          disabled={trip.candidates.length === 0}
          className="flex-1 py-4 rounded-2xl font-semibold disabled:opacity-40"
          style={{ background: "#f3e8ff", color: "#9333ea" }}
        >
          🔀 랜덤 투표
        </button>
        <button
          onClick={() => { if (pendingVote) { vote(pendingVote); setPendingVote(null); } }}
          disabled={!pendingVote}
          className="flex-1 py-4 rounded-2xl font-semibold disabled:opacity-40"
          style={{ background: "#dbeafe", color: "#2563eb" }}
        >
          투표하기
        </button>
      </div>

      {showTie && (
        <TieRandomSheet
          candidates={tieCandidates}
          onPick={pickFromTie}
          onClose={() => setShowTie(false)}
        />
      )}
    </div>
  );
}
