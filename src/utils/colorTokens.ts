/**
 * MiTRAA TaskFlow 5-Colour Palette & Functional Design Tokens
 * 
 * 1. Violet    (#7B3AEC) - Brand colour, active menu, focus ring, Open status, primary chart series
 * 2. Fuchsia   (#D946EF) - Accent gradient, On Hold status, Holiday
 * 3. Lime      (#A3D801) - Closed status, Approved WFH, progress fill, positive success
 * 4. Sky Blue  (#00A6FC) - In Progress status, secondary chart series, info badges
 * 5. Charcoal  (#191627) - Text, dark primary pill buttons (Create New Task), sidebar mascot card
 * 
 * Functional colors:
 * - Amber      (#D97706) - Overdue, Unplanned Leave, warning alerts
 * - Red        (#DC2626) - Critical P0 priority, destructive actions
 */

export const BRAND_COLORS = {
  violet: '#7B3AEC',
  fuchsia: '#D946EF',
  lime: '#A3D801',
  skyblue: '#00A6FC',
  charcoal: '#191627',
  amber: '#D97706',
  red: '#DC2626',
} as const;

export type TaskStatus = 'Open' | 'In Progress' | 'Closed' | 'On Hold' | 'Overdue';
export type PriorityLevel = 'Critical P0' | 'Urgent P1' | 'Normal P2' | 'Low P3';
export type LeaveType = 'Leave' | 'Unplanned Leave' | 'WFH (Approved)' | 'WFH (Unapproved)' | 'Holiday';

export function getStatusStyle(status: string): { bg: string; text: string; border: string; dot: string; semantic: 'green' | 'orange' | 'red' } {
  const normalized = (status || '').replace(/_/g, ' ').trim().toLowerCase();

  // GREEN: Completed, Active, Success, Healthy, Approved, Closed, Resolved
  if (
    normalized === 'completed' ||
    normalized === 'closed' ||
    normalized === 'active' ||
    normalized === 'success' ||
    normalized === 'healthy' ||
    normalized === 'approved' ||
    normalized === 'resolved' ||
    normalized === 'done'
  ) {
    return {
      bg: 'bg-emerald-50 dark:bg-emerald-950/30',
      text: 'text-emerald-800 dark:text-emerald-300',
      border: 'border-emerald-300 dark:border-emerald-700/50',
      dot: 'bg-emerald-600 dark:bg-emerald-400',
      semantic: 'green',
    };
  }

  // RED: Overdue, Failed, Rejected, Blocked, Critical, Error, Cancelled
  if (
    normalized === 'overdue' ||
    normalized === 'failed' ||
    normalized === 'rejected' ||
    normalized === 'blocked' ||
    normalized === 'critical' ||
    normalized === 'error' ||
    normalized === 'cancelled' ||
    normalized.includes('fail')
  ) {
    return {
      bg: 'bg-rose-50 dark:bg-rose-950/30',
      text: 'text-rose-800 dark:text-rose-300',
      border: 'border-rose-300 dark:border-rose-700/50',
      dot: 'bg-rose-600 dark:bg-rose-400',
      semantic: 'red',
    };
  }

  // ORANGE: Pending, In Progress, Waiting, Warning, Requires Attention, Open, On Hold, Scheduled (Default)
  return {
    bg: 'bg-amber-50 dark:bg-amber-950/30',
    text: 'text-amber-900 dark:text-amber-300',
    border: 'border-amber-300 dark:border-amber-700/50',
    dot: 'bg-amber-500 dark:bg-amber-400',
    semantic: 'orange',
  };
}

export function getPriorityStyle(priority: string): { bg: string; text: string; border: string } {
  const p = priority.toLowerCase();
  if (p.includes('p0') || p.includes('critical')) {
    return { bg: 'bg-red-50', text: 'text-red-700', border: 'border-red-200' };
  }
  if (p.includes('p1') || p.includes('urgent')) {
    return { bg: 'bg-fuchsia-50', text: 'text-fuchsia-700', border: 'border-fuchsia-200' };
  }
  if (p.includes('p2') || p.includes('normal')) {
    return { bg: 'bg-sky-50', text: 'text-sky-700', border: 'border-sky-200' };
  }
  // Low P3
  return { bg: 'bg-slate-100', text: 'text-slate-600', border: 'border-slate-200' };
}

export function getLeaveTypeStyle(type: string): { bg: string; text: string; border: string; borderStyle?: string } {
  switch (type) {
    case 'Leave':
    case 'Planned Leave':
      return { bg: 'bg-[#7B3AEC]/15', text: 'text-[#5B21B6]', border: 'border-[#7B3AEC]' };
    case 'Unplanned Leave':
      return { bg: 'bg-amber-100', text: 'text-amber-900', border: 'border-amber-500' };
    case 'WFH (Approved)':
      return { bg: 'bg-[#A3D801]/25', text: 'text-[#365314]', border: 'border-[#84B500]' };
    case 'WFH (Unapproved)':
      return { bg: 'bg-[#00A6FC]/15', text: 'text-[#0369A1]', border: 'border-[#00A6FC] border-dashed', borderStyle: 'dashed' };
    case 'Holiday':
      return { bg: 'bg-[#D946EF]/20', text: 'text-[#86198F]', border: 'border-[#D946EF]' };
    default:
      return { bg: 'bg-slate-100', text: 'text-slate-700', border: 'border-slate-300' };
  }
}
