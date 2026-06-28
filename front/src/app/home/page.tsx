"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useStore, Trip } from "../store";
import { Avatar, formatDate } from "../lib";

function TripCard({ trip }: { trip: Trip }) {
  return (
    <Link href={`/trip/${trip.id}`} className="block">
      <div className="p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
        <div className="flex items-start justify-between mb-3">
          <div>
            <p className="font-bold text-base">{trip.title}</p>
            <p className="text-sm text-gray-500 mt-0.5">{trip.region} · {trip.nights}박 {trip.nights + 1}일</p>
          </div>
          {!trip.isTimeSettingSaved ? (
            <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-orange-100 text-orange-600 shrink-0 ml-2">시간 설정 필요</span>
          ) : (
            <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-blue-100 text-blue-600 shrink-0 ml-2">계획 작성 가능</span>
          )}
        </div>
        <div className="flex items-center gap-3 text-xs text-gray-400">
          <span>👥 {trip.members.length}명</span>
          <span>✅ {trip.days.filter(d => d.isPlanCompleted).length}일 완료</span>
          {trip.days.filter(d => d.isPlanSkipped).length > 0 && (
            <span>⏭ {trip.days.filter(d => d.isPlanSkipped).length}일 건너뜀</span>
          )}
        </div>
        <p className="text-xs text-gray-400 mt-2">{formatDate(trip.startDate)} 시작</p>
      </div>
    </Link>
  );
}

