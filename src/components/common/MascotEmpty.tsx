import React from 'react';
import { MascotPenguin } from '../mascot/MascotPenguin';

interface MascotEmptyProps {
  title?: string;
  description?: string;
  actionText?: string;
  onAction?: () => void;
  className?: string;
}

export const MascotEmpty: React.FC<MascotEmptyProps> = ({
  title = 'Data Not Available',
  description = 'No matching records found for the selected filters or date range.',
  actionText,
  onAction,
  className = ''
}) => {
  return (
    <div className={`flex flex-col items-center justify-center py-12 px-4 text-center ${className}`}>
      <div className="w-24 h-24 mb-3 flex items-center justify-center">
        <MascotPenguin pose="confused-empty" size={90} />
      </div>
      <h3 className="text-base font-semibold text-[#191627] mb-1">
        {title}
      </h3>
      <p className="text-xs text-slate-500 max-w-sm mb-4">
        {description}
      </p>
      {actionText && onAction && (
        <button
          type="button"
          onClick={onAction}
          className="inline-flex items-center gap-1.5 px-4 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-medium shadow-sm transition-colors"
        >
          {actionText}
        </button>
      )}
    </div>
  );
};
