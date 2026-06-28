"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { useStore, TripDay, PlanCandidate, uid } from "../../../../../../store";
import { THEME, ThemeBadge, timeText, Avatar } from "../../../../../../lib";

// ── Add candidate sheet ────────────────────────────────────────────────────────

function AddCandidateSheet({
  members, blockId, theme, existingAuthorIds,
  onAdd, onClose,
}: {
  members: { id: number; name: string; color: string }[];
  blockId: string;
  theme: string;
  existingAuthorIds: number[];
  onAdd: (c: PlanCandidate) => void;
  onClose: () => void;
}) {
  const [authorId, setAuthorId] = useState(members[0]?.id ?? 0);
  const [placeName, setPlaceName] = useState("");
  const [address, setAddress] = useState("");

  const alreadyRegistered = existingAuthorIds.includes(authorId);
  const canSubmit = placeName.trim() && address.trim() && !alreadyRegistered;

  const author = members.find(m => m.id === authorId) ?? members[0];

  const handleAdd = () => {
    if (!canSubmit) return;
    onAdd({
      id: uid(),
      blockId,
      authorId: author.id,
      authorName: author.name,
      placeName: placeName.trim(),
      address: address.trim(),
      theme: theme as PlanCandidate["theme"],
      votedUserIds: [],
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative w-full max-w-md bg-white rounded-t-3xl max-h-[85vh] overflow-y-auto">
        <div className="flex items-center justify-between px-4 pt-5 pb-3 border-b border-gray-100">
          <h2 className="text-lg font-bold">후보 올리기</h2>
          <button onClick={onClose} className="text-blue-500 font-medium">닫기</button>
        </div>
        <div className="p-4 flex flex-col gap-4">
          <div>
            <label className="text-sm font-semibold mb-1.5 block">등록자</label>
            <div className="flex flex-col gap-2">
              {members.map(m => (
                <button
                  key={m.id}
                  onClick={() => setAuthorId(m.id)}
                  className="flex items-center gap-3 p-3 rounded-xl text-left transition-all"
                  style={{ background: authorId === m.id ? "#dbeafe" : "#f9fafb" }}
                >
                  <Avatar user={m} size={28} />
                  <span className="text-sm font-medium">{m.name}</span>
                  {authorId === m.id && <span className="ml-auto text-blue-500">✓</span>}
                </button>
              ))}
            </div>
            {alreadyRegistered && (
              <p className="text-xs text-red-500 mt-1.5">한 사람은 이 활동에 후보를 하나만 올릴 수 있습니다.</p>
            )}
          </div>
          <div>
            <label className="text-sm font-semibold mb-1.5 block">장소 이름</label>
            <input
              className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none"
              placeholder="식당/카페/장소 이름을 입력해주세요"
              value={placeName}
              onChange={e => setPlaceName(e.target.value)}
            />
          </div>
          <div>
            <label className="text-sm font-semibold mb-1.5 block">주소</label>
            <input
              className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none"
              placeholder="주소지를 입력해주세요"
              value={address}
              onChange={e => setAddress(e.target.value)}
            />
          </div>
          <button
            onClick={handleAdd}
            disabled={!canSubmit}
            className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold disabled:opacity-40"
          >
            후보 등록하기
          </button>
        </div>
      </div>
    </div>
  );
}

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
        {picked && (
          <p className="text-center font-semibold text-purple-600 mb-3">뽑힌 후보: {picked.placeName}</p>
        )}
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
  const { trips, updateTrip } = useStore();

  const [showAdd, setShowAdd] = useState(false);
  const [showTie, setShowTie] = useState(false);
  const [tieCandidates, setTieCandidates] = useState<PlanCandidate[]>([]);

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;
  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];
  const block = day.blocks.find(b => b.id === blockId);
  if (!block) return null;

  const blockCandidates = day.candidates.filter(c => c.blockId === blockId);
  const selectedId = day.selectedCandidateByBlock[blockId];
  const selected = day.candidates.find(c => c.id === selectedId);

  const currentUserId = 0;

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const vote = (candidateId: string) => {
    const sameBlockIds = new Set(blockCandidates.map(c => c.id));
    const candidates = day.candidates.map(c => {
      if (!sameBlockIds.has(c.id)) return c;
      const ids = c.votedUserIds.filter(id => id !== currentUserId);
      if (c.id === candidateId) return { ...c, votedUserIds: [...ids, currentUserId] };
      return { ...c, votedUserIds: ids };
    });
    setDay({ ...day, candidates });
  };

  const randomPick = () => {
    if (blockCandidates.length === 0) return;
    const picked = blockCandidates[Math.floor(Math.random() * blockCandidates.length)];
    setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: picked.id } });
  };

  const decideByVote = () => {
    if (blockCandidates.length === 0) return;
    const maxVote = Math.max(...blockCandidates.map(c => c.votedUserIds.length));
    const winners = blockCandidates.filter(c => c.votedUserIds.length === maxVote);
    if (winners.length === 1) {
      setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: winners[0].id } });
    } else {
      setTieCandidates(winners);
      setShowTie(true);
    }
  };

  const addCandidate = (candidate: PlanCandidate) => {
    setDay({ ...day, candidates: [...day.candidates, candidate] });
  };

  const pickFromTie = (candidate: PlanCandidate) => {
    setDay({ ...day, selectedCandidateByBlock: { ...day.selectedCandidateByBlock, [blockId]: candidate.id } });
  };

  const t = THEME[block.theme];

  return (
    <div className="min-h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{block.order}번째 활동</h1>
        <div className="w-8" />
      </div>

      <div className="px-4 pb-10 flex flex-col gap-5">
        {/* Header info */}
        <div>
          <ThemeBadge theme={block.theme} />
          <p className="text-2xl font-bold mt-2">{block.order}번째 활동 후보</p>
          <p className="text-sm text-gray-500 mt-1">이 화면에서는 후보 등록과 확정만 진행합니다.</p>
        </div>

        {/* Read-only category */}
        <div className="p-4 rounded-2xl flex items-center" style={{ background: t.bg }}>
          <div className="flex-1">
            <p className="text-sm font-semibold text-gray-600 mb-1">지정된 카테고리</p>
            <p className="font-bold" style={{ color: t.text }}>{t.icon} {t.label}</p>
          </div>
          <span className="text-xs text-gray-400 font-semibold px-2.5 py-1 bg-white/60 rounded-full">읽기 전용</span>
        </div>

        {/* Selected */}
        {selected && (
          <div className="p-4 rounded-2xl" style={{ background: "#dcfce7" }}>
            <p className="text-sm font-semibold text-green-700 mb-2">확정된 후보</p>
            <div className="flex items-start gap-3">
              <span className="text-green-500 text-xl mt-0.5">✓</span>
              <div>
                <p className="font-bold">{selected.placeName}</p>
                <p className="text-xs text-gray-500 mt-0.5">{selected.address}</p>
                <p className="text-xs text-gray-400 mt-0.5">등록자 {selected.authorName}</p>
              </div>
            </div>
          </div>
        )}

        {/* Candidates */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <p className="font-semibold">등록된 후보</p>
            <button onClick={() => setShowAdd(true)} className="text-sm text-blue-500 font-semibold">후보 올리기</button>
          </div>

          {blockCandidates.length === 0 ? (
            <div className="p-4 bg-gray-50 rounded-2xl">
              <p className="text-sm text-gray-400">아직 후보가 없습니다.</p>
            </div>
          ) : (
            <div className="flex flex-col gap-3">
              {blockCandidates.map(c => {
                const isSelected = c.id === selectedId;
                const isVoted = c.votedUserIds.includes(currentUserId);
                return (
                  <div
                    key={c.id}
                    className="p-4 rounded-2xl border"
                    style={{ background: isSelected ? "#dcfce7" : "white", borderColor: isSelected ? "#4ade80" : "#e5e7eb" }}
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
                      <div className="flex items-center gap-3">
                        <span className="text-xs font-bold text-gray-600">{c.votedUserIds.length}표</span>
                        <button
                          onClick={() => vote(c.id)}
                          className="text-xs font-semibold px-3 py-1 rounded-lg"
                          style={{ background: isVoted ? "#dbeafe" : "#f3f4f6", color: isVoted ? "#2563eb" : "#374151" }}
                        >
                          {isVoted ? "투표 완료" : "투표"}
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Decision buttons */}
        <div className="flex flex-col gap-2">
          <button
            onClick={randomPick}
            disabled={blockCandidates.length === 0}
            className="w-full py-4 rounded-2xl font-semibold disabled:opacity-40"
            style={{ background: "#f3e8ff", color: "#9333ea" }}
          >
            🔀 랜덤 뽑기
          </button>
          <button
            onClick={decideByVote}
            disabled={blockCandidates.length === 0}
            className="w-full py-4 rounded-2xl font-semibold disabled:opacity-40"
            style={{ background: "#dbeafe", color: "#2563eb" }}
          >
            📊 투표로 확정
          </button>
        </div>
      </div>

      {showAdd && (
        <AddCandidateSheet
          members={trip.members}
          blockId={blockId}
          theme={block.theme}
          existingAuthorIds={blockCandidates.map(c => c.authorId)}
          onAdd={addCandidate}
          onClose={() => setShowAdd(false)}
        />
      )}

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
