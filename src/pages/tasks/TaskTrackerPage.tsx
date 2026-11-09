import React, { useState, useEffect } from 'react';
import {
  Plus,
  Clock,
  CheckCircle2,
  ListTodo,
  Eye,
  Trash2,
  Calendar,
  Layers,
  FileText,
  UserCheck,
  Download,
  AlertCircle
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FeedbackHistory, WorkLogEntry, UserAccount } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { StatusBadge } from '../../components/common/StatusBadge';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { useToast } from '../../components/common/Toast';
import { PROJECTS, TEAMS, CUSTOMERS, MODULES } from '../../api/mockData';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';
import { exportToCsv } from '../../utils/exportUtils';

export const TaskTrackerPage: React.FC = () => {
  const { showToast } = useToast();
  const [tasks, setTasks] = useState<FeedbackHistory[]>([]);
  const [loading, setLoading] = useState(true);
  const [usersList, setUsersList] = useState<UserAccount[]>([]);

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

  // Assign & Close task states
  const [assignModalTask, setAssignModalTask] = useState<FeedbackHistory | null>(null);
  const [selectedAssignee, setSelectedAssignee] = useState<string>('');
  const [closeConfirmTask, setCloseConfirmTask] = useState<FeedbackHistory | null>(null);

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
    // Load users for assignment
    ApiClient.users.getUsers()
      .then(res => {
        if (res.data) setUsersList(res.data);
      })
      .catch(() => {});
  }, []);

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

  // Export to CSV
  const handleExport = () => {
    exportToCsv(tasks, 'task-force-deliverables', [
      { key: 'ticket_number', label: 'Ticket ID' },
      { key: 'subject', label: 'Subject' },
      { key: 'description', label: 'Description' },
      { key: 'status', label: 'Status' },
      { key: 'priority', label: 'Priority' },
      { key: 'project', label: 'Project' },
      { key: 'customer', label: 'Customer' },
      { key: 'team', label: 'Team' },
      { key: 'module', label: 'Module' },
      { key: 'task_owner', label: 'Assigned To' },
      { key: 'assigned_by', label: 'Assigned By' },
      { key: 'estimated_effort', label: 'Est Effort (hrs)' },
      { key: 'actual_effort', label: 'Actual Effort (hrs)' },
      { key: 'estimated_start_date', label: 'Start Date' },
      { key: 'estimated_close_date', label: 'Due Date' },
    ]);
    showToast('Export Complete', 'Exported Task Force records to CSV');
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
      description: newDesc || 'Sprint task logged in Task Force',
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

  // Assign Task
  const handleOpenAssignModal = (task: FeedbackHistory) => {
    setAssignModalTask(task);
    setSelectedAssignee(task.task_owner || (usersList[0]?.name || 'Deenesh Sabat'));
  };

  const handleConfirmAssign = (e: React.FormEvent) => {
    e.preventDefault();
    if (!assignModalTask || !selectedAssignee) return;

    ApiClient.tasks.saveTask({
      ...assignModalTask,
      task_owner: selectedAssignee,
      date_assigned: '09-Nov-2026'
    }).then(res => {
      setTasks(prev => prev.map(t => (t.id === assignModalTask.id ? res.data : t)));
      showToast('Task Assigned', `Assigned ${assignModalTask.ticket_number} to ${selectedAssignee}`);
      setAssignModalTask(null);
    }).catch(err => {
      showToast('Assignment Error', err.message || 'Failed to update assignment', 'error');
    });
  };

  // Close Task
  const handleConfirmClose = () => {
    if (!closeConfirmTask) return;

    ApiClient.tasks.saveTask({
      ...closeConfirmTask,
      status: 'Closed',
      actual_close_date: '09-Nov-2026'
    }).then(res => {
      setTasks(prev => prev.map(t => (t.id === closeConfirmTask.id ? res.data : t)));
      showToast('Task Closed', `Marked ${closeConfirmTask.ticket_number} as Closed`);
      setCloseConfirmTask(null);
    }).catch(err => {
      showToast('Close Error', err.message || 'Failed to close task', 'error');
    });
  };

  // Work Log: actual_effort is strictly calculated from daily entries
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

  // Standard table columns following Section 2 specification:
  // 1. Record ID
  // 2. All business data fields
  // 3. Related project/customer/team information
  // 4. Assigned person or owner
  // 5. Created and updated information
  // 6. Status
  // 7. Actions (ALWAYS the last column on the right side)
  const columns: Column<FeedbackHistory>[] = [
    {
      key: 'ticket_number',
      header: 'Ticket ID',
      width: 'w-28',
      render: row => (
        <span className="font-mono font-bold text-[#7B3AEC] bg-violet-50 dark:bg-violet-950/40 px-2 py-0.5 rounded-lg border border-violet-100 dark:border-violet-800/40">
          {row.ticket_number}
        </span>
      )
    },
    {
      key: 'subject',
      header: 'Task Subject & Description',
      render: row => (
        <div className="max-w-xs">
          <p className="font-semibold text-slate-800 dark:text-slate-100 truncate" title={row.subject}>
            {row.subject}
          </p>
          <p className="text-[11px] text-slate-400 dark:text-zinc-400 mt-0.5 truncate" title={row.description}>
            {row.description || 'Sprint task logged in Task Force'}
          </p>
        </div>
      )
    },
    {
      key: 'priority',
      header: 'Priority',
      render: row => <StatusBadge status={row.priority} type="priority" size="sm" />
    },
    {
      key: 'effort',
      header: 'Effort (Act / Est)',
      align: 'right',
      render: row => (
        <span className="font-mono font-semibold text-slate-800 dark:text-slate-200">
          <span className="text-[#7B3AEC] font-bold">{row.actual_effort.toFixed(1)}h</span>
          <span className="text-slate-400"> / </span>
          <span>{row.estimated_effort}h</span>
        </span>
      )
    },
    {
      key: 'dates',
      header: 'Start – Due Date',
      render: row => (
        <span className="font-mono text-[11px] text-slate-500 dark:text-zinc-400 whitespace-nowrap">
          {row.estimated_start_date} – {row.estimated_close_date}
        </span>
      )
    },
    {
      key: 'project_team',
      header: 'Project · Team',
      render: row => (
        <div className="text-[11px]">
          <span className="font-medium text-slate-800 dark:text-slate-200 block truncate">{row.project}</span>
          <span className="text-slate-400 dark:text-zinc-400 truncate">{row.customer} · {row.team}</span>
        </div>
      )
    },
    {
      key: 'task_owner',
      header: 'Assignee & Owner',
      render: row => (
        <div className="flex items-center gap-1.5">
          <div className="w-6 h-6 rounded-full bg-slate-800 text-white text-[10px] font-bold flex items-center justify-center shrink-0">
            {row.task_owner ? row.task_owner.split(' ').map(n => n[0]).join('') : 'U'}
          </div>
          <div className="truncate">
            <span className="text-slate-700 dark:text-slate-200 font-medium block truncate">{row.task_owner}</span>
            <span className="text-[10px] text-slate-400 dark:text-zinc-500 block truncate">By {row.assigned_by || 'Admin'}</span>
          </div>
        </div>
      )
    },
    {
      key: 'status',
      header: 'Status',
      render: row => <StatusBadge status={row.status} size="sm" />
    },
    {
      key: 'actions',
      header: 'Actions',
      align: 'right',
      width: 'w-44',
      render: row => (
        <div className="flex items-center justify-end gap-1">
          <button
            type="button"
            onClick={() => setDetailModalTask(row)}
            className="p-1.5 text-slate-500 hover:text-[#00A6FC] hover:bg-sky-50 dark:hover:bg-sky-950/40 rounded-lg transition-colors"
            title="View Details"
          >
            <Eye className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => handleOpenAssignModal(row)}
            className="p-1.5 text-slate-500 hover:text-[#7B3AEC] hover:bg-violet-50 dark:hover:bg-violet-950/40 rounded-lg transition-colors"
            title="Assign Task"
          >
            <UserCheck className="w-3.5 h-3.5" />
          </button>
          {row.status !== 'Closed' && (
            <button
              type="button"
              onClick={() => setCloseConfirmTask(row)}
              className="p-1.5 text-slate-500 hover:text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950/40 rounded-lg transition-colors"
              title="Close Task"
            >
              <CheckCircle2 className="w-3.5 h-3.5" />
            </button>
          )}
          <button
            type="button"
            onClick={() => setWorkLogModalTask(row)}
            className="p-1.5 text-slate-500 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-amber-950/40 rounded-lg transition-colors"
            title="Log Daily Hours"
          >
            <Clock className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTargetId(row.id)}
            className="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/40 rounded-lg transition-colors"
            title="Delete Task"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      )
    }
  ];

  return (
    <div className="space-y-6">
      {/* Page Header with Right-Aligned Action Toolbar (Section 3) */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] dark:text-white tracking-tight">
            Task Force
          </h1>
          <p className="text-xs text-slate-500 dark:text-zinc-400 mt-0.5">
            Operational task execution, sprint deliverables, assignment tracking, and daily work logs.
          </p>
        </div>

        {/* Right-aligned action toolbar: Add / Create, Upload, Download / Export */}
        <div className="flex items-center gap-2">
          {/* Download / Export */}
          <button
            type="button"
            onClick={handleExport}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-white dark:bg-zinc-800 border border-slate-200 dark:border-zinc-700 hover:bg-slate-50 dark:hover:bg-zinc-700 text-slate-700 dark:text-slate-200 rounded-full text-xs font-semibold shadow-xs transition-colors cursor-pointer"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export CSV</span>
          </button>

          {/* Upload */}
          <ModuleUploadTrigger
            moduleName="Task Force"
            defaultProject={selectedProject !== 'All' ? selectedProject : 'AEC Kuwait'}
            buttonLabel="Attach Document"
          />

          {/* Add / Create */}
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

      {/* Assign Task Modal */}
      {assignModalTask && (
        <Modal
          isOpen={!!assignModalTask}
          onClose={() => setAssignModalTask(null)}
          title={`Assign Task: ${assignModalTask.ticket_number}`}
          subtitle={`Current Assignee: ${assignModalTask.task_owner}`}
          maxWidth="md"
        >
          <form onSubmit={handleConfirmAssign} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Task Subject</label>
              <p className="p-3 bg-slate-50 rounded-xl text-xs text-slate-700 font-medium border border-slate-100">
                {assignModalTask.subject}
              </p>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Select Assignee *</label>
              <select
                required
                value={selectedAssignee}
                onChange={e => setSelectedAssignee(e.target.value)}
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              >
                {usersList.length > 0 ? (
                  usersList.map(u => (
                    <option key={u.id} value={u.name}>
                      {u.name} ({u.role} · {u.team})
                    </option>
                  ))
                ) : (
                  <>
                    <option value="Deenesh Sabat">Deenesh Sabat (Admin · Technology)</option>
                    <option value="Priya Sharma">Priya Sharma (Manager · QA)</option>
                    <option value="Aman Verma">Aman Verma (Employee · Engineering)</option>
                    <option value="Rohan Mehta">Rohan Mehta (Employee · Design)</option>
                  </>
                )}
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setAssignModalTask(null)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2 bg-[#7B3AEC] hover:bg-[#6828d9] text-white rounded-full text-xs font-semibold shadow-2xs transition-colors"
              >
                Confirm Assignment
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Close Task Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!closeConfirmTask}
        onClose={() => setCloseConfirmTask(null)}
        onConfirm={handleConfirmClose}
        title="Confirm Task Closure"
        message={
          closeConfirmTask
            ? `Are you sure you want to close task ${closeConfirmTask.ticket_number} ("${closeConfirmTask.subject}")? This marks the deliverable as completed without deleting historical audit records.`
            : ''
        }
        confirmText="Close Task"
        type="warning"
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
          subtitle="Add a new work item into the Task Force repository"
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
