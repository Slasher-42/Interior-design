import "server-only";

import { getToken } from "./session";

export const SERVICE_URLS = {
  auth: process.env.AUTH_SERVICE_URL ?? "http://localhost:8081",
  userClient: process.env.USER_CLIENT_SERVICE_URL ?? "http://localhost:8082",
  serviceRequestQuotation:
    process.env.SERVICE_REQUEST_QUOTATION_SERVICE_URL ?? "http://localhost:8083",
  projectTask: process.env.PROJECT_TASK_SERVICE_URL ?? "http://localhost:8084",
  documentPortfolio: process.env.DOCUMENT_PORTFOLIO_SERVICE_URL ?? "http://localhost:8085",
  vendorInventory: process.env.VENDOR_INVENTORY_SERVICE_URL ?? "http://localhost:8086",
  feedbackCommunication:
    process.env.FEEDBACK_COMMUNICATION_SERVICE_URL ?? "http://localhost:8087",
  reportingAnalytics: process.env.REPORTING_ANALYTICS_SERVICE_URL ?? "http://localhost:8088",
} as const;

export type ServiceName = keyof typeof SERVICE_URLS;

export type FieldErrors = Record<string, string>;

export class ApiError extends Error {
  status: number;
  fieldErrors?: FieldErrors;

  constructor(status: number, message: string, fieldErrors?: FieldErrors) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

type ProxyFetchOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
  auth?: boolean;
};

/**
 * Every backend service trusts the JWT's own signature for authorization, so this helper
 * just forwards it - it does not re-validate anything client-side.
 */
export async function proxyFetch<T>(
  service: ServiceName,
  path: string,
  { auth = true, body, headers, ...init }: ProxyFetchOptions = {},
): Promise<T> {
  const requestHeaders = new Headers(headers);
  requestHeaders.set("Content-Type", "application/json");

  if (auth) {
    const token = await getToken();
    if (token) {
      requestHeaders.set("Authorization", `Bearer ${token}`);
    }
  }

  const res = await fetch(`${SERVICE_URLS[service]}${path}`, {
    ...init,
    headers: requestHeaders,
    body: body !== undefined ? JSON.stringify(body) : undefined,
    cache: "no-store",
  });

  if (res.status === 204) {
    return undefined as T;
  }

  const text = await res.text();
  const data = text ? JSON.parse(text) : undefined;

  if (!res.ok) {
    const message = data?.message ?? `Request to ${service} failed with status ${res.status}`;
    throw new ApiError(res.status, message, data?.fieldErrors);
  }

  return data as T;
}
