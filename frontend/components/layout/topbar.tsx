import { ROLE_LABELS, type Role } from "@/lib/roles";
import { LogoutButton } from "./logout-button";

export function Topbar({ email, role }: { email: string; role: Role }) {
  return (
    <header className="flex h-16 items-center justify-between border-b border-brand-100 bg-white px-8">
      <div />
      <div className="flex items-center gap-4">
        <div className="text-right">
          <p className="text-sm font-medium text-brand-900">{email}</p>
          <p className="text-xs text-brand-400">{ROLE_LABELS[role]}</p>
        </div>
        <LogoutButton />
      </div>
    </header>
  );
}
