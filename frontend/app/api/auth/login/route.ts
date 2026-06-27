import { NextResponse } from "next/server";
import { ApiError, proxyFetch } from "@/lib/server/services";
import { setSessionCookie } from "@/lib/server/session";

type AuthResponse = {
  accessToken: string;
  role: string;
  mfaRequired: boolean;
  mfaSetupRequired: boolean;
  challengeToken: string | null;
};

export async function POST(request: Request) {
  const body = await request.json();

  try {
    const data = await proxyFetch<AuthResponse>("auth", "/auth/login", {
      method: "POST",
      body,
      auth: false,
    });

    if (data.mfaRequired) {
      return NextResponse.json({
        mfaRequired: true,
        mfaSetupRequired: data.mfaSetupRequired,
        challengeToken: data.challengeToken,
      });
    }

    await setSessionCookie(data.accessToken);
    return NextResponse.json({ mfaRequired: false, role: data.role });
  } catch (error) {
    if (error instanceof ApiError) {
      return NextResponse.json(
        { message: error.message, fieldErrors: error.fieldErrors },
        { status: error.status },
      );
    }
    throw error;
  }
}
