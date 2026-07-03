"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { useStore, Trip, TripDay, ActivityBlock, PlanTheme, uid } from "../../../../store";
import { THEME, ThemeBadge, timeText, durationText } from "../../../../lib";

const THEMES: PlanTheme[] = ["meal", "cafe", "activity", "etc"];
const API_BASE = "http://localhost:8080";

const isoToMinutes = (iso: string) => {
  const [h, m] = iso.split("T")[1].split(":").map(Number);
  return h * 60 + (m || 0);
};

const toTimeStr = (minutes: number) =>
  `${String(Math.floor(minutes / 60)).padStart(2, "0")}:${String(minutes % 60).padStart(2, "0")}`;

const fromTimeStr = (str: string) => {
  const [h, m] = str.split(":").map(Number);
  return h * 60 + (m || 0);
};

// ── BlockCard ─────────────────────────────────────────────────────────────────

function BlockCard({
  trip, day, block, onStartChange, onEndChange,
}: {
  trip: Trip;
  day: TripDay;
  block: ActivityBlock;
  onStartChange: (v: number) => void;
  onEndChange: (v: number) => void;
}) {
  const duration = Math.max(0, block.endMinute - block.startMinute);
  const selected = trip.candidates.find(c => c.id === day.selectedCandidateByBlock[block.id]);

  return (
    <div className="p-4 bg-gray-50 rounded-2xl flex flex-col gap-3">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-semibold">{block.order}번째 시간 구간</p>
          <p className="text-xs mt-0.5 text-gray-400">
            {durationText(duration)}
          </p>
        </div>
        {selected && <span className="text-green-500 text-lg">✓</span>}
      </div>

      <div className="flex items-center gap-3">
        <div className="flex-1 flex flex-col gap-1">
          <p className="text-xs text-gray-500">시작</p>
          <input
            type="time"
            value={toTimeStr(block.startMinute)}
            onChange={e => e.target.value && onStartChange(fromTimeStr(e.target.value))}
            className="w-full p-2.5 bg-white rounded-xl text-sm font-semibold border border-gray-200 outline-none time-input"
          />
        </div>
        <span className="text-gray-300 mt-4 font-bold">~</span>
        <div className="flex-1 flex flex-col gap-1">
          <p className="text-xs text-gray-500">종료</p>
          <input
            type="time"
            value={toTimeStr(block.endMinute)}
            onChange={e => e.target.value && onEndChange(fromTimeStr(e.target.value))}
            className="w-full p-2.5 bg-white rounded-xl text-sm font-semibold border border-gray-200 outline-none time-input"
          />
        </div>
      </div>


      {selected && (
        <div className="p-3 rounded-xl" style={{ background: "#dcfce7" }}>
          <p className="text-xs text-green-600 mb-1">이 구간에 확정된 후보</p>
          <p className="font-semibold text-sm">{selected.placeName}</p>
          <p className="text-xs text-gray-500">{selected.address}</p>
        </div>
      )}
    </div>
  );
}

// ── Main ──────────────────────────────────────────────────────────────────────

