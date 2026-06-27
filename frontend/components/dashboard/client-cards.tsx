import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { formatCurrency, formatDate } from "@/lib/format";
import type { ClientDashboard } from "@/lib/types/dashboard";

export function ClientCards({ data }: { data: ClientDashboard }) {
  return (
    <div className="space-y-6">
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
            <p className="py-3 text-sm text-brand-300">No projects yet.</p>
          ) : null}
        </div>
      </Card>

      <Card>
        <h2 className="text-sm font-semibold text-brand-900">Feedback History</h2>
        <div className="mt-4 divide-y divide-brand-100">
          {data.feedbackHistory.map((feedback) => (
            <div key={feedback.id} className="flex items-center justify-between py-3 text-sm">
              <p className="text-brand-400">{formatDate(feedback.submittedAt)}</p>
              <Badge tone={feedback.rating < 3 ? "warning" : "neutral"}>
                {feedback.rating} / 5
              </Badge>
            </div>
          ))}
          {data.feedbackHistory.length === 0 ? (
            <p className="py-3 text-sm text-brand-300">No feedback submitted yet.</p>
          ) : null}
        </div>
      </Card>
    </div>
  );
}
