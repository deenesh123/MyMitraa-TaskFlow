import React from 'react';
import { getStatusStyle, getPriorityStyle, getLeaveTypeStyle } from '../../utils/colorTokens';

interface StatusBadgeProps {
  status: string;
  type?: 'status' | 'priority' | 'leave';
  className?: string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  type = 'status',
  className = '',
  size = 'md'
}) => {
  if (type === 'priority') {
    const style = getPriorityStyle(status);
    return (
      <span
        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${style.bg} ${style.text} ${style.border} ${className}`}
      >
        {status}
      </span>
    );
  }

  if (type === 'leave') {
    const style = getLeaveTypeStyle(status);
    return (
      <span
        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${style.bg} ${style.text} ${style.border} ${className}`}
      >
        {status}
      </span>
    );
  }

  // Default: task status
  const normalized = status.replace('_', ' ');
  const style = getStatusStyle(status);
  const sizeClasses = size === 'sm' ? 'px-2 py-0.5 text-[11px]' : 'px-2.5 py-1 text-xs';

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full font-medium border ${sizeClasses} ${style.bg} ${style.text} ${style.border} ${className}`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${style.dot}`} />
      <span>{normalized}</span>
    </span>
  );
};
