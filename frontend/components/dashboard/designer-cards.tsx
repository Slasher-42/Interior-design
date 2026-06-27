import { Badge } from "@/components/ui/badge";
import { Card, StatCard } from "@/components/ui/card";
import { formatDate, formatNumber } from "@/lib/format";
import type { DesignerDashboard } from "@/lib/types/dashboard";

export function DesignerCards({ data }: { data: DesignerDashboard }) {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard label="Total Tasks" value={formatNumber(data.totalTasks)} />
        <StatCard label="Pending Tasks" value={formatNumber(data.pendingTasks)} />
        <StatCard label="Completed Tasks" value={formatNumber(data.completedTasks)} />
      </div>

      <Card>
        <h2 className="text-sm font-semibold text-brand-900">Your Tasks</h2>
        <div className="mt-4 divide-y divide-brand-100">
          {data.tasks.map((task) => (
            <div key={task.id} className="flex items-center justify-between py-3 text-sm">
              <div>
                <p className="font-medium text-brand-950">Task {task.id.slice(0, 8)}</p>
                <p className="text-brand-400">Deadline {formatDate(task.deadline)}</p>
              </div>
              <div className="flex items-center gap-3">
                <Badge tone="neutral">{task.priority}</Badge>
                <Badge tone="accent">{task.status}</Badge>
              </div>
            </div>
          ))}
          {data.tasks.length === 0 ? (
            <p className="py-3 text-sm text-brand-300">No tasks assigned yet.</p>
          ) : null}
        </div>
      </Card>
    </div>
  );
}
