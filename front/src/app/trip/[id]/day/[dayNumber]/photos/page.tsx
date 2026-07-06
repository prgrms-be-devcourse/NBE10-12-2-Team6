"use client";

import { useRef, useState, useEffect } from "react";
import Link from "next/link";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import { useStore, TripDay, PhotoRecord, uid } from "../../../../../store";
import { timeText, API_BASE, apiFetch } from "../../../../../lib";

interface TimelineBlock {
  timelineId: number;
  startTime: string;
  endTime: string;
  voteId: number | null;
}

function isoToMinutes(iso: string) {
  const [h, m] = iso.split("T")[1].split(":").map(Number);
  return h * 60 + (m || 0);
}

export default function PhotoUploadPage() {
  const router = useRouter();
  const { id, dayNumber } = useParams<{ id: string; dayNumber: string }>();
  const searchParams = useSearchParams();
  const { trips, updateTrip, upsertTrip } = useStore();

  const fileInputRef = useRef<HTMLInputElement>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [currentBlock, setCurrentBlock] = useState<TimelineBlock | null>(null);
  const [timelineLoaded, setTimelineLoaded] = useState(false);

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;

  const isDuringTrip = (() => {
    if (!trip) return false;
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const startDate = new Date(trip.startDate); startDate.setHours(0, 0, 0, 0);
    const endDate = new Date(trip.startDate); endDate.setDate(endDate.getDate() + trip.nights); endDate.setHours(23, 59, 59, 999);
    return today >= startDate && today <= endDate;
  })();

  useEffect(() => {
    if (trip || !id) return;
    apiFetch(`${API_BASE}/api/v1/trips/${id}`)
      .then(r => r.json())
      .then(body => {
        const tripData = body.data;
        if (!tripData) return;
        upsertTrip({
          id: String(tripData.id), name: tripData.name, region: tripData.region,
          startDate: tripData.startDate, nights: tripData.nights, members: [],
          days: Array.from({ length: (tripData.nights ?? 0) + 1 }, (_, i) => {
            const d = new Date(tripData.startDate + "T00:00:00");
            d.setDate(d.getDate() + i);
            const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
            return { id: `day-${i + 1}`, dayNumber: i + 1, date: dateStr, blocks: [{ id: uid(), order: 1, theme: "meal" as const, startMinute: 9 * 60, endMinute: 10 * 60 }], isPlanCompleted: false, isPlanSkipped: false, selectedCandidateByBlock: {}, votedUserIDsByBlockAndCandidate: {}, records: [] };
          }),
          candidates: [], inviteCode: tripData.joinCode ?? "", inviteJoinIndex: 0,
        });
      }).catch(() => {});
  }, [id, trip]);

  useEffect(() => {
    if (!trip) return;
    if (!id || !isDuringTrip) {
      setTimelineLoaded(true);
      return;
    }
    apiFetch(`${API_BASE}/api/v1/trips/${id}/timelines?dayNumber=${dayNum}`)
      .then(r => r.json())
      .then(body => {
        const items: TimelineBlock[] = body.data ?? [];
        const now = new Date();
        const currentMinutes = now.getHours() * 60 + now.getMinutes();
        const matched = items.find(item =>
          currentMinutes >= isoToMinutes(item.startTime) &&
          currentMinutes <= isoToMinutes(item.endTime)
        );
        setCurrentBlock(matched ?? null);
      })
      .catch(() => {})
      .finally(() => setTimelineLoaded(true));
  }, [id, dayNum, isDuringTrip, trip]);

  if (!trip || dayIdx < 0) return (
    <div className="flex items-center justify-center min-h-screen">
      <p className="text-gray-400 text-sm">불러오는 중...</p>
    </div>
  );

  const day = trip.days[dayIdx];

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const recordKey = currentBlock ? String(currentBlock.timelineId) : `free-${dayNum}`;
  const record = day.records.find(r => r.blockId === recordKey);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setSelectedFile(file);
    setPreviewUrl(URL.createObjectURL(file));
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      fileInputRef.current?.click();
      return;
    }
    setUploading(true);
    try {
      const form = new FormData();
      form.append("image", selectedFile);
      form.append(
        "request",
        new Blob([JSON.stringify({ timeLineId: currentBlock?.timelineId ?? null })], { type: "application/json" })
      );
      const res = await apiFetch(`${API_BASE}/api/v1/trips/${id}/posts`, {
        method: "POST",
        body: form,
      });
      if (res.ok) {
        alert("사진이 업로드되었습니다!");
        router.push(`/trip/${id}`);
        return;
      }
      const title = currentBlock
        ? `${timeText(isoToMinutes(currentBlock.startTime))}~${timeText(isoToMinutes(currentBlock.endTime))} 활동`
        : "자유 시간";
      const already = day.records.find(r => r.blockId === recordKey);
      const newRecords = already
        ? day.records.map(r => r.blockId === recordKey ? { ...r, status: "uploaded" as const } : r)
        : [...day.records, { id: uid(), blockId: recordKey, title, status: "uploaded" } as PhotoRecord];
      setDay({ ...day, records: newRecords });
      setSelectedFile(null);
      setPreviewUrl(null);
    } catch (e) {
      console.error(e);
    } finally {
      setUploading(false);
    }
  };

  const handleSkip = () => {
    const title = currentBlock
      ? `${timeText(isoToMinutes(currentBlock.startTime))}~${timeText(isoToMinutes(currentBlock.endTime))} 활동`
      : "자유 시간";
    const already = day.records.find(r => r.blockId === recordKey);
    const newRecords = already
      ? day.records.map(r => r.blockId === recordKey ? { ...r, status: "skipped" as const } : r)
      : [...day.records, { id: uid(), blockId: recordKey, title, status: "skipped" } as PhotoRecord];
    setDay({ ...day, records: newRecords });
  };

  const goBack = () => {
    if (searchParams.get("from") === "timeline") router.push(`/trip/${id}`);
    else router.back();
  };

  return (
    <div className="flex flex-col h-screen px-4">
      <div className="flex items-center gap-3 pt-12 pb-2 shrink-0">
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

      {/* 현재 시간대 정보 */}
      <div className="shrink-0 pb-3">
        {!timelineLoaded ? (
          <div className="w-full p-4 rounded-3xl bg-gray-100 flex items-center justify-center">
            <p className="text-sm text-gray-400">불러오는 중...</p>
          </div>
        ) : currentBlock ? (
          <div className="w-full px-5 py-3 rounded-3xl bg-blue-50 flex flex-col items-center gap-1">
            <p className="text-xs font-bold text-blue-400">
              {timeText(isoToMinutes(currentBlock.startTime))} ~ {timeText(isoToMinutes(currentBlock.endTime))}
            </p>
            <p className="font-bold text-base text-blue-800 text-center">지금 이 시간대의 사진을 올려보세요</p>
          </div>
        ) : (
          <div className="w-full px-5 py-3 rounded-3xl bg-gray-50 flex items-center gap-3">
            <span className="text-2xl">😊</span>
            <div>
              <p className="font-bold text-gray-700">자유 시간</p>
              <p className="text-xs text-gray-400">자유 시간에도 사진을 남겨보세요!</p>
            </div>
          </div>
        )}
      </div>

      {/* 카메라 / 미리보기 영역 */}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        capture="environment"
        className="hidden"
        onChange={handleFileChange}
      />
      <button
        type="button"
        onClick={() => fileInputRef.current?.click()}
        className="flex-1 rounded-3xl overflow-hidden flex flex-col items-center justify-center gap-2 bg-gray-100 active:opacity-80 transition-opacity"
      >
        {previewUrl ? (
          <img src={previewUrl} alt="preview" className="w-full h-full object-contain" />
        ) : (
          <>
            <span className="text-5xl text-gray-300">📷</span>
            <p className="text-sm text-gray-400">탭해서 사진 찍기</p>
          </>
        )}
      </button>

      {/* 하단 버튼 — 공간은 항상 유지, 사진 선택 후에만 표시 */}
      <div className="py-4 shrink-0">
        <button
          onClick={handleUpload}
          disabled={uploading || (isDuringTrip && !timelineLoaded)}
          className={`w-full py-4 rounded-2xl text-white font-semibold transition-colors disabled:opacity-60 ${
            selectedFile || record?.status === "uploaded" ? "visible" : "invisible"
          }`}
          style={{
            background: record?.status === "uploaded"
              ? "#16a34a"
              : uploading ? "#86efac" : "#22c55e",
          }}
        >
          {uploading ? "업로드 중..." : record?.status === "uploaded" ? "✓ 사진 올렸어요" : "사진 올리기"}
        </button>
      </div>
    </div>
  );
}
