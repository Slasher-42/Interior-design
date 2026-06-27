import { cn } from "@/lib/cn";

/**
 * Text-based stand-in for the Space Design Group mark. Swap for the real logo file
 * (e.g. an <Image src="/logo.png" .../>) once it's dropped into frontend/public/.
 */
export function Logo({
  className,
  tone = "dark",
}: {
  className?: string;
  tone?: "dark" | "light";
}) {
  const isLight = tone === "light";

  return (
    <div className={cn("flex items-center gap-3", className)}>
      <span
        className={cn(
          "flex h-10 w-10 items-center justify-center rounded-full",
          isLight ? "bg-white text-brand-950" : "bg-brand-950 text-white",
        )}
      >
        <span className="text-sm font-semibold lowercase tracking-tight">sp</span>
      </span>
      <span className="flex flex-col leading-none">
        <span
          className={cn(
            "text-lg font-semibold lowercase tracking-tight",
            isLight ? "text-white" : "text-brand-950",
          )}
        >
          space
        </span>
        <span
          className={cn(
            "text-[10px] font-medium uppercase tracking-[0.2em]",
            isLight ? "text-brand-300" : "text-brand-400",
          )}
        >
          Design Group
        </span>
      </span>
    </div>
  );
}
