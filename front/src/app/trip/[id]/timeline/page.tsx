"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import { useStore, uid } from "../../../store";
import { formatDate, apiFetch, API_BASE } from "../../../lib";

interface Post {
  postId: number;
  timeLineId: number | null;
  contentUrl: string;
  createdAt?: string;
  startTime?: string;
  endTime?: string;
  placeName?: string;
}

interface DateGroup {
  date: string;
  posts: Post[];
}

type Segment =
  | { type: "timeline"; timeLineId: number; posts: Post[] }
  | { type: "free"; slotKey: string; label: string; posts: Post[] };

function isoToMins(iso: string) {
  const t = iso.includes("T") ? iso.split("T")[1] : iso;
  const [h, m] = t.split(":").map(Number);
  return h * 60 + (m || 0);
}

function fmtMins(m: number) {
  return `${String(Math.floor(m / 60)).padStart(2, "0")}:${String(m % 60).padStart(2, "0")}`;
}

function getFreeSlot(createdAt: string, sortedBlocks: { startTime: string; endTime: string }[]): { key: string; label: string } {
  const mins = isoToMins(createdAt);
  let gapStart = 0;
  let gapEnd = 24 * 60;

  for (let i = 0; i < sortedBlocks.length; i++) {
    const bStart = isoToMins(sortedBlocks[i].startTime);
    const bEnd = isoToMins(sortedBlocks[i].endTime);
    if (mins < bStart) {
      gapEnd = bStart;
      if (i > 0) gapStart = isoToMins(sortedBlocks[i - 1].endTime);
      break;
    }
    gapStart = bEnd;
  }

  const offset = mins - gapStart;
  const chunkIdx = Math.floor(offset / 60);
  const slotStart = gapStart + chunkIdx * 60;
  const slotEnd = Math.min(slotStart + 60, gapEnd);
  const key = `free-${slotStart}-${slotEnd}`;
  return { key, label: `${fmtMins(slotStart)} ~ ${fmtMins(slotEnd)}` };
}

function resolveUrl(contentUrl: string): string {
  if (!contentUrl.startsWith("http")) return `${API_BASE}${contentUrl}`;
  try {
    const src = new URL(contentUrl);
    const base = new URL(API_BASE);
    src.hostname = base.hostname;
    src.port = base.port;
    src.protocol = base.protocol;
    return src.toString();
  } catch {
    return contentUrl;
  }
}

function toSegments(posts: Post[]): Segment[] {
  const blockMap = new Map<number, { startTime: string; endTime: string }>();
  for (const post of posts) {
    if (post.timeLineId !== null && post.startTime && !blockMap.has(post.timeLineId)) {
      blockMap.set(post.timeLineId, { startTime: post.startTime, endTime: post.endTime! });
    }
  }
  const sortedBlocks = [...blockMap.values()].sort((a, b) => a.startTime.localeCompare(b.startTime));

  const timelineSegs = new Map<number, Segment & { type: "timeline" }>();
  const freeSegs = new Map<string, Segment & { type: "free" }>();
  const order: string[] = [];

  for (const post of posts) {
    if (post.timeLineId !== null) {
      const key = `tl-${post.timeLineId}`;
      if (!timelineSegs.has(post.timeLineId)) {
        const seg: Segment & { type: "timeline" } = { type: "timeline", timeLineId: post.timeLineId, posts: [post] };
        timelineSegs.set(post.timeLineId, seg);
        order.push(key);
      } else {
        timelineSegs.get(post.timeLineId)!.posts.push(post);
      }
    } else {
      const { key, label } = post.createdAt
        ? getFreeSlot(post.createdAt, sortedBlocks)
        : { key: "free-unknown", label: "자유 시간" };
      if (!freeSegs.has(key)) {
        const seg: Segment & { type: "free" } = { type: "free", slotKey: key, label, posts: [post] };
        freeSegs.set(key, seg);
        order.push(key);
      } else {
        freeSegs.get(key)!.posts.push(post);
      }
    }
  }

  return order.map(k => k.startsWith("tl-")
    ? timelineSegs.get(Number(k.slice(3)))!
    : freeSegs.get(k)!
  );
}

