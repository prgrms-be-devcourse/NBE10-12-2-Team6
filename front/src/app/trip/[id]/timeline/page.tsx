"use client";

import { useRouter, useParams, useSearchParams } from "next/navigation";
import { useStore } from "../../../store";
import { formatDate } from "../../../lib";

export default function TimelinePage() {
  const router = useRouter();
  const { id } = useParams<{ id: string }>();
  const searchParams = useSearchParams();
  const { trips } = useStore();
  const goBack = () => {
    if (searchParams.get("from") === "timeline") router.push(`/trip/${id}?tab=timeline`);
    else router.back();
  };
  const trip = trips.find(t => t.id === id);

  if (!trip) return null;

  return (
    <div className="min-h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={goBack} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">여행 타임라인</h1>
        <div className="w-8" />
      </div>

      <div className="px-4 pb-10 flex flex-col gap-4">
        <p className="text-2xl font-bold">{trip.name} 타임라인</p>

        {trip.days.map(day => (
          <div key={day.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="px-4 py-3 border-b border-gray-100">
              <p className="font-semibold">{day.dayNumber}일차</p>
              <p className="text-xs text-gray-400">{formatDate(day.date)}</p>
            </div>

            <div className="p-4">
              {day.isPlanSkipped ? (
                <p className="text-sm text-gray-500">계획을 건너뛰었습니다.</p>
              ) : !day.isPlanCompleted ? (
                <p className="text-sm text-gray-500">아직 계획이 완료되지 않았습니다.</p>
              ) : day.records.length === 0 ? (
                <p className="text-sm text-gray-500">아직 사진 기록이 없습니다.</p>
              ) : (
                <div className="flex flex-col gap-2">
                  {day.records.map(record => (
                    <div key={record.id} className="flex items-center gap-3 p-3 bg-gray-50 rounded-xl">
                      <span className="text-lg">{record.status === "uploaded" ? "📷" : "⏭"}</span>
                      <div className="flex-1 min-w-0">
                        <p className="font-semibold text-sm truncate">{record.title}</p>
                        <p className="text-xs text-gray-400">
                          {record.status === "uploaded" ? "사진 업로드 완료" : "사진 건너뜀"}
                        </p>
                      </div>
                      <span
                        className="text-xs font-bold px-2 py-0.5 rounded-full"
                        style={record.status === "uploaded"
                          ? { background: "#dcfce7", color: "#16a34a" }
                          : { background: "#f3f4f6", color: "#6b7280" }
                        }
                      >
                        {record.status === "uploaded" ? "업로드" : "건너뜀"}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
