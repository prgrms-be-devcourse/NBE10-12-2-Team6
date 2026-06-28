"use client";

import { User, PlanTheme } from "./store";

// ── Colors ────────────────────────────────────────────────────────────────────

const COLOR_STYLES: Record<string, { bg: string; text: string }> = {
  blue: { bg: "#dbeafe", text: "#2563eb" },
  orange: { bg: "#ffedd5", text: "#ea580c" },
  green: { bg: "#dcfce7", text: "#16a34a" },
  purple: { bg: "#f3e8ff", text: "#9333ea" },
  pink: { bg: "#fce7f3", text: "#db2777" },
  teal: { bg: "#ccfbf1", text: "#0d9488" },
  indigo: { bg: "#e0e7ff", text: "#4338ca" },
  cyan: { bg: "#cffafe", text: "#0891b2" },
  brown: { bg: "#fef3c7", text: "#92400e" },
  red: { bg: "#fee2e2", text: "#dc2626" },
  yellow: { bg: "#fef9c3", text: "#ca8a04" },
  gray: { bg: "#f3f4f6", text: "#4b5563" },
  mint: { bg: "#f0fff4", text: "#276749" },
};

export function colorStyle(color: string) {
  return COLOR_STYLES[color] ?? COLOR_STYLES.gray;
}

// ── Shared components ─────────────────────────────────────────────────────────

export function Avatar({ user, size = 36 }: { user: User; size?: number }) {
  const c = colorStyle(user.color);
  return (
    <div
      className="rounded-full flex items-center justify-center font-bold text-sm shrink-0"
      style={{ width: size, height: size, background: c.bg, color: c.text }}
    >
      {user.name[0]}
    </div>
  );
}

// ── Theme ─────────────────────────────────────────────────────────────────────

export const THEME: Record<PlanTheme, { label: string; icon: string; bg: string; text: string }> = {
  meal: { label: "식사", icon: "🍴", bg: "#ffedd5", text: "#ea580c" },
  cafe: { label: "카페", icon: "☕", bg: "#fef3c7", text: "#92400e" },
  activity: { label: "활동", icon: "🚶", bg: "#dbeafe", text: "#2563eb" },
  etc: { label: "기타", icon: "•••", bg: "#f3f4f6", text: "#4b5563" },
};

export function ThemeBadge({ theme }: { theme: PlanTheme }) {
  const t = THEME[theme];
  return (
    <span className="text-xs font-bold px-3 py-1.5 rounded-full" style={{ background: t.bg, color: t.text }}>
      {t.icon} {t.label}
    </span>
  );
}

// ── Utilities ─────────────────────────────────────────────────────────────────

export function timeText(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}`;
}

export function durationText(minutes: number): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  if (h > 0 && m > 0) return `${h}시간 ${m}분`;
  if (h > 0) return `${h}시간`;
  return `${m}분`;
}

export function formatDate(dateStr: string): string {
  return new Intl.DateTimeFormat("ko-KR", { month: "long", day: "numeric" })
    .format(new Date(dateStr + "T00:00:00"));
}

export function distributeDurations(totalMinutes: number, count: number): number[] {
  const safe = Math.max(15, totalMinutes);
  const base = Math.max(15, Math.floor(safe / count / 15) * 15);
  const durations = Array(count).fill(base);
  const remainder = safe - base * count;
  if (remainder > 0) durations[count - 1] += remainder;
  return durations;
}

// ── Layout helpers ────────────────────────────────────────────────────────────

export function PageHeader({
  title,
  onBack,
  right,
}: {
  title: string;
  onBack?: () => void;
  right?: React.ReactNode;
}) {
  return (
    <div className="flex items-center gap-3 pt-12 pb-2 px-4">
      {onBack ? (
        <button onClick={onBack} className="text-blue-500 p-1 -ml-1">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
      ) : (
        <div className="w-8" />
      )}
      <h1 className="font-semibold text-base flex-1 text-center">{title}</h1>
      <div className="w-8 flex justify-end">{right}</div>
    </div>
  );
}

export function BigActionCard({
  icon, title, subtitle, colorKey,
}: { icon: string; title: string; subtitle: string; colorKey: string }) {
  const c = colorStyle(colorKey);
  return (
    <div className="flex items-center gap-4 p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
      <div
        className="w-11 h-11 rounded-full flex items-center justify-center text-xl shrink-0"
        style={{ background: c.bg, color: c.text }}
      >
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-semibold">{title}</p>
        <p className="text-xs text-gray-500 mt-0.5 leading-relaxed">{subtitle}</p>
      </div>
      <svg className="w-4 h-4 text-gray-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
      </svg>
    </div>
  );
}

export function TimeStepper({
  label, minutes, onChange,
}: { label: string; minutes: number; onChange: (v: number) => void }) {
  return (
    <div className="flex items-center">
      <div className="flex-1">
        <p className="text-sm text-gray-600">{label}</p>
        <p className="text-lg font-bold">{timeText(minutes)}</p>
      </div>
      <div className="flex items-center gap-3">
        <button
          onClick={() => onChange(Math.max(0, minutes - 30))}
          className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center text-lg font-bold active:bg-gray-200"
        >
          −
        </button>
        <button
          onClick={() => onChange(Math.min(23 * 60 + 30, minutes + 30))}
          className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center text-lg font-bold active:bg-gray-200"
        >
          +
        </button>
      </div>
    </div>
  );
}
