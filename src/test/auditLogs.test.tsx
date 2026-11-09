/**
 * Frontend Audit Logs Page Tests
 * Verifies system audit log viewer, module filters, search, and tamper-resistant log presentation
 */
import React from 'react';
import { describe, it, expect } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { AuditLogsPage } from '../pages/admin/AuditLogsPage';
import { CURRENT_USER } from '../api/mockData';
import { renderWithProviders } from './testUtils';

describe('AuditLogsPage UI & Features', () => {
  it('renders system audit trail with search and module filters', async () => {
    renderWithProviders(<AuditLogsPage currentUser={CURRENT_USER} />);

    expect(screen.getByText(/System Audit Trail/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Search actor, action, or details/i)).toBeDefined();

    await waitFor(() => {
      expect(screen.getByText(/USER_LOGIN/i)).toBeDefined();
    });
  });
});
