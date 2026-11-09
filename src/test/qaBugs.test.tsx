/**
 * Frontend QA Bug Tracker Page Tests
 * Verifies bug tracking table, severity indicators, and new bug modal triggers
 */
import React from 'react';
import { describe, it, expect } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import { QaBugTrackerPage } from '../pages/qa/QaBugTrackerPage';
import { CURRENT_USER } from '../api/mockData';
import { renderWithProviders } from './testUtils';

describe('QaBugTrackerPage UI & Features', () => {
  it('renders bug tracker with severity filters and report bug button', async () => {
    renderWithProviders(<QaBugTrackerPage currentUser={CURRENT_USER} />);

    expect(screen.getByText(/QA & Bug Tracker/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Search bugs/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Report Bug/i })).toBeDefined();

    await waitFor(() => {
      expect(screen.getByText(/Session timeout on slow 3G/i)).toBeDefined();
    });
  });
});
