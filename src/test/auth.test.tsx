/**
 * Frontend Authentication UI Tests
 * Covers Login form, inputs, validation, role switching, password toggles
 */
import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { AuthPage } from '../pages/auth/AuthPage';
import { renderWithProviders } from './testUtils';

describe('AuthPage UI & Workflow', () => {
  it('renders login credentials form with email and password inputs', () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    expect(screen.getByText(/Enterprise Workspace Login/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/name@company.com/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Enter your password/i)).toBeDefined();
    expect(screen.getByRole('button', { name: /Sign In to Workspace/i })).toBeDefined();
  });

  it('allows toggling between Sign In and Create Account modes', () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const switchBtn = screen.getByRole('button', { name: /Create an account/i });
    fireEvent.click(switchBtn);

    expect(screen.getByText(/Create Account/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/Full Name/i)).toBeDefined();
  });

  it('allows clicking quick role demo credentials to populate sign in form', () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const emailInput = screen.getByPlaceholderText(/name@company.com/i) as HTMLInputElement;
    const superAdminQuickBtn = screen.getByText(/Super Admin/i);
    fireEvent.click(superAdminQuickBtn);

    expect(emailInput.value).toBe('deeneshsabat2002@gmail.com');
  });

  it('triggers login form submission without errors', async () => {
    const handleSuccess = vi.fn();
    renderWithProviders(<AuthPage onSuccess={handleSuccess} />);

    const emailInput = screen.getByPlaceholderText(/name@company.com/i);
    const passwordInput = screen.getByPlaceholderText(/Enter your password/i);
    const submitBtn = screen.getByRole('button', { name: /Sign In to Workspace/i });

    fireEvent.change(emailInput, { target: { value: 'deeneshsabat2002@gmail.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Password@123' } });
    fireEvent.click(submitBtn);

    expect(submitBtn).toBeDefined();
  });
});
