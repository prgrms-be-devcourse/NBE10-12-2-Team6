"use client";

import { createContext, useContext, useState, ReactNode } from "react";

// ── Types ─────────────────────────────────────────────────────────────────────

export type PlanTheme = "meal" | "cafe" | "activity" | "etc";

export interface User {
  id: number;
  name: string;
  color: string;
}

export interface FriendRequest {
  id: string;
  user: User;
}

export interface ActivityBlock {
  id: string;
  order: number;
  theme: PlanTheme;
  durationMinutes: number;
}

export interface PlanCandidate {
  id: string;
  blockId: string;
  authorId: number;
  authorName: string;
  placeName: string;
  address: string;
  theme: PlanTheme;
  votedUserIds: number[];
}

export interface PhotoRecord {
  id: string;
  blockId: string;
  title: string;
  status: "uploaded" | "skipped";
}

export interface TripDay {
  id: string;
  dayNumber: number;
  date: string;
  arrivalTime: number;
  wakeTime: number;
  sleepTime: number;
  leaveTime: number;
  isPlanSkipped: boolean;
  isPlanCompleted: boolean;
  blocks: ActivityBlock[];
  candidates: PlanCandidate[];
  selectedCandidateByBlock: Record<string, string>;
  records: PhotoRecord[];
}

export interface Trip {
  id: string;
  title: string;
  region: string;
  startDate: string;
  nights: number;
  members: User[];
  days: TripDay[];
  isTimeSettingSaved: boolean;
}

interface CreateTripData {
  title: string;
  region: string;
  startDate: string;
  nights: number;
  memberIds: number[];
}

interface StoreCtx {
  isLoggedIn: boolean;
  currentUser: User;
  friends: User[];
  friendRequests: FriendRequest[];
  trips: Trip[];
  login: () => void;
  signup: (nickname: string) => void;
  acceptFriendRequest: (id: string) => void;
  rejectFriendRequest: (id: string) => void;
  addFriend: (name: string) => void;
  createTrip: (data: CreateTripData) => string;
  updateTrip: (trip: Trip) => void;
}

// ── Helpers ───────────────────────────────────────────────────────────────────

export function uid() {
  return Math.random().toString(36).slice(2, 10);
}

function makeDay(dayNumber: number, startDate: string, offsetDays: number): TripDay {
  const d = new Date(startDate + "T00:00:00");
  d.setDate(d.getDate() + offsetDays);
  return {
    id: uid(),
    dayNumber,
    date: d.toISOString().split("T")[0],
    arrivalTime: 10 * 60,
    wakeTime: 9 * 60,
    sleepTime: 22 * 60,
    leaveTime: 18 * 60,
    isPlanSkipped: false,
    isPlanCompleted: false,
    blocks: [{ id: uid(), order: 1, theme: "meal", durationMinutes: 60 }],
    candidates: [],
    selectedCandidateByBlock: {},
    records: [],
  };
}

// ── Seed data ─────────────────────────────────────────────────────────────────

const INITIAL_FRIENDS: User[] = [
  { id: 1, name: "민준", color: "orange" },
  { id: 2, name: "서연", color: "green" },
  { id: 3, name: "도윤", color: "purple" },
  { id: 4, name: "하린", color: "pink" },
  { id: 5, name: "유찬", color: "teal" },
  { id: 6, name: "지민", color: "indigo" },
  { id: 7, name: "태오", color: "teal" },
  { id: 8, name: "나은", color: "cyan" },
  { id: 9, name: "이준", color: "brown" },
  { id: 10, name: "소율", color: "red" },
];

const INITIAL_REQUESTS: FriendRequest[] = [
  { id: "req-1", user: { id: 101, name: "하늘", color: "yellow" } },
  { id: "req-2", user: { id: 102, name: "지우", color: "gray" } },
];

// ── Context ───────────────────────────────────────────────────────────────────

const Ctx = createContext<StoreCtx | null>(null);

export function TripLogProvider({ children }: { children: ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUser, setCurrentUser] = useState<User>({ id: 0, name: "루트", color: "blue" });
  const [friends, setFriends] = useState<User[]>(INITIAL_FRIENDS);
  const [friendRequests, setFriendRequests] = useState<FriendRequest[]>(INITIAL_REQUESTS);
  const [trips, setTrips] = useState<Trip[]>([]);

  const login = () => setIsLoggedIn(true);

  const signup = (nickname: string) => {
    if (nickname.trim()) setCurrentUser(u => ({ ...u, name: nickname.trim() }));
    setIsLoggedIn(true);
  };

  const acceptFriendRequest = (id: string) => {
    const req = friendRequests.find(r => r.id === id);
    if (!req) return;
    setFriends(f => [...f, req.user]);
    setFriendRequests(r => r.filter(x => x.id !== id));
  };

  const rejectFriendRequest = (id: string) => {
    setFriendRequests(r => r.filter(x => x.id !== id));
  };

  const addFriend = (name: string) => {
    const maxId = Math.max(10, ...friends.map(f => f.id), ...friendRequests.map(r => r.user.id)) + 1;
    setFriends(f => [...f, { id: maxId, name, color: "pink" }]);
  };

  const createTrip = (data: CreateTripData): string => {
    const selectedFriends = friends.filter(f => data.memberIds.includes(f.id));
    const members = [currentUser, ...selectedFriends];
    const days = Array.from({ length: data.nights + 1 }, (_, i) =>
      makeDay(i + 1, data.startDate, i)
    );
    const id = uid();
    setTrips(t => [
      ...t,
      { id, title: data.title, region: data.region, startDate: data.startDate, nights: data.nights, members, days, isTimeSettingSaved: false },
    ]);
    return id;
  };

  const updateTrip = (trip: Trip) => {
    setTrips(t => t.map(x => (x.id === trip.id ? trip : x)));
  };

  return (
    <Ctx.Provider value={{ isLoggedIn, currentUser, friends, friendRequests, trips, login, signup, acceptFriendRequest, rejectFriendRequest, addFriend, createTrip, updateTrip }}>
      {children}
    </Ctx.Provider>
  );
}

export function useStore() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useStore outside TripLogProvider");
  return ctx;
}
