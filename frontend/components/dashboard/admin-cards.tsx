import { Card, StatCard } from "@/components/ui/card";
import { formatCurrency, formatNumber } from "@/lib/format";
import type { AdminDashboard } from "@/lib/types/dashboard";

export function AdminCards({ data }: { data: AdminDashboard }) {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Total Users" value={formatNumber(data.totalUsers)} />
        <StatCard label="Total Clients" value={formatNumber(data.totalClients)} />
        <StatCard label="Active Projects" value={formatNumber(data.activeProjects)} />
        <StatCard
          label="Completed Projects"
          value={formatNumber(data.completedProjects)}
        />
        <StatCard
          label="Pending Quotation Value"
          value={formatCurrency(data.pendingQuotationValue)}
        />
        <StatCard
          label="Converted Quotation Value"
          value={formatCurrency(data.convertedQuotationValue)}
        />
        <StatCard
          label="Customer Satisfaction"
          value={
            data.customerSatisfactionAverage != null
              ? `${data.customerSatisfactionAverage.toFixed(1)} / 5`
              : "—"
          }
          hint={`${formatNumber(data.lowRatedFeedbackCount)} low-rated reviews flagged`}
        />
        <StatCard
          label="Task On-Track Rate"
          value={`${data.taskOnTrackPercentage.toFixed(0)}%`}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <Card>
          <h2 className="text-sm font-semibold text-brand-900">Service Requests by Category</h2>
          <ul className="mt-4 space-y-2">
            {Object.entries(data.requestsByCategory).map(([category, count]) => (
              <li key={category} className="flex justify-between text-sm">
                <span className="text-brand-500">{category}</span>
                <span className="font-medium text-brand-950">{formatNumber(count)}</span>
              </li>
            ))}
            {Object.keys(data.requestsByCategory).length === 0 ? (
              <p className="text-sm text-brand-300">No service requests yet.</p>
            ) : null}
          </ul>
        </Card>

        <Card>
          <h2 className="text-sm font-semibold text-brand-900">Client Segmentation</h2>
          <ul className="mt-4 space-y-2">
            {data.clientSegmentation.slice(0, 6).map((segment, idx) => (
              <li key={idx} className="flex justify-between text-sm">
                <span className="text-brand-500">
                  {segment.industry} · {segment.city || segment.country}
                </span>
                <span className="font-medium text-brand-950">{formatNumber(segment.total)}</span>
              </li>
            ))}
            {data.clientSegmentation.length === 0 ? (
              <p className="text-sm text-brand-300">No client segmentation data yet.</p>
            ) : null}
          </ul>
        </Card>
      </div>
    </div>
  );
}
