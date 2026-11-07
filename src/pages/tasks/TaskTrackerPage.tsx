import React, { useState, useEffect } from 'react';
import {
  Plus,
  Clock,
  CheckCircle2,
  ListTodo,
  Eye,
  Edit2,
  Trash2,
  Calendar,
  Layers,
  FileText
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FeedbackHistory, WorkLogEntry } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { StatusBadge } from '../../components/common/StatusBadge';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { useToast } from '../../components/common/Toast';
import { PROJECTS, TEAMS, CUSTOMERS, MODULES } from '../../api/mockData';

import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const TaskTrackerPage: React.FC = () => {
  const { showToast } = useToast();
  const [tasks, setTasks] = useState<FeedbackHistory[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [selectedProject, setSelectedProject] = useState('All');
  const [selectedTeam, setSelectedTeam] = useState('All');
  const [selectedStatus, setSelectedStatus] = useState('All');
  const [selectedPriority, setSelectedPriority] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');

  // Subtask modals state
  const [createSubtaskModalOpen, setCreateSubtaskModalOpen] = useState(false);
  const [updateSubtaskModalOpen, setUpdateSubtaskModalOpen] = useState(false);
  const [deleteSubtaskTargetId, setDeleteSubtaskTargetId] = useState<string | null>(null);
  const [deleteSubtaskEpicId, setDeleteSubtaskEpicId] = useState<string | null>(null);
  const [selectedSubtaskForUpdate, setSelectedSubtaskForUpdate] = useState<FeedbackHistory | null>(null);

  // Work Log form inputs
  const [logDate, setLogDate] = useState('28-Oct-2026');
  const [logHours, setLogHours] = useState(4);
  const [logDescription, setLogDescription] = useState('');

  // Modal state
  const [workLogModalTask, setWorkLogModalTask] = useState<FeedbackHistory | null>(null);
  const [detailModalTask, setDetailModalTask] = useState<FeedbackHistory | null>(null);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  // Create Task form inputs
  const [newSubject, setNewSubject] = useState('');
  const [newProject, setNewProject] = useState(PROJECTS[0]);
  const [newTeam, setNewTeam] = useState(TEAMS[0]);
  const [newPriority, setNewPriority] = useState<'Critical P0' | 'Urgent P1' | 'Normal P2' | 'Low P3'>('Normal P2');
  const [newEffortEst, setNewEffortEst] = useState(16);
  const [newStartDate, setNewStartDate] = useState('15-Oct-2026');
  const [newCloseDate, setNewCloseDate] = useState('28-Oct-2026');
  const [newDesc, setNewDesc] = useState('');

  // Subtask tracking
  const [epicIdForSubtask, setEpicIdForSubtask] = useState<string | null>(null);

  const loadTasks = () => {
    ApiClient.tasks.getAll({
      project: selectedProject !== 'All' ? selectedProject : undefined,
      team: selectedTeam !== 'All' ? selectedTeam : undefined,
      status: selectedStatus !== 'All' ? selectedStatus : undefined,
      priority: selectedPriority !== 'All' ? selectedPriority : undefined,
      search_query: searchQuery || undefined
    })
      .then(res => setTasks(res.data))
      .catch(() => showToast('Error', 'Failed to load task records', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadTasks();
  }, [selectedProject, selectedTeam, selectedStatus, selectedPriority]);

  useEffect(() => {
    loadTasks();
  }, [selectedProject, selectedTeam, selectedStatus, selectedPriority]);

  const handleSearch = () => {
    loadTasks();
    showToast('Search Applied', `Filtered tasks matching criteria (${tasks.length} found)`);
  };

  const handleClear = () => {
    setSelectedProject('All');
    setSelectedTeam('All');
    setSelectedStatus('All');
    setSelectedPriority('All');
    setSearchQuery('');
    ApiClient.tasks.getAll().then(res => setTasks(res.data));
    showToast('Filters Reset', 'Showing all tasks', 'info');
  };

  const handleCreateTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newSubject.trim()) {
      showToast('Validation Error', 'Task title cannot be empty', 'error');
      return;
    }

    const ticketNo = `${newProject.split(' ')[0].toUpperCase()}-${Math.floor(100 + Math.random() * 900)}`;

    ApiClient.tasks.saveTask({
      ticket_number: ticketNo,
      subject: newSubject,
      description: newDesc || 'Sprint task logged in Task Tracker',
      status: 'In Progress',
      priority: newPriority,
      project: newProject,
      module: MODULES[0],
      team: newTeam,
      customer: CUSTOMERS[0],
      task_owner: 'Deenesh Sabat',
      estimated_start_date: newStartDate,
      estimated_close_date: newCloseDate,
      estimated_effort: Number(newEffortEst),
      subtask_count: 0,
      work_log_entries: [],
      attachments: []
    }).then(res => {
      setTasks(prev => [res.data, ...prev]);
      setCreateModalOpen(false);
      setNewSubject('');
      setNewDesc('');
      showToast('Task Created', `Created ${res.data.ticket_number} successfully`);
    });
  };

  // Work Log: actual_effort is strictly calculated from daily entries (CRITICAL BRD RULE)
  const handleAddWorkLog = (e: React.FormEvent) => {
    e.preventDefault();
    if (!workLogModalTask) return;

    if (!logDescription.trim()) {
      showToast('Validation Error', 'Work log description cannot be empty', 'error');
      return;
    }

    ApiClient.tasks.addWorklogHours(workLogModalTask.id, {
      date: logDate,
      hours: Number(logHours),
      description: logDescription
    }).then(res => {
      setTasks(prev => prev.map(t => (t.id === workLogModalTask.id ? res.data : t)));
      setWorkLogModalTask(res.data);
      setLogDescription('');
      showToast('Work Log Added', `Logged ${logHours}h on ${workLogModalTask.ticket_number}`);
    });
  };

  const handleDelete = () => {
    if (!deleteTargetId) return;
    ApiClient.tasks.deleteTask(deleteTargetId).then(() => {
      setTasks(prev => prev.filter(t => t.id !== deleteTargetId));
      showToast('Deleted', 'Task record removed successfully');
      setDeleteTargetId(null);
    });
  };

  // Subtask operations
  const handleAddSubtask = async (taskId: string, e: React.FormEvent) => {
    e.preventDefault();
    if (!newSubject.trim()) {
      showToast('Validation Error', 'Subtask subject cannot be empty', 'error');
      return;
    }

    try {
      const result = await ApiClient.tasks.addSubtask(taskId, {
        subject: newSubject,
        description: newDesc || '',
        status: 'Open',
        priority: newPriority,
        project: selectedProject,
        team: selectedTeam,
        customer: CUSTOMERS[0],
        task_owner: 'Deenesh Sabat',
        estimated_start_date: newStartDate,
        estimated_close_date: newCloseDate,
        epic: epicIdForSubtask || undefined,
      });

      if (result.code === 200) {
        setTasks(prev => prev.map(t => t.id === taskId || t.ticket_number === taskId ? result.data : t));
        setCreateSubtaskModalOpen(false);
        setNewSubject('');
        setNewDesc('');
        showToast('Subtask Added', `Subtask added successfully to task`);
      } else {
        showToast('Error', result.message || 'Failed to add subtask', 'error');
      }
    } catch (err: any) {
      showToast('Error', err.message || 'Failed to add subtask', 'error');
    }
  };

  const handleUpdateSubtask = async (subtaskId: string) => {
    if (!selectedSubtaskForUpdate) return;

    try {
      const result = await ApiClient.tasks.updateSubtask(subtaskId, {
        subject: selectedSubtaskForUpdate.subject,
        description: selectedSubtaskForUpdate.description,
        status: selectedSubtaskForUpdate.status,
        priority: selectedSubtaskForUpdate.priority,
      });

      if (result.code === 200) {
        setTasks(prev => prev.map(t => t.id === subtaskId || t.ticket_number === subtaskId ? result.data : t));
        setUpdateSubtaskModalOpen(false);
        setSelectedSubtaskForUpdate(null);
        showToast('Subtask Updated', 'Subtask updated successfully');
      } else {
        showToast('Error', result.message || 'Failed to update subtask', 'error');
      }
    } catch (err: any) {
      showToast('Error', err.message || 'Failed to update subtask', 'error');
    }
  };

  const handleDeleteSubtask = async (subtaskId: string, epicId: string) => {
    if (!subtaskId) return;

    const confirmed = window.confirm(
      'Are you sure you want to delete this subtask?'
    );
    if (!confirmed) return;

    try {
      const result = await ApiClient.tasks.deleteSubtask(subtaskId, epicId);
      if (result.code === 200) {
        setTasks(prev => prev.filter(t => !(t.id === subtaskId || t.ticket_number === subtaskId)));
        setDeleteSubtaskTargetId(null);
        setDeleteSubtaskEpicId(null);
        showToast('Subtask Deleted', 'Subtask removed successfully');
      } else {
        showToast('Error', result.message || 'Failed to delete subtask', 'error');
      }
    } catch (err: any) {
      showToast('Error', err.message || 'Failed to delete subtask', 'error');
    }
  };

  const handleOpenCreateSubtaskModal = (taskId: string, epicId: string) => {
    setEpicIdForSubtask(epicId);
    setCreateSubtaskModalOpen(true);
  };

  const handleOpenUpdateSubtaskModal = (subtask: FeedbackHistory) => {
    setSelectedSubtaskForUpdate(subtask);
    setUpdateSubtaskModalOpen(true);
  };

  // Table columns
  const columns: Column<FeedbackHistory>[] = [
    {
      key: 'actions',
      header: 'Actions',
      width: 'w-24',
      render: row => (
        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={() => setWorkLogModalTask(row)}
            className="p-1.5 text-slate-500 hover:text-[#7B3AEC] hover:bg-violet-50 rounded-lg transition-colors"
            title="Log Daily Hours"
          >
            <Clock className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDetailModalTask(row)}
            className="p-1.5 text-slate-500 hover:text-[#00A6FC] hover:bg-sky-50 rounded-lg transition-colors"
            title="View Details"
          >
            <Eye className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTargetId(row.id)}
            className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            title="Delete Task"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      )
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
      key: 'subject',
      header: 'Task Subject',
      render: row => (
        <div className="max-w-md">
          <p className="font-semibold text-slate-800 truncate" title={row.subject}>
            {row.subject}
          </p>
          <p className="text-[11px] text-slate-400 mt-0.5 truncate">
            {row.project} · {row.team}
          </p>
        </div>
      )
    },
    {
      key: 'status',
      header: 'Status',
      render: row => <StatusBadge status={row.status} size="sm" />
    },
    {
      key: 'priority',
      header: 'Priority',
      render: row => <StatusBadge status={row.priority} type="priority" size="sm" />
    },
    {
      key: 'task_owner',
      header: 'Owner',
      render: row => (
        <div className="flex items-center gap-1.5">
          <div className="w-6 h-6 rounded-full bg-slate-800 text-white text-[10px] font-bold flex items-center justify-center">
            {row.task_owner.split(' ').map(n => n[0]).join('')}
          </div>
          <span className="text-slate-700 font-medium">{row.task_owner}</span>
        </div>
      )
    },
    {
      key: 'effort',
      header: 'Effort (Act / Est)',
      align: 'right',
      render: row => (
        <span className="font-mono font-semibold text-slate-800">
          <span className="text-[#7B3AEC] font-bold">{row.actual_effort.toFixed(1)}h</span>
          <span className="text-slate-400"> / </span>
          <span>{row.estimated_effort}h</span>
        </span>
      )
    },
    {
      key: 'dates',
      header: 'Dates',
      render: row => (
        <span className="font-mono text-[11px] text-slate-500">
          {row.estimated_start_date} – {row.estimated_close_date}
        </span>
      )
    }
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Task Tracker &amp; Work Log
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Manage feedback work items, sub-tasks, and log daily effort. Actual effort is automatically accumulated from work log entries.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <ModuleUploadTrigger
            moduleName="Task Tracker"
            defaultProject={selectedProject !== 'All' ? selectedProject : 'AEC Kuwait'}
            buttonLabel="Attach Task Document"
          />

          {/* Charcoal Pill Button */}
          <button
            type="button"
            onClick={() => setCreateModalOpen(true)}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add New Task</span>
          </button>
        </div>
      </div>

      {/* Filter Row */}
      <FilterBar onSearch={handleSearch} onClear={handleClear} showClear>
        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Project:</label>
          <select
            value={selectedProject}
            onChange={e => setSelectedProject(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Projects</option>
            {PROJECTS.map(p => <option key={p} value={p}>{p}</option>)}
          </select>
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Team:</label>
          <select
            value={selectedTeam}
            onChange={e => setSelectedTeam(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Teams</option>
            {TEAMS.map(t => <option key={t} value={t}>{t}</option>)}
          </select>
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Status:</label>
          <select
            value={selectedStatus}
            onChange={e => setSelectedStatus(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Statuses</option>
            <option value="In Progress">In Progress</option>
            <option value="Open">Open</option>
            <option value="Closed">Closed</option>
            <option value="On Hold">On Hold</option>
          </select>
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Priority:</label>
          <select
            value={selectedPriority}
            onChange={e => setSelectedPriority(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Priorities</option>
            <option value="Critical P0">Critical P0</option>
            <option value="Urgent P1">Urgent P1</option>
            <option value="Normal P2">Normal P2</option>
            <option value="Low P3">Low P3</option>
          </select>
        </div>

        <div className="flex items-center gap-1.5 flex-1 min-w-[180px]">
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Search ticket number, subject or owner..."
            className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-[#7B3AEC]"
          />
        </div>
      </FilterBar>

      {/* DataTable */}
      <DataTable
        columns={columns}
        data={tasks}
        defaultPageSize={25}
        keyExtractor={t => t.id}
        emptyTitle="No Tasks Found"
        emptyDescription="No tasks match the active filters or search terms."
        onEmptyAction={() => setCreateModalOpen(true)}
        emptyActionText="Create First Task"
      />

      {/* Work Log Modal (actual_effort read-only, logs daily entries) */}
      {workLogModalTask && (
        <Modal
          isOpen={!!workLogModalTask}
          onClose={() => setWorkLogModalTask(null)}
          title={`Work Log: ${workLogModalTask.ticket_number}`}
          subtitle={`${workLogModalTask.subject} · Total Actual Effort: ${workLogModalTask.actual_effort.toFixed(1)} hrs`}
          maxWidth="lg"
        >
          <div className="space-y-5">
            {/* Log New Entry Form */}
            <form onSubmit={handleAddWorkLog} className="p-4 bg-slate-50 rounded-2xl border border-slate-200/80 space-y-3">
              <div className="flex items-center justify-between pb-1 border-b border-slate-200/60">
                <span className="text-xs font-bold text-slate-800 flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-[#7B3AEC]" />
                  <span>Log Daily Effort</span>
                </span>
                <span className="text-[11px] text-slate-500 font-mono">
                  Actual effort is read-only and computed from work logs
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="text-[11px] font-semibold text-slate-700 block mb-1">Date *</label>
                  <input
                    type="text"
                    required
                    value={logDate}
                    onChange={e => setLogDate(e.target.value)}
                    placeholder="DD-Mon-YYYY"
                    className="w-full px-3 py-1.5 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                  />
                </div>
                <div>
                  <label className="text-[11px] font-semibold text-slate-700 block mb-1">Hours Spent *</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0.5"
                    max="24"
                    required
                    value={logHours}
                    onChange={e => setLogHours(Number(e.target.value))}
                    className="w-full px-3 py-1.5 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                  />
                </div>
              </div>

              <div>
                <label className="text-[11px] font-semibold text-slate-700 block mb-1">What was done? *</label>
                <textarea
                  rows={2}
                  required
                  value={logDescription}
                  onChange={e => setLogDescription(e.target.value)}
                  placeholder="Code implementation, testing, code review, deployment..."
                  className="w-full px-3 py-1.5 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div className="flex justify-end">
                <button
                  type="submit"
                  className="px-4 py-1.5 bg-[#7B3AEC] hover:bg-[#6828d9] text-white rounded-full text-xs font-semibold shadow-2xs"
                >
                  Add Work Log Entry
                </button>
              </div>
            </form>

            {/* Existing Work Logs List */}
            <div className="space-y-2">
              <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Logged Work History ({workLogModalTask.work_log_entries.length} entries)
              </h4>

              {workLogModalTask.work_log_entries.length === 0 ? (
                <p className="text-xs text-slate-400 py-3 text-center">No daily hours logged yet.</p>
              ) : (
                <div className="divide-y divide-slate-100 max-h-48 overflow-y-auto">
                  {workLogModalTask.work_log_entries.map((entry, idx) => (
                    <div key={entry.id || idx} className="py-2 flex items-start justify-between text-xs gap-3">
                      <div>
                        <div className="flex items-center gap-2 mb-0.5">
                          <span className="font-mono font-bold text-slate-800">{entry.date}</span>
                          <span className="font-mono font-bold text-[#7B3AEC] bg-violet-50 px-2 py-0.5 rounded-md">
                            {entry.hours} hrs
                          </span>
                        </div>
                        <p className="text-slate-600 text-[11px]">{entry.description}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="flex justify-end pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setWorkLogModalTask(null)}
                className="px-5 py-2 bg-[#191627] text-white rounded-full text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Task Details Modal */}
      {detailModalTask && (
        <Modal
          isOpen={!!detailModalTask}
          onClose={() => setDetailModalTask(null)}
          title={`Ticket Details: ${detailModalTask.ticket_number}`}
          subtitle={detailModalTask.subject}
          maxWidth="lg"
        >
          <div className="space-y-4 text-xs">
            <div className="grid grid-cols-2 gap-3 p-4 bg-slate-50 rounded-2xl">
              <div><span className="text-slate-400">Project:</span> <strong className="text-slate-800">{detailModalTask.project}</strong></div>
              <div><span className="text-slate-400">Team:</span> <strong className="text-slate-800">{detailModalTask.team}</strong></div>
              <div><span className="text-slate-400">Owner:</span> <strong className="text-slate-800">{detailModalTask.task_owner}</strong></div>
              <div><span className="text-slate-400">Status:</span> <StatusBadge status={detailModalTask.status} size="sm" /></div>
              <div><span className="text-slate-400">Priority:</span> <StatusBadge status={detailModalTask.priority} type="priority" size="sm" /></div>
              <div><span className="text-slate-400">Actual Effort:</span> <strong className="font-mono text-[#7B3AEC]">{detailModalTask.actual_effort}h / {detailModalTask.estimated_effort}h</strong></div>
            </div>

            <div>
              <span className="text-slate-500 font-semibold block mb-1">Description</span>
              <p className="p-3 bg-slate-50 rounded-xl text-slate-700 leading-relaxed">
                {detailModalTask.description}
              </p>
            </div>

            <div className="flex justify-end pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setDetailModalTask(null)}
                className="px-5 py-2 bg-[#191627] text-white rounded-full text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Create Task Modal */}
      {createModalOpen && (
        <Modal
          isOpen={createModalOpen}
          onClose={() => setCreateModalOpen(false)}
          title="Create New Task"
          subtitle="Add a new work item into the task tracker repository"
          maxWidth="lg"
        >
          <form onSubmit={handleCreateTask} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Subject *</label>
              <input
                type="text"
                required
                value={newSubject}
                onChange={e => setNewSubject(e.target.value)}
                placeholder="e.g. Implement WebSocket heartbeat retry mechanism"
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Project</label>
                <select
                  value={newProject}
                  onChange={e => setNewProject(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {PROJECTS.map(p => <option key={p} value={p}>{p}</option>)}
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Team</label>
                <select
                  value={newTeam}
                  onChange={e => setNewTeam(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {TEAMS.map(t => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Priority</label>
                <select
                  value={newPriority}
                  onChange={e => setNewPriority(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Critical P0">Critical P0</option>
                  <option value="Urgent P1">Urgent P1</option>
                  <option value="Normal P2">Normal P2</option>
                  <option value="Low P3">Low P3</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Estimated Hours</label>
                <input
                  type="number"
                  min="1"
                  max="160"
                  value={newEffortEst}
                  onChange={e => setNewEffortEst(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Estimated Start</label>
                <input
                  type="text"
                  value={newStartDate}
                  onChange={e => setNewStartDate(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Estimated Close</label>
                <input
                  type="text"
                  value={newCloseDate}
                  onChange={e => setNewCloseDate(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Description</label>
              <textarea
                rows={2}
                value={newDesc}
                onChange={e => setNewDesc(e.target.value)}
                placeholder="Technical notes, acceptance criteria..."
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
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
                Save Task
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTargetId}
        onClose={() => setDeleteTargetId(null)}
        onConfirm={handleDelete}
        title="Delete Task"
        message="Are you sure you want to delete this task? All associated work logs will be removed."
      />
    </div>
  );
};
