import React, { useRef, useState } from 'react';
import { Upload, Paperclip, CheckCircle2, X, FileText, Image, FileCode, Archive } from 'lucide-react';
import { useToast } from './Toast';
import { ApiClient } from '../../api/client';
import { useAppTheme } from '../../context/ThemeContext';

export interface ModuleUploadTriggerProps {
  moduleName: string;
  defaultProject?: string;
  ticketNumber?: string;
  compact?: boolean;
  onUploaded?: (doc: any) => void;
  className?: string;
  buttonLabel?: string;
}

export const ModuleUploadTrigger: React.FC<ModuleUploadTriggerProps> = ({
  moduleName,
  defaultProject = 'AEC Kuwait',
  ticketNumber,
  compact = false,
  onUploaded,
  className = '',
  buttonLabel = 'Upload Document'
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { showToast } = useToast();
  const { isGlass } = useAppTheme();
  const [uploading, setUploading] = useState(false);
  const [recentUpload, setRecentUpload] = useState<{ name: string; size: string } | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    const sizeStr = file.size > 1024 * 1024
      ? `${(file.size / (1024 * 1024)).toFixed(1)} MB`
      : `${Math.round(file.size / 1024)} KB`;
    
    const ext = file.name.split('.').pop()?.toUpperCase() || 'DOC';
    const generatedTicket = ticketNumber || `${defaultProject.split(' ')[0].toUpperCase()}-${Math.floor(100 + Math.random() * 900)}`;

    ApiClient.documents.uploadDocument({
      ticket_number: generatedTicket,
      file_name: file.name,
      file_size: sizeStr,
      file_type: ext,
      uploaded_date: '04-Nov-2026',
      customer: 'Internal',
      project: defaultProject,
      task_team: 'Engineering',
      task_owner: 'Deenesh Sabat',
      task_status: 'In Progress',
      task_priority: 'Normal P2'
    }).then(res => {
      setUploading(false);
      setRecentUpload({ name: file.name, size: sizeStr });
      showToast('Document Uploaded', `${file.name} attached to ${moduleName}`);
      if (onUploaded) onUploaded(res.data);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }).catch(() => {
      setUploading(false);
      showToast('Upload Error', 'Failed to upload document', 'error');
    });
  };

  return (
    <div className={`inline-flex items-center gap-2 ${className}`}>
      <input
        ref={fileInputRef}
        type="file"
        className="hidden"
        onChange={handleFileChange}
      />

      <button
        type="button"
        disabled={uploading}
        onClick={() => fileInputRef.current?.click()}
        title={`Upload and attach file to ${moduleName}`}
        className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
          compact
            ? isGlass
              ? 'bg-white/10 hover:bg-white/20 text-white border border-white/15'
              : 'bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200'
            : isGlass
            ? 'bg-gradient-to-r from-amber-500/20 to-amber-600/20 hover:from-amber-500/30 hover:to-amber-600/30 text-amber-300 border border-amber-500/40 shadow-xs'
            : 'bg-violet-50 hover:bg-violet-100 text-[#7B3AEC] border border-violet-200 shadow-2xs'
        }`}
      >
        <Upload className="w-3.5 h-3.5 shrink-0" />
        <span>{uploading ? 'Uploading...' : buttonLabel}</span>
      </button>

      {/* Uploaded File Chip */}
      {recentUpload && (
        <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] animate-in fade-in zoom-in-95 ${
          isGlass
            ? 'bg-emerald-500/20 border border-emerald-400/30 text-emerald-300'
            : 'bg-emerald-50 border border-emerald-200 text-emerald-800'
        }`}>
          <CheckCircle2 className="w-3 h-3 text-emerald-400 shrink-0" />
          <span className="font-medium truncate max-w-[140px]">{recentUpload.name}</span>
          <span className="opacity-70 font-mono text-[10px]">({recentUpload.size})</span>
          <button
            type="button"
            onClick={() => setRecentUpload(null)}
            className="hover:opacity-100 opacity-60 p-0.5 cursor-pointer"
            title="Dismiss chip"
          >
            <X className="w-3 h-3" />
          </button>
        </div>
      )}
    </div>
  );
};
