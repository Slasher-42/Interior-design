import type { HTMLAttributes } from "react";
import { cn } from "@/lib/cn";

type BadgeTone = "neutral" | "accent" | "warning";

const TONE_CLASSES: Record<BadgeTone, string> = {
  neutral: "bg-brand-50 text-brand-700",
  accent: "bg-accent-400/20 text-accent-600",
  warning: "bg-amber-100 text-amber-800",
};

export function Badge({
  tone = "neutral",
  className,
  ...props
}: HTMLAttributes<HTMLSpanElement> & { tone?: BadgeTone }) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium",
        TONE_CLASSES[tone],
        className,
      )}
      {...props}
    />
  );
}