export default function DayPlanPage() {
  const router = useRouter();
  const { id, dayNumber } = useParams<{ id: string; dayNumber: string }>();
  const { trips, updateTrip } = useStore();

  const trip = trips.find(t => t.id === id);
  const dayNum = parseInt(dayNumber);
  const dayIdx = trip?.days.findIndex(d => d.dayNumber === dayNum) ?? -1;

  const [showSkipConfirm, setShowSkipConfirm] = useState(false);
  const [validationError, setValidationError] = useState("");

  useEffect(() => {
    if (!id || !trip || dayIdx < 0) return;
    fetch(`${API_BASE}/api/v1/trips/${id}/timelines?dayNumber=${dayNum}`, { credentials: "include" })
      .then(r => r.json())
      .then(body => {
        const items: { timeLineId?: number; timelineId?: number; dayNumber: number; startTime: string; endTime: string }[] = body.data ?? [];
        if (items.length === 0) return;
        const blocks: ActivityBlock[] = items.map((item, i) => ({
          id: String(item.timeLineId ?? item.timelineId ?? i),
          order: i + 1,
          theme: THEMES[i % THEMES.length],
          startMinute: isoToMinutes(item.startTime),
          endMinute: isoToMinutes(item.endTime),
        }));
        const currentDay = trip.days[dayIdx];
        updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? { ...currentDay, blocks, isPlanCompleted: true } : d) });
      })
      .catch(() => {});
  }, [id, dayNum]);

  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const normalizeOrders = (blocks: ActivityBlock[]): ActivityBlock[] =>
    [...blocks].sort((a, b) => a.startMinute - b.startMinute).map((b, i) => ({ ...b, order: i + 1 }));

  const canComplete = day.blocks.length > 0;

  const suggestedNextStart = () => {
    const lastEnd = day.blocks.reduce((max, b) => Math.max(max, b.endMinute), 9 * 60);
    const rounded = Math.min(23 * 60, Math.floor(lastEnd / 15) * 15);
    return rounded >= 23 * 60 ? 9 * 60 : rounded;
  };

  const increaseBlocks = () => {
    const nextStart = suggestedNextStart();
    const nextEnd = Math.min(24 * 60, nextStart + 60);
    const theme = THEMES[day.blocks.length % THEMES.length];
    const newBlock: ActivityBlock = { id: uid(), order: day.blocks.length + 1, theme, startMinute: nextStart, endMinute: nextEnd };
    setDay({ ...day, blocks: normalizeOrders([...day.blocks, newBlock]) });
  };

  const decreaseBlocks = () => {
    if (day.blocks.length <= 1) return;
    const sorted = [...day.blocks].sort((a, b) => a.startMinute - b.startMinute);
    const removed = sorted[sorted.length - 1];
    const remaining = day.blocks.filter(b => b.id !== removed.id);
    const sel = { ...day.selectedCandidateByBlock };
    delete sel[removed.id];
    const voted = { ...day.votedUserIDsByBlockAndCandidate };
    delete voted[removed.id];
    setDay({ ...day, blocks: normalizeOrders(remaining), selectedCandidateByBlock: sel, votedUserIDsByBlockAndCandidate: voted });
  };

  const updateBlockTime = (blockId: string, start: number, end: number) => {
    setDay({ ...day, blocks: normalizeOrders(day.blocks.map(b => b.id === blockId ? { ...b, startMinute: start, endMinute: end } : b)) });
  };

  const completePlan = async () => {
    const hasInvalid = day.blocks.some(b => b.endMinute <= b.startMinute);
    if (hasInvalid) {
      setValidationError("종료 시간이 시작 시간보다 늦어야 합니다.");
      return;
    }
    const sorted = [...day.blocks].sort((a, b) => a.startMinute - b.startMinute);
    const hasOverlap = sorted.some((b, i) => i > 0 && sorted[i - 1].endMinute > b.startMinute);
    if (hasOverlap) {
      setValidationError("시간 구간이 서로 겹칩니다. 겹치지 않게 조정해주세요.");
      return;
    }
    setValidationError("");
    const dateStr = (() => {
      const d = new Date(trip.startDate);
      d.setDate(d.getDate() + dayNum - 1);
      return d.toISOString().split("T")[0];
    })();
    const toDateTime = (mins: number) => {
      const h = String(Math.floor(mins / 60)).padStart(2, "0");
      const m = String(mins % 60).padStart(2, "0");
      return `${dateStr}T${h}:${m}:00`;
    };
    const timeLines = sorted.map(block => ({
      dayNumber: dayNum,
      startTime: toDateTime(block.startMinute),
      endTime: toDateTime(block.endMinute),
    }));
    try {
      await fetch(`${API_BASE}/api/v1/trips/${id}/timelines/batch`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ dayNumber: dayNum, timeLines }),
      });
    } catch (e) {
      console.error("[타임라인 저장 실패]", e);
    }
    setDay({ ...day, blocks: normalizeOrders(day.blocks), isPlanCompleted: true, isPlanSkipped: false, records: [] });
    router.push(`/trip/${id}`);
  };

  const skipPlan = () => {
    setDay({ ...day, isPlanSkipped: true, isPlanCompleted: false, selectedCandidateByBlock: {}, votedUserIDsByBlockAndCandidate: {}, records: [] });
    setShowSkipConfirm(false);
  };

  const sortedBlocks = [...day.blocks].sort((a, b) => a.startMinute - b.startMinute);

  return (
    <div className="flex flex-col h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{dayNum}일차</h1>
        <div className="w-8" />
      </div>

      {/* 스크롤 영역 */}
      <div className="flex-1 overflow-y-scroll px-4 pt-2 pb-4 flex flex-col gap-5">
        <div>
          <p className="text-2xl font-bold">{dayNum}일차 계획</p>
          <p className="text-xs text-gray-500 mt-1">시간 구간을 정한 뒤, 여행 전체 후보 중 하나를 선택합니다.</p>
        </div>

        {day.isPlanSkipped ? (
          <div className="p-4 bg-gray-50 rounded-2xl">
            <p className="font-semibold mb-1">이 일차는 계획을 건너뛰었습니다.</p>
            <p className="text-sm text-gray-500">여행 타임라인에서도 건너뛴 일차로 표시됩니다.</p>
          </div>
        ) : day.isPlanCompleted ? (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <p className="font-semibold">확정된 계획</p>
              <div className="flex items-center gap-3">
                <span className="text-xs text-gray-400">후보는 사라지지 않고 재사용됩니다.</span>
                <button onClick={() => setDay({ ...day, isPlanCompleted: false })} className="text-xs text-gray-500 underline">수정</button>
              </div>
            </div>
            {sortedBlocks.map(block => {
              const sel = trip.candidates.find(c => c.id === day.selectedCandidateByBlock[block.id]);
              return (
                <Link key={block.id} href={`/trip/${id}/day/${dayNum}/block/${block.id}`}>
                  <div className="flex gap-3 p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
                    <div className="flex flex-col items-center text-xs text-gray-400 shrink-0 pt-0.5">
                      <span className="font-bold">{timeText(block.startMinute)}</span>
                      <div className="w-0.5 h-7 bg-gray-200 my-1" />
                      <span className="font-bold">{timeText(block.endMinute)}</span>
                    </div>
                    {sel ? (
                      <div className="flex-1 min-w-0">
                        <ThemeBadge theme={block.theme} />
                        <p className="font-semibold mt-1.5">{sel.placeName}</p>
                        <p className="text-xs text-gray-400">{sel.address}</p>
                      </div>
                    ) : (
                      <div className="flex-1 min-w-0 flex flex-col justify-center">
                        <p className="text-sm font-semibold text-gray-400">아직 계획을 안 세웠어요</p>
                        <p className="text-xs text-blue-400 mt-0.5">탭해서 후보 투표하러 가기 →</p>
                      </div>
                    )}
                    {sel && <span className="text-green-500 shrink-0">✓</span>}
                  </div>
                </Link>
              );
            })}
          </div>
        ) : (
          <div className="flex flex-col gap-4">
            <div className="flex items-center">
              <p className="font-semibold flex-1">시간 구간</p>
              <div className="flex items-center gap-3">
                <button onClick={decreaseBlocks} className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center font-bold text-lg">−</button>
                <span className="font-bold w-6 text-center">{day.blocks.length}</span>
                <button onClick={increaseBlocks} className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center font-bold text-lg">+</button>
              </div>
            </div>

            {sortedBlocks.map(block => (
              <BlockCard
                key={block.id}
                trip={trip}
                day={day}
                block={block}
                onStartChange={s => updateBlockTime(block.id, s, block.endMinute)}
                onEndChange={e => updateBlockTime(block.id, block.startMinute, e)}
              />
            ))}
          </div>
        )}

      </div>

      {/* 하단 고정 버튼 */}
      {!day.isPlanCompleted && !day.isPlanSkipped && (
        <div className="px-4 py-4 border-t border-gray-100 flex flex-col gap-2 bg-white">
          {validationError && (
            <p className="text-sm text-red-500 font-semibold text-center">{validationError}</p>
          )}
          <button
            onClick={completePlan}
            disabled={!canComplete}
            className="w-full py-4 rounded-2xl font-semibold text-white disabled:opacity-40"
            style={{ background: "#3b82f6" }}
          >
            시간 범위 설정 완료
          </button>
          <button
            onClick={() => setShowSkipConfirm(true)}
            className="w-full py-4 rounded-2xl font-semibold bg-gray-100 text-gray-700"
          >
            이 일차 계획 건너뛰기
          </button>
        </div>
      )}

      {showSkipConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-6">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowSkipConfirm(false)} />
          <div className="relative bg-white rounded-2xl p-6 w-full max-w-sm">
            <p className="font-bold text-base mb-2">정말 이 일차 계획을 건너뛰겠어요?</p>
            <p className="text-sm text-gray-500 mb-5">건너뛰면 이 일차는 계획 없이 타임라인에 표시됩니다.</p>
            <div className="flex gap-3">
              <button onClick={() => setShowSkipConfirm(false)} className="flex-1 py-3 rounded-xl bg-gray-100 font-semibold text-sm">취소</button>
              <button onClick={skipPlan} className="flex-1 py-3 rounded-xl bg-red-500 text-white font-semibold text-sm">건너뛰기</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
