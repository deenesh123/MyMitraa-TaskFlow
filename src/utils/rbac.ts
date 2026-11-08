/**
 * Role-Based Access Control (RBAC) Architecture
 * Authoritative permission matrices and access control utilities for MyMitraa TaskFlow
 */

export type RoleType =
  | 'Super Admin'
  | 'Admin'
  | 'Project Manager'
  | 'Developer'
  | 'Sales Executive'
  | 'Sales Manager'
  | 'QA Tester'
  | 'HR Manager'
  | 'Employee'
  | 'Client';

export const ALL_ROLES: RoleType[] = [
  'Super Admin',
  'Admin',
  'Project Manager',
  'Developer',
  'Sales Executive',
  'Sales Manager',
  'QA Tester',
  'HR Manager',
  'Employee',
  'Client'
];

export type Permission =
  // User Management
  | 'users.view'
  | 'users.create'
  | 'users.update'
  | 'users.delete'
  | 'users.assign_role'
  // Roles & Security
  | 'roles.manage'
  | 'permissions.manage'
  // Projects
  | 'projects.view'
  | 'projects.create'
  | 'projects.update'
  | 'projects.delete'
  // Tasks
  | 'tasks.view'
  | 'tasks.create'
  | 'tasks.assign'
  | 'tasks.update'
  | 'tasks.delete'
  // Sales CRM
  | 'sales.leads.view'
  | 'sales.leads.create'
  | 'sales.leads.update'
  | 'sales.leads.delete'
  | 'sales.reports.view'
  // QA & Testing
  | 'qa.tests.view'
  | 'qa.tests.create'
  | 'qa.tests.execute'
  | 'qa.bugs.view'
  | 'qa.bugs.create'
  | 'qa.bugs.update'
  // HR & People
  | 'hr.employees.view'
  | 'hr.employees.manage'
  | 'hr.leave.view'
  | 'hr.leave.apply'
  | 'hr.leave.approve'
  | 'hr.attendance.manage'
  // Meetings & Calendar
  | 'meetings.view'
  | 'meetings.manage'
  // Reports & Analytics
  | 'reports.view'
  | 'reports.export'
  // Documents
  | 'documents.upload'
  | 'documents.download'
  // Audit Trail & Logs
  | 'audit.view'
  // Settings & Integrations
  | 'settings.manage'
  | 'email.manage';

export const ROLE_PERMISSIONS: Record<RoleType, Permission[]> = {
  'Super Admin': [
    'users.view',
    'users.create',
    'users.update',
    'users.delete',
    'users.assign_role',
    'roles.manage',
    'permissions.manage',
    'projects.view',
    'projects.create',
    'projects.update',
    'projects.delete',
    'tasks.view',
    'tasks.create',
    'tasks.assign',
    'tasks.update',
    'tasks.delete',
    'sales.leads.view',
    'sales.leads.create',
    'sales.leads.update',
    'sales.leads.delete',
    'sales.reports.view',
    'qa.tests.view',
    'qa.tests.create',
    'qa.tests.execute',
    'qa.bugs.view',
    'qa.bugs.create',
    'qa.bugs.update',
    'hr.employees.view',
    'hr.employees.manage',
    'hr.leave.view',
    'hr.leave.apply',
    'hr.leave.approve',
    'hr.attendance.manage',
    'meetings.view',
    'meetings.manage',
    'reports.view',
    'reports.export',
    'documents.upload',
    'documents.download',
    'audit.view',
    'settings.manage',
    'email.manage'
  ],

  Admin: [
    'users.view',
    'users.create',
    'users.update',
    'users.assign_role',
    'projects.view',
    'projects.create',
    'projects.update',
    'projects.delete',
    'tasks.view',
    'tasks.create',
    'tasks.assign',
    'tasks.update',
    'tasks.delete',
    'sales.leads.view',
    'sales.reports.view',
    'qa.tests.view',
    'qa.bugs.view',
    'hr.employees.view',
    'hr.employees.manage',
    'hr.leave.view',
    'hr.leave.apply',
    'hr.leave.approve',
    'hr.attendance.manage',
    'meetings.view',
    'meetings.manage',
    'reports.view',
    'reports.export',
    'documents.upload',
    'documents.download',
    'audit.view',
    'settings.manage',
    'email.manage'
  ],

  'Project Manager': [
    'projects.view',
    'projects.create',
    'projects.update',
    'tasks.view',
    'tasks.create',
    'tasks.assign',
    'tasks.update',
    'qa.tests.view',
    'qa.bugs.view',
    'qa.bugs.create',
    'qa.bugs.update',
    'meetings.view',
    'meetings.manage',
    'reports.view',
    'documents.upload',
    'documents.download'
  ],

  Developer: [
    'projects.view',
    'tasks.view',
    'tasks.update',
    'tasks.create',
    'qa.bugs.view',
    'qa.bugs.update',
    'meetings.view',
    'hr.leave.view',
    'hr.leave.apply',
    'documents.upload',
    'documents.download',
    'reports.view'
  ],

  'Sales Executive': [
    'sales.leads.view',
    'sales.leads.create',
    'sales.leads.update',
    'sales.reports.view',
    'meetings.view',
    'hr.leave.view',
    'hr.leave.apply',
    'documents.upload',
    'documents.download'
  ],

  'Sales Manager': [
    'sales.leads.view',
    'sales.leads.create',
    'sales.leads.update',
    'sales.leads.delete',
    'sales.reports.view',
    'reports.view',
    'reports.export',
    'meetings.view',
    'meetings.manage',
    'hr.leave.view',
    'hr.leave.apply',
    'documents.upload',
    'documents.download'
  ],

  'QA Tester': [
    'projects.view',
    'tasks.view',
    'qa.tests.view',
    'qa.tests.create',
    'qa.tests.execute',
    'qa.bugs.view',
    'qa.bugs.create',
    'qa.bugs.update',
    'meetings.view',
    'hr.leave.view',
    'hr.leave.apply',
    'reports.view',
    'documents.upload',
    'documents.download'
  ],

  'HR Manager': [
    'users.view',
    'hr.employees.view',
    'hr.employees.manage',
    'hr.leave.view',
    'hr.leave.approve',
    'hr.attendance.manage',
    'meetings.view',
    'meetings.manage',
    'reports.view',
    'reports.export',
    'documents.upload',
    'documents.download'
  ],

  Employee: [
    'tasks.view',
    'tasks.update',
    'meetings.view',
    'hr.leave.view',
    'hr.leave.apply',
    'documents.upload',
    'documents.download'
  ],

  Client: [
    'projects.view',
    'documents.download'
  ]
};

