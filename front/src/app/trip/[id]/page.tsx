"use client";

import { useRouter } from "next/navigation";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useStore, TripDay } from "../../store";
import { Avatar, BigActionCard, formatDate, timeText } from "../../lib";

function statusBadge(day: TripDay) {
  if (day.isPlanSkipped) return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-gray-100 text-gray-500">계획 건너뜀</span>;
  if (day.isPlanCompleted) return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-blue-100 text-blue-600">계획 완료</span>;
  return <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-orange-100 text-orange-600">작성 필요</span>;
}

function timeDesc(day: TripDay, isLast: boolean): string {
  if (day.dayNumber === 1) return `도착 ${timeText(day.arrivalTime)} ~ 취침 ${timeText(day.sleepTime)}`;
  if (isLast) return `기상 ${timeText(day.wakeTime)} ~ 귀가 ${timeText(day.leaveTime)}`;
  return `기상 ${timeText(day.wakeTime)} ~ 취침 ${timeText(day.sleepTime)}`;
}

export default function TripDetailPage() {
  const router = useRouter();
  const { id } = useParams<{ id: string }>();
  const { trips } = useStore();
  const trip = trips.find(t => t.id === id);

  if (!trip) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4">
        <p className="text-gray-500">여행을 찾을 수 없습니다.</p>
        <button onClick={() => router.push("/home")} className="text-blue-500 font-medium">홈으로</button>
      </div>
    );
  }

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.push("/home")} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">여행 모임</h1>
        <div className="w-8" />
      </div>

      <div className="px-4 pb-10 flex flex-col gap-5">
        {/* Trip info */}
        <div>
          <p className="text-2xl font-bold">{trip.title}</p>
          <p className="text-sm text-gray-500 mt-1">{trip.region} · {trip.nights}박 {trip.nights + 1}일</p>
          <p className="text-xs text-gray-400 mt-0.5">시작일 {formatDate(trip.startDate)}</p>
        </div>

        {/* Members */}
        <div className="p-4 bg-gray-50 rounded-2xl">
          <p className="font-semibold mb-3">여행 멤버</p>
          <div className="flex gap-4 overflow-x-auto pb-1">
            {trip.members.map(m => (
              <div key={m.id} className="flex flex-col items-center gap-1.5 shrink-0">
                <Avatar user={m} size={42} />
                <span className="text-xs text-gray-600">{m.name}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Time setting or day list */}
        {!trip.isTimeSettingSaved ? (
          <Link href={`/trip/${trip.id}/time-setting`}>
            <BigActionCard
              icon="⏰"
              title="취침 및 기상 시간 설정"
              subtitle="각 일차의 자유시간 범위를 먼저 정해주세요."
              colorKey="orange"
            />
          </Link>
        ) : (
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
                  <p className="text-xs text-gray-500">{timeDesc(day, day.dayNumber === trip.days.length)}</p>
                </div>
              </Link>
            ))}
          </div>
        )}

        {/* Timeline link */}
        {trip.isTimeSettingSaved && (
          <Link href={`/trip/${trip.id}/timeline`}>
            <BigActionCard
              icon="📸"
              title="여행 타임라인 보기"
              subtitle="올린 사진과 건너뛴 기록을 일차별로 확인합니다."
              colorKey="purple"
            />
          </Link>
        )}
      </div>
    </div>
  );
}
