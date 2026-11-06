import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Calendar as CalendarIcon, Info, Filter } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FeedbackHistory } from '../../api/types';
import { PROJECTS } from '../../api/mockData';
import { getStatusStyle } from '../../utils/colorTokens';
import { Modal } from '../../components/common/Modal';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const TaskCalendarView: React.FC = () => {
  const [tasks, setTasks] = useState<FeedbackHistory[]>([]);
  const [selectedProject, setSelectedProject] = useState('AEC Kuwait'); // CV-1
  const [activeStatusFilter, setActiveStatusFilter] = useState<string>('All');

  // Month navigation (October 2026) (CV-3)
  const [currentYear, setCurrentYear] = useState(2026);
  const [currentMonth, setCurrentMonth] = useState(9); // 9 = October

  // Day overflow modal (CV-5)
  const [overflowModal, setOverflowModal] = useState<{ day: number; tasks: FeedbackHistory[] } | null>(null);

  // Hover tooltip state (CV-6)
  const [hoveredTask, setHoveredTask] = useState<{
    task: FeedbackHistory;
    x: number;
    y: number;
  } | null>(null);

  useEffect(() => {
    ApiClient.tasks.getAll({ project: selectedProject }).then(res => {
      setTasks(res.data);
    });
  }, [selectedProject]);

  const monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  const handlePrev = () => {
    if (currentMonth === 0) {
      setCurrentMonth(11);
      setCurrentYear(prev => prev - 1);
    } else {
      setCurrentMonth(prev => prev - 1);
    }
  };

  const handleNext = () => {
    if (currentMonth === 11) {
      setCurrentMonth(0);
      setCurrentYear(prev => prev + 1);
    } else {
      setCurrentMonth(prev => prev + 1);
    }
  };

  const handleToday = () => {
    setCurrentMonth(9);
    setCurrentYear(2026);
  };

  const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
  const firstDay = new Date(currentYear, currentMonth, 1).getDay();

  const daysArray = Array.from({ length: daysInMonth }, (_, i) => i + 1);
  const paddingArray = Array.from({ length: firstDay }, (_, i) => i);

  // Filter tasks
  const projectTasks = tasks.filter(t => {
    if (activeStatusFilter !== 'All' && t.status !== activeStatusFilter) return false;
    return true;
  });

  const getTasksForDay = (day: number) => {
    return projectTasks.filter(t => {
      const startParts = t.estimated_start_date.split('-');
      const endParts = t.estimated_close_date.split('-');
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
            Calendar View
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Tasks rendered as multi-day bars across the month calendar to highlight team delivery overlaps.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <ModuleUploadTrigger
            moduleName="Calendar View"
            defaultProject={selectedProject}
            buttonLabel="Upload Schedule / Attachment"
          />

          {/* Project Selector (CV-1: Fixed typo from 'Prcject' to 'Project') */}
          <div className="flex items-center gap-2 bg-white px-3.5 py-1.5 rounded-2xl border border-slate-200/80 shadow-2xs">
            <label className="text-xs font-bold text-slate-700 whitespace-nowrap">Project:</label>
            <select
              value={selectedProject}
              onChange={e => setSelectedProject(e.target.value)}
              className="bg-transparent text-xs font-semibold text-[#7B3AEC] focus:outline-none cursor-pointer"
            >
              {PROJECTS.map(p => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* CV-2: Status panel with four chips: Closed, In Progress, Open, On Hold */}
      <div className="bg-white rounded-3xl p-4 border border-slate-200/80 shadow-xs flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <span className="text-xs font-semibold text-slate-500 mr-1">Filter by Status:</span>
          <button
            type="button"
            onClick={() => setActiveStatusFilter('All')}
            className={`px-3 py-1 rounded-full text-xs font-semibold transition-all ${
              activeStatusFilter === 'All' ? 'bg-[#191627] text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            All
          </button>
          <button
            type="button"
            onClick={() => setActiveStatusFilter('Closed')}
            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border transition-all ${
              activeStatusFilter === 'Closed'
                ? 'bg-[#A3D801] text-[#191627] border-[#84B500] shadow-xs'
                : 'bg-[#A3D801]/15 text-[#4D6B00] border-[#A3D801]/30 hover:bg-[#A3D801]/25'
            }`}
          >
            <span className="w-2 h-2 rounded-full bg-[#4D6B00]" />
            <span>Closed</span>
          </button>
          <button
            type="button"
            onClick={() => setActiveStatusFilter('In Progress')}
            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border transition-all ${
              activeStatusFilter === 'In Progress'
                ? 'bg-[#00A6FC] text-white border-[#0082CA] shadow-xs'
                : 'bg-[#00A6FC]/15 text-[#0082CA] border-[#00A6FC]/30 hover:bg-[#00A6FC]/25'
            }`}
          >
            <span className="w-2 h-2 rounded-full bg-[#00A6FC]" />
            <span>In Progress</span>
          </button>
          <button
            type="button"
            onClick={() => setActiveStatusFilter('Open')}
            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border transition-all ${
              activeStatusFilter === 'Open'
                ? 'bg-[#7B3AEC] text-white border-[#6D28D9] shadow-xs'
                : 'bg-[#7B3AEC]/15 text-[#7B3AEC] border-[#7B3AEC]/30 hover:bg-[#7B3AEC]/25'
            }`}
          >
            <span className="w-2 h-2 rounded-full bg-[#7B3AEC]" />
            <span>Open</span>
          </button>
          <button
            type="button"
            onClick={() => setActiveStatusFilter('On Hold')}
            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border transition-all ${
              activeStatusFilter === 'On Hold'
                ? 'bg-[#D946EF] text-white border-[#B024C5] shadow-xs'
                : 'bg-[#D946EF]/15 text-[#B024C5] border-[#D946EF]/30 hover:bg-[#D946EF]/25'
            }`}
          >
            <span className="w-2 h-2 rounded-full bg-[#D946EF]" />
            <span>On Hold</span>
          </button>
        </div>

        <div className="text-xs text-slate-500 font-medium">
          Showing <strong className="text-slate-800">{projectTasks.length}</strong> tasks in {selectedProject}
        </div>
      </div>

      {/* Month Calendar Grid (CV-3: No license banner!) */}
      <div className="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
        {/* Navigation Bar */}
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
              onClick={handlePrev}
              className="p-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors"
              title="Previous Month"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={handleNext}
              className="p-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors"
              title="Next Month"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Days of week header */}
        <div className="grid grid-cols-7 gap-2 text-center text-xs font-bold text-slate-400 uppercase tracking-wider py-1">
          <span>Sun</span>
          <span>Mon</span>
          <span>Tue</span>
          <span>Wed</span>
          <span>Thu</span>
          <span>Fri</span>
          <span>Sat</span>
        </div>

        {/* 7-column calendar matrix */}
        <div className="grid grid-cols-7 gap-2">
          {paddingArray.map(i => (
            <div key={`pad-${i}`} className="min-h-[105px] bg-slate-50/50 rounded-2xl border border-dashed border-slate-100 opacity-40" />
          ))}

          {daysArray.map(day => {
            const dayTasks = getTasksForDay(day);
            const isToday = currentMonth === 9 && day === 21;
            const maxVisible = 2;
            const visible = dayTasks.slice(0, maxVisible);
            const overflow = dayTasks.length - maxVisible;

            return (
              <div
                key={day}
                className={`min-h-[110px] rounded-2xl p-2 border transition-all flex flex-col justify-between ${
                  isToday
                    ? 'bg-violet-50/30 border-[#7B3AEC]/40 shadow-xs'
                    : 'bg-white border-slate-100 hover:border-slate-300'
                }`}
              >
                <div className="flex items-center justify-between mb-1">
                  <span
                    className={`text-xs font-mono font-bold ${
                      isToday ? 'w-5 h-5 rounded-full bg-[#7B3AEC] text-white flex items-center justify-center text-[11px]' : 'text-slate-700'
                    }`}
                  >
                    {day}
                  </span>
                  {dayTasks.length > 0 && (
                    <span className="text-[10px] text-slate-400 font-mono">
                      {dayTasks.length} {dayTasks.length === 1 ? 'task' : 'tasks'}
                    </span>
                  )}
                </div>

                {/* Task Bars (CV-4: Owner – Ticket, CV-6: Hover tooltip) */}
                <div className="space-y-1 flex-1">
                  {visible.map(task => {
                    const style = getStatusStyle(task.status);
                    return (
                      <div
                        key={task.id}
                        onMouseEnter={e => {
                          const rect = e.currentTarget.getBoundingClientRect();
                          setHoveredTask({
                            task,
                            x: rect.left,
                            y: rect.top - 8
                          });
                        }}
                        onMouseLeave={() => setHoveredTask(null)}
                        className={`px-2 py-0.5 rounded-lg text-[10px] font-semibold truncate border cursor-pointer transition-transform hover:scale-[1.02] ${style.bg} ${style.text} ${style.border}`}
                      >
                        {task.task_owner.split(' ')[0]} – {task.ticket_number}
                      </div>
                    );
                  })}

                  {/* +N More Link (CV-5) */}
                  {overflow > 0 && (
                    <button
                      type="button"
                      onClick={() => setOverflowModal({ day, tasks: dayTasks })}
                      className="text-[10px] font-bold text-[#7B3AEC] hover:underline block text-left pt-0.5"
                    >
                      +{overflow} more
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Floating Hover Tooltip (CV-6: shows full owner, ticket, status and dates) */}
      {hoveredTask && (
        <div
          style={{
            position: 'fixed',
            left: `${hoveredTask.x}px`,
            top: `${hoveredTask.y}px`,
            transform: 'translateY(-100%)',
            pointerEvents: 'none'
          }}
          className="z-50 bg-[#191627] text-white rounded-2xl p-3 shadow-xl max-w-xs space-y-1 border border-slate-700 animate-in fade-in duration-100"
        >
          <div className="flex items-center justify-between gap-2 border-b border-slate-700 pb-1">
            <span className="font-mono font-bold text-[#A3D801] text-xs">
              {hoveredTask.task.ticket_number}
            </span>
            <span className="text-[10px] px-2 py-0.5 rounded-full bg-white/10 text-white font-medium">
              {hoveredTask.task.status}
            </span>
          </div>
          <p className="text-xs font-semibold leading-tight pt-1">
            {hoveredTask.task.subject}
          </p>
          <div className="text-[11px] text-slate-300 space-y-0.5 pt-1">
            <p><span className="text-slate-400">Owner:</span> {hoveredTask.task.task_owner}</p>
            <p><span className="text-slate-400">Dates:</span> {hoveredTask.task.estimated_start_date} – {hoveredTask.task.estimated_close_date}</p>
            <p><span className="text-slate-400">Effort:</span> {hoveredTask.task.actual_effort}h / {hoveredTask.task.estimated_effort}h</p>
          </div>
        </div>
      )}

      {/* Overflow Day Modal (CV-5) */}
      {overflowModal && (
        <Modal
          isOpen={!!overflowModal}
          onClose={() => setOverflowModal(null)}
          title={`Tasks Scheduled for ${overflowModal.day} ${monthNames[currentMonth]} ${currentYear}`}
          subtitle={`${overflowModal.tasks.length} total tasks active on this day`}
          maxWidth="md"
        >
          <div className="space-y-2.5 max-h-96 overflow-y-auto">
            {overflowModal.tasks.map(task => {
              const style = getStatusStyle(task.status);
              return (
                <div
                  key={task.id}
                  className={`p-3 rounded-2xl border ${style.bg} ${style.border} text-xs space-y-1`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-mono font-bold text-slate-800">{task.ticket_number}</span>
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-semibold ${style.text}`}>
                      {task.status}
                    </span>
                  </div>
                  <p className="font-medium text-slate-800">{task.subject}</p>
                  <p className="text-[11px] text-slate-500">
                    Owner: {task.task_owner} · Team: {task.team} · Effort: {task.actual_effort}h
                  </p>
                </div>
              );
            })}
            <div className="flex justify-end pt-2">
              <button
                type="button"
                onClick={() => setOverflowModal(null)}
                className="px-4 py-2 bg-[#191627] text-white rounded-full text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
