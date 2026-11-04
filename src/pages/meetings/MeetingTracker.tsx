import React, { useState, useEffect } from 'react';
import { Plus, MoreVertical, Eye, Edit2, Trash2, Users } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { MeetingTimeTracker } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { DateRangeBox } from '../../components/common/DateRangeBox';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { useToast } from '../../components/common/Toast';
import { PROJECTS, CUSTOMERS, TEAMS, MODULES } from '../../api/mockData';

export const MeetingTracker: React.FC = () => {
  const { showToast } = useToast();
  const [meetings, setMeetings] = useState<MeetingTimeTracker[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters (MT-2)
  const [searchParameter, setSearchParameter] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [startDate, setStartDate] = useState('01-Jan-2026');
  const [endDate, setEndDate] = useState('31-Oct-2026');

  // Modals state
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [viewMeeting, setViewMeeting] = useState<MeetingTimeTracker | null>(null);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  // Form state (MT-1)
  const [formDate, setFormDate] = useState('28-Oct-2026');
  const [formCustomer, setFormCustomer] = useState(CUSTOMERS[0]);
  const [formProject, setFormProject] = useState(PROJECTS[0]);
  const [formModule, setFormModule] = useState(MODULES[1]);
  const [formTeam, setFormTeam] = useState(TEAMS[0]);
  const [formCategory, setFormCategory] = useState('Sprint Planning');
  const [formMeetingBy, setFormMeetingBy] = useState('Deenesh Sabat');
  const [formAttendees, setFormAttendees] = useState('Ajay Kumar, Vivek Singh');
  const [formLeadAttended, setFormLeadAttended] = useState(true);
  const [formArchAttended, setFormArchAttended] = useState(false);
  const [formStartTime, setFormStartTime] = useState('10:00 AM');
  const [formEndTime, setFormEndTime] = useState('11:00 AM');
  const [formComments, setFormComments] = useState('');

  // Active action menu row ID
  const [activeMenuId, setActiveMenuId] = useState<string | null>(null);

  const loadMeetings = () => {
    setLoading(true);
    ApiClient.meetings.getAll({ search_query: searchQuery })
      .then(res => setMeetings(res.data))
      .catch(() => showToast('Error', 'Failed to load meetings', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadMeetings();
  }, []);

  const handleSearch = () => {
    loadMeetings();
    showToast('Search Applied', `Showing meetings matching "${searchQuery || searchParameter}"`);
  };

  const handleDownload = () => {
    showToast('Download Prepared', 'Exporting 657 meetings log to CSV format...');
  };

  const handleSaveMeeting = (e: React.FormEvent) => {
    e.preventDefault();
    ApiClient.meetings.saveMeeting({
      date: formDate,
      customer: formCustomer,
      project: formProject,
      module: formModule,
      team: formTeam,
      category: formCategory,
      meeting_by: formMeetingBy,
      meeting_with: formAttendees.split(',').map(s => s.trim()),
      lead_attended: formLeadAttended,
      architect_attended: formArchAttended,
      start_time: formStartTime,
      end_time: formEndTime,
      total_time: 60,
      comments: formComments || 'Sprint sync and deliverables review'
    }).then(res => {
      setMeetings(prev => [res.data, ...prev]);
      setAddModalOpen(false);
      showToast('Meeting Logged', 'Meeting entry recorded successfully');
    });
  };

  const handleDeleteMeeting = () => {
    if (!deleteTargetId) return;
    ApiClient.meetings.deleteMeeting(deleteTargetId).then(() => {
      setMeetings(prev => prev.filter(m => m.id !== deleteTargetId));
      showToast('Deleted', 'Meeting record removed.');
      setDeleteTargetId(null);
    });
  };

  // Columns definition (MT-3: Actions column first, Date, Customer, Project, Module, Team)
  const columns: Column<MeetingTimeTracker>[] = [
    {
      key: 'actions',
      header: 'Actions',
      width: 'w-20',
      render: row => (
        <div className="relative inline-block text-left">
          <button
            type="button"
            onClick={() => setActiveMenuId(activeMenuId === row.id ? null : row.id)}
            className="p-1 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <MoreVertical className="w-4 h-4" />
          </button>

          {activeMenuId === row.id && (
            <div className="absolute left-0 mt-1 w-32 bg-white border border-slate-200 rounded-2xl shadow-xl p-1 z-30 animate-in fade-in zoom-in-95">
              <button
                type="button"
                onClick={() => {
                  setActiveMenuId(null);
                  setViewMeeting(row);
                }}
                className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-slate-700 hover:bg-slate-50 rounded-xl"
              >
                <Eye className="w-3.5 h-3.5 text-slate-400" />
                <span>View</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveMenuId(null);
                  showToast('Edit', `Editing meeting: ${row.category}`);
                }}
                className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-slate-700 hover:bg-slate-50 rounded-xl"
              >
                <Edit2 className="w-3.5 h-3.5 text-slate-400" />
                <span>Edit</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveMenuId(null);
                  setDeleteTargetId(row.id);
                }}
                className="w-full flex items-center gap-2 px-3 py-1.5 text-xs text-red-600 hover:bg-red-50 rounded-xl"
              >
                <Trash2 className="w-3.5 h-3.5 text-red-500" />
                <span>Delete</span>
              </button>
            </div>
          )}
        </div>
      )
    },
    {
      key: 'date',
      header: 'Date',
      render: row => <span className="font-mono font-medium text-slate-800">{row.date}</span>
    },
    {
      key: 'customer',
      header: 'Customer',
      render: row => <span className="font-semibold text-slate-700">{row.customer}</span>
    },
    {
      key: 'project',
      header: 'Project',
      render: row => <span className="text-slate-800 font-medium">{row.project}</span>
    },
    {
      key: 'module',
      header: 'Module',
      render: row => <span className="text-slate-600">{row.module}</span>
    },
    {
      key: 'team',
      header: 'Team',
      render: row => (
        <span className="px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-slate-100 text-slate-700">
          {row.team}
        </span>
      )
    },
    {
      key: 'meeting_by',
      header: 'Organizer',
      render: row => <span className="text-slate-700">{row.meeting_by}</span>
    },
    {
      key: 'duration',
      header: 'Duration',
      render: row => <span className="font-mono text-slate-600">{row.total_time} mins</span>
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header with Title and Add New Button */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Meeting Tracker
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Log and search customer and internal meetings. Currently tracking {meetings.length} meetings.
          </p>
        </div>

        {/* Charcoal Pill Button (MT-1) */}
        <button
          type="button"
          onClick={() => setAddModalOpen(true)}
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Meeting</span>
        </button>
      </div>

      {/* Filter Row (MT-2) */}
      <FilterBar onSearch={handleSearch} onDownload={handleDownload} downloadTooltip="Download Meetings Excel">
        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">
            Search Parameter:
          </label>
          <select
            value={searchParameter}
            onChange={e => setSearchParameter(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Parameters</option>
            <option value="Customer">Customer</option>
            <option value="Project">Project</option>
            <option value="Module">Module</option>
            <option value="Team">Team</option>
            <option value="Organizer">Organizer</option>
          </select>
        </div>

        <div className="flex-1 min-w-[200px]">
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Search meeting description, customer or organizer..."
            className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-[#7B3AEC]"
          />
        </div>

        <DateRangeBox startDate={startDate} endDate={endDate} onChange={(s, e) => { setStartDate(s); setEndDate(e); }} />
      </FilterBar>

      {/* DataTable (MT-4: newest first, MT-5: default 100/page, reads 1 - 100 of 657) */}
      <DataTable
        columns={columns}
        data={meetings}
        defaultPageSize={100}
        keyExtractor={m => m.id}
        emptyTitle="No Meetings Found"
        emptyDescription="No meetings matching your selected date range or query."
        onEmptyAction={() => setAddModalOpen(true)}
        emptyActionText="Log First Meeting"
      />

      {/* Add New Meeting Modal (MT-1) */}
      {addModalOpen && (
        <Modal
          isOpen={addModalOpen}
          onClose={() => setAddModalOpen(false)}
          title="Log New Meeting"
          subtitle="Record discussion items, customer feedback, and participant attendance"
          maxWidth="xl"
        >
          <form onSubmit={handleSaveMeeting} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Date *</label>
                <input
                  type="text"
                  required
                  value={formDate}
                  onChange={e => setFormDate(e.target.value)}
                  placeholder="DD-Mon-YYYY"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Category</label>
                <select
                  value={formCategory}
                  onChange={e => setFormCategory(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Sprint Planning">Sprint Planning</option>
                  <option value="Client Standup">Client Standup</option>
                  <option value="Architecture Sync">Architecture Sync</option>
                  <option value="Backlog Grooming">Backlog Grooming</option>
                  <option value="Retrospective">Retrospective</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Customer</label>
                <select
                  value={formCustomer}
                  onChange={e => setFormCustomer(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {CUSTOMERS.map(c => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Project</label>
                <select
                  value={formProject}
                  onChange={e => setFormProject(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {PROJECTS.map(p => (
                    <option key={p} value={p}>{p}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Module</label>
                <select
                  value={formModule}
                  onChange={e => setFormModule(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {MODULES.map(m => (
                    <option key={m} value={m}>{m}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Team</label>
                <select
                  value={formTeam}
                  onChange={e => setFormTeam(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {TEAMS.map(t => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Meeting By (Organizer)</label>
                <input
                  type="text"
                  value={formMeetingBy}
                  onChange={e => setFormMeetingBy(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Attendees (Comma separated)</label>
                <input
                  type="text"
                  value={formAttendees}
                  onChange={e => setFormAttendees(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="flex items-center gap-6 py-1">
              <label className="inline-flex items-center gap-2 text-xs font-medium text-slate-700 cursor-pointer">
                <input
                  type="checkbox"
                  checked={formLeadAttended}
                  onChange={e => setFormLeadAttended(e.target.checked)}
                  className="rounded text-[#7B3AEC] focus:ring-[#7B3AEC]"
                />
                <span>Lead Attended</span>
              </label>

              <label className="inline-flex items-center gap-2 text-xs font-medium text-slate-700 cursor-pointer">
                <input
                  type="checkbox"
                  checked={formArchAttended}
                  onChange={e => setFormArchAttended(e.target.checked)}
                  className="rounded text-[#7B3AEC] focus:ring-[#7B3AEC]"
                />
                <span>Architect Attended</span>
              </label>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Start Time</label>
                <input
                  type="text"
                  value={formStartTime}
                  onChange={e => setFormStartTime(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">End Time</label>
                <input
                  type="text"
                  value={formEndTime}
                  onChange={e => setFormEndTime(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Calculated Total</label>
                <input
                  type="text"
                  disabled
                  value="60 mins (1.0h)"
                  className="w-full px-3 py-2 bg-slate-100 border border-slate-200 rounded-xl text-xs font-mono text-slate-600 cursor-not-allowed"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Comments / Action Items</label>
              <textarea
                rows={2}
                value={formComments}
                onChange={e => setFormComments(e.target.value)}
                placeholder="Key meeting takeaways and next milestones..."
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setAddModalOpen(false)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold transition-colors"
              >
                Save Meeting
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* View Meeting Modal */}
      {viewMeeting && (
        <Modal
          isOpen={!!viewMeeting}
          onClose={() => setViewMeeting(null)}
          title={`Meeting: ${viewMeeting.category}`}
          subtitle={`${viewMeeting.date} · ${viewMeeting.project}`}
          maxWidth="md"
        >
          <div className="space-y-3 text-xs">
            <div className="grid grid-cols-2 gap-2 p-3 bg-slate-50 rounded-2xl">
              <div><span className="text-slate-400">Customer:</span> <strong className="text-slate-800">{viewMeeting.customer}</strong></div>
              <div><span className="text-slate-400">Team:</span> <strong className="text-slate-800">{viewMeeting.team}</strong></div>
              <div><span className="text-slate-400">Organizer:</span> <strong className="text-slate-800">{viewMeeting.meeting_by}</strong></div>
              <div><span className="text-slate-400">Duration:</span> <strong className="font-mono text-[#7B3AEC]">{viewMeeting.total_time} mins</strong></div>
            </div>
            <div>
              <span className="text-slate-400 block mb-1">Attendees:</span>
              <div className="flex flex-wrap gap-1">
                {viewMeeting.meeting_with.map((att, i) => (
                  <span key={i} className="px-2 py-0.5 bg-violet-100 text-[#7B3AEC] rounded-full text-[11px] font-semibold">
                    {att}
                  </span>
                ))}
              </div>
            </div>
            {viewMeeting.comments && (
              <div className="p-3 bg-slate-50 rounded-xl">
                <span className="text-slate-400 block mb-1">Comments:</span>
                <p className="text-slate-700">{viewMeeting.comments}</p>
              </div>
            )}
            <div className="flex justify-end pt-2">
              <button
                type="button"
                onClick={() => setViewMeeting(null)}
                className="px-4 py-2 bg-[#191627] text-white rounded-full text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={!!deleteTargetId}
        onClose={() => setDeleteTargetId(null)}
        onConfirm={handleDeleteMeeting}
        title="Delete Meeting Record"
        message="Are you sure you want to delete this meeting? This will remove logged minutes from monthly team effort rollups."
      />
    </div>
  );
};
