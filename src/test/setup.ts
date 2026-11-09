/**
 * Vitest Global Setup for MyMitraa TaskFlow Frontend
 */
import { afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';

// Automatically unmount React trees after each test run
afterEach(() => {
  cleanup();
});

// Polyfill window.scrollTo if undefined in jsdom
if (typeof window !== 'undefined' && !window.scrollTo) {
  window.scrollTo = () => {};
}
