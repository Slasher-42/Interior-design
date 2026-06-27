import { Badge } from "@/components/ui/badge";
import { Card, StatCard } from "@/components/ui/card";
import { formatCurrency, formatDate, formatNumber } from "@/lib/format";
import type { ProjectManagerDashboard } from "@/lib/types/dashboard";

export function ProjectManagerCards({ data }: { data: ProjectManagerDashboard }) {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
        <StatCard label="Total Projects" value={formatNumber(data.totalProjects)} />
        <StatCard label="Active Projects" value={formatNumber(data.activeProjects)} />
        <StatCard label="Completed Projects" value={formatNumber(data.completedProjects)} />
        <StatCard label="Total Tasks" value={formatNumber(data.totalTasks)} />
        <StatCard
          label="Pending Tasks"
          value={formatNumber(data.pendingTasks)}
          hint={`${formatNumber(data.completedTasks)} completed`}
        />
      </div>

      <Card>
        <h2 className="text-sm font-semibold text-brand-900">Your Projects</h2>
        <div className="mt-4 divide-y divide-brand-100">
          {data.projects.map((project) => (
            <div key={project.id} className="flex items-center justify-between py-3 text-sm">
              <div>
                <p className="font-medium text-brand-950">Project {project.id.slice(0, 8)}</p>
                <p className="text-brand-400">Started {formatDate(project.createdAt)}</p>
              </div>
              <div className="flex items-center gap-3">
                <span className="text-brand-500">{formatCurrency(project.approvedBudget)}</span>
                <Badge tone="accent">{project.status}</Badge>
              </div>
            </div>
          ))}
          {data.projects.length === 0 ? (
            <p className="py-3 text-sm text-brand-300">No projects assigned yet.</p>
          ) : null}
        </div>
      </Card>
    </div>
  );
}
