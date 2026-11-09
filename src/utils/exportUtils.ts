/**
 * MyMitraa TaskFlow — Data Export Utilities
 * Exports real tabular records to formatted CSV files and triggers native browser download.
 */

export function exportToCsv<T extends Record<string, any>>(
  data: T[],
  filename: string,
  columns?: { key: keyof T | string; label: string; formatter?: (val: any, row: T) => string }[]
): void {
  if (!data || data.length === 0) {
    const emptyBlob = new Blob(['No data available for export'], { type: 'text/csv;charset=utf-8;' });
    triggerDownload(emptyBlob, `${filename}.csv`);
    return;
  }

  // Derive columns if not provided
  const cols = columns || Object.keys(data[0]).map(key => ({
    key,
    label: key.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()),
  }));

  // Build CSV Header
  const headers = cols.map(c => `"${c.label.replace(/"/g, '""')}"`).join(',');

  // Build CSV Rows
  const rows = data.map(row => {
    return cols
      .map(c => {
        let val: any;
        if (c.formatter) {
          val = c.formatter(row[c.key], row);
        } else {
          val = row[c.key];
        }

        if (val === null || val === undefined) {
          return '""';
        }

        if (typeof val === 'object') {
          val = JSON.stringify(val);
        }

        const str = String(val).replace(/"/g, '""');
        return `"${str}"`;
      })
      .join(',');
  });

  const csvContent = [headers, ...rows].join('\r\n');
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
  triggerDownload(blob, `${filename}.csv`);
}

function triggerDownload(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
