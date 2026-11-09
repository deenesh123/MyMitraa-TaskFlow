/**
 * Frontend User Management Page Tests
 * Verifies user directory listing, filters, search, and action triggers
 */
import React from 'react';
import { describe, it, expect } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { UserManagementPage } from '../pages/admin/UserManagementPage';
import { renderWithProviders } from './testUtils';

describe('UserManagementPage UI & Features', () => {
  it('renders user directory table with headers and add user button', async () => {
    renderWithProviders(<UserManagementPage />);

    expect(screen.getByText(/User Directory & Access Control/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Search by name or email/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Add New User/i })).toBeDefined();

    await waitFor(() => {
      expect(screen.getByText(/Deenesh Sabat/i)).toBeDefined();
    });
  });

  it('renders filter controls for Role, Team, and Status', () => {
    renderWithProviders(<UserManagementPage />);

    const comboboxes = screen.getAllByRole('combobox');
    expect(comboboxes.length).toBeGreaterThanOrEqual(3);
  });
});
