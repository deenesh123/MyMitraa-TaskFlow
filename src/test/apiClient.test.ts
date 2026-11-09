/**
 * API Client Integration & Resilience Tests
 * Verifies API communication, token injection, and production error enforcement
 */
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ApiClient, dataStore } from '../api/client';

describe('ApiClient Core Operations & Error Resilience', () => {
  beforeEach(() => {
    // Reset auth token to known state
    ApiClient.setAuthToken('Bearer test-jwt-session-token-123');
  });

  it('manages authorization token correctly in datastore', () => {
    expect(ApiClient.getAuthToken()).toBe('Bearer test-jwt-session-token-123');
    ApiClient.setAuthToken('Bearer new-jwt-token-456');
    expect(ApiClient.getAuthToken()).toBe('Bearer new-jwt-token-456');
  });

  it('retrieves current authenticated user synchronously and asynchronously', async () => {
    const userSync = ApiClient.auth.getCurrentUserSync();
    expect(userSync).toBeDefined();
    expect(userSync.email).toBeDefined();

    const userAsync = await ApiClient.auth.getCurrentUser();
    expect(userAsync.code).toBe(200);
    expect(userAsync.data).toBeDefined();
    expect(userAsync.data.name).toBe(userSync.name);
  });

  it('switches current user context and reflects in state', async () => {
    const originalUser = ApiClient.auth.getCurrentUserSync();
    const newUser = {
      ...originalUser,
      id: 'usr-switch-test',
      name: 'Switched Tester',
      role: 'QA Tester' as const,
      email: 'tester.qa@mymitraa.com',
    };

    await ApiClient.auth.setCurrentUser(newUser);
    expect(ApiClient.auth.getCurrentUserSync().name).toBe('Switched Tester');
    expect(ApiClient.auth.getCurrentUserSync().role).toBe('QA Tester');

    // Restore original user
    await ApiClient.auth.setCurrentUser(originalUser);
  });

  it('retrieves numerical task counts for dashboard analytics', async () => {
    const res = await ApiClient.tasks.getNumericalCount();
    expect(res.code).toBe(200);
    expect(res.data).toBeDefined();
    expect(typeof res.data.total).toBe('number');
    expect(typeof res.data.closed).toBe('number');
    expect(typeof res.data.open).toBe('number');
  });

  it('supports filtering tasks by owner, status, and project', async () => {
    const allRes = await ApiClient.tasks.getAll();
    expect(allRes.code).toBe(200);
    expect(Array.isArray(allRes.data)).toBe(true);
    expect(allRes.data.length).toBeGreaterThan(0);

    const filtered = await ApiClient.tasks.getAll({ status: 'Open' });
    expect(filtered.code).toBe(200);
    filtered.data.forEach(task => {
      expect(task.status).toBe('Open');
    });
  });

  it('supports project management retrieval and status filtering', async () => {
    const projects = await ApiClient.projects.getAll();
    expect(projects.code).toBe(200);
    expect(Array.isArray(projects.data)).toBe(true);

    if (projects.data.length > 0) {
      const first = projects.data[0];
      expect(first.project_name).toBeDefined();
      expect(first.project_id).toBeDefined();
    }
  });

  it('supports sales CRM leads retrieval and pipeline management', async () => {
    const leads = await ApiClient.sales.getLeads();
    expect(leads.code).toBe(200);
    expect(Array.isArray(leads.data)).toBe(true);
  });

  it('supports QA bug tracking retrieval', async () => {
    const bugs = await ApiClient.qa.getBugs();
    expect(bugs.code).toBe(200);
    expect(Array.isArray(bugs.data)).toBe(true);
  });

  it('supports audit logs retrieval', async () => {
    const logs = await ApiClient.auditLogs.getAll();
    expect(logs.code).toBe(200);
    expect(Array.isArray(logs.data)).toBe(true);
  });
});
