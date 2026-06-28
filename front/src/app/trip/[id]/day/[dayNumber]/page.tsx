"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { useStore, Trip, TripDay, ActivityBlock, PlanCandidate, PlanTheme, uid } from "../../../../store";
import { THEME, ThemeBadge, BigActionCard, timeText, durationText, distributeDurations, formatDate } from "../../../../lib";

// ── Helpers ───────────────────────────────────────────────────────────────────

function freeRange(day: TripDay, isLast: boolean) {
  const start = day.dayNumber === 1 ? day.arrivalTime : day.wakeTime;
  const end = isLast ? day.leaveTime : day.sleepTime;
  return { start, end, free: Math.max(15, end - start) };
}

function blockStart(day: TripDay, block: ActivityBlock, rangeStart: number): number {
  const idx = day.blocks.findIndex(b => b.id === block.id);
  return rangeStart + day.blocks.slice(0, idx).reduce((s, b) => s + b.durationMinutes, 0);
}

const THEMES: PlanTheme[] = ["meal", "cafe", "activity", "etc"];

// ── Components ────────────────────────────────────────────────────────────────

function BlockCard({
  day, block, rangeStart, onIncrease, onDecrease, onThemeChange, tripId, dayNumber,
}: {
  day: TripDay;
  block: ActivityBlock;
  rangeStart: number;
  onIncrease: () => void;
  onDecrease: () => void;
  onThemeChange: (t: PlanTheme) => void;
  tripId: string;
  dayNumber: number;
}) {
  const t = THEME[block.theme];
  const start = blockStart(day, block, rangeStart);
  const end = start + block.durationMinutes;
  const selected = day.candidates.find(c => c.id === day.selectedCandidateByBlock[block.id]);
  const candidateCount = day.candidates.filter(c => c.blockId === block.id).length;

  return (
    <div className="p-4 bg-gray-50 rounded-2xl flex flex-col gap-3">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-semibold">{block.order}번째 활동</p>
          <p className="text-xs text-gray-400 mt-0.5">{timeText(start)} ~ {timeText(end)} · {durationText(block.durationMinutes)}</p>
        </div>
        {selected && <span className="text-green-500 text-lg">✓</span>}
      </div>

      {/* Theme selector */}
      <div>
        <p className="text-xs font-bold text-gray-400 mb-1.5">카테고리</p>
        <div className="flex gap-2">
          {THEMES.map(th => {
            const cfg = THEME[th];
            const active = block.theme === th;
            return (
              <button
                key={th}
                onClick={() => onThemeChange(th)}
                className="flex-1 py-1.5 rounded-lg text-xs font-semibold border transition-all"
                style={active ? { background: cfg.bg, color: cfg.text, borderColor: cfg.text } : { borderColor: "#e5e7eb", color: "#9ca3af" }}
              >
                {cfg.icon} {cfg.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* Duration */}
      <div className="flex items-center">
        <span className="text-xs font-bold text-gray-400 flex-1">활동 시간</span>
        <div className="flex items-center gap-3">
          <button onClick={onDecrease} className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center font-bold">−</button>
          <span className="text-sm font-bold w-16 text-center">{durationText(block.durationMinutes)}</span>
          <button onClick={onIncrease} className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center font-bold">+</button>
        </div>
      </div>

      {/* Candidate summary */}
      <div className="flex items-center justify-between">
        <ThemeBadge theme={block.theme} />
        <span className="text-xs text-gray-400">{candidateCount}개 후보</span>
      </div>

      {selected ? (
        <div className="p-3 rounded-xl" style={{ background: "#dcfce7" }}>
          <p className="text-xs text-green-600 mb-1">확정된 후보</p>
          <p className="font-semibold text-sm">{selected.placeName}</p>
          <p className="text-xs text-gray-500">{selected.address}</p>
        </div>
      ) : (
        <p className="text-xs text-gray-400">카테고리를 지정한 뒤 후보를 올리고 계획을 확정하세요.</p>
      )}

      <Link href={`/trip/${tripId}/day/${dayNumber}/block/${block.id}`}>
        <div className="w-full py-2.5 rounded-xl text-center text-sm font-semibold" style={{ background: "#dbeafe", color: "#2563eb" }}>
          후보 보기 / 정하기
        </div>
      </Link>
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

  if (!trip || dayIdx < 0) return null;

  const day = trip.days[dayIdx];
  const isLast = day.dayNumber === trip.days.length;
  const { start, end, free } = freeRange(day, isLast);

  const setDay = (updated: TripDay) => {
    updateTrip({ ...trip, days: trip.days.map((d, i) => i === dayIdx ? updated : d) });
  };

  const redistributeBlocks = (blocks: ActivityBlock[], totalMinutes: number) => {
    const durations = distributeDurations(totalMinutes, blocks.length);
    return blocks.map((b, i) => ({ ...b, durationMinutes: durations[i] }));
  };

  const increaseBlocks = () => {
    const nextOrder = day.blocks.length + 1;
    const theme = THEMES[(nextOrder - 1) % THEMES.length];
    const newBlock: ActivityBlock = { id: uid(), order: nextOrder, theme, durationMinutes: 15 };
    const blocks = redistributeBlocks([...day.blocks, newBlock], free);
    blocks.forEach((b, i) => b.order = i + 1);
    setDay({ ...day, blocks });
  };

  const decreaseBlocks = () => {
    if (day.blocks.length <= 1) return;
    const removed = day.blocks[day.blocks.length - 1];
    const remaining = day.blocks.slice(0, -1);
    const candidates = day.candidates.filter(c => c.blockId !== removed.id);
    const sel = { ...day.selectedCandidateByBlock };
    delete sel[removed.id];
    const blocks = redistributeBlocks(remaining, free);
    setDay({ ...day, blocks, candidates, selectedCandidateByBlock: sel });
  };

  const increaseDuration = (blockId: string) => {
    if (day.blocks.length <= 1) return;
    const idx = day.blocks.findIndex(b => b.id === blockId);
    // find a donor block (next then prev)
    const donorIdx = idx + 1 < day.blocks.length && day.blocks[idx + 1].durationMinutes > 15
      ? idx + 1
      : day.blocks.findIndex((b, i) => i !== idx && b.durationMinutes > 15);
    if (donorIdx < 0) return;
    const blocks = day.blocks.map((b, i) => {
      if (i === idx) return { ...b, durationMinutes: b.durationMinutes + 15 };
      if (i === donorIdx) return { ...b, durationMinutes: b.durationMinutes - 15 };
      return b;
    });
    setDay({ ...day, blocks });
  };

  const decreaseDuration = (blockId: string) => {
    if (day.blocks.length <= 1) return;
    const idx = day.blocks.findIndex(b => b.id === blockId);
    if (day.blocks[idx].durationMinutes <= 15) return;
    const receiverIdx = idx + 1 < day.blocks.length ? idx + 1 : Math.max(0, idx - 1);
    const blocks = day.blocks.map((b, i) => {
      if (i === idx) return { ...b, durationMinutes: b.durationMinutes - 15 };
      if (i === receiverIdx) return { ...b, durationMinutes: b.durationMinutes + 15 };
      return b;
    });
    setDay({ ...day, blocks });
  };

  const changeTheme = (blockId: string, theme: PlanTheme) => {
    setDay({ ...day, blocks: day.blocks.map(b => b.id === blockId ? { ...b, theme } : b) });
  };

  const canComplete = day.blocks.every(b => day.selectedCandidateByBlock[b.id] != null);

  const completePlan = () => {
    setDay({ ...day, isPlanCompleted: true, isPlanSkipped: false, records: [] });
  };

  const skipPlan = () => {
    setDay({ ...day, isPlanSkipped: true, isPlanCompleted: false, selectedCandidateByBlock: {}, records: [] });
    setShowSkipConfirm(false);
  };

  return (
    <div className="min-h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">{dayNum}일차</h1>
        <div className="w-8" />
      </div>

      <div className="px-4 pb-10 flex flex-col gap-5">
        {/* Day header */}
        <div>
          <p className="text-2xl font-bold">{dayNum}일차 자유시간</p>
          <p className="font-semibold text-blue-500 mt-1">{timeText(start)} ~ {timeText(end)}</p>
          <p className="text-xs text-gray-500 mt-1">총 {durationText(free)} 안에서 활동 시간을 나누어 계획합니다.</p>
        </div>

        {/* Plan body */}
        {day.isPlanSkipped ? (
          <div className="p-4 bg-gray-50 rounded-2xl">
            <p className="font-semibold mb-1">이 일차는 계획을 건너뛰었습니다.</p>
            <p className="text-sm text-gray-500">여행 타임라인에서도 건너뛴 일차로 표시됩니다.</p>
          </div>
        ) : day.isPlanCompleted ? (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <p className="font-semibold">확정된 계획</p>
              <button
                onClick={() => setDay({ ...day, isPlanCompleted: false })}
                className="text-xs text-gray-500 underline"
              >다시 수정</button>
            </div>
            {day.blocks.map(block => {
              const sel = day.candidates.find(c => c.id === day.selectedCandidateByBlock[block.id]);
              if (!sel) return null;
              const s = blockStart(day, block, start);
              return (
                <Link key={block.id} href={`/trip/${id}/day/${dayNum}/block/${block.id}`}>
                  <div className="flex gap-3 p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
                    <div className="flex flex-col items-center text-xs text-gray-400 shrink-0 pt-0.5">
                      <span className="font-bold">{timeText(s)}</span>
                      <div className="w-0.5 h-7 bg-gray-200 my-1" />
                      <span className="font-bold">{timeText(s + block.durationMinutes)}</span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <ThemeBadge theme={block.theme} />
                      <p className="font-semibold mt-1.5">{sel.placeName}</p>
                      <p className="text-xs text-gray-400">{sel.address}</p>
                    </div>
                    <span className="text-green-500 shrink-0">✓</span>
                  </div>
                </Link>
              );
            })}
          </div>
        ) : (
          <div className="flex flex-col gap-4">
            {/* Block count */}
            <div className="flex items-center">
              <p className="font-semibold flex-1">활동 개수</p>
              <div className="flex items-center gap-3">
                <button onClick={decreaseBlocks} className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center font-bold text-lg">−</button>
                <span className="font-bold w-6 text-center">{day.blocks.length}</span>
                <button onClick={increaseBlocks} className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center font-bold text-lg">+</button>
              </div>
            </div>
            <p className="text-xs text-gray-400 -mt-2">활동 시간은 15분 단위로 조절할 수 있습니다.</p>

            {/* Blocks */}
            {day.blocks.map(block => (
              <BlockCard
                key={block.id}
                day={day}
                block={block}
                rangeStart={start}
                onIncrease={() => increaseDuration(block.id)}
                onDecrease={() => decreaseDuration(block.id)}
                onThemeChange={t => changeTheme(block.id, t)}
                tripId={id}
                dayNumber={dayNum}
              />
            ))}

            <button
              onClick={completePlan}
              disabled={!canComplete}
              className="w-full py-4 rounded-2xl font-semibold text-white disabled:opacity-40"
              style={{ background: canComplete ? "#3b82f6" : "#9ca3af" }}
            >
              이 일차 계획 완료
            </button>
            <button
              onClick={() => setShowSkipConfirm(true)}
              className="w-full py-4 rounded-2xl font-semibold bg-gray-100 text-gray-700"
            >
              이 일차 계획 건너뛰기
            </button>
          </div>
        )}

        {/* Photo section */}
        <div className="flex flex-col gap-3">
          <p className="font-semibold">사진 기록</p>
          {day.isPlanCompleted ? (
            <Link href={`/trip/${id}/day/${dayNum}/photos`}>
              <BigActionCard icon="📷" title={`${dayNum}일차 사진 올리기`} subtitle="확정된 계획 순서대로 사진을 올리거나 건너뜁니다." colorKey="green" />
            </Link>
          ) : day.isPlanSkipped ? (
            <p className="text-sm text-gray-500">계획을 건너뛴 일차라 사진 업로드 단계가 없습니다.</p>
          ) : (
            <p className="text-sm text-gray-500">이 일차의 계획이 완료되면 사진을 올릴 수 있습니다.</p>
          )}
        </div>
      </div>

      {/* Skip confirm dialog */}
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
