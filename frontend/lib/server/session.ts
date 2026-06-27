import "server-only";

import { cookies } from "next/headers";
import type { Role } from "../roles";

const SESSION_COOKIE = "session";

export type SessionPayload = {
  userId: string;
  role: Role;
  email: string;
};

export async function getToken(): Promise<string | null> {
  const store = await cookies();
  return store.get(SESSION_COOKIE)?.value ?? null;
}

/**
 * Decodes the JWT payload for UI branching only (which dashboard to show, which nav items
 * to render). Every backend service independently verifies the signature on each request,
 * so there is no security reason to verify it again here.
 */
export async function getSession(): Promise<SessionPayload | null> {
  const token = await getToken();
  if (!token) return null;

  try {
    const [, payloadSegment] = token.split(".");
    const json = Buffer.from(payloadSegment, "base64url").toString("utf-8");
    const claims = JSON.parse(json);
    return {
      userId: claims.sub,
      role: claims.role,
      email: claims.email,
    };
  } catch {
    return null;
  }
}

export async function setSessionCookie(token: string): Promise<void> {
  const store = await cookies();
  store.set(SESSION_COOKIE, token, {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/",
  });
}

export async function clearSessionCookie(): Promise<void> {
  const store = await cookies();
  store.delete(SESSION_COOKIE);
}
