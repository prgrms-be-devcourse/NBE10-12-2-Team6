"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useStore } from "../store";
import { formatDate, apiFetch, useAuthGuard, API_BASE } from "../lib";
import { useTripOwnerStore } from "../stores/tripOwnerStore";

type ApiTrip = {
  id: number;
  name: string;
  region: string;
  nights: number;
  startDate: string;
  joinUrl?: string;
  joinCode?: string;
};

function getTripStatus(startDate: string, nights: number) {
  const today = new Date(); today.setHours(0, 0, 0, 0);
  const start = new Date(startDate); start.setHours(0, 0, 0, 0);
  const end = new Date(startDate); end.setDate(end.getDate() + nights); end.setHours(0, 0, 0, 0);
  if (today < start) return "before";
  if (today <= end) return "during";
  return "after";
}

const STATUS_BADGE: Record<string, { label: string; className: string }> = {
  before: { label: "여행 전", className: "bg-blue-100 text-blue-600" },
  during: { label: "여행 중", className: "bg-green-100 text-green-600" },
  after:  { label: "여행 완료", className: "bg-gray-100 text-gray-500" },
};

const WEEKDAYS = ["일", "월", "화", "수", "목", "금", "토"];
const MONTHS = Array.from({ length: 12 }, (_, i) => i);

function toDateValue(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function fromDateValue(value: string) {
  return value ? new Date(value + "T00:00:00") : new Date();
}

function dateButtonText(value: string) {
  if (!value) return "연도. 월. 일.";
  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "short",
  }).format(fromDateValue(value));
}

function MobileDatePicker({
  title,
  value,
  onSelect,
  onClose,
}: {
  title: string;
  value: string;
  onSelect: (value: string) => void;
  onClose: () => void;
}) {
  const selectedDate = value ? fromDateValue(value) : null;
  const [viewDate, setViewDate] = useState(() => {
    const base = selectedDate ?? new Date();
    return new Date(base.getFullYear(), base.getMonth(), 1);
  });
  const [draftValue, setDraftValue] = useState(selectedDate ? toDateValue(selectedDate) : "");
  const year = viewDate.getFullYear();
  const month = viewDate.getMonth();
  const currentYear = new Date().getFullYear();
  const yearOptions = Array.from(
    new Set([
      ...Array.from({ length: 16 }, (_, i) => currentYear - 5 + i),
      year,
    ])
  ).sort((a, b) => a - b);
  const todayValue = toDateValue(new Date());
  const firstWeekday = new Date(year, month, 1).getDay();
  const lastDate = new Date(year, month + 1, 0).getDate();
  const cells = [
    ...Array.from({ length: firstWeekday }, () => null),
    ...Array.from({ length: lastDate }, (_, i) => new Date(year, month, i + 1)),
  ];

  const moveMonth = (offset: number) => {
    setViewDate(current => new Date(current.getFullYear(), current.getMonth() + offset, 1));
  };

  return (
    <div className="fixed inset-0 z-[60] flex items-end justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative w-full max-w-md bg-white rounded-t-3xl px-5 pt-4 pb-6 sheet-slide-up">
        <div className="flex items-center justify-between mb-4">
          <div>
            <p className="text-lg font-bold">{title}</p>
            <p className="text-xs text-gray-500 mt-0.5">날짜를 선택하세요.</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-gray-100 text-gray-500 flex items-center justify-center"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>

        <div className="grid grid-cols-2 gap-3 mb-3">
          <label className="flex flex-col gap-1.5">
            <span className="text-xs font-bold text-gray-500">연도</span>
            <div className="relative">
              <select
                value={year}
                onChange={e => setViewDate(current => new Date(Number(e.target.value), current.getMonth(), 1))}
                className="w-full appearance-none py-3 pl-4 pr-12 rounded-2xl bg-gray-100 text-sm font-bold outline-none"
              >
                {yearOptions.map(option => (
                  <option key={option} value={option}>{option}년</option>
                ))}
              </select>
              <svg className="pointer-events-none absolute right-4 top-1/2 w-4 h-4 -translate-y-1/2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M6 9l6 6 6-6" />
              </svg>
            </div>
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-xs font-bold text-gray-500">월</span>
            <div className="relative">
              <select
                value={month}
                onChange={e => setViewDate(current => new Date(current.getFullYear(), Number(e.target.value), 1))}
                className="w-full appearance-none py-3 pl-4 pr-12 rounded-2xl bg-gray-100 text-sm font-bold outline-none"
              >
                {MONTHS.map(option => (
                  <option key={option} value={option}>{option + 1}월</option>
                ))}
              </select>
              <svg className="pointer-events-none absolute right-4 top-1/2 w-4 h-4 -translate-y-1/2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M6 9l6 6 6-6" />
              </svg>
            </div>
          </label>
        </div>

        <div className="flex items-center justify-between bg-gray-50 rounded-2xl px-3 py-2 mb-4">
          <button
            type="button"
            onClick={() => moveMonth(-1)}
            className="w-10 h-10 rounded-full bg-white border border-gray-100 text-blue-500 flex items-center justify-center"
            aria-label="이전 달"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M15 18l-6-6 6-6" />
            </svg>
          </button>
          <p className="font-bold">{year}년 {month + 1}월</p>
          <button
            type="button"
            onClick={() => moveMonth(1)}
            className="w-10 h-10 rounded-full bg-white border border-gray-100 text-blue-500 flex items-center justify-center"
            aria-label="다음 달"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M9 6l6 6-6 6" />
            </svg>
          </button>
        </div>

        <div className="grid grid-cols-7 gap-1 mb-2">
          {WEEKDAYS.map(day => (
            <div key={day} className="h-8 flex items-center justify-center text-xs font-bold text-gray-400">
              {day}
            </div>
          ))}
        </div>

        <div className="grid grid-cols-7 gap-1">
          {cells.map((date, i) => {
            if (!date) return <div key={`empty-${i}`} className="aspect-square" />;
            const dateValue = toDateValue(date);
            const isSelected = dateValue === draftValue;
            const isToday = dateValue === todayValue;
            return (
              <button
                key={dateValue}
                type="button"
                onClick={() => setDraftValue(dateValue)}
                className={`aspect-square rounded-2xl flex flex-col items-center justify-center text-sm font-bold transition ${
                  isSelected
                    ? "bg-blue-500 text-white"
                    : isToday
                      ? "bg-blue-50 text-blue-600"
                      : "text-gray-700 hover:bg-gray-100"
                }`}
              >
                <span>{date.getDate()}</span>
                {isToday && !isSelected && <span className="text-[10px] mt-0.5">오늘</span>}
              </button>
            );
          })}
        </div>

        <button
          type="button"
          onClick={() => {
            if (!draftValue) return;
            onSelect(draftValue);
            onClose();
          }}
          disabled={!draftValue}
          className={`w-full mt-5 py-4 rounded-2xl font-bold transition ${
            draftValue ? "bg-blue-500 text-white" : "bg-gray-100 text-gray-400"
          }`}
        >
          날짜 선택
        </button>
      </div>
    </div>
  );
}

