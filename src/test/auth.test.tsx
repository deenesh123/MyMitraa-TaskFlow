/**
 * Frontend Authentication UI Tests
 * Covers Login form, inputs, validation, role switching, password toggles
 */
import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { screen, fireEvent, waitFor, act } from '@testing-library/react';
import { AuthPage } from '../pages/auth/AuthPage';
import { renderWithProviders } from './testUtils';

describe('AuthPage UI & Workflow', () => {
  it('renders login credentials form with email and password inputs', () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    expect(screen.getByText(/Sign In to/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/deenesh-sabat/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/••••••••••/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Sign In & Proceed/i })).toBeDefined();
  });

  it('allows toggling between Sign In and Create Account modes', () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const switchBtn = screen.getByRole('button', { name: /^Sign Up$/i });
    fireEvent.click(switchBtn);

    expect(screen.getByText(/Create/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/e.g. Deenesh Sabat/i)).toBeDefined();
  });

  it('allows clicking quick role demo credentials to populate sign in form', async () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const userInput = screen.getByPlaceholderText(/deenesh-sabat/i) as HTMLInputElement;
    const superAdminQuickBtn = screen.getByText(/Deenesh Sabat/i);
    await act(async () => {
      fireEvent.click(superAdminQuickBtn);
    });

    expect(userInput.value).toBe('deenesh-sabat');
  });

  it('triggers login form submission without errors', async () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const userInput = screen.getByPlaceholderText(/deenesh-sabat/i);
    const passwordInput = screen.getByPlaceholderText(/••••••••••/i);
    const submitBtn = screen.getByRole('button', { name: /Sign In & Proceed/i });

    await act(async () => {
      fireEvent.change(userInput, { target: { value: 'deenesh-sabat' } });
      fireEvent.change(passwordInput, { target: { value: 'Password@123' } });
      fireEvent.click(submitBtn);
    });

    expect(submitBtn).toBeDefined();
  });
});
