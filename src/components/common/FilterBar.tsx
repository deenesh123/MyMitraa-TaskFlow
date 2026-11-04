import React, { ReactNode } from 'react';
import { Search, RotateCcw, Download } from 'lucide-react';

interface FilterBarProps {
  children: ReactNode;
  onSearch?: () => void;
  onClear?: () => void;
  showClear?: boolean;
  onDownload?: () => void;
  downloadTooltip?: string;
  className?: string;
}

export const FilterBar: React.FC<FilterBarProps> = ({
  children,
  onSearch,
  onClear,
  showClear = false,
  onDownload,
  downloadTooltip = 'Download export',
  className = ''
}) => {
  return (
    <div
      className={`bg-white p-4 rounded-3xl border border-slate-200/80 shadow-xs flex flex-wrap items-center justify-between gap-3 ${className}`}
    >
      {/* Filter items row (wraps smoothly without truncating labels) */}
      <div className="flex flex-wrap items-center gap-3 flex-1 min-w-[280px]">
        {children}

        {/* Search button */}
        {onSearch && (
          <button
            type="button"
            onClick={onSearch}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-[#7B3AEC] hover:bg-[#6828d9] text-white rounded-xl text-xs font-semibold shadow-xs transition-colors shrink-0"
          >
            <Search className="w-3.5 h-3.5" />
            <span>Search</span>
          </button>
        )}

        {/* Clear Filters (shows when >3 filters or explicitly enabled) */}
        {showClear && onClear && (
          <button
            type="button"
            onClick={onClear}
            className="inline-flex items-center gap-1 px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-xl text-xs font-medium transition-colors shrink-0"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            <span>Clear Filters</span>
          </button>
        )}
      </div>

      {/* Optional Download button icon at far right */}
      {onDownload && (
        <div className="flex items-center shrink-0 pl-2 border-l border-slate-100">
          <button
            type="button"
            onClick={onDownload}
            title={downloadTooltip}
            className="p-2.5 bg-slate-50 hover:bg-[#7B3AEC]/10 text-slate-700 hover:text-[#7B3AEC] border border-slate-200 rounded-xl transition-colors"
          >
            <Download className="w-4 h-4" />
          </button>
        </div>
      )}
    </div>
  );
};
