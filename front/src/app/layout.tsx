import type { Metadata, Viewport } from "next";
import "./globals.css";
import { TripLogProvider } from "./store";

export const metadata: Metadata = {
  title: "TripLog",
  description: "친구들과 여행을 계획하고, 순간을 기록하세요.",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  viewportFit: "cover",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className="h-full">
      <body className="min-h-full bg-gray-50 flex justify-center">
        <TripLogProvider>
          <div className="w-full max-w-md bg-white relative overflow-x-hidden" style={{ minHeight: "100dvh" }}>
            {children}
          </div>
        </TripLogProvider>
      </body>
    </html>
  );
}
