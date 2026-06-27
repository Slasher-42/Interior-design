export const ROLES = ["ADMIN", "PROJECT_MANAGER", "DESIGNER", "SALES_TEAM", "CLIENT"] as const;

export type Role = (typeof ROLES)[number];

/** Roles a new user can self-select at registration - admin accounts are seeded/manual only. */
export const SELF_REGISTERABLE_ROLES: Role[] = ["PROJECT_MANAGER", "DESIGNER", "SALES_TEAM", "CLIENT"];

export const ROLE_LABELS: Record<Role, string> = {
  ADMIN: "Administrator",
  PROJECT_MANAGER: "Project Manager",
  DESIGNER: "Designer",
  SALES_TEAM: "Sales Team",
  CLIENT: "Client",
};

export function dashboardEndpointForRole(role: Role): string {
  switch (role) {
    case "ADMIN":
      return "/dashboard/admin";
    case "PROJECT_MANAGER":
      return "/dashboard/project-manager";
    case "DESIGNER":
      return "/dashboard/designer";
    case "SALES_TEAM":
      return "/dashboard/sales";
    case "CLIENT":
      return "/dashboard/client";
  }
}

export type NavItem = {
  label: string;
  href: string;
  roles: Role[];
  /** False until that module's pages are actually built - rendered disabled in the sidebar. */
  enabled: boolean;
};

export const NAV_ITEMS: NavItem[] = [
  { label: "Dashboard", href: "/dashboard", roles: [...ROLES], enabled: true },
  {
    label: "Clients",
    href: "/clients",
    roles: ["ADMIN", "PROJECT_MANAGER", "SALES_TEAM"],
    enabled: false,
  },
  {
    label: "Service Requests",
    href: "/requests",
    roles: ["ADMIN", "SALES_TEAM", "CLIENT"],
    enabled: false,
  },
  {
    label: "Quotations",
    href: "/quotations",
    roles: ["ADMIN", "SALES_TEAM", "CLIENT"],
    enabled: false,
  },
  {
    label: "Projects",
    href: "/projects",
    roles: ["ADMIN", "PROJECT_MANAGER", "DESIGNER", "CLIENT"],
    enabled: false,
  },
  {
    label: "Tasks",
    href: "/tasks",
    roles: ["ADMIN", "PROJECT_MANAGER", "DESIGNER"],
    enabled: false,
  },
  {
    label: "Documents",
    href: "/documents",
    roles: ["ADMIN", "PROJECT_MANAGER", "DESIGNER", "CLIENT"],
    enabled: false,
  },
  {
    label: "Vendors & Inventory",
    href: "/vendors",
    roles: ["ADMIN", "PROJECT_MANAGER"],
    enabled: false,
  },
  {
    label: "Feedback",
    href: "/feedback",
    roles: ["ADMIN", "PROJECT_MANAGER", "CLIENT"],
    enabled: false,
  },
  {
    label: "Notifications",
    href: "/notifications",
    roles: [...ROLES],
    enabled: false,
  },
  {
    label: "Reports & Analytics",
    href: "/reports",
    roles: ["ADMIN", "PROJECT_MANAGER", "SALES_TEAM"],
    enabled: false,
  },
  {
    label: "Admin",
    href: "/admin",
    roles: ["ADMIN"],
    enabled: false,
  },
];

export function navItemsForRole(role: Role): NavItem[] {
  return NAV_ITEMS.filter((item) => item.roles.includes(role));
}
