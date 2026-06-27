"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Badge } from "@/components/ui/badge";
import { navItemsForRole, type Role } from "@/lib/roles";
import { cn } from "@/lib/cn";
import { Logo } from "./logo";

export function Sidebar({ role }: { role: Role }) {
  const pathname = usePathname();
  const items = navItemsForRole(role);

  return (
    <aside className="flex h-full w-64 flex-col border-r border-brand-100 bg-white px-4 py-6">
      <div className="px-2">
        <Logo />
      </div>
      <nav className="mt-8 flex flex-1 flex-col gap-1">
        {items.map((item) => {
          const isActive = pathname === item.href || pathname?.startsWith(`${item.href}/`);

          if (!item.enabled) {
            return (
              <span
                key={item.href}
                className="flex items-center justify-between rounded-md px-3 py-2 text-sm text-brand-300"
                title="Coming soon"
              >
                {item.label}
                <Badge tone="neutral">Soon</Badge>
              </span>
            );
          }

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "rounded-md px-3 py-2 text-sm font-medium transition-colors",
                isActive ? "bg-brand-800 text-white" : "text-brand-700 hover:bg-brand-50",
              )}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
