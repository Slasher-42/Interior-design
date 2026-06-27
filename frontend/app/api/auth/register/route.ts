import { NextResponse } from "next/server";
import { ApiError, proxyFetch } from "@/lib/server/services";

export async function POST(request: Request) {
  const body = await request.json();

  try {
    const data = await proxyFetch("auth", "/auth/register", {
      method: "POST",
      body,
      auth: false,
    });
    return NextResponse.json(data, { status: 201 });
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
