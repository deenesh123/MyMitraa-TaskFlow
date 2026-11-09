/**
 * Test Utilities for React Testing Library
 * Provides ThemeProvider and ToastProvider wrappers
 */
import React, { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { ThemeProvider } from '../context/ThemeContext';
import { ToastProvider } from '../components/common/Toast';

export function renderWithProviders(
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>
) {
  return render(
    <ThemeProvider>
      <ToastProvider>
        {ui}
      </ToastProvider>
    </ThemeProvider>,
    options
  );
}
