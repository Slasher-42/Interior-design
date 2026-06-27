export type TimeSeriesPoint = { period: string; value: number };

export type ClientSegmentSummary = {
  industry: string;
  country: string;
  city: string;
  total: number;
};

export type FeedbackSummary = {
  id: string;
  projectId: string;
  clientId: string;
  rating: number;
  submittedAt: string;
};

export type ProjectSummary = {
  id: string;
  clientId: string;
  status: string;
  approvedBudget: number;
  finalCost: number | null;
  createdAt: string;
  completedAt: string | null;
};

export type TaskSummary = {
  id: string;
  projectId: string;
  priority: string;
  status: string;
  deadline: string | null;
  completedAt: string | null;
};

export type ServiceRequestSummary = {
  id: string;
  clientId: string;
  category: string;
  priority: string;
  createdAt: string;
};

export type QuotationSummary = {
  id: string;
  clientId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
};

export type AdminDashboard = {
  totalUsers: number;
  userSignupTrend: TimeSeriesPoint[];
  totalClients: number;
  clientGrowthTrend: TimeSeriesPoint[];
  clientSegmentation: ClientSegmentSummary[];
  totalServiceRequests: number;
  requestsByCategory: Record<string, number>;
  requestsByPriority: Record<string, number>;
  pendingQuotationValue: number;
  convertedQuotationValue: number;
  activeProjects: number;
  completedProjects: number;
  averageProjectDurationDays: number | null;
  totalApprovedBudget: number;
  totalFinalCost: number;
  taskOnTrackPercentage: number;
  totalProcurementSpend: number;
  customerSatisfactionAverage: number | null;
  lowRatedFeedbackCount: number;
  lowRatedFeedback: FeedbackSummary[];
  revenueTrend: TimeSeriesPoint[];
  projectCompletionTrend: TimeSeriesPoint[];
  procurementCostTrend: TimeSeriesPoint[];
};

export type ProjectManagerDashboard = {
  totalProjects: number;
  activeProjects: number;
  completedProjects: number;
  projects: ProjectSummary[];
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
};

export type DesignerDashboard = {
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  tasks: TaskSummary[];
};

export type ClientDashboard = {
  projects: ProjectSummary[];
  feedbackHistory: FeedbackSummary[];
};

export type SalesDashboard = {
  pendingServiceRequestsCount: number;
  pendingServiceRequests: ServiceRequestSummary[];
  openQuotationsCount: number;
  openQuotationsValue: number;
  openQuotations: QuotationSummary[];
};