export interface UserAuthContext {
  id: string;
  name: string;
  email: string;
  role: RoleType;
  team?: string;
  department?: string;
  permissions?: string[];
}

/**
 * Check if a user possesses a specific permission either by role or individual override
 */
export function hasPermission(user: UserAuthContext | null | undefined, permission: Permission): boolean {
  if (!user) return false;
  // Super Admin always has full access
  if (user.role === 'Super Admin') return true;

  // Check explicit permission overrides first
  if (user.permissions && user.permissions.includes(permission)) {
    return true;
  }

  // Check role-based permissions
  const rolePerms = ROLE_PERMISSIONS[user.role] || [];
  return rolePerms.includes(permission);
}

/**
 * Check if a user possesses any of the specified permissions
 */
export function hasAnyPermission(user: UserAuthContext | null | undefined, permissions: Permission[]): boolean {
  if (!user) return false;
  if (user.role === 'Super Admin') return true;
  return permissions.some(p => hasPermission(user, p));
}

/**
 * Get route accessibility based on user role and permissions
 */
export function canAccessRoute(user: UserAuthContext | null | undefined, route: string): boolean {
  if (!user) return false;
  if (user.role === 'Super Admin') return true;

  switch (route) {
    case '/':
      return true; // Dashboard is role-tailored for all roles
    case '/task-tracker':
    case '/users/task-calendar-view':
    case '/users/timeline-view':
      return hasAnyPermission(user, ['tasks.view', 'tasks.create', 'tasks.update']);
    case '/projects':
      return hasPermission(user, 'projects.view');
    case '/sales-crm':
      return hasPermission(user, 'sales.leads.view');
    case '/qa-bugs':
      return hasAnyPermission(user, ['qa.tests.view', 'qa.bugs.view']);
    case '/meeting-tracker':
      return hasPermission(user, 'meetings.view');
    case '/leave-wfh-dashboard':
      return hasAnyPermission(user, ['hr.leave.view', 'hr.leave.apply']);
    case '/project-weekly-status':
      return hasPermission(user, 'projects.view');
    case '/task-upload-files':
      return hasAnyPermission(user, ['documents.upload', 'documents.download']);
    case '/task-dashboard':
      return hasPermission(user, 'reports.view');
    case '/users/user-management':
      return hasPermission(user, 'users.view');
    case '/admin/roles-permissions':
      return hasPermission(user, 'roles.manage');
    case '/admin/audit-logs':
      return hasPermission(user, 'audit.view');
    case '/admin/email-zoho':
      return hasPermission(user, 'email.manage');
    case '/users/dropdown-management':
      return hasPermission(user, 'settings.manage');
    case '/help-support':
      return true;
    default:
      return true;
  }
}
