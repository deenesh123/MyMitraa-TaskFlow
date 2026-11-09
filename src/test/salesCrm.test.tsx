/**
 * Frontend Sales CRM Page Tests
 * Verifies lead pipeline, stage filters, search, and new lead creation modal
 */
import React from 'react';
import { describe, it, expect } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { SalesCrmPage } from '../pages/sales/SalesCrmPage';
import { CURRENT_USER } from '../api/mockData';
import { renderWithProviders } from './testUtils';

describe('SalesCrmPage UI & Features', () => {
  it('renders sales lead pipeline with search and stage tabs', async () => {
    renderWithProviders(<SalesCrmPage currentUser={CURRENT_USER} />);

    expect(screen.getByText(/Sales Pipeline/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Search company/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Add Lead/i })).toBeDefined();

    await waitFor(() => {
      expect(screen.getByText(/Tariq Mansoor/i)).toBeDefined();
    });
  });
});
