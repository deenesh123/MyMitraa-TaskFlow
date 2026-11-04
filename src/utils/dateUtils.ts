/**
 * Date formatting and parsing utilities for MiTRAA TaskFlow.
 * Enforces strict DD-Mon-YYYY format (e.g., 03-Sep-2026).
 */

const MONTH_NAMES = [
  'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
];

export function formatDateToDDMonYYYY(date: Date | string | number | null | undefined): string {
  if (!date) return '';
  const d = new Date(date);
  if (isNaN(d.getTime())) return String(date);

  const day = String(d.getDate()).padStart(2, '0');
  const month = MONTH_NAMES[d.getMonth()];
  const year = d.getFullYear();
  return `${day}-${month}-${year}`;
}

export function parseDDMonYYYY(str: string): Date | null {
  if (!str) return null;
  const parts = str.trim().split(/[-/ ]/);
  if (parts.length !== 3) return new Date(str);

  const day = parseInt(parts[0], 10);
  const monthStr = parts[1].toLowerCase();
  const year = parseInt(parts[2], 10);

  const monthIndex = MONTH_NAMES.findIndex(m => m.toLowerCase() === monthStr);
  if (monthIndex === -1 || isNaN(day) || isNaN(year)) {
    const fallback = new Date(str);
    return isNaN(fallback.getTime()) ? null : fallback;
  }

  return new Date(year, monthIndex, day);
}

export function formatTimeRange(start: string, end: string): string {
  return `${start} – ${end}`;
}

export function formatDurationMinutes(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours > 0 && mins > 0) return `${hours}h ${mins}m`;
  if (hours > 0) return `${hours}h`;
  return `${mins}m`;
}