function DateField({
  value,
  onChange,
  label,
  className = "",
}: {
  value: string;
  onChange: (value: string) => void;
  label: string;
  className?: string;
}) {
  const [open, setOpen] = useState(false);

  return (
    <>
      <button
        type="button"
        onClick={() => setOpen(true)}
        className={`p-3 bg-gray-100 rounded-xl text-sm outline-none flex items-center justify-between gap-3 text-left ${className}`}
        aria-label={label}
      >
        <span className={value ? "font-semibold text-gray-800" : "font-semibold text-gray-500"}>
          {dateButtonText(value)}
        </span>
        <svg className="w-5 h-5 text-gray-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3M4 11h16M5 5h14a1 1 0 011 1v14a1 1 0 01-1 1H5a1 1 0 01-1-1V6a1 1 0 011-1z" />
        </svg>
      </button>
      {open && (
        <MobileDatePicker
          title={label}
          value={value}
          onSelect={onChange}
          onClose={() => setOpen(false)}
        />
      )}
    </>
  );
}

function TripCard({ trip }: { trip: ApiTrip }) {
  const status = getTripStatus(trip.startDate, trip.nights);
  const badge = STATUS_BADGE[status];
  return (
    <Link href={`/trip/${trip.id}`} className="block">
      <div className="p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
        <div className="flex items-start justify-between mb-3">
          <div>
            <p className="font-bold text-base">{trip.name}</p>
            <p className="text-sm text-gray-500 mt-0.5">{trip.region} · {trip.nights}박 {trip.nights + 1}일</p>
          </div>
          <span className={`text-xs font-bold px-2.5 py-1 rounded-full shrink-0 ml-2 ${badge.className}`}>
            {badge.label}
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
  const clearOwnerId = useTripOwnerStore((state) => state.clearOwnerId);

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
    clearOwnerId();
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
            <DateField
              className="flex-1"
              value={searchDate}
              onChange={setSearchDate}
              label="여행 시작일 검색"
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
            {[...trips].sort((a, b) => {
              const order = { during: 0, before: 1, after: 2 };
              return order[getTripStatus(a.startDate, a.nights)] - order[getTripStatus(b.startDate, b.nights)];
            }).map(trip => <TripCard key={trip.id} trip={trip} />)}
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
                <DateField
                  className="w-full"
                  value={tripDate}
                  onChange={setTripDate}
                  label="여행 시작일 선택"
                />
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
