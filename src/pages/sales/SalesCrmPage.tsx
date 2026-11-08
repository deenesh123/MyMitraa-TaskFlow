import React, { useState, useEffect } from 'react';
import {
  TrendingUp,
  DollarSign,
  UserCheck,
  Target,
  Plus,
  Search,
  Filter,
  Calendar,
  Phone,
  Mail,
  Building,
  CheckCircle2,
  XCircle,
  ArrowRight,
  MoreVertical,
  Edit,
  Trash2,
  FileSpreadsheet
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { LeadRecord, UserAccount } from '../../api/types';
import { Modal } from '../../components/common/Modal';
import { useToast } from '../../components/common/Toast';
import { useAppTheme } from '../../context/ThemeContext';
import { hasPermission } from '../../utils/rbac';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';

interface SalesCrmPageProps {
  currentUser?: UserAccount;
}

const STAGES: LeadRecord['stage'][] = [
  'New',
  'Contacted',
  'Qualified',
  'Proposal',
  'Negotiation',
  'Won',
  'Lost'
];

export const SalesCrmPage: React.FC<SalesCrmPageProps> = ({ currentUser }) => {
  const { isGlass } = useAppTheme();
  const { showToast } = useToast();

  const [leads, setLeads] = useState<LeadRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [stageFilter, setStageFilter] = useState('All');
  const [search, setSearch] = useState('');
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editingLead, setEditingLead] = useState<LeadRecord | null>(null);

  // Form inputs
  const [contactName, setContactName] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [stage, setStage] = useState<LeadRecord['stage']>('New');
  const [assignedTo, setAssignedTo] = useState('Rahul Khanna');
  const [expectedRevenue, setExpectedRevenue] = useState(50000);
  const [probability, setProbability] = useState(50);
  const [source, setSource] = useState('Inbound Web');
  const [followUpDate, setFollowUpDate] = useState('15-Nov-2026');
  const [notes, setNotes] = useState('');
  const [lostReason, setLostReason] = useState('');

  const canCreate = hasPermission(currentUser as any, 'sales.leads.create');
  const canUpdate = hasPermission(currentUser as any, 'sales.leads.update');
  const canDelete = hasPermission(currentUser as any, 'sales.leads.delete');

  const loadLeads = () => {
    setLoading(true);
    ApiClient.sales.getLeads({ stage: stageFilter, search })
      .then(res => setLeads(res.data))
      .catch(() => showToast('Error', 'Failed to load leads from backend', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadLeads();
  }, [stageFilter, search]);

  const handleOpenCreate = () => {
    setEditingLead(null);
    setContactName('');
    setCompanyName('');
    setEmail('');
    setPhone('');
    setStage('New');
    setAssignedTo(currentUser?.name || 'Rahul Khanna');
    setExpectedRevenue(45000);
    setProbability(40);
    setSource('Inbound Web');
    setFollowUpDate('20-Nov-2026');
    setNotes('');
    setLostReason('');
    setCreateModalOpen(true);
  };

  const handleOpenEdit = (lead: LeadRecord) => {
    setEditingLead(lead);
    setContactName(lead.contact_name);
    setCompanyName(lead.company_name);
    setEmail(lead.email);
    setPhone(lead.phone);
    setStage(lead.stage);
    setAssignedTo(lead.assigned_to);
    setExpectedRevenue(lead.expected_revenue);
    setProbability(lead.probability);
    setSource(lead.source);
    setFollowUpDate(lead.follow_up_date);
    setNotes(lead.notes || '');
    setLostReason(lead.lost_reason || '');
    setCreateModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!contactName.trim() || !companyName.trim()) {
      showToast('Validation Error', 'Contact and company name are required', 'error');
      return;
    }

    if (editingLead) {
      ApiClient.sales.updateLead(editingLead.id, {
        contact_name: contactName,
        company_name: companyName,
        email,
        phone,
        stage,
        assigned_to: assignedTo,
        expected_revenue: Number(expectedRevenue),
        probability: Number(probability),
        source,
        follow_up_date: followUpDate,
        notes,
        lost_reason: lostReason,
      }).then(res => {
        setLeads(prev => prev.map(l => l.id === editingLead.id ? res.data : l));
        setCreateModalOpen(false);
        showToast('Lead Updated', `${companyName} lead updated successfully`);
      });
    } else {
      const code = `LEAD-${Math.floor(100 + Math.random() * 900)}`;
      ApiClient.sales.saveLead({
        lead_code: code,
        contact_name: contactName,
        company_name: companyName,
        email,
        phone,
        stage,
        assigned_to: assignedTo,
        expected_revenue: Number(expectedRevenue),
        probability: Number(probability),
        source,
        follow_up_date: followUpDate,
        notes,
        lost_reason: lostReason,
      }).then(res => {
        setLeads(prev => [res.data, ...prev]);
        setCreateModalOpen(false);
        showToast('Lead Created', `Added ${companyName} (${code}) to sales pipeline`);
      });
    }
  };

  const handleAdvanceStage = (lead: LeadRecord) => {
    const currentIndex = STAGES.indexOf(lead.stage);
    if (currentIndex < STAGES.length - 2) {
      const nextStage = STAGES[currentIndex + 1];
      ApiClient.sales.updateLead(lead.id, { stage: nextStage }).then(res => {
        setLeads(prev => prev.map(l => l.id === lead.id ? res.data : l));
        showToast('Stage Advanced', `${lead.company_name} advanced to ${nextStage}`);
      });
    }
  };

  const handleDelete = (id: string, name: string) => {
    if (!window.confirm(`Delete lead record for ${name}?`)) return;
    ApiClient.sales.deleteLead(id).then(() => {
      setLeads(prev => prev.filter(l => l.id !== id && l.lead_code !== id));
      showToast('Lead Deleted', `${name} deleted`);
    });
  };

  // Pipeline metrics
  const totalPipelineValue = leads.filter(l => l.stage !== 'Lost').reduce((sum, l) => sum + l.expected_revenue, 0);
  const weightedPipeline = leads.filter(l => l.stage !== 'Lost').reduce((sum, l) => sum + (l.expected_revenue * (l.probability / 100)), 0);
  const wonCount = leads.filter(l => l.stage === 'Won').length;
  const closedCount = leads.filter(l => l.stage === 'Won' || l.stage === 'Lost').length;
  const winRate = closedCount > 0 ? Math.round((wonCount / closedCount) * 100) : 67;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 pb-2">
        <div>
          <h1 className={`text-xl font-extrabold tracking-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
            Sales Pipeline &amp; CRM
          </h1>
          <p className={`text-xs mt-0.5 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
            Track opportunities, stage transitions, revenue forecasting, and executive quota performance.
          </p>
        </div>

        {canCreate && (
          <button
            type="button"
            onClick={handleOpenCreate}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#7B3AEC] hover:bg-[#682ecf] text-white rounded-full text-xs font-semibold shadow-md shadow-[#7B3AEC]/25 transition-all cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add Lead</span>
          </button>
        )}
      </div>

      {/* 4 Pipeline Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Pipeline */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Total Pipeline Value</span>
            <div className="p-2 bg-[#7B3AEC]/15 rounded-xl text-[#7B3AEC]">
              <DollarSign className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight">
            ${totalPipelineValue.toLocaleString()}
          </p>
          <p className="text-[11px] text-[#7B3AEC] font-semibold mt-1 flex items-center gap-1">
            <TrendingUp className="w-3.5 h-3.5" />
            <span>{leads.length} active opportunities</span>
          </p>
        </div>

        {/* Weighted Forecast */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Weighted Forecast</span>
            <div className="p-2 bg-[#00A6FC]/15 rounded-xl text-[#00A6FC]">
              <Target className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-[#00A6FC]">
            ${Math.round(weightedPipeline).toLocaleString()}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">
            Probability adjusted revenue
          </p>
        </div>

        {/* Win Rate */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Deal Win Rate</span>
            <div className="p-2 bg-[#A3D801]/20 rounded-xl text-[#4D6B00]">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-[#4D6B00]">
            {winRate}%
          </p>
          <p className="text-[11px] text-[#4D6B00] font-semibold mt-1">
            {wonCount} won of {closedCount} resolved deals
          </p>
        </div>

        {/* Monthly Target */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Monthly Target</span>
            <div className="p-2 bg-[#D946EF]/15 rounded-xl text-[#D946EF]">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-[#D946EF]">
            $250,000
          </p>
          <p className="text-[11px] text-slate-500 mt-1">
            Current pace: 78% of Q4 target
          </p>
        </div>
      </div>

      {/* Filter bar */}
      <div className={`p-4 rounded-3xl border flex flex-wrap items-center justify-between gap-4 transition-all ${
        isGlass ? 'bg-white/[0.04] border-white/10' : 'bg-white border-slate-200/80 shadow-xs'
      }`}>
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {['All', ...STAGES].map(st => (
            <button
              key={st}
              type="button"
              onClick={() => setStageFilter(st)}
              className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-all whitespace-nowrap cursor-pointer ${
                stageFilter === st
                  ? 'bg-[#7B3AEC] text-white shadow-xs'
                  : isGlass
                  ? 'text-zinc-400 hover:text-white hover:bg-white/10'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              {st}
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
            placeholder="Search company, contact, code..."
            className={`w-full pl-9 pr-3 py-1.5 rounded-full text-xs border focus:outline-none transition-all ${
              isGlass
                ? 'bg-black/30 border-white/15 text-white placeholder-zinc-500 focus:border-amber-400'
                : 'bg-slate-50 border-slate-200 text-slate-800 placeholder-slate-400 focus:border-[#7B3AEC]'
            }`}
          />
        </div>
      </div>

      {/* Leads Table */}
      <div className={`rounded-3xl border overflow-hidden shadow-xs transition-all ${
        isGlass ? 'bg-white/[0.03] border-white/10' : 'bg-white border-slate-200/80'
      }`}>
        {loading ? (
          <div className="p-8 space-y-3">
            {[1, 2, 3].map(n => (
              <div key={n} className="h-12 bg-slate-200/40 rounded-xl animate-pulse" />
            ))}
          </div>
        ) : leads.length === 0 ? (
          <div className="p-12 text-center">
            <MascotPenguin pose="sidebar-peek" size={80} />
            <h3 className="text-sm font-bold text-slate-800 mt-2">No leads found</h3>
            <p className="text-xs text-slate-500 mt-0.5">Adjust filter options or create a new lead to start tracking.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className={`border-b text-[11px] font-bold uppercase tracking-wider ${
                  isGlass ? 'border-white/10 text-zinc-400 bg-white/[0.02]' : 'border-slate-100 text-slate-400 bg-slate-50/60'
                }`}>
                  <th className="py-3 px-4">Lead / Company</th>
                  <th className="py-3 px-4">Contact</th>
                  <th className="py-3 px-4">Stage</th>
                  <th className="py-3 px-4">Expected Value</th>
                  <th className="py-3 px-4">Probability</th>
                  <th className="py-3 px-4">Next Follow-up</th>
                  <th className="py-3 px-4">Owner</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {leads.map(lead => {
                  const stageColor =
                    lead.stage === 'Won'
                      ? 'bg-[#A3D801]/25 text-[#4D6B00]'
                      : lead.stage === 'Lost'
                      ? 'bg-red-100 text-red-700'
                      : lead.stage === 'Negotiation' || lead.stage === 'Proposal'
                      ? 'bg-[#00A6FC]/20 text-[#0082CA]'
                      : 'bg-violet-100 text-[#7B3AEC]';

                  return (
                    <tr
                      key={lead.id}
                      className={`hover:bg-slate-50/70 transition-colors ${
                        isGlass ? 'hover:bg-white/[0.04]' : ''
                      }`}
                    >
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-2.5">
                          <div className="w-7 h-7 rounded-xl bg-[#7B3AEC]/15 text-[#7B3AEC] flex items-center justify-center font-bold text-xs">
                            <Building className="w-3.5 h-3.5" />
                          </div>
                          <div>
                            <p className="font-bold text-slate-900 leading-tight">{lead.company_name}</p>
                            <span className="font-mono text-[10px] text-slate-400">{lead.lead_code}</span>
                          </div>
                        </div>
                      </td>

                      <td className="py-3.5 px-4">
                        <div>
                          <p className="font-semibold text-slate-800">{lead.contact_name}</p>
                          <p className="text-[11px] text-slate-500">{lead.email}</p>
                        </div>
                      </td>

                      <td className="py-3.5 px-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${stageColor}`}>
                          {lead.stage}
                        </span>
                      </td>

                      <td className="py-3.5 px-4 font-mono font-bold text-slate-800">
                        ${lead.expected_revenue.toLocaleString()}
                      </td>

                      <td className="py-3.5 px-4 font-mono">
                        <div className="flex items-center gap-1.5">
                          <div className="w-12 h-1.5 rounded-full bg-slate-100 overflow-hidden">
                            <div
                              className="h-full bg-[#7B3AEC] rounded-full"
                              style={{ width: `${lead.probability}%` }}
                            />
                          </div>
                          <span className="text-[11px] font-semibold">{lead.probability}%</span>
                        </div>
                      </td>

                      <td className="py-3.5 px-4 font-mono text-[11px] text-slate-600">
                        {lead.follow_up_date}
                      </td>

                      <td className="py-3.5 px-4">
                        <span className="text-xs font-medium text-slate-700">{lead.assigned_to}</span>
                      </td>

                      <td className="py-3.5 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          {canUpdate && lead.stage !== 'Won' && lead.stage !== 'Lost' && (
                            <button
                              type="button"
                              onClick={() => handleAdvanceStage(lead)}
                              className="p-1 rounded-lg text-slate-400 hover:text-[#00A6FC] hover:bg-sky-50 transition-colors"
                              title="Advance to next stage"
                            >
                              <ArrowRight className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {canUpdate && (
                            <button
                              type="button"
                              onClick={() => handleOpenEdit(lead)}
                              className="p-1 rounded-lg text-slate-400 hover:text-[#7B3AEC] hover:bg-slate-100 transition-colors"
                              title="Edit lead"
                            >
                              <Edit className="w-3.5 h-3.5" />
                            </button>
                          )}
                          {canDelete && (
                            <button
                              type="button"
                              onClick={() => handleDelete(lead.id, lead.company_name)}
                              className="p-1 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                              title="Delete lead"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal: Create or Edit Lead */}
      {createModalOpen && (
        <Modal
          isOpen={createModalOpen}
          onClose={() => setCreateModalOpen(false)}
          title={editingLead ? 'Edit Lead Opportunity' : 'Add New Lead'}
          subtitle="Record customer interaction, budget expectations, and deal stages"
          maxWidth="lg"
        >
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Company / Organization *</label>
                <input
                  type="text"
                  required
                  value={companyName}
                  onChange={e => setCompanyName(e.target.value)}
                  placeholder="e.g. Gulf Trading Corp"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Primary Contact *</label>
                <input
                  type="text"
                  required
                  value={contactName}
                  onChange={e => setContactName(e.target.value)}
                  placeholder="e.g. Tariq Mansoor"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  placeholder="contact@company.com"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Phone</label>
                <input
                  type="tel"
                  value={phone}
                  onChange={e => setPhone(e.target.value)}
                  placeholder="+971 4 330 9821"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Stage</label>
                <select
                  value={stage}
                  onChange={e => setStage(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {STAGES.map(s => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Expected Revenue ($)</label>
                <input
                  type="number"
                  value={expectedRevenue}
                  onChange={e => setExpectedRevenue(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Probability (%)</label>
                <input
                  type="number"
                  min="0"
                  max="100"
                  value={probability}
                  onChange={e => setProbability(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Lead Source</label>
                <input
                  type="text"
                  value={source}
                  onChange={e => setSource(e.target.value)}
                  placeholder="e.g. Partner Referral, Web Inbound"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Follow-up Date</label>
                <input
                  type="text"
                  value={followUpDate}
                  onChange={e => setFollowUpDate(e.target.value)}
                  placeholder="20-Nov-2026"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Opportunity Notes &amp; Strategy</label>
              <textarea
                rows={2}
                value={notes}
                onChange={e => setNotes(e.target.value)}
                placeholder="Client requirements, SLA expectations, licensing model..."
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            {stage === 'Lost' && (
              <div>
                <label className="text-xs font-semibold text-red-600 block mb-1">Reason for Loss</label>
                <input
                  type="text"
                  value={lostReason}
                  onChange={e => setLostReason(e.target.value)}
                  placeholder="e.g. Competitor price, project cancelled"
                  className="w-full px-3.5 py-2 border border-red-200 rounded-xl text-xs focus:outline-none focus:border-red-500"
                />
              </div>
            )}

            <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setCreateModalOpen(false)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold transition-colors"
              >
                {editingLead ? 'Save Changes' : 'Add to Pipeline'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
