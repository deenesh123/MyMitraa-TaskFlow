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

export function getStatusStyle(status: string): { bg: string; text: string; border: string; dot: string } {
  const normalized = status.replace('_', ' ').trim();
  switch (normalized.toLowerCase()) {
    case 'open':
      return {
        bg: 'bg-[#7B3AEC]/10',
        text: 'text-[#7B3AEC]',
        border: 'border-[#7B3AEC]/20',
        dot: 'bg-[#7B3AEC]',
      };
    case 'in progress':
      return {
        bg: 'bg-[#00A6FC]/10',
        text: 'text-[#0082CA]',
        border: 'border-[#00A6FC]/25',
        dot: 'bg-[#00A6FC]',
      };
    case 'closed':
    case 'completed':
      return {
        bg: 'bg-[#A3D801]/15',
        text: 'text-[#4D6B00]',
        border: 'border-[#A3D801]/30',
        dot: 'bg-[#A3D801]',
      };
    case 'on hold':
      return {
        bg: 'bg-[#D946EF]/10',
        text: 'text-[#B024C5]',
        border: 'border-[#D946EF]/25',
        dot: 'bg-[#D946EF]',
      };
    case 'overdue':
      return {
        bg: 'bg-amber-50',
        text: 'text-amber-800',
        border: 'border-amber-200',
        dot: 'bg-amber-500',
      };
    default:
      return {
        bg: 'bg-slate-100',
        text: 'text-slate-700',
        border: 'border-slate-200',
        dot: 'bg-slate-400',
      };
  }
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
