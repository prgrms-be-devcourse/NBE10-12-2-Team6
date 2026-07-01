"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { useStore, Trip, TripDay, PlanCandidate, uid, MOCK_USERS } from "../../store";
import { Avatar, BigActionCard, formatDate } from "../../lib";

// ── InviteModal ───────────────────────────────────────────────────────────────

function InviteModal({ trip, onUpdate, onClose }: { trip: Trip; onUpdate: (t: Trip) => void; onClose: () => void }) {
  const [copied, setCopied] = useState(false);
  const inviteLink = `https://triplog.app/invite/${trip.inviteCode}`;

  const copyLink = () => {
    navigator.clipboard.writeText(inviteLink).catch(() => {});
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const joinMockMember = () => {
    const user = MOCK_USERS[trip.inviteJoinIndex % MOCK_USERS.length];
    const alreadyIn = trip.members.some(m => m.id === user.id);
    onUpdate({
      ...trip,
      members: alreadyIn ? trip.members : [...trip.members, user],
      inviteJoinIndex: trip.inviteJoinIndex + 1,
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative w-full max-w-md bg-white rounded-t-3xl p-6 flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <p className="text-lg font-bold">초대 링크</p>
          <button onClick={onClose} className="text-blue-500 font-medium">닫기</button>
        </div>

        <div>
          <p className="text-xs font-bold text-gray-400 mb-1">초대 코드</p>
          <p className="text-2xl font-bold tracking-widest">{trip.inviteCode}</p>
        </div>

        <div className="p-3 rounded-xl text-xs text-blue-500 truncate" style={{ background: "#eff6ff" }}>
          {inviteLink}
        </div>

        <div className="flex gap-2">
          <button
            onClick={copyLink}
            className="flex-1 py-3 rounded-xl text-sm font-semibold"
            style={{ background: "#dbeafe", color: "#2563eb" }}
          >
            {copied ? "복사 완료 ✓" : "링크 복사"}
          </button>
          <button
            onClick={joinMockMember}
            className="flex-1 py-3 rounded-xl text-sm font-semibold"
            style={{ background: "#dcfce7", color: "#16a34a" }}
          >
            예시 멤버 입장
          </button>
        </div>
      </div>
    </div>
  );
}

// ── AddCandidateSheet ─────────────────────────────────────────────────────────

interface KakaoPlace {
  place_name: string;
  address_name: string;
  road_address_name: string;
  category_group_name: string;
}

function AddCandidateSheet({
  trip, onAdd, onClose,
}: { trip: Trip; onAdd: (c: PlanCandidate) => void; onClose: () => void }) {
  const { currentUser } = useStore();
  const [authorId, setAuthorId] = useState(currentUser.id);
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<KakaoPlace[]>([]);
  const [selected, setSelected] = useState<KakaoPlace | null>(null);
  const [loading, setLoading] = useState(false);

  const author = trip.members.find(m => m.id === authorId) ?? trip.members[0];

  const search = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setSelected(null);
    try {
      const res = await fetch(`/api/places?query=${encodeURIComponent(query)}`);
      const data = await res.json();
      setResults(data.documents ?? []);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = () => {
    if (!selected || !author) return;
    onAdd({
      id: uid(),
      authorId: author.id,
      authorName: author.name,
      placeName: selected.place_name,
      address: selected.road_address_name || selected.address_name,
      category: selected.category_group_name || undefined,
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
          {/* 등록자 */}
          <div>
            <label className="text-sm font-semibold mb-1.5 block">등록자</label>
            <div className="flex flex-col gap-2">
              {trip.members.map(m => (
                <button
                  key={m.id}
                  onClick={() => setAuthorId(m.id)}
                  className="flex items-center gap-3 p-3 rounded-xl text-left"
                  style={{ background: authorId === m.id ? "#dbeafe" : "#f9fafb" }}
                >
                  <Avatar user={m} size={28} />
                  <span className="text-sm font-medium flex-1">{m.name}</span>
                  {authorId === m.id && <span className="text-blue-500">✓</span>}
                </button>
              ))}
            </div>
          </div>

          {/* 장소 검색 */}
          <div>
            <label className="text-sm font-semibold mb-1.5 block">장소 검색</label>
            <div className="flex gap-2">
              <input
                className="flex-1 p-3 bg-gray-100 rounded-xl text-sm outline-none"
                placeholder="장소 이름으로 검색"
                value={query}
                onChange={e => setQuery(e.target.value)}
                onKeyDown={e => e.key === "Enter" && search()}
              />
              <button
                onClick={search}
                className="px-4 py-3 rounded-xl text-sm font-semibold text-white"
                style={{ background: "#3b82f6" }}
              >
                검색
              </button>
            </div>
          </div>

          {/* 검색 결과 */}
          {loading && <p className="text-sm text-gray-400 text-center">검색 중...</p>}
          {loading && <p className="text-sm text-gray-400 text-center">검색 중...</p>}
          {!loading && results.length > 0 && (
            <div className="flex flex-col gap-2">
              {results.map((place, i) => {
                const isSelected = selected?.place_name === place.place_name && selected?.address_name === place.address_name;
                const isDup = trip.candidates.some(c => c.placeName === place.place_name && c.address === (place.road_address_name || place.address_name));
                return (
                  <div
                    key={i}
                    className="p-3 rounded-xl border flex items-center gap-2 transition-all"
                    style={{ background: isSelected ? "#dbeafe" : "#f9fafb", borderColor: isSelected ? "#3b82f6" : "transparent" }}
                  >
                    <button onClick={() => setSelected(place)} className="flex-1 text-left min-w-0">
                      <p className="text-sm font-semibold truncate">{place.place_name}</p>
                      <p className="text-xs text-gray-400 mt-0.5 truncate">{place.road_address_name || place.address_name}</p>
                      {place.category_group_name && <p className="text-xs text-gray-400">{place.category_group_name}</p>}
                    </button>
                    {isSelected && (
                      isDup
                        ? <span className="text-xs text-red-400 shrink-0">이미 등록됨</span>
                        : <button
                            onClick={handleAdd}
                            className="shrink-0 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"
                            style={{ background: "#3b82f6" }}
                          >
                            등록
                          </button>
                    )}
                  </div>
                );
              })}
            </div>
          )}
          {!loading && query && results.length === 0 && (
            <p className="text-sm text-gray-400 text-center">검색 결과가 없습니다.</p>
          )}
        </div>
      </div>
    </div>
  );
}

// ── TripCandidatePoolCard ─────────────────────────────────────────────────────

function TripCandidatePoolCard({ trip, onUpdate }: { trip: Trip; onUpdate: (t: Trip) => void }) {
  const [showAdd, setShowAdd] = useState(false);

  return (
    <div className="p-4 flex flex-col gap-3 rounded-2xl" style={{ background: "#f0fdf4" }}>
      <div className="flex items-start justify-between">
        <div>
          <p className="font-semibold">후보 장소</p>
          <p className="text-xs text-gray-500 mt-0.5">일차와 상관없이 여행 전체에서 사용할 후보를 올립니다.</p>
        </div>
        <span className="text-xs font-bold text-gray-500 shrink-0 ml-2">{trip.candidates.length}개</span>
      </div>

      {trip.candidates.length === 0 ? (
        <div className="p-3 rounded-xl flex items-start gap-2" style={{ background: "#dcfce7" }}>
          <span className="text-base shrink-0">📍</span>
          <div>
            <p className="text-sm font-semibold">아직 후보 장소가 없습니다.</p>
            <p className="text-xs text-gray-500 mt-0.5">후보를 올린 뒤 각 일차의 시간 구간에서 투표나 랜덤으로 선택합니다.</p>
          </div>
        </div>
      ) : (
        <div className="flex flex-col gap-2">
          {trip.candidates.slice(0, 5).map(c => (
            <div key={c.id} className="flex items-center gap-3 p-3 bg-white rounded-xl">
              <span className="text-base shrink-0" style={{ color: "#16a34a" }}>📍</span>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold truncate">{c.placeName}</p>
                <p className="text-xs text-gray-400 truncate">{c.address}</p>
                <p className="text-xs text-gray-400">등록자 {c.authorName}</p>
              </div>
              <span className="text-xs font-bold px-2 py-1 rounded-full shrink-0" style={{ background: "#dcfce7", color: "#16a34a" }}>전체 후보</span>
            </div>
          ))}
          {trip.candidates.length > 5 && (
            <p className="text-xs text-gray-400 text-center">외 {trip.candidates.length - 5}개 후보</p>
          )}
        </div>
      )}

      <button
        onClick={() => setShowAdd(true)}
        className="w-full py-3 rounded-xl font-semibold text-sm"
        style={{ background: "#dcfce7", color: "#16a34a" }}
      >
        + 후보 올리기
      </button>

      {showAdd && (
        <AddCandidateSheet
          trip={trip}
          onAdd={c => onUpdate({ ...trip, candidates: [...trip.candidates, c] })}
          onClose={() => setShowAdd(false)}
        />
      )}
    </div>
  );
}

// ── Status Badge ──────────────────────────────────────────────────────────────

function statusBadge(day: TripDay) {
  if (day.isPlanSkipped) return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-gray-100 text-gray-500">계획 건너뜀</span>;
  if (day.isPlanCompleted) return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-blue-100 text-blue-600">계획 완료</span>;
  return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-orange-100 text-orange-600">작성 필요</span>;
}

// ── Main ──────────────────────────────────────────────────────────────────────

type Tab = "trip" | "candidates" | "vote" | "timeline";

export default function TripDetailPage() {
  const router = useRouter();
  const { id } = useParams<{ id: string }>();
  const { trips, updateTrip } = useStore();
  const [showInvite, setShowInvite] = useState(false);
  const [tab, setTab] = useState<Tab>("trip");
  const [candidateBlink, setCandidateBlink] = useState(false);
  const trip = trips.find(t => t.id === id);

  const allDaysComplete = trip ? trip.days.length > 0 && trip.days.every(d => d.isPlanCompleted || d.isPlanSkipped) : false;

  useEffect(() => {
    if (!allDaysComplete || tab === "candidates") return;
    const interval = setInterval(() => {
      setCandidateBlink(true);
      setTimeout(() => setCandidateBlink(false), 800);
    }, 5000);
    return () => clearInterval(interval);
  }, [allDaysComplete, tab]);

  if (!trip) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4">
        <p className="text-gray-500">여행을 찾을 수 없습니다.</p>
        <button onClick={() => router.push("/home")} className="text-blue-500 font-medium">홈으로</button>
      </div>
    );
  }

  return (
    <div className="min-h-screen pb-24">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.push("/home")} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div className="flex-1 text-center">
          <p className="font-semibold text-base">{trip.title}</p>
          <p className="text-xs text-gray-400">{trip.region} · {trip.nights}박 {trip.nights + 1}일</p>
        </div>
        <button onClick={() => setShowInvite(true)} className="w-8 h-8 rounded-full flex items-center justify-center" style={{ background: "#dbeafe" }}>
          <svg className="w-4 h-4" fill="none" stroke="#2563eb" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
          </svg>
        </button>
      </div>

      {/* Tab content */}
      <div className="px-4 pt-2 flex flex-col gap-5">
        {tab === "trip" && (
          <>
            {/* Members */}
            <div className="p-4 bg-gray-50 rounded-2xl">
              <div className="flex items-center justify-between mb-3">
                <p className="font-semibold">여행 멤버</p>
                <span className="text-xs text-gray-400 font-bold">{trip.members.length}명</span>
              </div>
              <div className="flex gap-4 overflow-x-auto pb-1">
                {trip.members.map(m => (
                  <div key={m.id} className="flex flex-col items-center gap-1.5 shrink-0">
                    <Avatar user={m} size={42} />
                    <span className="text-xs text-gray-600">{m.name}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Day list */}
            <div className="flex flex-col gap-3">
              <p className="font-semibold">일차별 계획</p>
              {trip.days.map(day => (
                <Link key={day.id} href={`/trip/${trip.id}/day/${day.dayNumber}`}>
                  <div className="p-4 bg-gray-50 rounded-2xl">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <p className="font-semibold">{day.dayNumber}일차</p>
                        <p className="text-xs text-gray-400 mt-0.5">{formatDate(day.date)}</p>
                      </div>
                      {statusBadge(day)}
                    </div>
                    {!day.isPlanSkipped && day.blocks.length > 0 && (
                      <div className="flex items-center gap-3 mt-1 text-xs font-bold">
                        <span className="text-blue-500">{day.blocks.length}개 시간 구간</span>
                      </div>
                    )}
                  </div>
                </Link>
              ))}
            </div>
          </>
        )}

        {tab === "candidates" && (
          <TripCandidatePoolCard trip={trip} onUpdate={updateTrip} />
        )}

        {tab === "vote" && (
          <div className="flex flex-col gap-4">
            <p className="font-semibold">일차별 투표</p>
            {trip.candidates.length === 0 ? (
              <div className="p-4 bg-gray-50 rounded-2xl">
                <p className="text-sm text-gray-400">후보 장소를 먼저 등록해야 투표할 수 있습니다.</p>
              </div>
            ) : (
              trip.days.map(day => {
                const completedBlocks = day.blocks.filter(b => !day.isPlanSkipped);
                if (day.isPlanSkipped || completedBlocks.length === 0) return (
                  <div key={day.id} className="p-4 bg-gray-50 rounded-2xl flex items-center justify-between">
                    <div>
                      <p className="font-semibold text-sm">{day.dayNumber}일차</p>
                      <p className="text-xs text-gray-400 mt-0.5">{formatDate(day.date)}</p>
                    </div>
                    <span className="text-xs text-gray-400">{day.isPlanSkipped ? "계획 건너뜀" : "시간 구간 없음"}</span>
                  </div>
                );
                return (
                  <div key={day.id} className="flex flex-col gap-2">
                    <p className="text-sm font-semibold text-gray-500">{day.dayNumber}일차 · {formatDate(day.date)}</p>
                    {[...day.blocks].sort((a, b) => a.startMinute - b.startMinute).map(block => {
                      const selectedId = day.selectedCandidateByBlock[block.id];
                      const selected = trip.candidates.find(c => c.id === selectedId);
                      return (
                        <Link key={block.id} href={`/trip/${trip.id}/day/${day.dayNumber}/block/${block.id}`}>
                          <div className="p-4 bg-gray-50 rounded-2xl flex items-center justify-between">
                            <div className="flex-1 min-w-0">
                              <p className="text-xs text-gray-400">{block.order}번째 구간</p>
                              <p className="text-sm font-semibold mt-0.5 truncate">
                                {selected ? selected.placeName : "미확정"}
                              </p>
                            </div>
                            <div className="flex items-center gap-2 shrink-0">
                              {selected
                                ? <span className="text-xs font-bold px-2 py-1 rounded-full" style={{ background: "#dcfce7", color: "#16a34a" }}>확정됨</span>
                                : <span className="text-xs font-bold px-2 py-1 rounded-full" style={{ background: "#dbeafe", color: "#2563eb" }}>투표하기</span>
                              }
                              <svg className="w-4 h-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                              </svg>
                            </div>
                          </div>
                        </Link>
                      );
                    })}
                  </div>
                );
              })
            )}
          </div>
        )}

        {tab === "timeline" && (
          <div className="flex flex-col gap-3">
            <p className="font-semibold">사진 기록</p>
            {trip.days.map(day => (
              day.isPlanCompleted ? (
                <Link key={day.id} href={`/trip/${trip.id}/day/${day.dayNumber}/photos`}>
                  <BigActionCard
                    icon="📷"
                    title={`${day.dayNumber}일차 사진 올리기`}
                    subtitle={formatDate(day.date)}
                    colorKey="green"
                  />
                </Link>
              ) : (
                <div key={day.id} className="p-4 bg-gray-50 rounded-2xl flex items-center justify-between">
                  <div>
                    <p className="font-semibold text-sm">{day.dayNumber}일차</p>
                    <p className="text-xs text-gray-400 mt-0.5">{formatDate(day.date)}</p>
                  </div>
                  <span className="text-xs text-gray-400">계획 완료 후 가능</span>
                </div>
              )
            ))}
            <Link href={`/trip/${trip.id}/timeline`}>
              <BigActionCard
                icon="📸"
                title="전체 타임라인 보기"
                subtitle="올린 사진과 기록을 일차별로 확인합니다."
                colorKey="purple"
              />
            </Link>
          </div>
        )}
      </div>

      {/* Bottom tab bar */}
      <div className="fixed bottom-0 left-0 right-0 flex justify-center">
        <div className="w-full max-w-md bg-white border-t border-gray-100" style={{ paddingBottom: "env(safe-area-inset-bottom)" }}>
        <div className="flex">
          {([
            { key: "trip", label: "여행 모임", icon: (
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            )},
            { key: "candidates", label: "후보 장소", icon: (
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            )},
            { key: "vote", label: "투표", icon: (
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
              </svg>
            )},
            { key: "timeline", label: "타임라인", icon: (
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            )},
          ] as const).map(({ key, label, icon }) => {
            const active = tab === key;
            const isBlinking = key === "candidates" && candidateBlink;
            return (
              <button
                key={key}
                onClick={() => setTab(key)}
                className="flex-1 flex flex-col items-center gap-0.5 py-2 transition-all"
                style={{ color: active ? "#3b82f6" : isBlinking ? "#f97316" : "#9ca3af" }}
              >
                {icon}
                <span className="text-[10px] font-medium">{label}</span>
                {active
                  ? <span className="w-1 h-1 rounded-full bg-blue-500 mt-0.5" />
                  : isBlinking
                  ? <span className="w-1 h-1 rounded-full bg-orange-400 mt-0.5" />
                  : null
                }
              </button>
            );
          })}
        </div>
        </div>
      </div>

      {showInvite && (
        <InviteModal trip={trip} onUpdate={updateTrip} onClose={() => setShowInvite(false)} />
      )}
    </div>
  );
}
