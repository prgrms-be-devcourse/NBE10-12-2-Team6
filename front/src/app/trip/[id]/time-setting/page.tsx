"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { useStore, Trip, TripDay } from "../../../store";
import { TimeStepper, timeText, distributeDurations } from "../../../lib";

function applyTimesToTrip(trip: Trip): Trip {
  const days = trip.days.map(day => {
    const isLast = day.dayNumber === trip.days.length;
    const start = day.dayNumber === 1 ? day.arrivalTime : day.wakeTime;
    const end = isLast ? day.leaveTime : day.sleepTime;
    const freeMinutes = Math.max(15, end - start);
    const durations = distributeDurations(freeMinutes, day.blocks.length);
    const blocks = day.blocks.map((b, i) => ({ ...b, durationMinutes: durations[i] }));
    return { ...day, blocks };
  });
  return { ...trip, days, isTimeSettingSaved: true };
}

export default function TimeSettingPage() {
  const router = useRouter();
  const { id } = useParams<{ id: string }>();
  const { trips, updateTrip } = useStore();
  const trip = trips.find(t => t.id === id);

  const [days, setDays] = useState<TripDay[]>(trip?.days ?? []);

  if (!trip) return null;

  const updateDay = (index: number, patch: Partial<TripDay>) => {
    setDays(prev => prev.map((d, i) => i === index ? { ...d, ...patch } : d));
  };

  const handleSave = () => {
    const updated = applyTimesToTrip({ ...trip, days });
    updateTrip(updated);
    router.back();
  };

  return (
    <div className="min-h-screen">
      <div className="flex items-center gap-3 px-4 pt-12 pb-2">
        <button onClick={() => router.back()} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">시간 설정</h1>
        <div className="w-8" />
      </div>

      <div className="px-4 pb-10 flex flex-col gap-4">
        <p className="text-sm text-gray-500">각 일차의 자유시간 범위를 정해주세요.</p>

        {days.map((day, i) => {
          const isLast = day.dayNumber === days.length;
          return (
            <div key={day.id} className="p-4 bg-gray-50 rounded-2xl flex flex-col gap-4">
              <p className="font-semibold">{day.dayNumber}일차</p>

              {day.dayNumber === 1 ? (
                <>
                  <TimeStepper label="지역 예상 도착 시간" minutes={day.arrivalTime} onChange={v => updateDay(i, { arrivalTime: v })} />
                  <TimeStepper label="취침 시간" minutes={day.sleepTime} onChange={v => updateDay(i, { sleepTime: v })} />
                </>
              ) : isLast ? (
                <>
                  <TimeStepper label="기상 시간" minutes={day.wakeTime} onChange={v => updateDay(i, { wakeTime: v })} />
                  <TimeStepper label="집에 가는 시간" minutes={day.leaveTime} onChange={v => updateDay(i, { leaveTime: v })} />
                </>
              ) : (
                <>
                  <TimeStepper label="기상 시간" minutes={day.wakeTime} onChange={v => updateDay(i, { wakeTime: v })} />
                  <TimeStepper label="취침 시간" minutes={day.sleepTime} onChange={v => updateDay(i, { sleepTime: v })} />
                </>
              )}
            </div>
          );
        })}

        <button
          onClick={handleSave}
          className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold"
        >
          저장하고 일차 만들기
        </button>
      </div>
    </div>
  );
}
