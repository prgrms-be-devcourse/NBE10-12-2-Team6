"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useStore } from "./store";

export default function LoginPage() {
  const router = useRouter();
  const { isLoggedIn, login, signup } = useStore();
  const [showSignup, setShowSignup] = useState(false);
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");

  useEffect(() => {
    if (isLoggedIn) router.replace("/home");
  }, [isLoggedIn, router]);

  if (showSignup) {
    return (
      <div className="flex flex-col min-h-screen px-6">
        <div className="flex items-center gap-3 pt-14 pb-4 border-b border-gray-100">
          <button onClick={() => setShowSignup(false)} className="text-blue-500">
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
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>
          <div>
            <label className="text-sm font-semibold mb-1.5 block">닉네임</label>
            <input
              className="w-full p-3.5 bg-gray-100 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-300"
              placeholder="닉네임을 입력해주세요"
              value={nickname}
              onChange={e => setNickname(e.target.value)}
            />
          </div>
          <button
            onClick={() => signup(nickname)}
            disabled={!nickname.trim()}
            className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold text-base mt-2 disabled:opacity-40"
          >
            가입 완료
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen px-6 pb-8">
      <div className="flex-1 flex flex-col items-center justify-center gap-4">
        <span className="text-6xl">🗺️</span>
        <h1 className="text-4xl font-bold tracking-tight">TripLog</h1>
        <p className="text-gray-500 text-center leading-relaxed">
          친구들과 여행을 계획하고,<br />여행 중 순간을 기록해보세요.
        </p>
      </div>

      <div className="flex flex-col gap-3">
        <button
          onClick={login}
          className="w-full py-4 rounded-2xl bg-blue-500 text-white font-semibold text-base active:opacity-80"
        >
          로그인하기
        </button>
        <button
          onClick={() => setShowSignup(true)}
          className="w-full py-4 rounded-2xl bg-gray-100 text-gray-800 font-semibold text-base active:opacity-80"
        >
          회원가입
        </button>
      </div>
    </div>
  );
}
