import React, { useState, useEffect } from 'react';
import { GanttChartSquare, Filter, Layers, Users, Calendar } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FeedbackHistory } from '../../api/types';
import { PROJECTS } from '../../api/mockData';
import { FilterBar } from '../../components/common/FilterBar';
import { getStatusStyle } from '../../utils/colorTokens';

export const TimelineView: React.FC = () => {
  const [tasks, setTasks] = useState<FeedbackHistory[]>([]);
  const [groupBy, setGroupBy] = useState<'project' | 'owner'>('project');
  const [selectedProject, setSelectedProject] = useState('All');
  const [selectedStatus, setSelectedStatus] = useState('All');

  useEffect(() => {
    ApiClient.tasks.getAll({
      project: selectedProject !== 'All' ? selectedProject : undefined,
      status: selectedStatus !== 'All' ? selectedStatus : undefined
    }).then(res => {
      setTasks(res.data);
    });
  }, [selectedProject, selectedStatus]);

  // Days in sprint timeline (October 1 to 31)
  const sprintDays = Array.from({ length: 31 }, (_, i) => i + 1);

  // Group tasks
  const groups: Record<string, FeedbackHistory[]> = {};
  tasks.forEach(t => {
    const key = groupBy === 'project' ? t.project : t.task_owner;
    if (!groups[key]) groups[key] = [];
    groups[key].push(t);
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Timeline View (Gantt)
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Gantt chart timeline mapping task bars across sprint dates, grouped by project or owner.
          </p>
        </div>

        {/* Group By Toggle */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-slate-200/80 rounded-2xl shadow-2xs text-xs">
          <button
            type="button"
            onClick={() => setGroupBy('project')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all ${
              groupBy === 'project' ? 'bg-[#7B3AEC] text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            <span>Group by Project</span>
          </button>
          <button
            type="button"
            onClick={() => setGroupBy('owner')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all ${
              groupBy === 'owner' ? 'bg-[#7B3AEC] text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Users className="w-3.5 h-3.5" />
            <span>Group by Owner</span>
          </button>
        </div>
      </div>

      {/* Filter Row */}
      <FilterBar>
        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500">Project:</label>
          <select
            value={selectedProject}
            onChange={e => setSelectedProject(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Projects</option>
            {PROJECTS.map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </div>

        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500">Status:</label>
          <select
            value={selectedStatus}
            onChange={e => setSelectedStatus(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Statuses</option>
            <option value="In Progress">In Progress</option>
            <option value="Open">Open</option>
            <option value="Closed">Closed</option>
            <option value="On Hold">On Hold</option>
          </select>
        </div>

        <div className="text-xs text-slate-500 font-medium ml-auto">
          Sprint Period: <strong className="text-slate-800">October 2026</strong>
        </div>
      </FilterBar>

      {/* Gantt Timeline Container */}
      <div className="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
        {/* Timeline Day Header */}
        <div className="overflow-x-auto">
          <div className="min-w-[1100px]">
            {/* Header row */}
            <div className="flex items-center bg-slate-50 border-b border-slate-200 py-3 text-xs font-semibold text-slate-500">
              <div className="w-72 px-4 shrink-0 font-bold uppercase tracking-wider text-[11px]">
                {groupBy === 'project' ? 'Project / Ticket' : 'Owner / Ticket'}
              </div>
              <div className="flex-1 grid grid-cols-31 text-center font-mono text-[10px]">
                {sprintDays.map(d => (
                  <div key={d} className={`py-0.5 ${d === 21 ? 'bg-[#7B3AEC]/10 text-[#7B3AEC] font-bold rounded' : ''}`}>
                    {d}
                  </div>
                ))}
              </div>
            </div>

            {/* Groups Body */}
            <div className="divide-y divide-slate-100">
              {Object.keys(groups).map(groupName => (
                <div key={groupName} className="py-2">
                  {/* Group Header */}
                  <div className="px-4 py-2 bg-slate-50/70 font-bold text-xs text-[#191627] flex items-center justify-between">
                    <span>{groupName}</span>
                    <span className="text-[11px] font-mono text-slate-400 font-normal">
                      {groups[groupName].length} tasks
                    </span>
                  </div>

                  {/* Task Bars in this group */}
                  <div className="space-y-1.5 py-1.5">
                    {groups[groupName].map(task => {
                      const startDay = parseInt(task.estimated_start_date.split('-')[0], 10) || 1;
                      const endDay = Math.min(31, parseInt(task.estimated_close_date.split('-')[0], 10) || 14);
                      const duration = Math.max(1, endDay - startDay + 1);
                      const leftPercent = ((startDay - 1) / 31) * 100;
                      const widthPercent = (duration / 31) * 100;

                      const style = getStatusStyle(task.status);

                      return (
                        <div key={task.id} className="flex items-center hover:bg-slate-50/80 py-1 transition-colors group">
                          {/* Left Ticket Info */}
                          <div className="w-72 px-4 shrink-0 truncate text-xs flex items-center gap-2">
                            <span className="font-mono font-bold text-[#7B3AEC] text-[11px] shrink-0">
                              {task.ticket_number}
                            </span>
                            <span className="truncate text-slate-700" title={task.subject}>
                              {task.subject}
                            </span>
                          </div>

                          {/* Right Gantt Bar Grid */}
                          <div className="flex-1 relative h-7 flex items-center">
                            {/* Grid vertical lines */}
                            <div className="absolute inset-0 grid grid-cols-31 pointer-events-none opacity-20 divide-x divide-slate-300 h-full" />

                            {/* Task Bar */}
                            <div
                              style={{
                                left: `${leftPercent}%`,
                                width: `${widthPercent}%`,
                              }}
                              className={`absolute h-6 rounded-lg border px-2 flex items-center justify-between text-[10px] font-semibold truncate shadow-2xs group-hover:shadow-xs transition-all ${style.bg} ${style.text} ${style.border}`}
                              title={`${task.ticket_number}: ${task.subject} (${task.status}) - ${task.task_owner}`}
                            >
                              <span className="truncate">{task.task_owner.split(' ')[0]} ({task.actual_effort}h)</span>
                              <span className="font-mono text-[9px] opacity-75 shrink-0 hidden sm:inline-block">
                                {task.status}
                              </span>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
