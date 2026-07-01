"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { useStore, TripDay, PhotoRecord, uid } from "../../../../../store";
import { THEME, timeText } from "../../../../../lib";

export default function PhotoUploadPage() {
  const router = useRouter();
  const { id, dayNumber } = useParams<{ id: string; dayNumber: string }>();
  const { trips, updateTrip } = useStore();
  const [currentIndex, setCurrentIndex] = useState(0);

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;

  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];
  const confirmedBlocks = [...day.blocks]
    .filter(b => day.selectedCandidateByBlock[b.id] != null)
    .sort((a, b) => a.startMinute - b.startMinute);

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const addRecord = (blockId: string, title: string, status: "uploaded" | "skipped") => {
    const record: PhotoRecord = { id: uid(), blockId, title, status };
    setDay({ ...day, records: [...day.records, record] });
    setCurrentIndex(i => i + 1);
  };

  if (currentIndex >= confirmedBlocks.length) {
    return (
      <div className="flex flex-col min-h-screen px-4">
        <div className="flex items-center gap-3 pt-12 pb-2">
          <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h1 className="font-semibold text-base flex-1 text-center">사진 기록</h1>
          <div className="w-8" />
        </div>

        <div className="flex-1 flex flex-col items-center justify-center gap-5 text-center px-4">
          <span className="text-6xl">✅</span>
          <p className="text-2xl font-bold">사진 기록 완료</p>
          <p className="text-gray-500 text-sm">기록이 여행 타임라인에 반영되었습니다.</p>
          <button
            onClick={() => router.back()}
            className="px-8 py-3.5 rounded-2xl bg-blue-500 text-white font-semibold"
          >
            닫기
          </button>
        </div>
      </div>
    );
  }

  const block = confirmedBlocks[currentIndex];
  const candidateId = day.selectedCandidateByBlock[block.id];
  const candidate = trip.candidates.find(c => c.id === candidateId);
  const t = THEME[block.theme];

  return (
    <div className="flex flex-col min-h-screen px-4">
      <div className="flex items-center gap-3 pt-12 pb-2">
        <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{dayNum}일차 사진 기록</h1>
        <div className="w-8" />
      </div>

      {/* Progress */}
      <div className="flex gap-1 px-2 py-2">
        {confirmedBlocks.map((_, i) => (
          <div
            key={i}
            className="h-1 rounded-full flex-1 transition-all"
            style={{ background: i <= currentIndex ? "#f97316" : "#e5e7eb" }}
          />
        ))}
      </div>
      <p className="text-center text-xs text-gray-400 mb-4">{currentIndex + 1} / {confirmedBlocks.length}</p>

      <div className="flex-1 flex flex-col items-center justify-center gap-5 py-4">
        {/* Place card */}
        <div className="w-full p-5 rounded-3xl flex flex-col items-center gap-3" style={{ background: t.bg }}>
          <span className="text-4xl">{t.icon}</span>
          <p className="text-xs font-bold" style={{ color: t.text }}>{timeText(block.startMinute)} ~ {timeText(block.endMinute)}</p>
          <p className="font-bold text-xl text-center" style={{ color: t.text }}>
            {candidate?.placeName ?? `${block.order}번째 활동`}
          </p>
          {candidate && <p className="text-sm text-gray-500 text-center">{candidate.address}</p>}
          <p className="text-sm font-semibold" style={{ color: t.text }}>이 장소의 사진을 올리거나 건너뜁니다.</p>
        </div>

        {/* Camera placeholder */}
        <div className="w-full h-44 bg-gray-100 rounded-3xl flex flex-col items-center justify-center gap-2">
          <span className="text-4xl text-gray-300">📷</span>
          <p className="text-sm text-gray-400">사진 또는 숏폼 업로드 영역</p>
        </div>
      </div>

      <div className="flex flex-col gap-3 py-4">
        <button
          onClick={() => addRecord(block.id, candidate?.placeName ?? "", "uploaded")}
          className="w-full py-4 rounded-2xl text-white font-semibold"
          style={{ background: "#22c55e" }}
        >
          사진 올리기
        </button>
        <button
          onClick={() => addRecord(block.id, candidate?.placeName ?? "", "skipped")}
          className="w-full py-4 rounded-2xl bg-gray-100 text-gray-700 font-semibold"
        >
          건너뛰기
        </button>
      </div>
    </div>
  );
}
