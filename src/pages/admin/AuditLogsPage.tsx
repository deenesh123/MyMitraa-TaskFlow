import React, { useState, useEffect } from 'react';
import {
  ShieldAlert,
  Search,
  Filter,
  CheckCircle2,
  XCircle,
  AlertOctagon,
  Clock,
  User,
  Activity,
  Download,
  Lock
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { AuditLogRecord, UserAccount } from '../../api/types';
import { useToast } from '../../components/common/Toast';
import { useAppTheme } from '../../context/ThemeContext';
import { hasPermission } from '../../utils/rbac';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';

interface AuditLogsPageProps {
  currentUser?: UserAccount;
}

export const AuditLogsPage: React.FC<AuditLogsPageProps> = ({ currentUser }) => {
  const { isGlass } = useAppTheme();
  const { showToast } = useToast();

  const [logs, setLogs] = useState<AuditLogRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [moduleFilter, setModuleFilter] = useState('All');
  const [outcomeFilter, setOutcomeFilter] = useState('All');
  const [search, setSearch] = useState('');

  const canViewAudit = hasPermission(currentUser as any, 'audit.view');
  const canExport = hasPermission(currentUser as any, 'reports.export');

  const loadLogs = () => {
    setLoading(true);
    ApiClient.auditLogs.getAll({
      module: moduleFilter,
      outcome: outcomeFilter,
      search,
    }).then(res => {
      setLogs(res.data);
    }).catch(() => showToast('Error', 'Failed to load audit logs', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadLogs();
  }, [moduleFilter, outcomeFilter, search]);

  const handleExport = () => {
    if (!canExport) {
      showToast('Access Denied', 'Export permission required', 'error');
      return;
    }
    const csvContent = 'data:text/csv;charset=utf-8,' +
      ['Timestamp,Actor,Role,Action,Module,Outcome,Details'].concat(
        logs.map(l => `"${l.timestamp}","${l.actor_name}","${l.actor_role}","${l.action}","${l.module}","${l.outcome}","${l.details.replace(/"/g, '""')}"`)
      ).join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `mymitraa_audit_log_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast('Export Complete', 'Audit log exported to CSV');
  };

  if (!canViewAudit) {
    return (
      <div className="p-12 text-center max-w-lg mx-auto bg-white rounded-3xl border border-slate-200 shadow-sm">
        <div className="w-12 h-12 rounded-2xl bg-red-100 text-red-600 flex items-center justify-center mx-auto mb-3">
          <Lock className="w-6 h-6" />
        </div>
        <h2 className="text-base font-bold text-slate-900">Access Restricted</h2>
        <p className="text-xs text-slate-500 mt-1">
          You lack the <code className="font-mono text-red-600">audit.view</code> permission required to inspect the system compliance stream.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 pb-2">
        <div>
          <h1 className={`text-xl font-extrabold tracking-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
            System Audit Trail &amp; Compliance Logs
          </h1>
          <p className={`text-xs mt-0.5 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
            Tamper-evident record of security events, administrative operations, role assignments, and permission checks.
          </p>
        </div>

        {canExport && (
          <button
            type="button"
            onClick={handleExport}
            className="inline-flex items-center gap-2 px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 rounded-full text-xs font-semibold shadow-xs transition-colors cursor-pointer"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export CSV</span>
          </button>
        )}
      </div>

      {/* Filter and Search Bar */}
      <div className={`p-4 rounded-3xl border flex flex-wrap items-center justify-between gap-4 transition-all ${
        isGlass ? 'bg-white/[0.04] border-white/10' : 'bg-white border-slate-200/80 shadow-xs'
      }`}>
        <div className="flex items-center gap-2 flex-wrap">
          <span className="text-xs font-bold text-slate-500">Outcome:</span>
          {['All', 'SUCCESS', 'DENIED', 'FAILURE'].map(out => (
            <button
              key={out}
              type="button"
              onClick={() => setOutcomeFilter(out)}
              className={`px-3 py-1 rounded-full text-xs font-semibold transition-all cursor-pointer ${
                outcomeFilter === out
                  ? 'bg-[#7B3AEC] text-white shadow-xs'
                  : 'text-slate-600 hover:bg-slate-100'
              }`}
            >
              {out}
            </button>
          ))}
        </div>

        <div className="relative min-w-[240px]">
          <Search className={`w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 ${
            isGlass ? 'text-zinc-400' : 'text-slate-400'
          }`} />
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search actor, action, details..."
            className={`w-full pl-9 pr-3 py-1.5 rounded-full text-xs border focus:outline-none transition-all ${
              isGlass
                ? 'bg-black/30 border-white/15 text-white placeholder-zinc-500 focus:border-amber-400'
                : 'bg-slate-50 border-slate-200 text-slate-800 placeholder-slate-400 focus:border-[#7B3AEC]'
            }`}
          />
        </div>
      </div>

      {/* Audit Log Table */}
      <div className={`rounded-3xl border overflow-hidden shadow-xs transition-all ${
        isGlass ? 'bg-white/[0.03] border-white/10' : 'bg-white border-slate-200/80'
      }`}>
        {loading ? (
          <div className="p-8 space-y-3">
            {[1, 2, 3].map(n => <div key={n} className="h-10 bg-slate-200/40 rounded-xl animate-pulse" />)}
          </div>
        ) : logs.length === 0 ? (
          <div className="p-12 text-center">
            <MascotPenguin pose="sidebar-peek" size={80} />
            <h3 className="text-sm font-bold text-slate-800 mt-2">No audit events found</h3>
            <p className="text-xs text-slate-500 mt-0.5">Filter criteria returned zero matching compliance records.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className={`border-b text-[11px] font-bold uppercase tracking-wider ${
                  isGlass ? 'border-white/10 text-zinc-400 bg-white/[0.02]' : 'border-slate-100 text-slate-400 bg-slate-50/60'
                }`}>
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4">Actor</th>
                  <th className="py-3 px-4">Role</th>
                  <th className="py-3 px-4">Action</th>
                  <th className="py-3 px-4">Module</th>
                  <th className="py-3 px-4">Outcome</th>
                  <th className="py-3 px-4">Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {logs.map(log => {
                  const outcomeBadge = log.outcome === 'SUCCESS'
                    ? 'bg-[#A3D801]/25 text-[#4D6B00]'
                    : log.outcome === 'DENIED'
                    ? 'bg-amber-100 text-amber-800'
                    : 'bg-red-100 text-red-700';

                  return (
                    <tr
                      key={log.id}
                      className={`hover:bg-slate-50/70 transition-colors ${isGlass ? 'hover:bg-white/[0.04]' : ''}`}
                    >
                      <td className="py-3 px-4 font-mono text-[11px] text-slate-500 whitespace-nowrap">
                        {log.timestamp}
                      </td>

                      <td className="py-3 px-4 font-bold text-slate-900 whitespace-nowrap">
                        {log.actor_name}
                      </td>

                      <td className="py-3 px-4 whitespace-nowrap">
                        <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                          {log.actor_role}
                        </span>
                      </td>

                      <td className="py-3 px-4 font-mono font-bold text-[#7B3AEC] whitespace-nowrap">
                        {log.action}
                      </td>

                      <td className="py-3 px-4 text-slate-600 whitespace-nowrap">
                        {log.module}
                      </td>

                      <td className="py-3 px-4 whitespace-nowrap">
                        <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold ${outcomeBadge}`}>
                          {log.outcome}
                        </span>
                      </td>

                      <td className="py-3 px-4 text-slate-600 leading-snug max-w-md">
                        {log.details}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
