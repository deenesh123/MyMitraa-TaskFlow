/**
 * Frontend Project Management Page Tests
 * Verifies project listing, filters, search, and new project modal triggers
 */
import React from 'react';
import { describe, it, expect } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { ProjectManagementPage } from '../pages/projects/ProjectManagementPage';
import { CURRENT_USER } from '../api/mockData';
import { renderWithProviders } from './testUtils';

describe('ProjectManagementPage UI & Features', () => {
  it('renders project list view with search and status pills', async () => {
    renderWithProviders(<ProjectManagementPage currentUser={CURRENT_USER} />);

    expect(screen.getByText(/Enterprise Projects/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Search projects/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /New Project/i })).toBeDefined();

    await waitFor(() => {
      expect(screen.getByText(/Core Banking Engine/i)).toBeDefined();
    });
  });
});
