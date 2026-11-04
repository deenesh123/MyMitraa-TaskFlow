import React, { useState, useEffect, useRef } from 'react';
import { Upload, Download, Eye, Trash2, FileText, Check, Paperclip, X } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { DocumentRecord } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { DateRangeBox } from '../../components/common/DateRangeBox';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { StatusBadge } from '../../components/common/StatusBadge';
import { useToast } from '../../components/common/Toast';
import { PROJECTS, CUSTOMERS, TEAMS } from '../../api/mockData';

export const ViewDocuments: React.FC = () => {
  const { showToast } = useToast();
  const [documents, setDocuments] = useState<DocumentRecord[]>([]);
  const [loading, setLoading] = useState(true);

  // 7 Filters (VD-1)
  const [filterCustomer, setFilterCustomer] = useState('All');
  const [filterProject, setFilterProject] = useState('All');
  const [filterTeam, setFilterTeam] = useState('All');
  const [filterOwner, setFilterOwner] = useState('All');
  const [ticketSearch, setTicketSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('All');
  const [filterPriority, setFilterPriority] = useState('All');

  // Date range (VD-3)
  const [startDate, setStartDate] = useState('01-Oct-2026');
  const [endDate, setEndDate] = useState('31-Oct-2026');

  // File Upload State (VD-3: small chip beside the upload icon)
  const [uploadedFileChip, setUploadedFileChip] = useState<{ name: string; size: string } | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Modals
  const [previewDoc, setPreviewDoc] = useState<DocumentRecord | null>(null);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  const loadDocuments = () => {
    setLoading(true);
    ApiClient.documents.getAll({
      customer: filterCustomer !== 'All' ? filterCustomer : undefined,
      project: filterProject !== 'All' ? filterProject : undefined,
      task_team: filterTeam !== 'All' ? filterTeam : undefined,
      task_owner: filterOwner !== 'All' ? filterOwner : undefined,
      ticket_number: ticketSearch ? ticketSearch : undefined,
      task_status: filterStatus !== 'All' ? filterStatus : undefined,
      task_priority: filterPriority !== 'All' ? filterPriority : undefined,
      start_date: startDate,
      end_date: endDate
    })
      .then(res => setDocuments(res.data))
      .catch(() => showToast('Error', 'Failed to load documents', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadDocuments();
  }, []);

  // VD-2: Search applies filters, Clear Filters resets them
  const handleSearch = () => {
    loadDocuments();
    showToast('Filters Applied', `Loaded documents matching criteria (${documents.length} matches)`);
  };

  const handleClear = () => {
    setFilterCustomer('All');
    setFilterProject('All');
    setFilterTeam('All');
    setFilterOwner('All');
    setTicketSearch('');
    setFilterStatus('All');
    setFilterPriority('All');
    showToast('Filters Cleared', 'Reset to all documents', 'info');
    ApiClient.documents.getAll({}).then(res => setDocuments(res.data));
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const sizeStr = `${(file.size / (1024 * 1024)).toFixed(1)} MB`;
    setUploadedFileChip({ name: file.name, size: sizeStr });

    // Upload via ApiClient
    ApiClient.documents.uploadDocument({
      ticket_number: 'AECKUWAIT-422',
      file_name: file.name,
      file_size: sizeStr,
      file_type: file.name.split('.').pop()?.toUpperCase() || 'PDF',
      uploaded_date: '28-Oct-2026',
      customer: 'AEC',
      project: 'AEC Kuwait',
      task_team: 'Architecture',
      task_owner: 'Deenesh Sabat',
      task_status: 'In Progress',
      task_priority: 'Urgent P1'
    }).then(res => {
      setDocuments(prev => [res.data, ...prev]);
      showToast('Document Uploaded', `${file.name} attached to AECKUWAIT-422`);
    });
  };

  const handleDelete = () => {
    if (!deleteTargetId) return;
    ApiClient.documents.deleteDocument(deleteTargetId).then(() => {
      setDocuments(prev => prev.filter(d => d.id !== deleteTargetId));
      showToast('File Deleted', 'Document deleted successfully');
      setDeleteTargetId(null);
    });
  };

  const handleDownload = (doc: DocumentRecord) => {
    showToast('Downloading', `Downloading ${doc.file_name} (${doc.file_size})...`);
  };

  // VD-4: Action (download, view, delete), Uploaded Date (DD-Mon-YYYY), Customer, etc.
  const columns: Column<DocumentRecord>[] = [
    {
      key: 'actions',
      header: 'Actions',
      width: 'w-28',
      render: row => (
        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={() => handleDownload(row)}
            className="p-1.5 text-slate-500 hover:text-[#7B3AEC] hover:bg-[#7B3AEC]/10 rounded-lg transition-colors"
            title="Download Document"
          >
            <Download className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setPreviewDoc(row)}
            className="p-1.5 text-slate-500 hover:text-[#00A6FC] hover:bg-[#00A6FC]/10 rounded-lg transition-colors"
            title="Preview Document"
          >
            <Eye className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTargetId(row.id)}
            className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            title="Delete Document"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      )
    },
    {
      key: 'uploaded_date',
      header: 'Uploaded Date',
      render: row => <span className="font-mono text-slate-700 font-medium">{row.uploaded_date}</span>
    },
    {
      key: 'ticket_number',
      header: 'Ticket No',
      render: row => (
        <span className="font-mono font-bold text-[#7B3AEC] bg-violet-50 px-2 py-0.5 rounded-lg border border-violet-100">
          {row.ticket_number}
        </span>
      )
    },
    {
      key: 'file_name',
      header: 'Document Name',
      render: row => (
        <div className="flex items-center gap-2 max-w-xs">
          <FileText className="w-4 h-4 text-slate-400 shrink-0" />
          <span className="truncate font-semibold text-slate-800" title={row.file_name}>
            {row.file_name}
          </span>
        </div>
      )
    },
    {
      key: 'file_size',
      header: 'Size',
      render: row => <span className="font-mono text-slate-500">{row.file_size}</span>
    },
    {
      key: 'customer',
      header: 'Customer',
      render: row => <span className="text-slate-700 font-medium">{row.customer}</span>
    },
    {
      key: 'project',
      header: 'Project',
      render: row => <span className="text-slate-800">{row.project}</span>
    },
    {
      key: 'task_team',
      header: 'Team',
      render: row => (
        <span className="px-2 py-0.5 rounded-full text-[11px] bg-slate-100 text-slate-700">
          {row.task_team}
        </span>
      )
    },
    {
      key: 'task_status',
      header: 'Status',
      render: row => <StatusBadge status={row.task_status} size="sm" />
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            View Documents
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Find, preview, download, and delete attachments associated with tickets and project milestones.
          </p>
        </div>

        {/* Upload Button + Small Chip (VD-3) */}
        <div className="flex items-center gap-2">
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            className="hidden"
          />
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors"
          >
            <Upload className="w-4 h-4" />
            <span>Upload Document</span>
          </button>

          {/* Small chip beside the icon (VD-3: fixes large type bug) */}
          {uploadedFileChip && (
            <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-violet-50 text-[#7B3AEC] border border-violet-200 rounded-full text-xs font-semibold animate-in fade-in">
              <Paperclip className="w-3.5 h-3.5" />
              <span className="max-w-[120px] truncate">{uploadedFileChip.name}</span>
              <button
                type="button"
                onClick={() => setUploadedFileChip(null)}
                className="hover:text-violet-900"
              >
                <X className="w-3 h-3" />
              </button>
            </div>
          )}
        </div>
      </div>

      {/* 7 Filters Row (VD-1, VD-2) */}
      <FilterBar onSearch={handleSearch} onClear={handleClear} showClear>
        {/* 1. Customer */}
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Customer:</label>
          <select
            value={filterCustomer}
            onChange={e => setFilterCustomer(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Customers</option>
            {CUSTOMERS.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>

        {/* 2. Project */}
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Project:</label>
          <select
            value={filterProject}
            onChange={e => setFilterProject(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Projects</option>
            {PROJECTS.map(p => <option key={p} value={p}>{p}</option>)}
          </select>
        </div>

        {/* 3. Task Team */}
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Team:</label>
          <select
            value={filterTeam}
            onChange={e => setFilterTeam(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Teams</option>
            {TEAMS.map(t => <option key={t} value={t}>{t}</option>)}
          </select>
        </div>

        {/* 4. Search by Ticket No */}
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Ticket No:</label>
          <input
            type="text"
            value={ticketSearch}
            onChange={e => setTicketSearch(e.target.value)}
            placeholder="AECKUWAIT-422"
            className="w-32 bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-[#7B3AEC]"
          />
        </div>

        {/* 5. Status */}
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Status:</label>
          <select
            value={filterStatus}
            onChange={e => setFilterStatus(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Statuses</option>
            <option value="Closed">Closed</option>
            <option value="In Progress">In Progress</option>
            <option value="Open">Open</option>
          </select>
        </div>

        {/* 6. Date Range (VD-3, VD-5) */}
        <DateRangeBox startDate={startDate} endDate={endDate} onChange={(s, e) => { setStartDate(s); setEndDate(e); }} />
      </FilterBar>

      {/* DataTable (VD-6: defaults to 10 per page, reads 1 – 10 of 50) */}
      <DataTable
        columns={columns}
        data={documents}
        defaultPageSize={10}
        keyExtractor={d => d.id}
        emptyTitle="Data Not Available"
        emptyDescription="No document attachments found matching the current search parameters."
        onEmptyAction={handleClear}
        emptyActionText="Reset Filters"
      />

      {/* Document Preview Modal */}
      {previewDoc && (
        <Modal
          isOpen={!!previewDoc}
          onClose={() => setPreviewDoc(null)}
          title={`Document Preview: ${previewDoc.file_name}`}
          subtitle={`Attached to ${previewDoc.ticket_number} · ${previewDoc.file_size}`}
          maxWidth="lg"
        >
          <div className="space-y-4">
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200/80 space-y-2 text-xs">
              <div className="grid grid-cols-2 gap-2">
                <div><span className="text-slate-400">Customer:</span> <strong>{previewDoc.customer}</strong></div>
                <div><span className="text-slate-400">Project:</span> <strong>{previewDoc.project}</strong></div>
                <div><span className="text-slate-400">Team:</span> <strong>{previewDoc.task_team}</strong></div>
                <div><span className="text-slate-400">Uploaded Date:</span> <strong className="font-mono">{previewDoc.uploaded_date}</strong></div>
              </div>
            </div>

            {/* Document Render Mock Canvas */}
            <div className="h-64 bg-slate-100 rounded-2xl border border-dashed border-slate-300 flex flex-col items-center justify-center p-6 text-center text-slate-500">
              <FileText className="w-12 h-12 text-[#7B3AEC] mb-2" />
              <p className="text-xs font-bold text-slate-700">{previewDoc.file_name}</p>
              <p className="text-[11px] text-slate-400 max-w-sm mt-1">
                Technical specification attachment containing enterprise architecture schemas and milestone acceptance checklists.
              </p>
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => handleDownload(previewDoc)}
                className="inline-flex items-center gap-1.5 px-4 py-2 bg-[#7B3AEC] hover:bg-[#6828d9] text-white rounded-full text-xs font-semibold"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Download File</span>
              </button>

              <button
                type="button"
                onClick={() => setPreviewDoc(null)}
                className="px-4 py-2 border border-slate-200 rounded-full text-xs text-slate-600 hover:bg-slate-50 font-medium"
              >
                Close Preview
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTargetId}
        onClose={() => setDeleteTargetId(null)}
        onConfirm={handleDelete}
        title="Delete Document"
        message="Are you sure you want to permanently delete this file? This will remove the attachment reference from the ticket."
      />
    </div>
  );
};