export default function TimelinePage() {
  const router = useRouter();
  const { id } = useParams<{ id: string }>();
  const searchParams = useSearchParams();
  const { trips, upsertTrip } = useStore();
  const [groups, setGroups] = useState<DateGroup[]>([]);
  const [lightbox, setLightbox] = useState<string | null>(null);

  const goBack = () => {
    if (searchParams.get("from") === "timeline") router.push(`/trip/${id}?tab=timeline`);
    else router.back();
  };
  const trip = trips.find(t => t.id === id);

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
    if (!id) return;
    apiFetch(`${API_BASE}/api/v1/trips/${id}/posts`)
      .then(r => r.json())
      .then(body => {
        console.log("posts response:", body);
        const raw = Array.isArray(body) ? body : (body.data ?? []);
        setGroups(raw);
      })
      .catch(e => console.error(e));
  }, [id]);

  if (!trip) return (
    <div className="flex items-center justify-center min-h-screen">
      <p className="text-gray-400 text-sm">불러오는 중...</p>
    </div>
  );

  return (
    <>
      {lightbox && (
        <div className="fixed inset-0 z-50 bg-black/90 flex items-center justify-center" onClick={() => setLightbox(null)}>
          <img src={lightbox} alt="" className="max-w-full max-h-full object-contain" />
        </div>
      )}
    <div className="flex h-screen flex-col overflow-hidden">
      <div className="shrink-0 flex items-center gap-3 px-4 pt-12 pb-2">
        <button
          onClick={goBack}
          aria-label="뒤로가기"
          className="trip-header-icon-button w-10 h-10 rounded-full flex items-center justify-center"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="font-semibold text-base flex-1 text-center">여행 타임라인</h1>
        <div className="w-8" />
      </div>

      <div className="shrink-0 px-4 pb-3">
        <p className="text-2xl font-bold">{trip.name} 타임라인</p>
      </div>

      <div className="flex-1 min-h-0 overflow-y-auto px-4 pb-10 flex flex-col gap-4">
        {trip.days.map(day => {
          const group = groups.find(g => g.date === day.date);
          const dayPosts = group?.posts ?? [];

          return (
            <div key={day.id} className="shrink-0 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
              <div className="px-4 py-3 border-b border-gray-100">
                <p className="font-semibold">{day.dayNumber}일차</p>
                <p className="text-xs text-gray-400">{formatDate(day.date)}</p>
              </div>

              <div className="p-4">
                {dayPosts.length === 0 ? (
                  <p className="text-sm text-gray-500">아직 사진 기록이 없습니다.</p>
                ) : (
                  <div className="relative">
                  <div
                    data-scroll
                    className="flex gap-3 overflow-x-auto snap-x snap-mandatory select-none"
                    style={{ scrollbarWidth: "none", cursor: "grab" }}
                    onScroll={(e) => {
                      const el = e.currentTarget;
                      const step = el.clientWidth + 12;
                      const idx = Math.round(el.scrollLeft / step);
                      el.parentElement!.querySelectorAll('[data-dot]').forEach((dot, i) => {
                        (dot as HTMLElement).style.opacity = i === idx ? "1" : "0.3";
                        (dot as HTMLElement).style.width = i === idx ? "16px" : "6px";
                      });
                    }}
                    onMouseDown={(e) => {
                      e.preventDefault();
                      const el = e.currentTarget;
                      el.style.scrollSnapType = "none";
                      const startX = e.clientX;
                      const startScrollLeft = el.scrollLeft;
                      el.style.cursor = "grabbing";
                      let hasDragged = false;
                      const onMove = (ev: MouseEvent) => {
                        if (Math.abs(ev.clientX - startX) > 5) hasDragged = true;
                        el.scrollLeft = startScrollLeft - (ev.clientX - startX);
                      };
                      const onUp = (ev: MouseEvent) => {
                        el.style.cursor = "grab";
                        window.removeEventListener("mousemove", onMove);
                        window.removeEventListener("mouseup", onUp);
                        const step = el.clientWidth + 12;
                        const dx = ev.clientX - startX;
                        const baseIdx = Math.round(startScrollLeft / step);
                        let targetIdx = baseIdx;
                        if (dx < -step * 0.15) targetIdx = baseIdx + 1;
                        else if (dx > step * 0.15) targetIdx = baseIdx - 1;
                        const maxIdx = el.children.length - 1;
                        targetIdx = Math.max(0, Math.min(targetIdx, maxIdx));
                        el.scrollTo({ left: targetIdx * step, behavior: "smooth" });
                        setTimeout(() => { el.style.scrollSnapType = ""; }, 400);
                        if (hasDragged) {
                          const blockClick = (ec: MouseEvent) => { ec.stopPropagation(); window.removeEventListener("click", blockClick, true); };
                          window.addEventListener("click", blockClick, true);
                        }
                      };
                      window.addEventListener("mousemove", onMove);
                      window.addEventListener("mouseup", onUp);
                    }}
                  >
                    {toSegments(dayPosts).flatMap(seg => {
                      const label = seg.type === "timeline"
                        ? (seg.posts[0]?.startTime && seg.posts[0]?.endTime
                            ? `${seg.posts[0].startTime.slice(11, 16)} ~ ${seg.posts[0].endTime.slice(11, 16)}${seg.posts[0].placeName ? ` · ${seg.posts[0].placeName}` : ""}`
                            : `타임라인 #${seg.timeLineId}`)
                        : `자유 시간 · ${seg.label}`;
                      const labelColor = seg.type === "timeline" ? "text-blue-400" : "text-gray-400";
                      const borderColor = seg.type === "timeline" ? "border-blue-100 bg-blue-50" : "border-gray-200 bg-gray-50";

                      return seg.posts.map(post => {
                        const src = resolveUrl(post.contentUrl);
                        return (
                          <div key={post.postId} className={`rounded-2xl border p-3 flex flex-col gap-2 snap-start basis-full shrink-0 ${borderColor}`}>
                            <p className={`text-xs font-semibold ${labelColor}`}>{label}</p>
                            <div className="flex items-center justify-center rounded-xl overflow-hidden" style={{ height: "360px" }}>
                              <img src={src} alt="" draggable={false} className="max-w-full max-h-full object-contain cursor-pointer" onClick={() => setLightbox(src)} />
                            </div>
                          </div>
                        );
                      });
                    })}
                  </div>
                  {dayPosts.length > 1 && (
                    <div className="flex justify-center items-center gap-1.5 mt-4">
                      {Array.from({ length: dayPosts.length }, (_, i) => (
                        <div
                          key={i}
                          data-dot
                          className="h-1.5 rounded-full bg-gray-400 transition-all duration-200 cursor-pointer"
                          style={{ width: i === 0 ? "16px" : "6px", opacity: i === 0 ? 1 : 0.3 }}
                          onClick={(e) => {
                            const rel = (e.currentTarget as HTMLElement).closest('.relative');
                            const el = rel?.querySelector('[data-scroll]') as HTMLElement | null;
                            if (el) el.scrollTo({ left: i * (el.clientWidth + 12), behavior: "smooth" });
                          }}
                        />
                      ))}
                    </div>
                  )}
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
    </>
  );
}
