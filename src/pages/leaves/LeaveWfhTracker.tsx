import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Plus, Calendar as CalendarIcon, Info } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { OptimaLeaveTracker } from '../../api/types';
import { Modal } from '../../components/common/Modal';
import { useToast } from '../../components/common/Toast';
import { getLeaveTypeStyle } from '../../utils/colorTokens';

export const LeaveWfhTracker: React.FC = () => {
  const { showToast } = useToast();
  const [leaves, setLeaves] = useState<OptimaLeaveTracker[]>([]);
  const [currentYear, setCurrentYear] = useState(2026);
  const [currentMonth, setCurrentMonth] = useState(9); // 0-indexed: 9 = October
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [selectedDayEntries, setSelectedDayEntries] = useState<{ day: number; entries: OptimaLeaveTracker[] } | null>(null);

  // Form state (LW-1)
  const [formName, setFormName] = useState('Deenesh Sabat');
  const [formRole, setFormRole] = useState('Production Engineer');
  const [formStartDate, setFormStartDate] = useState('15-Oct-2026');
  const [formEndDate, setFormEndDate] = useState('16-Oct-2026');
  const [formDayType, setFormDayType] = useState<'Full day' | 'Half day'>('Full day');
  const [formLeaveType, setFormLeaveType] = useState<OptimaLeaveTracker['leave_type']>('WFH (Approved)');
  const [formComments, setFormComments] = useState('');

  const loadLeaves = () => {
    ApiClient.leaves.getAll().then(res => setLeaves(res.data));
  };

  useEffect(() => {
    loadLeaves();
  }, []);

  const monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  const handlePrevMonth = () => {
    if (currentMonth === 0) {
      setCurrentMonth(11);
      setCurrentYear(prev => prev - 1);
    } else {
      setCurrentMonth(prev => prev - 1);
    }
  };

  const handleNextMonth = () => {
    if (currentMonth === 11) {
      setCurrentMonth(0);
      setCurrentYear(prev => prev + 1);
    } else {
      setCurrentMonth(prev => prev + 1);
    }
  };

  const handleToday = () => {
    setCurrentMonth(9); // October
    setCurrentYear(2026);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    ApiClient.leaves.saveLeave({
      name: formName,
      role: formRole,
      start_date: formStartDate,
      end_date: formEndDate,
      day: formDayType,
      leave_type: formLeaveType,
      comments: formComments || 'Logged via Leaves / WFH Tracker'
    }).then(res => {
      setLeaves(prev => [res.data, ...prev]);
      setAddModalOpen(false);
      showToast('Record Saved', `Saved ${formLeaveType} for ${formName}`);
    });
  };

  // Calendar math
  const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
  const firstDayOfWeek = new Date(currentYear, currentMonth, 1).getDay(); // 0 = Sun

  const daysArray = Array.from({ length: daysInMonth }, (_, i) => i + 1);
  const paddingArray = Array.from({ length: firstDayOfWeek }, (_, i) => i);

  // Helper to test if a leave spans a specific day of this month
  const getEntriesForDay = (day: number) => {
    return leaves.filter(l => {
      // Very simple parsing for mock dates like "05-Oct-2026"
      const startParts = l.start_date.split('-');
      const endParts = l.end_date.split('-');
      const startDay = parseInt(startParts[0], 10);
      const endDay = parseInt(endParts[0], 10);
      return day >= startDay && day <= endDay;
    });
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Leaves / WFH Tracker
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Month calendar of scheduled leaves, approved remote work days, and corporate holidays.
          </p>
        </div>

        {/* Charcoal Pill Button (LW-1) */}
        <button
          type="button"
          onClick={() => setAddModalOpen(true)}
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors"
        >
          <Plus className="w-4 h-4" />
          <span>Add Leave / WFH</span>
        </button>
      </div>

      {/* Calendar Navigation & Month Bar (LW-2 - NO LICENSE KEY BANNER!) */}
      <div className="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-4 pb-2 border-b border-slate-100">
          <div className="flex items-center gap-3">
            <h2 className="text-lg font-bold text-[#191627]">
              {monthNames[currentMonth]} {currentYear}
            </h2>
            <button
              type="button"
              onClick={handleToday}
              className="px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-semibold transition-colors"
            >
              Today
            </button>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={handlePrevMonth}
              className="p-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors"
              title="Previous Month"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={handleNextMonth}
              className="p-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors"
              title="Next Month"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Day Header Row */}
        <div className="grid grid-cols-7 gap-2 text-center text-xs font-bold text-slate-400 uppercase tracking-wider py-1">
          <span>Sun</span>
          <span>Mon</span>
          <span>Tue</span>
          <span>Wed</span>
          <span>Thu</span>
          <span>Fri</span>
          <span>Sat</span>
        </div>

        {/* Calendar Month Grid */}
        <div className="grid grid-cols-7 gap-2">
          {paddingArray.map(i => (
            <div key={`pad-${i}`} className="min-h-[96px] bg-slate-50/50 rounded-2xl p-1.5 border border-dashed border-slate-100 opacity-40" />
          ))}

          {daysArray.map(day => {
            const dayEntries = getEntriesForDay(day);
            const isToday = currentMonth === 9 && day === 21; // Reference date: 21 Oct 2026
            const maxVisible = 2;
            const visible = dayEntries.slice(0, maxVisible);
            const overflow = dayEntries.length - maxVisible;

            return (
              <div
                key={day}
                className={`min-h-[105px] rounded-2xl p-2 border transition-all flex flex-col justify-between ${
                  isToday
                    ? 'bg-violet-50/40 border-[#7B3AEC]/40 shadow-xs'
                    : 'bg-white border-slate-100 hover:border-slate-300'
                }`}
              >
                {/* Day Number */}
                <div className="flex items-center justify-between mb-1">
                  <span
                    className={`text-xs font-mono font-bold ${
                      isToday ? 'w-5 h-5 rounded-full bg-[#7B3AEC] text-white flex items-center justify-center text-[11px]' : 'text-slate-700'
                    }`}
                  >
                    {day}
                  </span>
                  {dayEntries.length > 0 && (
                    <span className="text-[10px] text-slate-400 font-mono">
                      {dayEntries.length} {dayEntries.length === 1 ? 'entry' : 'entries'}
                    </span>
                  )}
                </div>

                {/* Entry Bars (LW-3 & LW-4) */}
                <div className="space-y-1 flex-1">
                  {visible.map(entry => {
                    const style = getLeaveTypeStyle(entry.leave_type);
                    return (
                      <div
                        key={entry.id}
                        title={`${entry.name} (${entry.leave_type}) - ${entry.comments || ''}`}
                        className={`px-2 py-0.5 rounded-lg text-[10px] font-semibold truncate border ${style.bg} ${style.text} ${style.border}`}
                      >
                        {entry.name.split(' ')[0]} ({entry.leave_type})
                      </div>
                    );
                  })}

                  {/* +N More Link (LW-6) */}
                  {overflow > 0 && (
                    <button
                      type="button"
                      onClick={() => setSelectedDayEntries({ day, entries: dayEntries })}
                      className="text-[10px] font-bold text-[#7B3AEC] hover:underline block text-left"
                    >
                      +{overflow} more
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Legend directly under the calendar (LW-5) */}
        <div className="pt-4 border-t border-slate-100 flex flex-wrap items-center justify-center gap-6 text-xs font-semibold">
          <div className="flex items-center gap-2">
            <span className="w-3.5 h-3.5 rounded-md bg-[#D946EF]/20 border border-[#D946EF]" />
            <span className="text-slate-700">Holiday</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-3.5 h-3.5 rounded-md bg-[#A3D801]/30 border border-[#84B500]" />
            <span className="text-slate-700">WFH (Approved)</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-3.5 h-3.5 rounded-md bg-[#00A6FC]/20 border border-[#00A6FC] border-dashed" />
            <span className="text-slate-700">WFH (Unapproved)</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-3.5 h-3.5 rounded-md bg-[#7B3AEC]/20 border border-[#7B3AEC]" />
            <span className="text-slate-700">Leave</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="w-3.5 h-3.5 rounded-md bg-amber-100 border border-amber-500" />
            <span className="text-slate-700">Unplanned Leave</span>
          </div>
        </div>
      </div>

      {/* Overflow Day Popover Modal (LW-6) */}
      {selectedDayEntries && (
        <Modal
          isOpen={!!selectedDayEntries}
          onClose={() => setSelectedDayEntries(null)}
          title={`Scheduled Entries for ${selectedDayEntries.day} ${monthNames[currentMonth]} ${currentYear}`}
          maxWidth="sm"
        >
          <div className="space-y-2">
            {selectedDayEntries.entries.map(entry => {
              const style = getLeaveTypeStyle(entry.leave_type);
              return (
                <div
                  key={entry.id}
                  className={`p-3 rounded-2xl border ${style.bg} ${style.border} text-xs space-y-1`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-slate-800">{entry.name}</span>
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-semibold ${style.text}`}>
                      {entry.leave_type}
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-500">{entry.role} · {entry.day}</p>
                  {entry.comments && (
                    <p className="text-[11px] text-slate-700 pt-1 border-t border-black/5">{entry.comments}</p>
                  )}
                </div>
              );
            })}
            <div className="flex justify-end pt-2">
              <button
                type="button"
                onClick={() => setSelectedDayEntries(null)}
                className="px-4 py-2 bg-[#191627] text-white rounded-full text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add New Leave Modal (LW-1) */}
      {addModalOpen && (
        <Modal
          isOpen={addModalOpen}
          onClose={() => setAddModalOpen(false)}
          title="Record Leave / WFH / Holiday"
          subtitle="Submit time off request or remote work notification"
          maxWidth="md"
        >
          <form onSubmit={handleSave} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Name</label>
              <input
                type="text"
                required
                value={formName}
                onChange={e => setFormName(e.target.value)}
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Role</label>
                <input
                  type="text"
                  value={formRole}
                  onChange={e => setFormRole(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Day Type</label>
                <select
                  value={formDayType}
                  onChange={e => setFormDayType(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Full day">Full day</option>
                  <option value="Half day">Half day</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Start Date</label>
                <input
                  type="text"
                  value={formStartDate}
                  onChange={e => setFormStartDate(e.target.value)}
                  placeholder="15-Oct-2026"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">End Date</label>
                <input
                  type="text"
                  value={formEndDate}
                  onChange={e => setFormEndDate(e.target.value)}
                  placeholder="16-Oct-2026"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Type</label>
              <select
                value={formLeaveType}
                onChange={e => setFormLeaveType(e.target.value as any)}
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              >
                <option value="Planned Leave">Planned Leave</option>
                <option value="Unplanned Leave">Unplanned Leave</option>
                <option value="WFH (Approved)">WFH (Approved)</option>
                <option value="WFH (Unapproved)">WFH (Unapproved)</option>
                <option value="Holiday">Holiday</option>
              </select>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Comments / Reason</label>
              <textarea
                rows={2}
                value={formComments}
                onChange={e => setFormComments(e.target.value)}
                placeholder="Reason for absence or remote work plan..."
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
                Save Entry
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
