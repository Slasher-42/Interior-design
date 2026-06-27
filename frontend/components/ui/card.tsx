import type { HTMLAttributes, ReactNode } from "react";
import { cn } from "@/lib/cn";

export function Card({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("rounded-xl border border-brand-100 bg-white p-6 shadow-sm", className)}
      {...props}
    />
  );
}

type StatCardProps = {
  label: string;
  value: ReactNode;
  hint?: ReactNode;
};

export function StatCard({ label, value, hint }: StatCardProps) {
  return (
    <Card>
      <p className="text-sm font-medium text-brand-500">{label}</p>
      <p className="mt-2 text-3xl font-semibold text-brand-950">{value}</p>
      {hint ? <p className="mt-1 text-sm text-brand-400">{hint}</p> : null}
    </Card>
  );
}
