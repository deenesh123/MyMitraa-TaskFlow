import React, { useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { MascotEmpty } from './MascotEmpty';

export interface Column<T> {
  key: string;
  header: string;
  render?: (row: T, index: number) => React.ReactNode;
  align?: 'left' | 'center' | 'right';
  width?: string;
  className?: string;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  defaultPageSize?: 10 | 25 | 100;
  emptyTitle?: string;
  emptyDescription?: string;
  onEmptyAction?: () => void;
  emptyActionText?: string;
  keyExtractor: (item: T, index: number) => string;
  className?: string;
}

export function DataTable<T>({
  columns,
  data,
  defaultPageSize = 25,
  emptyTitle,
  emptyDescription,
  onEmptyAction,
  emptyActionText,
  keyExtractor,
  className = ''
}: DataTableProps<T>) {
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [currentPage, setCurrentPage] = useState<number>(1);

  const totalItems = data.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const validPage = Math.min(currentPage, totalPages);

  const startIndex = (validPage - 1) * pageSize;
  const endIndex = Math.min(startIndex + pageSize, totalItems);
  const currentData = data.slice(startIndex, endIndex);

  const handlePageChange = (newPage: number) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    setCurrentPage(1);
  };

  return (
    <div className={`bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden flex flex-col ${className}`}>
      {/* Table Container with Horizontal Scroll only when required */}
      <div className="overflow-x-auto w-full">
        {totalItems === 0 ? (
          <MascotEmpty
            title={emptyTitle}
            description={emptyDescription}
            actionText={emptyActionText}
            onAction={onEmptyAction}
          />
        ) : (
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200/80 text-slate-500 font-semibold uppercase tracking-wider text-[11px]">
                {columns.map(col => (
                  <th
                    key={col.key}
                    className={`py-3.5 px-4 font-semibold ${
                      col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : 'text-left'
                    } ${col.width || ''} ${col.className || ''}`}
                  >
                    {col.header}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {currentData.map((row, index) => {
                const globalIndex = startIndex + index;
                const rowKey = keyExtractor(row, globalIndex);
                return (
                  <tr
                    key={rowKey}
                    className="hover:bg-slate-50/80 transition-colors group text-[#191627]"
                  >
                    {columns.map(col => (
                      <td
                        key={col.key}
                        className={`py-3 px-4 text-xs ${
                          col.align === 'right' ? 'text-right' : col.align === 'center' ? 'text-center' : 'text-left'
                        } ${col.className || ''}`}
                      >
                        {col.render ? col.render(row, globalIndex) : (row as Record<string, unknown>)[col.key] as React.ReactNode}
                      </td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* Pagination Footer */}
      {totalItems > 0 && (
        <div className="flex flex-wrap items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 bg-slate-50/40 text-xs text-slate-500">
          <div className="flex items-center gap-2">
            <span>Rows per page:</span>
            <select
              value={pageSize}
              onChange={e => handlePageSizeChange(Number(e.target.value))}
              className="bg-white border border-slate-200 rounded-lg px-2 py-1 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
            >
              <option value={10}>10</option>
              <option value={25}>25</option>
              <option value={100}>100</option>
            </select>
          </div>

          <div className="flex items-center gap-4">
            <span className="font-mono text-slate-600">
              {totalItems === 0 ? '0 of 0' : `${startIndex + 1} – ${endIndex} of ${totalItems}`}
            </span>

            <div className="flex items-center gap-1">
              <button
                type="button"
                disabled={validPage <= 1}
                onClick={() => handlePageChange(validPage - 1)}
                className="p-1.5 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                title="Previous page"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                type="button"
                disabled={validPage >= totalPages}
                onClick={() => handlePageChange(validPage + 1)}
                className="p-1.5 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                title="Next page"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
