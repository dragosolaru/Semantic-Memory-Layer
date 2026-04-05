/**
 * Root Layout
 * 
 * Application root layout with fonts, providers, and navbar.
 * Wraps all pages with authentication context.
 * 
 * @module app/layout
 */

import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/lib/auth";
import Navbar from "@/components/Navbar";

// Google Fonts - Geist
const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

/** Page metadata */
export const metadata: Metadata = {
  title: "Semantic Memory Layer",
  description: "AI-powered private semantic memory and retrieval layer",
};

/**
 * RootLayout Component
 * 
 * Application shell with:
 * - Google Fonts (Geist)
 * - AuthProvider (authentication context)
 * - Navbar (navigation)
 * - Main content area
 * 
 * @param {ReactNode} children - Page content
 * @returns {JSX.Element} Root layout
 */
export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${geistSans.variable} ${geistMono.variable}`}>
      <body>
        <AuthProvider>
          <Navbar />
          <main className="main-content">{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}