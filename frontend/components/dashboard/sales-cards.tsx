import { Badge } from "@/components/ui/badge";
import { Card, StatCard } from "@/components/ui/card";
import { formatCurrency, formatDate, formatNumber } from "@/lib/format";
import type { SalesDashboard } from "@/lib/types/dashboard";

export function SalesCards({ data }: { data: SalesDashboard }) {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard
          label="Pending Service Requests"
          value={formatNumber(data.pendingServiceRequestsCount)}
        />
        <StatCard label="Open Quotations" value={formatNumber(data.openQuotationsCount)} />
        <StatCard label="Open Quotations Value" value={formatCurrency(data.openQuotationsValue)} />
      </div>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <Card>
          <h2 className="text-sm font-semibold text-brand-900">Pending Service Requests</h2>
          <div className="mt-4 divide-y divide-brand-100">
            {data.pendingServiceRequests.map((request) => (
              <div key={request.id} className="flex items-center justify-between py-3 text-sm">
                <div>
                  <p className="font-medium text-brand-950">{request.category}</p>
                  <p className="text-brand-400">{formatDate(request.createdAt)}</p>
                </div>
                <Badge tone="neutral">{request.priority}</Badge>
              </div>
            ))}
            {data.pendingServiceRequests.length === 0 ? (
              <p className="py-3 text-sm text-brand-300">No pending requests.</p>
            ) : null}
          </div>
        </Card>

        <Card>
          <h2 className="text-sm font-semibold text-brand-900">Open Quotations</h2>
          <div className="mt-4 divide-y divide-brand-100">
            {data.openQuotations.map((quotation) => (
              <div key={quotation.id} className="flex items-center justify-between py-3 text-sm">
                <p className="text-brand-400">{formatDate(quotation.createdAt)}</p>
                <div className="flex items-center gap-3">
                  <span className="text-brand-500">{formatCurrency(quotation.totalAmount)}</span>
                  <Badge tone="accent">{quotation.status}</Badge>
                </div>
              </div>
            ))}
            {data.openQuotations.length === 0 ? (
              <p className="py-3 text-sm text-brand-300">No open quotations.</p>
            ) : null}
          </div>
        </Card>
      </div>
    </div>
  );
}
