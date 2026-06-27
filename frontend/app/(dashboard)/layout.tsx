import { redirect } from "next/navigation";
import { Sidebar } from "@/components/layout/sidebar";
import { Topbar } from "@/components/layout/topbar";
import { getSession } from "@/lib/server/session";

export default async function DashboardLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const session = await getSession();
  if (!session) {
    redirect("/login");
  }

  return (
    <div className="flex h-full flex-1">
      <Sidebar role={session.role} />
      <div className="flex flex-1 flex-col">
        <Topbar email={session.email} role={session.role} />
        <main className="flex-1 overflow-y-auto bg-background px-8 py-8">{children}</main>
      </div>
    </div>
  );
}
