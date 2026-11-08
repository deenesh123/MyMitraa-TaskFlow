import React, { useState, useEffect } from 'react';
import {
  FolderKanban,
  Plus,
  Search,
  Calendar,
  Clock,
  Users,
  CheckCircle2,
  AlertTriangle,
  ChevronRight,
  TrendingUp,
  MoreVertical,
  Edit,
  Trash2,
  Filter
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { ProjectRecord, UserAccount } from '../../api/types';
import { Modal } from '../../components/common/Modal';
import { useToast } from '../../components/common/Toast';
import { useAppTheme } from '../../context/ThemeContext';
import { hasPermission } from '../../utils/rbac';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';

interface ProjectManagementPageProps {
  currentUser?: UserAccount;
}

export const ProjectManagementPage: React.FC<ProjectManagementPageProps> = ({ currentUser }) => {
  const { isGlass } = useAppTheme();
  const { showToast } = useToast();

  const [projects, setProjects] = useState<ProjectRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editingProject, setEditingProject] = useState<ProjectRecord | null>(null);

  // Form states
  const [projectName, setProjectName] = useState('');
  const [customerName, setCustomerName] = useState('');
  const [projectManager, setProjectManager] = useState('Vikram Malhotra');
  const [priority, setPriority] = useState<'Critical P0' | 'Urgent P1' | 'Normal P2' | 'Low P3'>('Normal P2');
  const [status, setStatus] = useState<ProjectRecord['status']>('In Progress');
  const [healthScore, setHealthScore] = useState<ProjectRecord['health_score']>('Healthy');
  const [startDate, setStartDate] = useState('01-Nov-2026');
  const [targetDate, setTargetDate] = useState('31-Dec-2026');
  const [budgetHours, setBudgetHours] = useState(400);
  const [description, setDescription] = useState('');

  const canCreate = hasPermission(currentUser as any, 'projects.create');
  const canUpdate = hasPermission(currentUser as any, 'projects.update');
  const canDelete = hasPermission(currentUser as any, 'projects.delete');

  const loadProjects = () => {
    setLoading(true);
    ApiClient.projects.getAll({ status: statusFilter, search })
      .then(res => setProjects(res.data))
      .catch(() => showToast('Error', 'Failed to load projects', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadProjects();
  }, [statusFilter, search]);

  const handleOpenCreate = () => {
    setEditingProject(null);
    setProjectName('');
    setCustomerName('');
    setProjectManager(currentUser?.name || 'Vikram Malhotra');
    setPriority('Normal P2');
    setStatus('In Progress');
    setHealthScore('Healthy');
    setBudgetHours(500);
    setDescription('');
    setCreateModalOpen(true);
  };

  const handleOpenEdit = (proj: ProjectRecord) => {
    setEditingProject(proj);
    setProjectName(proj.project_name);
    setCustomerName(proj.customer_name);
    setProjectManager(proj.project_manager);
    setPriority(proj.priority);
    setStatus(proj.status);
    setHealthScore(proj.health_score);
    setStartDate(proj.start_date);
    setTargetDate(proj.target_date);
    setBudgetHours(proj.budget_hours || 400);
    setDescription(proj.description || '');
    setCreateModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!projectName.trim() || !customerName.trim()) {
      showToast('Validation Error', 'Project name and customer are required', 'error');
      return;
    }

    if (editingProject) {
      ApiClient.projects.updateProject(editingProject.id, {
        project_name: projectName,
        customer_name: customerName,
        project_manager: projectManager,
        priority,
        status,
        health_score: healthScore,
        start_date: startDate,
        target_date: targetDate,
        budget_hours: Number(budgetHours),
        description,
      }).then(res => {
        setProjects(prev => prev.map(p => p.id === editingProject.id ? res.data : p));
        setCreateModalOpen(false);
        showToast('Project Updated', `${projectName} updated successfully`);
      });
    } else {
      const code = `PRJ-${customerName.slice(0, 3).toUpperCase()}-${Math.floor(10 + Math.random() * 90)}`;
      ApiClient.projects.saveProject({
        project_id: code,
        project_name: projectName,
        customer_name: customerName,
        project_manager: projectManager,
        priority,
        status,
        health_score: healthScore,
        start_date: startDate,
        target_date: targetDate,
        completion_percentage: 10,
        budget_hours: Number(budgetHours),
        logged_hours: 0,
        members: [projectManager],
        description,
      }).then(res => {
        setProjects(prev => [res.data, ...prev]);
        setCreateModalOpen(false);
        showToast('Project Created', `${projectName} (${code}) created successfully`);
      });
    }
  };

  const handleDelete = (id: string, name: string) => {
    if (!window.confirm(`Are you sure you want to delete project ${name}?`)) return;
    ApiClient.projects.deleteProject(id).then(() => {
      setProjects(prev => prev.filter(p => p.id !== id && p.project_id !== id));
      showToast('Project Deleted', `${name} has been removed`);
    });
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 pb-2">
        <div>
          <h1 className={`text-xl font-extrabold tracking-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
            Project Portfolio
          </h1>
          <p className={`text-xs mt-0.5 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
            Track deliverables, health score, milestone delivery, and team allocation across active engagements.
          </p>
        </div>

        {canCreate && (
          <button
            type="button"
            onClick={handleOpenCreate}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#7B3AEC] hover:bg-[#682ecf] text-white rounded-full text-xs font-semibold shadow-md shadow-[#7B3AEC]/25 transition-all cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>New Project</span>
          </button>
        )}
      </div>

      {/* Filter and Search Bar */}
      <div className={`p-4 rounded-3xl border flex flex-wrap items-center justify-between gap-4 transition-all ${
        isGlass ? 'bg-white/[0.04] border-white/10' : 'bg-white border-slate-200/80 shadow-xs'
      }`}>
        {/* Status tabs */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {['All', 'In Progress', 'Planning', 'On Hold', 'Completed'].map(st => (
            <button
              key={st}
              type="button"
              onClick={() => setStatusFilter(st)}
              className={`px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all whitespace-nowrap cursor-pointer ${
                statusFilter === st
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

        {/* Search input */}
        <div className="relative min-w-[240px]">
          <Search className={`w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 ${
            isGlass ? 'text-zinc-400' : 'text-slate-400'
          }`} />
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search projects, client, PM..."
            className={`w-full pl-9 pr-3 py-1.5 rounded-full text-xs border focus:outline-none transition-all ${
              isGlass
                ? 'bg-black/30 border-white/15 text-white placeholder-zinc-500 focus:border-amber-400'
                : 'bg-slate-50 border-slate-200 text-slate-800 placeholder-slate-400 focus:border-[#7B3AEC]'
            }`}
          />
        </div>
      </div>

      {/* Projects Grid */}
      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {[1, 2, 3].map(n => (
            <div key={n} className="h-64 rounded-3xl bg-slate-200/50 animate-pulse" />
          ))}
        </div>
      ) : projects.length === 0 ? (
        <div className={`p-12 text-center rounded-3xl border ${
          isGlass ? 'bg-white/[0.03] border-white/10' : 'bg-white border-slate-200'
        }`}>
          <MascotPenguin pose="sleeping" size={90} />
          <h3 className="text-sm font-bold text-slate-800 mt-3">No projects found</h3>
          <p className="text-xs text-slate-500 mt-1">Try adjusting your status filter or search keywords.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {projects.map(proj => {
            const healthColor = proj.health_score === 'Healthy'
              ? 'bg-[#A3D801]/20 text-[#4D6B00] border-[#A3D801]/30'
              : proj.health_score === 'At Risk'
              ? 'bg-amber-500/20 text-amber-700 border-amber-500/30'
              : 'bg-red-500/20 text-red-700 border-red-500/30';

            return (
              <div
                key={proj.id}
                className={`rounded-3xl p-6 border shadow-xs flex flex-col justify-between transition-all group hover:shadow-md ${
                  isGlass
                    ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
                    : 'bg-white border-slate-200/80 text-[#191627]'
                }`}
              >
                <div>
                  {/* Top Bar: Code, Health Badge & Actions */}
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-[11px] font-mono font-bold text-[#7B3AEC] bg-[#7B3AEC]/10 px-2.5 py-0.5 rounded-full">
                      {proj.project_id}
                    </span>
                    <div className="flex items-center gap-2">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${healthColor}`}>
                        {proj.health_score}
                      </span>
                      {canUpdate && (
                        <button
                          type="button"
                          onClick={() => handleOpenEdit(proj)}
                          className="p-1 rounded-lg text-slate-400 hover:text-[#7B3AEC] hover:bg-slate-100 transition-colors"
                          title="Edit Project"
                        >
                          <Edit className="w-3.5 h-3.5" />
                        </button>
                      )}
                      {canDelete && (
                        <button
                          type="button"
                          onClick={() => handleDelete(proj.id, proj.project_name)}
                          className="p-1 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                          title="Delete Project"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Title & Customer */}
                  <h3 className="text-base font-extrabold tracking-tight mt-1 truncate">
                    {proj.project_name}
                  </h3>
                  <p className={`text-xs font-medium mb-3 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
                    Customer: <span className="font-semibold text-slate-700">{proj.customer_name}</span> · PM: {proj.project_manager}
                  </p>

                  {/* Description */}
                  {proj.description && (
                    <p className={`text-xs leading-relaxed line-clamp-2 mb-4 ${isGlass ? 'text-zinc-300' : 'text-slate-600'}`}>
                      {proj.description}
                    </p>
                  )}

                  {/* Progress Bar */}
                  <div className="space-y-1.5 mb-4">
                    <div className="flex items-center justify-between text-xs font-medium">
                      <span className={isGlass ? 'text-zinc-400' : 'text-slate-500'}>Completion</span>
                      <span className="font-mono font-bold">{proj.completion_percentage}%</span>
                    </div>
                    <div className="w-full h-2 rounded-full bg-slate-100 overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-[#7B3AEC] to-[#A3D801] rounded-full transition-all duration-500"
                        style={{ width: `${proj.completion_percentage}%` }}
                      />
                    </div>
                  </div>

                  {/* Hours breakdown */}
                  <div className={`grid grid-cols-2 gap-2 p-2.5 rounded-2xl text-[11px] font-medium mb-3 ${
                    isGlass ? 'bg-black/30 border border-white/10' : 'bg-slate-50 border border-slate-100'
                  }`}>
                    <div>
                      <span className="text-slate-400 block text-[10px]">Budget Hours</span>
                      <span className="font-mono font-bold">{proj.budget_hours || 0} hrs</span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[10px]">Logged Hours</span>
                      <span className="font-mono font-bold text-[#00A6FC]">{proj.logged_hours || 0} hrs</span>
                    </div>
                  </div>
                </div>

                {/* Footer Dates & Status */}
                <div className={`pt-3 border-t flex items-center justify-between text-[11px] ${
                  isGlass ? 'border-white/10 text-zinc-400' : 'border-slate-100 text-slate-500'
                }`}>
                  <div className="flex items-center gap-1 font-mono">
                    <Calendar className="w-3.5 h-3.5 text-slate-400" />
                    <span>Target: {proj.target_date}</span>
                  </div>
                  <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 font-bold text-[10px]">
                    {proj.status}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Project Modal (Create / Edit) */}
      {createModalOpen && (
        <Modal
          isOpen={createModalOpen}
          onClose={() => setCreateModalOpen(false)}
          title={editingProject ? 'Edit Project' : 'Create New Project'}
          subtitle="Configure deliverables, timeline, milestones, and PM assignments"
          maxWidth="lg"
        >
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Project Name *</label>
                <input
                  type="text"
                  required
                  value={projectName}
                  onChange={e => setProjectName(e.target.value)}
                  placeholder="e.g. NextGen Cloud Core"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Customer / Client *</label>
                <input
                  type="text"
                  required
                  value={customerName}
                  onChange={e => setCustomerName(e.target.value)}
                  placeholder="e.g. AEC Kuwait"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Project Manager</label>
                <input
                  type="text"
                  value={projectManager}
                  onChange={e => setProjectManager(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Status</label>
                <select
                  value={status}
                  onChange={e => setStatus(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Planning">Planning</option>
                  <option value="Not Started">Not Started</option>
                  <option value="In Progress">In Progress</option>
                  <option value="On Hold">On Hold</option>
                  <option value="Completed">Completed</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Health Score</label>
                <select
                  value={healthScore}
                  onChange={e => setHealthScore(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Healthy">Healthy</option>
                  <option value="At Risk">At Risk</option>
                  <option value="Critical">Critical</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Priority</label>
                <select
                  value={priority}
                  onChange={e => setPriority(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Critical P0">Critical P0</option>
                  <option value="Urgent P1">Urgent P1</option>
                  <option value="Normal P2">Normal P2</option>
                  <option value="Low P3">Low P3</option>
                </select>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Target Date</label>
                <input
                  type="text"
                  value={targetDate}
                  onChange={e => setTargetDate(e.target.value)}
                  placeholder="30-Nov-2026"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Budget Hours</label>
                <input
                  type="number"
                  value={budgetHours}
                  onChange={e => setBudgetHours(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Project Description &amp; Scope</label>
              <textarea
                rows={3}
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="High-level architecture, client objectives, and milestones..."
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

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
                {editingProject ? 'Save Changes' : 'Create Project'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
