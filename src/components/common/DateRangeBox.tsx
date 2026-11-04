import React, { useState, useRef, useEffect } from 'react';
import { Calendar as CalendarIcon, ChevronDown, Check } from 'lucide-react';

interface DateRangeBoxProps {
  startDate: string; // DD-Mon-YYYY
  endDate: string;   // DD-Mon-YYYY
  onChange?: (startDate: string, endDate: string) => void;
  className?: string;
}

export const DateRangeBox: React.FC<DateRangeBoxProps> = ({
  startDate,
  endDate,
  onChange,
  className = ''
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [tempStart, setTempStart] = useState(startDate);
  const [tempEnd, setTempEnd] = useState(endDate);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setTempStart(startDate);
    setTempEnd(endDate);
  }, [startDate, endDate]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleApply = () => {
    if (onChange) {
      onChange(tempStart, tempEnd);
    }
    setIsOpen(false);
  };

  const handlePreset = (preset: 'oct' | 'sep' | 'q4' | 'ytd') => {
    if (preset === 'oct') {
      setTempStart('01-Oct-2026');
      setTempEnd('31-Oct-2026');
      if (onChange) onChange('01-Oct-2026', '31-Oct-2026');
    } else if (preset === 'sep') {
      setTempStart('01-Sep-2026');
      setTempEnd('30-Sep-2026');
      if (onChange) onChange('01-Sep-2026', '30-Sep-2026');
    } else if (preset === 'q4') {
      setTempStart('01-Oct-2026');
      setTempEnd('31-Dec-2026');
      if (onChange) onChange('01-Oct-2026', '31-Dec-2026');
    } else if (preset === 'ytd') {
      setTempStart('01-Jan-2026');
      setTempEnd('31-Oct-2026');
      if (onChange) onChange('01-Jan-2026', '31-Oct-2026');
    }
    setIsOpen(false);
  };

  return (
    <div className={`relative inline-block ${className}`} ref={ref}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2.5 px-3.5 py-2 bg-white hover:bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 shadow-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#7B3AEC]/30"
      >
        <CalendarIcon className="w-3.5 h-3.5 text-slate-400" />
        <span className="font-mono tracking-tight text-slate-700">
          {startDate} – {endDate}
        </span>
        <ChevronDown className="w-3.5 h-3.5 text-slate-400" />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 z-50 w-72 p-3 bg-white border border-slate-200 rounded-2xl shadow-xl space-y-3">
          <div className="text-xs font-semibold text-slate-800 pb-1 border-b border-slate-100 flex items-center justify-between">
            <span>Select Date Range</span>
            <span className="text-[10px] text-slate-400 uppercase tracking-wider">DD-Mon-YYYY</span>
          </div>

          <div className="grid grid-cols-2 gap-2 text-xs">
            <div>
              <label className="text-[11px] text-slate-500 mb-1 block">From</label>
              <input
                type="text"
                value={tempStart}
                onChange={e => setTempStart(e.target.value)}
                placeholder="01-Oct-2026"
                className="w-full px-2.5 py-1.5 border border-slate-200 rounded-lg text-xs font-mono text-slate-800 focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>
            <div>
              <label className="text-[11px] text-slate-500 mb-1 block">To</label>
              <input
                type="text"
                value={tempEnd}
                onChange={e => setTempEnd(e.target.value)}
                placeholder="31-Oct-2026"
                className="w-full px-2.5 py-1.5 border border-slate-200 rounded-lg text-xs font-mono text-slate-800 focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>
          </div>

          <div className="space-y-1">
            <span className="text-[10px] text-slate-400 uppercase tracking-wider font-semibold">Quick Presets</span>
            <div className="grid grid-cols-2 gap-1.5">
              <button
                type="button"
                onClick={() => handlePreset('oct')}
                className="text-left px-2 py-1 text-xs text-slate-600 hover:bg-slate-50 rounded"
              >
                Oct 2026 (Current)
              </button>
              <button
                type="button"
                onClick={() => handlePreset('sep')}
                className="text-left px-2 py-1 text-xs text-slate-600 hover:bg-slate-50 rounded"
              >
                Sep 2026 (Previous)
              </button>
              <button
                type="button"
                onClick={() => handlePreset('q4')}
                className="text-left px-2 py-1 text-xs text-slate-600 hover:bg-slate-50 rounded"
              >
                Q4 2026
              </button>
              <button
                type="button"
                onClick={() => handlePreset('ytd')}
                className="text-left px-2 py-1 text-xs text-slate-600 hover:bg-slate-50 rounded"
              >
                Year to Date
              </button>
            </div>
          </div>

          <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsOpen(false)}
              className="px-2.5 py-1 text-xs text-slate-500 hover:text-slate-700"
            >
              Cancel
            </button>
            <button
              type="button"
              onClick={handleApply}
              className="inline-flex items-center gap-1 px-3 py-1 bg-[#191627] hover:bg-[#2b2740] text-white rounded-lg text-xs font-medium"
            >
              <Check className="w-3 h-3" />
              Apply
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
