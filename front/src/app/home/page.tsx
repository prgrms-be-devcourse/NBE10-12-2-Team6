"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useStore } from "../store";
import { formatDate, apiFetch, useAuthGuard, API_BASE } from "../lib";

type ApiTrip = {
  id: number;
  name: string;
  region: string;
  nights: number;
  startDate: string;
  joinUrl?: string;
  joinCode?: string;
};

function TripCard({ trip }: { trip: ApiTrip }) {
  return (
    <Link href={`/trip/${trip.id}`} className="block">
      <div className="p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
        <div className="flex items-start justify-between mb-3">
          <div>
            <p className="font-bold text-base">{trip.name}</p>
            <p className="text-sm text-gray-500 mt-0.5">{trip.region} · {trip.nights}박 {trip.nights + 1}일</p>
          </div>
          <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-blue-100 text-blue-600 shrink-0 ml-2 text-center leading-tight">
            전체 후보 등록 후<br />일차별 선택
          </span>
        </div>
        <p className="text-xs text-gray-400">{formatDate(trip.startDate)} 시작</p>
      </div>
    </Link>
  );
}

export default function HomePage() {
  useAuthGuard();
  const router = useRouter();
  const { currentUser, loadTrips } = useStore();

  const [trips, setTrips] = useState<ApiTrip[]>([]);
  const [loading, setLoading] = useState(true);

  const tripTitleRef = useRef<HTMLInputElement>(null);
  const [showCreate, setShowCreate] = useState(false);
  useEffect(() => { if (showCreate) setTimeout(() => tripTitleRef.current?.focus(), 50); }, [showCreate]);
  const [tripTitle, setTripTitle] = useState("");
  const [tripRegion, setTripRegion] = useState("");
  const [tripDate, setTripDate] = useState("");
  const [tripNights, setTripNights] = useState(2);
  const [keyWord, setKeyWord] = useState("");
  const [searchDate, setSearchDate] = useState("");

  const getInit = async () => {
    const p = new URLSearchParams();
    if (keyWord.trim()) p.set("keyword", keyWord.trim());
    if (searchDate) p.set("startDate", searchDate);
    const query = p.toString() ? `?${p.toString()}` : "";
    apiFetch(`${API_BASE}/api/v1/trips${query}`)
      .then(res => res.json())
      .then(body => { if (body.data) { setTrips(body.data); loadTrips(body.data); } })
      .catch(() => {})
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    localStorage.removeItem("pendingInviteCode");
    getInit()
  }, []);

  const handleCreateTrip = async () => {
    if (!tripTitle.trim() || !tripRegion.trim() || !tripDate) return;
    try {
      const res = await apiFetch(`${API_BASE}/api/v1/trips`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: tripTitle.trim(),
          region: tripRegion.trim(),
          startDate: tripDate,
          nights: String(tripNights),
        }),
      });
      const body = await res.json();
      if (!res.ok) throw new Error(body?.message ?? "생성 실패");
      const newTrip: ApiTrip = body.data;
      const updated = [...trips, newTrip];
      setTrips(updated);
      loadTrips(updated.map(t => ({ ...t, joinUrl: t.joinUrl ?? t.joinCode })));
      setShowCreate(false);
      setTripTitle(""); setTripRegion(""); setTripDate(""); setTripNights(2);
      router.push(`/trip/${newTrip.id}`);
    } catch (e) {
      console.error("[여행 만들기 실패]", e);
    }
  };

  return (
    <div className="min-h-screen">
      <div className="flex items-center justify-between px-4 pt-14 pb-2">
        <p className="text-xl font-bold">내 여행</p>
        <button onClick={() => setShowCreate(true)} className="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
        </button>
      </div>

      <div className="px-4 pb-10">
        <div className="mb-5">
          <p className="text-2xl font-bold">안녕하세요, {currentUser.name}님</p>
          <p className="text-sm text-gray-500 mt-1">여행 모임을 만들고 초대 링크로 멤버를 초대해보세요.</p>
        </div>

        <div className="flex flex-col gap-2 mb-4">
          <input
            className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none"
            placeholder="여행 이름, 지역, 멤버명으로 검색"
            value={keyWord}
            onChange={e => setKeyWord(e.target.value)}
            onKeyDown={e => { if (e.key === "Enter" && !e.nativeEvent.isComposing) { getInit(); (e.target as HTMLInputElement).blur(); } }}
          />
          <div className="flex gap-2">
            <input
              type="date"
              className="flex-1 p-3 bg-gray-100 rounded-xl text-sm outline-none"
              value={searchDate}
              onChange={e => setSearchDate(e.target.value)}
            />
            {searchDate && (
              <button
                onClick={() => setSearchDate("")}
                className="px-3 bg-gray-100 rounded-xl text-gray-400 hover:text-gray-600 text-sm"
              >
                ✕
              </button>
            )}
            <button
              onClick={getInit}
              className="px-4 py-2 bg-blue-500 text-white text-sm font-semibold rounded-xl"
            >
              검색
            </button>
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-10">
            <p className="text-sm text-gray-400">불러오는 중...</p>
          </div>
        ) : trips.length === 0 ? (
          <div className="flex flex-col items-center gap-4 p-7 bg-gray-50 rounded-2xl text-center">
            <span className="text-5xl">🔗</span>
            <p className="font-semibold">아직 여행 모임이 없어요</p>
            <p className="text-sm text-gray-500">여행 모임을 만들면 초대 링크가 생성됩니다.</p>
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
                <input ref={tripTitleRef} className="w-full p-3 bg-gray-100 rounded-xl text-sm outline-none" placeholder="여행 이름을 입력해주세요" value={tripTitle} onChange={e => setTripTitle(e.target.value)} />
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
