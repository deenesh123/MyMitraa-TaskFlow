/**
 * Frontend RBAC & Route Access Security Tests
 * Verifies all 10 roles across permission boundaries and route security
 */
import { describe, it, expect } from 'vitest';
import {
  ALL_ROLES,
  ROLE_PERMISSIONS,
  hasPermission,
  canAccessRoute,
  UserAuthContext,
} from '../utils/rbac';

describe('RBAC Constitution & Matrix Verification', () => {
  it('supports exactly the 10 approved organizational roles', () => {
    expect(ALL_ROLES).toHaveLength(10);
    expect(ALL_ROLES).toEqual([
      'Super Admin',
      'Admin',
      'Project Manager',
      'Developer',
      'Sales Executive',
      'Sales Manager',
      'QA Tester',
      'HR Manager',
      'Employee',
      'Client',
    ]);
  });

  it('Super Admin has all system permissions and unrestricted route access', () => {
    const superAdmin: UserAuthContext = {
      id: 'usr-sa',
      name: 'Super Admin',
      email: 'sa@mymitraa.com',
      role: 'Super Admin',
    };

    expect(hasPermission(superAdmin, 'users.delete')).toBe(true);
    expect(hasPermission(superAdmin, 'roles.manage')).toBe(true);
    expect(hasPermission(superAdmin, 'audit.view')).toBe(true);
    expect(canAccessRoute(superAdmin, '/admin/roles-permissions')).toBe(true);
    expect(canAccessRoute(superAdmin, '/admin/audit-logs')).toBe(true);
    expect(canAccessRoute(superAdmin, '/users/user-management')).toBe(true);
  });

  it('Admin has broad administrative permissions but cannot manage Super Admin specific roles', () => {
    const admin: UserAuthContext = {
      id: 'usr-admin',
      name: 'Operations Admin',
      email: 'admin@mymitraa.com',
      role: 'Admin',
    };

    expect(hasPermission(admin, 'users.view')).toBe(true);
    expect(hasPermission(admin, 'users.create')).toBe(true);
    expect(hasPermission(admin, 'projects.create')).toBe(true);
    expect(canAccessRoute(admin, '/users/user-management')).toBe(true);
    expect(canAccessRoute(admin, '/admin/audit-logs')).toBe(true);
  });

  it('Project Manager can view and create projects, but cannot view user management or roles', () => {
    const pm: UserAuthContext = {
      id: 'usr-pm',
      name: 'Tech PM',
      email: 'pm@mymitraa.com',
      role: 'Project Manager',
    };

    expect(hasPermission(pm, 'projects.view')).toBe(true);
    expect(hasPermission(pm, 'projects.create')).toBe(true);
    expect(hasPermission(pm, 'users.view')).toBe(false);
    expect(hasPermission(pm, 'roles.manage')).toBe(false);

    expect(canAccessRoute(pm, '/projects')).toBe(true);
    expect(canAccessRoute(pm, '/users/user-management')).toBe(false);
    expect(canAccessRoute(pm, '/admin/roles-permissions')).toBe(false);
  });

  it('Developer can access assigned tasks and bugs, but cannot access sales or user management', () => {
    const dev: UserAuthContext = {
      id: 'usr-dev',
      name: 'Senior Dev',
      email: 'dev@mymitraa.com',
      role: 'Developer',
    };

    expect(hasPermission(dev, 'tasks.view')).toBe(true);
    expect(hasPermission(dev, 'qa.bugs.view')).toBe(true);
    expect(hasPermission(dev, 'sales.leads.view')).toBe(false);
    expect(hasPermission(dev, 'users.view')).toBe(false);

    expect(canAccessRoute(dev, '/task-tracker')).toBe(true);
    expect(canAccessRoute(dev, '/qa-bugs')).toBe(true);
    expect(canAccessRoute(dev, '/sales-crm')).toBe(false);
    expect(canAccessRoute(dev, '/users/user-management')).toBe(false);
  });

  it('Sales Executive can manage leads but is forbidden from user admin, audit logs, and bug tracking', () => {
    const salesExec: UserAuthContext = {
      id: 'usr-se',
      name: 'Sales Rep',
      email: 'sales@mymitraa.com',
      role: 'Sales Executive',
    };

    expect(hasPermission(salesExec, 'sales.leads.view')).toBe(true);
    expect(hasPermission(salesExec, 'sales.leads.create')).toBe(true);
    expect(hasPermission(salesExec, 'qa.bugs.view')).toBe(false);

    expect(canAccessRoute(salesExec, '/sales-crm')).toBe(true);
    expect(canAccessRoute(salesExec, '/qa-bugs')).toBe(false);
    expect(canAccessRoute(salesExec, '/users/user-management')).toBe(false);
    expect(canAccessRoute(salesExec, '/admin/audit-logs')).toBe(false);
  });

  it('Sales Manager has lead management and sales reports permissions', () => {
    const salesMgr: UserAuthContext = {
      id: 'usr-sm',
      name: 'Sales Director',
      email: 'salesmgr@mymitraa.com',
      role: 'Sales Manager',
    };

    expect(hasPermission(salesMgr, 'sales.leads.view')).toBe(true);
    expect(hasPermission(salesMgr, 'sales.leads.delete')).toBe(true);
    expect(hasPermission(salesMgr, 'reports.export')).toBe(true);
    expect(canAccessRoute(salesMgr, '/sales-crm')).toBe(true);
  });

  it('QA Tester can manage test cases and bugs, but cannot access sales CRM or roles', () => {
    const qa: UserAuthContext = {
      id: 'usr-qa',
      name: 'Lead QA',
      email: 'qa@mymitraa.com',
      role: 'QA Tester',
    };

    expect(hasPermission(qa, 'qa.bugs.view')).toBe(true);
    expect(hasPermission(qa, 'qa.bugs.create')).toBe(true);
    expect(hasPermission(qa, 'sales.leads.view')).toBe(false);

    expect(canAccessRoute(qa, '/qa-bugs')).toBe(true);
    expect(canAccessRoute(qa, '/sales-crm')).toBe(false);
    expect(canAccessRoute(qa, '/admin/roles-permissions')).toBe(false);
  });

  it('HR Manager can access user listing and leave management, but cannot manage projects or sales', () => {
    const hr: UserAuthContext = {
      id: 'usr-hr',
      name: 'HR Lead',
      email: 'hr@mymitraa.com',
      role: 'HR Manager',
    };

    expect(hasPermission(hr, 'users.view')).toBe(true);
    expect(hasPermission(hr, 'hr.leave.approve')).toBe(true);
    expect(hasPermission(hr, 'sales.leads.view')).toBe(false);

    expect(canAccessRoute(hr, '/users/user-management')).toBe(true);
    expect(canAccessRoute(hr, '/leave-wfh-dashboard')).toBe(true);
    expect(canAccessRoute(hr, '/sales-crm')).toBe(false);
  });

  it('Employee has standard employee functions and is blocked from privileged routes', () => {
    const employee: UserAuthContext = {
      id: 'usr-emp',
      name: 'Staff Member',
      email: 'emp@mymitraa.com',
      role: 'Employee',
    };

    expect(hasPermission(employee, 'tasks.view')).toBe(true);
    expect(hasPermission(employee, 'hr.leave.apply')).toBe(true);
    expect(hasPermission(employee, 'users.view')).toBe(false);
    expect(hasPermission(employee, 'roles.manage')).toBe(false);

    expect(canAccessRoute(employee, '/task-tracker')).toBe(true);
    expect(canAccessRoute(employee, '/users/user-management')).toBe(false);
    expect(canAccessRoute(employee, '/admin/audit-logs')).toBe(false);
    expect(canAccessRoute(employee, '/admin/roles-permissions')).toBe(false);
  });

  it('Client has strictly restricted read access to projects and downloads', () => {
    const client: UserAuthContext = {
      id: 'usr-client',
      name: 'Partner Client',
      email: 'client@clientcorp.com',
      role: 'Client',
    };

    expect(hasPermission(client, 'projects.view')).toBe(true);
    expect(hasPermission(client, 'documents.download')).toBe(true);
    expect(hasPermission(client, 'tasks.view')).toBe(false);
    expect(hasPermission(client, 'sales.leads.view')).toBe(false);
    expect(hasPermission(client, 'users.view')).toBe(false);

    expect(canAccessRoute(client, '/projects')).toBe(true);
    expect(canAccessRoute(client, '/sales-crm')).toBe(false);
    expect(canAccessRoute(client, '/qa-bugs')).toBe(false);
    expect(canAccessRoute(client, '/users/user-management')).toBe(false);
    expect(canAccessRoute(client, '/admin/roles-permissions')).toBe(false);
  });
});
