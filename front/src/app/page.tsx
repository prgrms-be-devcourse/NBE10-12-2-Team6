"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useStore } from "./store";
import { API_BASE } from "./lib";

type Mode = "landing" | "login" | "signup";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useStore();
  const [mode, setMode] = useState<Mode>("landing");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) return;
    setLoading(true);
    setError("");
    try {
      const joinCode = localStorage.getItem("pendingInviteCode") ?? undefined;
      const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email: email.trim(), password, joinCode }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body?.message ?? "이메일 또는 비밀번호가 올바르지 않아요.");
      }
      const authHeader = res.headers.get("authorization");
      if (authHeader) {
        const parts = authHeader.split(" ");
        if (parts.length === 3) {
          localStorage.setItem("refreshToken", parts[1]);
          localStorage.setItem("accessToken", parts[2]);
        }
      }
      const body = await res.json().catch(() => ({}));
      login(body.data?.name, body.data?.id);
      router.replace("/home");
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "로그인에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async () => {
    if (!email.trim() || !password.trim() || !name.trim()) return;
    setLoading(true);
    setError("");
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/signup`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email: email.trim(), password, name: name.trim() }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body?.message ?? "회원가입에 실패했습니다.");
      }
      setEmail("");
      setPassword("");
      setName("");
      setMode("login");
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : "회원가입에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // ── 랜딩 ──────────────────────────────────────────────────────────────────────
  if (mode === "landing") {
    return (
      <div className="flex flex-col px-6" style={{ height: "100dvh", paddingBottom: "max(2rem, env(safe-area-inset-bottom))" }}>
        <div className="flex-1 flex flex-col items-center justify-center gap-4">
          <span className="text-6xl">🗺️</span>
          <h1 className="text-4xl font-bold tracking-tight">TripLog</h1>
          <p className="text-gray-500 text-center leading-relaxed">
            친구들과 여행을 계획하고,<br />여행 중 순간을 기록해보세요.
          </p>
        </div>
        <div className="flex flex-col gap-3">
          <button
            onClick={() => setMode("login")}
            className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold text-base active:opacity-80"
          >
            로그인하기
          </button>
          <button
            onClick={() => setMode("signup")}
            className="w-full py-4 rounded-2xl bg-gray-100 text-gray-800 font-semibold text-base active:opacity-80"
          >
            회원가입
          </button>
        </div>
      </div>
    );
  }

  // ── 로그인 ────────────────────────────────────────────────────────────────────
  if (mode === "login") {
    return (
      <div className="flex flex-col min-h-screen px-6">
        <div className="flex items-center gap-3 pt-14 pb-4 border-b border-gray-100">
          <button onClick={() => { setMode("landing"); setError(""); }} className="text-blue-500">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h1 className="text-lg font-bold">로그인</h1>
        </div>

        <div className="flex flex-col gap-5 pt-8">
          <div>
            <label className="text-sm font-semibold mb-1.5 block">이메일</label>
            <input
              className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
              placeholder="이메일을 입력해주세요"
              type="email"
              autoComplete="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>
          <div>
            <label className="text-sm font-semibold mb-1.5 block">비밀번호</label>
            <input
              className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
              placeholder="비밀번호를 입력해주세요"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              onKeyDown={e => e.key === "Enter" && handleLogin()}
            />
          </div>

          {error && <p className="text-sm text-red-500 font-semibold">{error}</p>}

          <button
            onClick={handleLogin}
            disabled={!email.trim() || !password.trim() || loading}
            className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold text-base mt-2 disabled:opacity-40"
          >
            {loading ? "로그인 중..." : "로그인하기"}
          </button>

          <button
            onClick={() => { setMode("signup"); setError(""); }}
            className="text-sm text-gray-500 text-center underline"
          >
            계정이 없으신가요? 회원가입
          </button>
        </div>
      </div>
    );
  }

  // ── 회원가입 ──────────────────────────────────────────────────────────────────
  return (
    <div className="flex flex-col min-h-screen px-6">
      <div className="flex items-center gap-3 pt-14 pb-4 border-b border-gray-100">
        <button onClick={() => { setMode("landing"); setError(""); }} className="text-blue-500">
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="text-lg font-bold">회원가입</h1>
      </div>

      <div className="flex flex-col gap-5 pt-8">
        <div>
          <label className="text-sm font-semibold mb-1.5 block">이메일</label>
          <input
            className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
            placeholder="이메일을 입력해주세요"
            type="email"
            autoComplete="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
          />
        </div>
        <div>
          <label className="text-sm font-semibold mb-1.5 block">비밀번호</label>
          <input
            className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
            placeholder="비밀번호를 입력해주세요"
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>
        <div>
          <label className="text-sm font-semibold mb-1.5 block">이름</label>
          <input
            className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
            placeholder="이름을 입력해주세요"
            value={name}
            onChange={e => setName(e.target.value)}
          />
        </div>

        {error && <p className="text-sm text-red-500 font-semibold">{error}</p>}

        <button
          onClick={handleSignup}
          disabled={!email.trim() || !password.trim() || !name.trim() || loading}
          className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold text-base mt-2 disabled:opacity-40"
        >
          {loading ? "가입 중..." : "가입 완료"}
        </button>

        <button
          onClick={() => { setMode("login"); setError(""); }}
          className="text-sm text-gray-500 text-center underline"
        >
          이미 계정이 있으신가요? 로그인
        </button>
      </div>
    </div>
  );
}
