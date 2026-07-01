"use client";

import Link from "next/link";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import { useStore, TripDay, PhotoRecord, uid } from "../../../../../store";
import { THEME, timeText } from "../../../../../lib";

type ActiveSlot =
  | { type: "block"; blockId: string; label: string; address?: string; themeKey: string; startMinute: number; endMinute: number }
  | { type: "free"; slotKey: string; startMinute: number; endMinute: number };

function getCurrentSlot(
  blocks: { id: string; startMinute: number; endMinute: number; order: number; theme: string }[],
  candidates: Record<string, string>,
  allCandidates: { id: string; placeName: string; address?: string }[],
  currentMinutes: number
): ActiveSlot {
  const sorted = [...blocks]
    .filter(b => candidates[b.id] != null)
    .sort((a, b) => a.startMinute - b.startMinute);

  // In a block?
  const active = sorted.find(b => currentMinutes >= b.startMinute && currentMinutes <= b.endMinute);
  if (active) {
    const c = allCandidates.find(x => x.id === candidates[active.id]);
    return {
      type: "block",
      blockId: active.id,
      label: c?.placeName ?? `${active.order}번째 활동`,
      address: c?.address,
      themeKey: active.theme,
      startMinute: active.startMinute,
      endMinute: active.endMinute,
    };
  }

  // Find the gap the current time falls in
  let gapStart = 0;
  let gapEnd = 24 * 60;

  for (let i = 0; i < sorted.length; i++) {
    if (currentMinutes < sorted[i].startMinute) {
      gapEnd = sorted[i].startMinute;
      break;
    }
    if (currentMinutes > sorted[i].endMinute) {
      gapStart = sorted[i].endMinute;
      gapEnd = sorted[i + 1]?.startMinute ?? 24 * 60;
    }
  }

  const gapDuration = gapEnd - gapStart;
  const slotSize = gapDuration >= 60 ? 60 : gapDuration; // 1시간 이상이면 1시간 단위, 아니면 갭 전체
  const slotIndex = slotSize > 0 ? Math.floor((currentMinutes - gapStart) / slotSize) : 0;
  const slotStart = gapStart + slotIndex * slotSize;
  const slotEnd = Math.min(slotStart + slotSize, gapEnd);

  return {
    type: "free",
    slotKey: `free-${slotStart}-${slotEnd}`,
    startMinute: slotStart,
    endMinute: slotEnd,
  };
}

export default function PhotoUploadPage() {
  const router = useRouter();
  const { id, dayNumber } = useParams<{ id: string; dayNumber: string }>();
  const searchParams = useSearchParams();
  const { trips, updateTrip } = useStore();
  const goBack = () => {
    if (searchParams.get("from") === "timeline") router.push(`/trip/${id}`);
    else router.back();
  };

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;

  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];

  const now = new Date();
  const today = new Date(); today.setHours(0, 0, 0, 0);
  const endDate = new Date(trip.startDate); endDate.setDate(endDate.getDate() + trip.nights); endDate.setHours(23, 59, 59, 999);
  const isDuringTrip = today <= endDate;
  const currentMinutes = now.getHours() * 60 + now.getMinutes();

  const slot = getCurrentSlot(day.blocks, day.selectedCandidateByBlock, trip.candidates, currentMinutes);
  const recordKey = slot.type === "block" ? slot.blockId : slot.slotKey;
  const record = day.records.find(r => r.blockId === recordKey);

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const saveRecord = (status: "uploaded" | "skipped") => {
    const title = slot.type === "block" ? slot.label : "자유 시간";
    const already = day.records.find(r => r.blockId === recordKey);
    const newRecords = already
      ? day.records.map(r => r.blockId === recordKey ? { ...r, status } : r)
      : [...day.records, { id: uid(), blockId: recordKey, title, status } as PhotoRecord];
    setDay({ ...day, records: newRecords });
  };

  const isFree = slot.type === "free";
  const themeKey = slot.type === "block" ? slot.themeKey : "travel";
  const t = THEME[themeKey] ?? THEME[Object.keys(THEME)[0]];

  return (
    <div className="flex flex-col min-h-screen px-4">
      <div className="flex items-center gap-3 pt-12 pb-2">
        <button onClick={goBack} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{dayNum}일차 사진 기록</h1>
        {isDuringTrip ? (
          <Link href={`/trip/${id}/timeline?from=timeline`} className="text-xs font-semibold text-blue-500">
            전체보기
          </Link>
        ) : (
          <div className="w-16" />
        )}
      </div>

      <div className="flex-1 flex flex-col items-center justify-center gap-5 py-4 px-2">
        <div
          className="w-full p-5 rounded-3xl flex flex-col items-center gap-3"
          style={{ background: isFree ? "#f1f5f9" : t.bg }}
        >
          <span className="text-4xl">{isFree ? "😊" : t.icon}</span>
          <p className="text-xs font-bold" style={{ color: isFree ? "#64748b" : t.text }}>
            {timeText(slot.startMinute)} ~ {timeText(slot.endMinute)}
          </p>
          <p className="font-bold text-xl text-center" style={{ color: isFree ? "#334155" : t.text }}>
            {isFree ? "지금은 뭐하세요?" : (slot.type === "block" ? slot.label : "")}
          </p>
          {slot.type === "block" && slot.address && (
            <p className="text-sm text-gray-500 text-center">{slot.address}</p>
          )}
          {isFree && (
            <p className="text-sm text-gray-400 text-center">자유 시간에도 사진을 남겨보세요!</p>
          )}
        </div>

        <div className="w-full h-44 bg-gray-100 rounded-3xl flex flex-col items-center justify-center gap-2">
          <span className="text-4xl text-gray-300">📷</span>
          <p className="text-sm text-gray-400">사진 또는 숏폼 업로드 영역</p>
        </div>
      </div>

      <div className="flex flex-col gap-3 py-4">
        <button
          onClick={() => saveRecord("uploaded")}
          className="w-full py-4 rounded-2xl text-white font-semibold"
          style={{ background: record?.status === "uploaded" ? "#16a34a" : "#22c55e" }}
        >
          {record?.status === "uploaded" ? "✓ 사진 올렸어요" : "사진 올리기"}
        </button>
        <button
          onClick={() => saveRecord("skipped")}
          className="w-full py-4 rounded-2xl font-semibold"
          style={record?.status === "skipped"
            ? { background: "#e5e7eb", color: "#374151" }
            : { background: "#f3f4f6", color: "#6b7280" }}
        >
          {record?.status === "skipped" ? "✓ 건너뜀" : "건너뛰기"}
        </button>
      </div>
    </div>
  );
}
