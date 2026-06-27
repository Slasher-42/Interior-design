import { redirect } from "next/navigation";
import { AdminCards } from "@/components/dashboard/admin-cards";
import { ClientCards } from "@/components/dashboard/client-cards";
import { DesignerCards } from "@/components/dashboard/designer-cards";
import { ProjectManagerCards } from "@/components/dashboard/project-manager-cards";
import { SalesCards } from "@/components/dashboard/sales-cards";
import { Card } from "@/components/ui/card";
import { dashboardEndpointForRole, ROLE_LABELS, type Role } from "@/lib/roles";
import { ApiError, proxyFetch } from "@/lib/server/services";
import { getSession } from "@/lib/server/session";
import type {
  AdminDashboard,
  ClientDashboard,
  DesignerDashboard,
  ProjectManagerDashboard,
  SalesDashboard,
} from "@/lib/types/dashboard";

type DashboardResult =
  | { role: "ADMIN"; data: AdminDashboard }
  | { role: "PROJECT_MANAGER"; data: ProjectManagerDashboard }
  | { role: "DESIGNER"; data: DesignerDashboard }
  | { role: "CLIENT"; data: ClientDashboard }
  | { role: "SALES_TEAM"; data: SalesDashboard };

async function loadDashboard(role: Role): Promise<DashboardResult> {
  const endpoint = dashboardEndpointForRole(role);

  switch (role) {
    case "ADMIN":
      return { role, data: await proxyFetch<AdminDashboard>("reportingAnalytics", endpoint) };
    case "PROJECT_MANAGER":
      return {
        role,
        data: await proxyFetch<ProjectManagerDashboard>("reportingAnalytics", endpoint),
      };
    case "DESIGNER":
      return { role, data: await proxyFetch<DesignerDashboard>("reportingAnalytics", endpoint) };
    case "CLIENT":
      return { role, data: await proxyFetch<ClientDashboard>("reportingAnalytics", endpoint) };
    case "SALES_TEAM":
      return { role, data: await proxyFetch<SalesDashboard>("reportingAnalytics", endpoint) };
  }
}

function renderDashboard(result: DashboardResult) {
  switch (result.role) {
    case "ADMIN":
      return <AdminCards data={result.data} />;
    case "PROJECT_MANAGER":
      return <ProjectManagerCards data={result.data} />;
    case "DESIGNER":
      return <DesignerCards data={result.data} />;
    case "CLIENT":
      return <ClientCards data={result.data} />;
    case "SALES_TEAM":
      return <SalesCards data={result.data} />;
  }
}

export default async function DashboardPage() {
  const session = await getSession();
  if (!session) {
    redirect("/login");
  }

  let result: DashboardResult | null = null;
  let errorMessage: string | null = null;

  try {
    result = await loadDashboard(session.role);
  } catch (error) {
    errorMessage =
      error instanceof ApiError
        ? error.message
        : "Could not reach the Reporting & Analytics Service.";
  }

  return (
    <div>
      <h1 className="text-2xl font-semibold text-brand-950">
        {ROLE_LABELS[session.role]} Dashboard
      </h1>
      <p className="mt-1 text-sm text-brand-400">
        A real-time overview of what matters for your role.
      </p>

      <div className="mt-6">
        {errorMessage ? (
          <Card>
            <p className="text-sm text-red-600">{errorMessage}</p>
          </Card>
        ) : (
          result && renderDashboard(result)
        )}
      </div>
    </div>
  );
}
