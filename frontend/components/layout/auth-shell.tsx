import type { ReactNode } from "react";
import { Logo } from "./logo";

export function AuthShell({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle: string;
  children: ReactNode;
}) {
  return (
    <div className="flex min-h-full flex-1">
      <div className="hidden flex-1 flex-col justify-between bg-brand-950 px-12 py-12 text-white lg:flex">
        <Logo tone="light" />
        <div>
          <p className="max-w-md text-3xl font-light leading-snug text-brand-50">
            Centralizing every client, project, and proposal in one studio workspace.
          </p>
        </div>
        <p className="text-xs uppercase tracking-[0.2em] text-brand-300">
          Interior Design Service Management System
        </p>
      </div>
      <div className="flex flex-1 items-center justify-center px-6 py-12">
        <div className="w-full max-w-sm">
          <div className="mb-8 lg:hidden">
            <Logo />
          </div>
          <h1 className="text-2xl font-semibold text-brand-950">{title}</h1>
          <p className="mt-1 text-sm text-brand-400">{subtitle}</p>
          <div className="mt-8">{children}</div>
        </div>
      </div>
    </div>
  );
}
