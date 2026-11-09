/**
 * Frontend Navigation & Sidebar RBAC Tests
 * Verifies role-tailored sidebar links and topbar rendering for different roles
 */
import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { screen } from '@testing-library/react';
import { Sidebar } from '../components/layout/Sidebar';
import { TopBar } from '../components/layout/TopBar';
import { UserAccount } from '../api/types';
import { renderWithProviders } from './testUtils';

describe('Sidebar & Navigation RBAC Rendering', () => {
  const superAdminUser: UserAccount = {
    id: 'usr-1',
    name: 'Deenesh Sabat',
    email: 'deeneshsabat2002@gmail.com',
    role: 'Super Admin',
    team: 'Management',
    status: 'Active',
    avatar: 'DS',
    created_date: '01-Jan-2026',
    last_login: 'Just now',
  };

  const clientUser: UserAccount = {
    id: 'usr-client',
    name: 'External Client',
    email: 'client@partner.com',
    role: 'Client',
    team: 'External',
    status: 'Active',
    avatar: 'EC',
    created_date: '01-Jan-2026',
    last_login: 'Yesterday',
  };

  it('renders full navigation menu items for Super Admin', () => {
    const handleNavigate = vi.fn();
    renderWithProviders(
      <Sidebar
        currentRoute="/"
        onNavigate={handleNavigate}
        openTasksCount={12}
        currentUser={superAdminUser}
        collapsed={false}
        onToggleCollapse={vi.fn()}
      />
    );

    expect(screen.getByText(/Dashboard/i)).toBeDefined();
    expect(screen.getByText(/User Management/i)).toBeDefined();
    expect(screen.getByText(/Audit Trail/i)).toBeDefined();
    expect(screen.getByText(/Roles & Permissions/i)).toBeDefined();
  });

  it('hides administrative and privileged routes from Client role', () => {
    const handleNavigate = vi.fn();
    renderWithProviders(
      <Sidebar
        currentRoute="/"
        onNavigate={handleNavigate}
        openTasksCount={0}
        currentUser={clientUser}
        collapsed={false}
        onToggleCollapse={vi.fn()}
      />
    );

    expect(screen.getByText(/Projects/i)).toBeDefined();
    expect(screen.queryByText(/User Management/i)).toBeNull();
    expect(screen.queryByText(/Audit Trail/i)).toBeNull();
    expect(screen.queryByText(/Roles & Permissions/i)).toBeNull();
  });

  it('renders TopBar with active user information and role badge', () => {
    const handleNavigate = vi.fn();
    const handleSignOut = vi.fn();
    const handleSwitch = vi.fn();
    const handleOpenSearch = vi.fn();

    renderWithProviders(
      <TopBar
        onOpenSearch={handleOpenSearch}
        onNavigate={handleNavigate}
        currentUser={superAdminUser}
        onSignOut={handleSignOut}
        onSwitchUser={handleSwitch}
      />
    );

    expect(screen.getByText(/Deenesh Sabat/i)).toBeDefined();
    expect(screen.getAllByText(/Super Admin/i).length).toBeGreaterThan(0);
  });
});
