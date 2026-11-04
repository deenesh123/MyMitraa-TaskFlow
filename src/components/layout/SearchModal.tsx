import React, { useState, useEffect, useRef } from 'react';
import { Search, X, CheckSquare, Calendar, Users, FileText, ArrowRight } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FeedbackHistory, MeetingTimeTracker, DocumentRecord } from '../../api/types';

interface SearchModalProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (route: string) => void;
}

export const SearchModal: React.FC<SearchModalProps> = ({ isOpen, onClose, onNavigate }) => {
  const [query, setQuery] = useState('');
  const [filterModule, setFilterModule] = useState<'all' | 'tasks' | 'meetings' | 'documents'>('all');
  const [taskResults, setTaskResults] = useState<FeedbackHistory[]>([]);
  const [meetingResults, setMeetingResults] = useState<MeetingTimeTracker[]>([]);
  const [docResults, setDocResults] = useState<DocumentRecord[]>([]);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 50);
    } else {
      setQuery('');
    }
  }, [isOpen]);

  useEffect(() => {
    if (!query.trim()) {
      setTaskResults([]);
      setMeetingResults([]);
      setDocResults([]);
      return;
    }

    const q = query.toLowerCase();

    // Query tasks
    ApiClient.tasks.getAll({ search_query: q }).then(res => {
      setTaskResults(res.data.slice(0, 5));
    });

    // Query meetings
    ApiClient.meetings.getAll({ search_query: q }).then(res => {
      setMeetingResults(res.data.slice(0, 4));
    });

    // Query documents
    ApiClient.documents.getAll({}).then(res => {
      const filtered = res.data.filter(d => 
        d.file_name.toLowerCase().includes(q) ||
        d.ticket_number.toLowerCase().includes(q) ||
        d.project.toLowerCase().includes(q)
      );
      setDocResults(filtered.slice(0, 4));
    });
  }, [query]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center pt-20 px-4 bg-slate-900/40 backdrop-blur-xs">
      <div className="w-full max-w-2xl bg-white rounded-3xl shadow-2xl border border-slate-100 overflow-hidden flex flex-col max-h-[80vh] animate-in fade-in zoom-in-95 duration-150">
        {/* Search Input Bar */}
        <div className="flex items-center gap-3 px-5 py-4 border-b border-slate-100">
          <Search className="w-5 h-5 text-[#7B3AEC]" />
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={e => setQuery(e.target.value)}
            placeholder="Search tasks, projects, tickets, meetings, documents..."
            className="flex-1 text-sm text-[#191627] placeholder:text-slate-400 focus:outline-none"
          />
          {query && (
            <button
              type="button"
              onClick={() => setQuery('')}
              className="p-1 text-slate-400 hover:text-slate-600 rounded-full"
            >
              <X className="w-4 h-4" />
            </button>
          )}
          <kbd className="hidden sm:inline-block px-2 py-0.5 text-[11px] font-mono text-slate-400 bg-slate-100 rounded-md border border-slate-200">
            ESC
          </kbd>
        </div>

        {/* Filter Module Pills */}
        <div className="flex items-center gap-1.5 px-5 py-2.5 bg-slate-50 border-b border-slate-100 text-xs">
          <span className="text-slate-400 mr-2 font-medium">Filter by:</span>
          {(['all', 'tasks', 'meetings', 'documents'] as const).map(mod => (
            <button
              key={mod}
              type="button"
              onClick={() => setFilterModule(mod)}
              className={`px-3 py-1 rounded-full text-xs font-medium capitalize transition-colors ${
                filterModule === mod
                  ? 'bg-[#7B3AEC] text-white shadow-xs'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-100'
              }`}
            >
              {mod}
            </button>
          ))}
        </div>

        {/* Results Body */}
        <div className="p-4 overflow-y-auto space-y-4 flex-1">
          {!query.trim() && (
            <div className="text-center py-10 text-slate-400 text-xs">
              Type to search across tickets, meetings, and shared documents...
            </div>
          )}

          {query.trim() && taskResults.length === 0 && meetingResults.length === 0 && docResults.length === 0 && (
            <div className="text-center py-10 text-slate-400 text-xs">
              No results found for &ldquo;{query}&rdquo;
            </div>
          )}

          {/* Tasks Results */}
          {(filterModule === 'all' || filterModule === 'tasks') && taskResults.length > 0 && (
            <div className="space-y-1.5">
              <div className="flex items-center gap-2 text-xs font-semibold text-slate-500 uppercase tracking-wider px-2">
                <CheckSquare className="w-3.5 h-3.5 text-[#7B3AEC]" />
                <span>Tasks ({taskResults.length})</span>
              </div>
              {taskResults.map(task => (
                <div
                  key={task.id}
                  onClick={() => {
                    onClose();
                    onNavigate('/task-tracker');
                  }}
                  className="p-3 bg-slate-50 hover:bg-[#7B3AEC]/5 rounded-2xl cursor-pointer border border-transparent hover:border-[#7B3AEC]/20 transition-all flex items-center justify-between group"
                >
                  <div className="min-w-0 pr-2">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-mono font-bold text-[#7B3AEC]">
                        {task.ticket_number}
                      </span>
                      <span className="text-[11px] px-2 py-0.5 rounded-full bg-slate-200 text-slate-700">
                        {task.team}
                      </span>
                      <span className="text-[11px] px-2 py-0.5 rounded-full bg-slate-200 text-slate-700">
                        {task.status}
                      </span>
                    </div>
                    <p className="text-xs font-medium text-[#191627] truncate">{task.subject}</p>
                    <p className="text-[11px] text-slate-500 mt-0.5">Owner: {task.task_owner} · Project: {task.project}</p>
                  </div>
                  <ArrowRight className="w-4 h-4 text-slate-300 group-hover:text-[#7B3AEC] group-hover:translate-x-0.5 transition-all shrink-0" />
                </div>
              ))}
            </div>
          )}

          {/* Meetings Results */}
          {(filterModule === 'all' || filterModule === 'meetings') && meetingResults.length > 0 && (
            <div className="space-y-1.5">
              <div className="flex items-center gap-2 text-xs font-semibold text-slate-500 uppercase tracking-wider px-2">
                <Calendar className="w-3.5 h-3.5 text-[#00A6FC]" />
                <span>Meetings ({meetingResults.length})</span>
              </div>
              {meetingResults.map(m => (
                <div
                  key={m.id}
                  onClick={() => {
                    onClose();
                    onNavigate('/meeting-tracker');
                  }}
                  className="p-3 bg-slate-50 hover:bg-[#00A6FC]/5 rounded-2xl cursor-pointer border border-transparent hover:border-[#00A6FC]/20 transition-all flex items-center justify-between group"
                >
                  <div className="min-w-0 pr-2">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-semibold text-slate-800">{m.category}</span>
                      <span className="text-[11px] font-mono text-slate-500">{m.date} ({m.total_time}m)</span>
                    </div>
                    <p className="text-xs text-slate-600 truncate">{m.project} · Organizer: {m.meeting_by}</p>
                  </div>
                  <ArrowRight className="w-4 h-4 text-slate-300 group-hover:text-[#00A6FC] group-hover:translate-x-0.5 transition-all shrink-0" />
                </div>
              ))}
            </div>
          )}

          {/* Document Results */}
          {(filterModule === 'all' || filterModule === 'documents') && docResults.length > 0 && (
            <div className="space-y-1.5">
              <div className="flex items-center gap-2 text-xs font-semibold text-slate-500 uppercase tracking-wider px-2">
                <FileText className="w-3.5 h-3.5 text-[#A3D801]" />
                <span>Documents ({docResults.length})</span>
              </div>
              {docResults.map(doc => (
                <div
                  key={doc.id}
                  onClick={() => {
                    onClose();
                    onNavigate('/task-upload-files');
                  }}
                  className="p-3 bg-slate-50 hover:bg-[#A3D801]/10 rounded-2xl cursor-pointer border border-transparent hover:border-[#A3D801]/30 transition-all flex items-center justify-between group"
                >
                  <div className="min-w-0 pr-2">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-mono font-bold text-slate-700">{doc.ticket_number}</span>
                      <span className="text-[11px] text-slate-500">{doc.file_size}</span>
                    </div>
                    <p className="text-xs font-medium text-[#191627] truncate">{doc.file_name}</p>
                    <p className="text-[11px] text-slate-500 mt-0.5">Project: {doc.project} · {doc.uploaded_date}</p>
                  </div>
                  <ArrowRight className="w-4 h-4 text-slate-300 group-hover:text-[#4D6B00] group-hover:translate-x-0.5 transition-all shrink-0" />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