export default function HomePage() {
  const router = useRouter();
  const { currentUser, friends, friendRequests, trips, acceptFriendRequest, rejectFriendRequest, addFriend, createTrip } = useStore();

  const [showFriends, setShowFriends] = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [newFriendName, setNewFriendName] = useState("");

  const [tripTitle, setTripTitle] = useState("");
  const [tripRegion, setTripRegion] = useState("");
  const [tripDate, setTripDate] = useState("");
  const [tripNights, setTripNights] = useState(2);
  const [selectedFriendIds, setSelectedFriendIds] = useState<Set<number>>(new Set());

  const toggleFriend = (id: number) => {
    setSelectedFriendIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  const handleCreateTrip = () => {
    if (!tripTitle.trim() || !tripRegion.trim() || !tripDate) return;
    const id = createTrip({ title: tripTitle, region: tripRegion, startDate: tripDate, nights: tripNights, memberIds: Array.from(selectedFriendIds) });
    setShowCreate(false);
    setTripTitle(""); setTripRegion(""); setTripDate(""); setTripNights(2); setSelectedFriendIds(new Set());
    router.push(`/trip/${id}`);
  };

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="flex items-center justify-between px-4 pt-14 pb-2">
        <button onClick={() => setShowFriends(true)} className="relative w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100">
          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
          </svg>
          {friendRequests.length > 0 && <span className="absolute top-1.5 right-1.5 w-2.5 h-2.5 bg-red-500 rounded-full" />}
        </button>
        <button onClick={() => setShowCreate(true)} className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
        </button>
      </div>

      <div className="px-4 pb-10">
        <div className="mb-5">
          <p className="text-2xl font-bold">안녕하세요, {currentUser.name}님</p>
          <p className="text-sm text-gray-500 mt-1">친구를 선택해서 여행 모임을 만들고, 일차별 계획을 세워보세요.</p>
        </div>

        {trips.length === 0 ? (
          <div className="flex flex-col items-center gap-4 p-7 bg-gray-50 rounded-2xl text-center">
            <span className="text-5xl">🧳</span>
            <p className="font-semibold">아직 여행 모임이 없어요</p>
            <p className="text-sm text-gray-500">친구들과 함께할 여행 모임을 만들어보세요.</p>
            <button onClick={() => setShowCreate(true)} className="px-6 py-3 rounded-2xl bg-blue-500 text-white font-semibold text-sm">
              여행 모임 만들기
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {trips.map(trip => <TripCard key={trip.id} trip={trip} />)}
          </div>
        )}
      </div>

      {/* Friends Sheet */}
      {showFriends && (
        <div className="fixed inset-0 z-50 flex items-end justify-center">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowFriends(false)} />
          <div className="relative w-full max-w-md bg-white rounded-t-3xl max-h-[85vh] overflow-y-auto">
            <div className="flex items-center justify-between px-4 pt-5 pb-3 border-b border-gray-100">
              <h2 className="text-lg font-bold">친구</h2>
              <button onClick={() => setShowFriends(false)} className="text-blue-500 font-medium">닫기</button>
            </div>
            <div className="p-4 flex flex-col gap-6">
              <section>
                <p className="text-xs font-bold text-gray-400 uppercase mb-3">받은 친구 요청</p>
                {friendRequests.length === 0 ? (
                  <p className="text-sm text-gray-400">받은 친구 요청이 없습니다.</p>
                ) : (
                  <div className="flex flex-col gap-2">
                    {friendRequests.map(req => (
                      <div key={req.id} className="flex items-center gap-3">
                        <Avatar user={req.user} size={36} />
                        <span className="flex-1 text-sm font-medium">{req.user.name}</span>
                        <button onClick={() => acceptFriendRequest(req.id)} className="px-3 py-1.5 rounded-lg bg-blue-500 text-white text-xs font-semibold">수락</button>
                        <button onClick={() => rejectFriendRequest(req.id)} className="px-3 py-1.5 rounded-lg border border-gray-200 text-xs font-semibold text-red-500">거절</button>
                      </div>
                    ))}
                  </div>
                )}
              </section>
              <section>
                <p className="text-xs font-bold text-gray-400 uppercase mb-3">친구 추가</p>
                <div className="flex gap-2">
                  <input
                    className="flex-1 p-3 bg-gray-100 rounded-xl text-sm outline-none"
                    placeholder="친구 이름을 입력해주세요"
                    value={newFriendName}
                    onChange={e => setNewFriendName(e.target.value)}
                  />
                  <button
                    onClick={() => { if (newFriendName.trim()) { addFriend(newFriendName.trim()); setNewFriendName(""); } }}
                    className="px-4 rounded-xl bg-blue-500 text-white text-sm font-semibold"
                  >추가</button>
                </div>
              </section>
              <section>
                <p className="text-xs font-bold text-gray-400 uppercase mb-3">내 친구</p>
                <div className="flex flex-col gap-2">
                  {friends.map(f => (
                    <div key={f.id} className="flex items-center gap-3 py-1">
                      <Avatar user={f} size={34} />
                      <span className="flex-1 text-sm font-medium">{f.name}</span>
                      <span className="text-xs text-gray-400">친구</span>
                    </div>
                  ))}
                </div>
              </section>
            </div>
          </div>
        </div>
      )}

      {/* Create Trip Sheet */}
      {showCreate && (
        <div className="fixed inset-0 z-50 flex items-end justify-center">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowCreate(false)} />
          <div className="relative w-full max-w-md bg-white rounded-t-3xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between px-4 pt-5 pb-3 border-b border-gray-100">
              <h2 className="text-lg font-bold">여행 모임 만들기</h2>
              <button onClick={() => setShowCreate(false)} className="text-blue-500 font-medium">닫기</button>
            </div>
            <div className="p-4 flex flex-col gap-5">
              <div>
                <label className="text-sm font-semibold mb-1.5 block">여행 이름</label>
                <input className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none" placeholder="여행 이름을 입력해주세요" value={tripTitle} onChange={e => setTripTitle(e.target.value)} />
              </div>
              <div>
                <label className="text-sm font-semibold mb-1.5 block">지역</label>
                <input className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none" placeholder="지역을 입력해주세요" value={tripRegion} onChange={e => setTripRegion(e.target.value)} />
              </div>
              <div>
                <label className="text-sm font-semibold mb-1.5 block">시작일</label>
                <input type="date" className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none" value={tripDate} onChange={e => setTripDate(e.target.value)} />
              </div>
              <div>
                <label className="text-sm font-semibold mb-1.5 block">기간</label>
                <div className="flex items-center gap-4">
                  <button onClick={() => setTripNights(n => Math.max(0, n - 1))} className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-lg font-bold">−</button>
                  <span className="flex-1 text-center font-semibold">{tripNights}박 {tripNights + 1}일</span>
                  <button onClick={() => setTripNights(n => Math.min(10, n + 1))} className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-lg font-bold">+</button>
                </div>
              </div>
              <div>
                <label className="text-sm font-semibold mb-2 block">함께할 친구 선택</label>
                <div className="flex flex-col gap-2">
                  {friends.map(f => (
                    <button key={f.id} onClick={() => toggleFriend(f.id)} className="flex items-center gap-3 p-3 rounded-xl bg-gray-50 text-left">
                      <Avatar user={f} size={32} />
                      <span className="flex-1 text-sm font-medium">{f.name}</span>
                      {selectedFriendIds.has(f.id) && <span className="text-blue-500 text-lg">✓</span>}
                    </button>
                  ))}
                </div>
              </div>
              <button
                onClick={handleCreateTrip}
                disabled={!tripTitle.trim() || !tripRegion.trim() || !tripDate}
                className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold disabled:opacity-40"
              >
                여행 모임 만들기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
