import React, { useState } from 'react';
import {
  PieChart,
  BarChart2,
  Clock,
  Palmtree,
  Calendar,
  AlertTriangle,
  Users
} from 'lucide-react';
import { FilterBar } from '../../components/common/FilterBar';
import { DateRangeBox } from '../../components/common/DateRangeBox';
import { PROJECTS, TEAMS, PEOPLE_NOT_FILLING_TASKS } from '../../api/mockData';
import { useToast } from '../../components/common/Toast';

export const ReportsPage: React.FC = () => {
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'status' | 'effort' | 'meetings' | 'leaves'>('status');

  // Filters state
  const [selectedProject, setSelectedProject] = useState('All Projects');
  const [selectedTeam, setSelectedTeam] = useState('All Teams');
  const [selectedOwner, setSelectedOwner] = useState('Deenesh Sabat');
  const [startDate, setStartDate] = useState('01-Oct-2026');
  const [endDate, setEndDate] = useState('31-Oct-2026');

  const handleSearch = () => {
    showToast('Filters Applied', `Updated report metrics for ${selectedProject}`);
  };

  const handleClear = () => {
    setSelectedProject('All Projects');
    setSelectedTeam('All Teams');
    setSelectedOwner('Deenesh Sabat');
    showToast('Filters Reset', 'Default report views restored', 'info');
  };

  return (
    <div className="space-y-6">
      {/* Page Title & Navigation Tabs */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Reports & Analytics
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Cross-module operational reporting for status, engineering effort, meeting time, and leave telemetry.
          </p>
        </div>

        {/* 4 Chart View Tabs (DB-1, DB-2, DB-3, DB-4) */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-slate-200/80 rounded-2xl shadow-2xs text-xs">
          <button
            type="button"
            onClick={() => setActiveTab('status')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl font-semibold transition-all ${
              activeTab === 'status'
                ? 'bg-[#7B3AEC] text-white shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <PieChart className="w-3.5 h-3.5" />
            <span>Status Overview</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('effort')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl font-semibold transition-all ${
              activeTab === 'effort'
                ? 'bg-[#7B3AEC] text-white shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <BarChart2 className="w-3.5 h-3.5" />
            <span>Effort</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('meetings')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl font-semibold transition-all ${
              activeTab === 'meetings'
                ? 'bg-[#7B3AEC] text-white shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Clock className="w-3.5 h-3.5" />
            <span>Meeting Time</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveTab('leaves')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl font-semibold transition-all ${
              activeTab === 'leaves'
                ? 'bg-[#7B3AEC] text-white shadow-xs'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Palmtree className="w-3.5 h-3.5" />
            <span>Leaves / WFH</span>
          </button>
        </div>
      </div>

      {/* Filter Row */}
      <FilterBar onSearch={handleSearch} onClear={handleClear} showClear>
        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500">Project:</label>
          <select
            value={selectedProject}
            onChange={e => setSelectedProject(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All Projects">All Projects</option>
            {PROJECTS.map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </div>

        {activeTab === 'effort' && (
          <div className="flex items-center gap-2">
            <label className="text-xs font-semibold text-slate-500">Task Owner:</label>
            <select
              value={selectedOwner}
              onChange={e => setSelectedOwner(e.target.value)}
              className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
            >
              <option value="Deenesh Sabat">Deenesh Sabat</option>
              <option value="Ajay Kumar">Ajay Kumar</option>
              <option value="Bharat Sharma">Bharat Sharma</option>
              <option value="Vivek Singh">Vivek Singh</option>
            </select>
          </div>
        )}

        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500">Team:</label>
          <select
            value={selectedTeam}
            onChange={e => setSelectedTeam(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All Teams">All Teams</option>
            {TEAMS.map(t => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <DateRangeBox startDate={startDate} endDate={endDate} onChange={(s, e) => { setStartDate(s); setEndDate(e); }} />
      </FilterBar>

      {/* VIEW 1: DB-1 STATUS OVERVIEW */}
      {activeTab === 'status' && (
        <div className="space-y-6">
          {/* Amber Alert: People not filling tasks in project management tool (DB-1) */}
          <div className="bg-amber-50 border border-amber-200 rounded-2xl p-4 flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-amber-500 text-white rounded-xl">
                <AlertTriangle className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs font-bold text-amber-900">
                  People not Filling Tasks in Project Management Tool ({PEOPLE_NOT_FILLING_TASKS.count})
                </p>
                <p className="text-[11px] text-amber-800/80 mt-0.5">
                  {PEOPLE_NOT_FILLING_TASKS.people.join(', ')}
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            {/* Status Donut (320 items: 287 Closed, 32 In Progress, 1 Open) */}
            <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-bold text-[#191627]">Status Overview (Total 320 items)</h3>
                <span className="text-xs font-mono text-slate-400">Oct 2026</span>
              </div>

              <div className="flex flex-col sm:flex-row items-center justify-around gap-6 py-4">
                {/* Visual SVG Donut */}
                <div className="relative w-44 h-44 shrink-0 flex items-center justify-center">
                  <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                    {/* Closed: 287 / 320 = ~89.7% */}
                    <circle cx="50" cy="50" r="38" stroke="#A3D801" strokeWidth="12" strokeDasharray="238.7" strokeDashoffset="0" fill="transparent" />
                    {/* In Progress: 32 / 320 = 10% */}
                    <circle cx="50" cy="50" r="38" stroke="#00A6FC" strokeWidth="12" strokeDasharray="23.8 214.9" strokeDashoffset="-214" fill="transparent" />
                    {/* Open: 1 / 320 = 0.3% */}
                    <circle cx="50" cy="50" r="38" stroke="#7B3AEC" strokeWidth="12" strokeDasharray="2 236.7" strokeDashoffset="-236" fill="transparent" />
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                    <span className="text-2xl font-black text-[#191627] font-mono">320</span>
                    <span className="text-[10px] text-slate-400 uppercase font-semibold">Total Items</span>
                  </div>
                </div>

                {/* Legend with Written Out Status Names */}
                <div className="space-y-3 text-xs w-full sm:w-auto">
                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#A3D801]" />
                      <span className="font-semibold text-slate-700">Closed</span>
                    </div>
                    <span className="font-mono font-bold text-slate-900">287 (89.7%)</span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#00A6FC]" />
                      <span className="font-semibold text-slate-700">In Progress</span>
                    </div>
                    <span className="font-mono font-bold text-slate-900">32 (10.0%)</span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#7B3AEC]" />
                      <span className="font-semibold text-slate-700">Open</span>
                    </div>
                    <span className="font-mono font-bold text-slate-900">1 (0.3%)</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Priority Breakdown (P0: 4, P1: 112, P2: 203, Low P3: 1) */}
            <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-bold text-[#191627]">Priority Breakdown</h3>
                <span className="text-xs font-mono text-slate-400">Total: 320</span>
              </div>

              <div className="space-y-4 py-2">
                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-red-700">Critical P0</span>
                    <span className="font-mono">4 tasks (1.2%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div className="h-full bg-red-600 rounded-full" style={{ width: '4%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-[#B024C5]">Urgent P1</span>
                    <span className="font-mono">112 tasks (35.0%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div className="h-full bg-[#D946EF] rounded-full" style={{ width: '35%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-sky-700">Normal P2</span>
                    <span className="font-mono">203 tasks (63.4%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div className="h-full bg-[#00A6FC] rounded-full" style={{ width: '63.4%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-slate-600">Low P3</span>
                    <span className="font-mono">1 task (0.3%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div className="h-full bg-slate-400 rounded-full" style={{ width: '2%' }} />
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Team Workload (Tasks per person, one person has 69) */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-[#191627]">Team Workload (Tasks Handled per Engineer)</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {[
                { name: 'Deenesh Sabat', count: 69, role: 'Production Engineer', top: true },
                { name: 'Bharat Sharma', count: 48, role: 'Lead Architect' },
                { name: 'Vivek Singh', count: 42, role: 'Backend Engineer' },
                { name: 'Priya Patel', count: 38, role: 'Frontend Engineer' },
                { name: 'Ajay Kumar', count: 35, role: 'Product Manager' },
                { name: 'Neha Gupta', count: 32, role: 'UI/UX Designer' },
                { name: 'Siddharth Sen', count: 30, role: 'Python Developer' },
                { name: 'Rohan Sharma', count: 26, role: 'QA Engineer' },
              ].map(w => (
                <div
                  key={w.name}
                  className={`p-4 rounded-2xl border transition-all ${
                    w.top ? 'bg-violet-50/60 border-violet-200 shadow-2xs' : 'bg-slate-50 border-slate-100'
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-bold text-[#191627]">{w.name}</span>
                    <span className="font-mono font-black text-sm text-[#7B3AEC]">{w.count}</span>
                  </div>
                  <p className="text-[11px] text-slate-500">{w.role}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* VIEW 2: DB-2 EFFORT */}
      {activeTab === 'effort' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            {/* Daily Bars for Chosen Owner */}
            <div className="lg:col-span-8 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-bold text-[#191627]">Daily Effort Hours: {selectedOwner}</h3>
                  <p className="text-[11px] text-slate-500">Split by Project and Meeting Time (136.00 hrs for the month)</p>
                </div>
                <span className="text-xs font-mono font-bold bg-violet-100 text-[#7B3AEC] px-2.5 py-1 rounded-full">
                  136.00 hrs
                </span>
              </div>

              {/* Bar visualization */}
              <div className="h-56 flex items-end gap-2 pt-6 pb-2 border-b border-slate-100 px-2 overflow-x-auto">
                {Array.from({ length: 20 }).map((_, i) => {
                  const day = i + 1;
                  const projectHours = (i % 3 === 0 ? 5 : i % 2 === 0 ? 6 : 7);
                  const meetingHours = (i % 4 === 0 ? 2 : 1);
                  const total = projectHours + meetingHours;
                  return (
                    <div key={day} className="flex-1 min-w-[20px] flex flex-col items-center gap-1 group relative">
                      {/* Tooltip on hover */}
                      <div className="absolute -top-10 opacity-0 group-hover:opacity-100 transition-opacity bg-slate-900 text-white text-[10px] rounded px-2 py-1 pointer-events-none whitespace-nowrap z-20">
                        Day {day}: {total}h ({projectHours}h Dev, {meetingHours}h Meeting)
                      </div>
                      <div className="w-full flex flex-col justify-end h-40 bg-slate-50 rounded-t-lg overflow-hidden">
                        <div className="w-full bg-[#00A6FC]" style={{ height: `${meetingHours * 10}%` }} title="Meeting Time" />
                        <div className="w-full bg-[#7B3AEC]" style={{ height: `${projectHours * 10}%` }} title="Project Work" />
                      </div>
                      <span className="text-[10px] font-mono text-slate-400">{day}</span>
                    </div>
                  );
                })}
              </div>

              {/* Grand Total Indicator */}
              <div className="flex items-center justify-between text-xs pt-2">
                <div className="flex items-center gap-4">
                  <span className="flex items-center gap-1 text-slate-600">
                    <span className="w-2.5 h-2.5 rounded-full bg-[#7B3AEC]" /> Project Tasks
                  </span>
                  <span className="flex items-center gap-1 text-slate-600">
                    <span className="w-2.5 h-2.5 rounded-full bg-[#00A6FC]" /> Meeting Time
                  </span>
                </div>
                <div className="font-semibold text-slate-800">
                  Total Month Effort: <span className="font-mono font-bold text-[#7B3AEC]">2975.78 hrs</span>
                </div>
              </div>
            </div>

            {/* Effort Distribution by Project */}
            <div className="lg:col-span-4 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
              <h3 className="text-sm font-bold text-[#191627]">Effort by Project</h3>
              <div className="space-y-3 py-2 text-xs">
                {[
                  { name: 'AEC Kuwait', hours: '1240.50 hrs', pct: 42, color: 'bg-[#7B3AEC]' },
                  { name: 'NextGen', hours: '820.25 hrs', pct: 28, color: 'bg-[#00A6FC]' },
                  { name: 'FinServe Global', hours: '450.00 hrs', pct: 15, color: 'bg-[#A3D801]' },
                  { name: 'TeleHealth Hub', hours: '280.03 hrs', pct: 9, color: 'bg-[#D946EF]' },
                  { name: 'Others', hours: '185.00 hrs', pct: 6, color: 'bg-[#191627]' },
                ].map(p => (
                  <div key={p.name} className="space-y-1">
                    <div className="flex justify-between font-semibold">
                      <span className="text-slate-700">{p.name}</span>
                      <span className="font-mono text-slate-900">{p.hours}</span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                      <div className={`h-full ${p.color} rounded-full`} style={{ width: `${p.pct}%` }} />
                    </div>
                  </div>
                ))}
              </div>
              <div className="pt-3 border-t border-slate-100 text-[11px] text-slate-500 text-center font-mono">
                Grand Total: 2975.78 hrs
              </div>
            </div>
          </div>
        </div>
      )}

      {/* VIEW 3: DB-3 MEETING TIME */}
      {activeTab === 'meetings' && (
        <div className="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-6">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <h3 className="text-sm font-bold text-[#191627]">Meeting Time per Engineer (Minutes & Hours)</h3>
              <p className="text-xs text-slate-500">Stacked duration by project with ordered 5-colour palette.</p>
            </div>
            <div className="flex items-center gap-3 text-xs">
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded bg-[#7B3AEC]" /> AEC Kuwait</span>
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded bg-[#00A6FC]" /> NextGen</span>
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded bg-[#A3D801]" /> FinServe</span>
              <span className="flex items-center gap-1.5"><span className="w-2.5 h-2.5 rounded bg-[#D946EF]" /> Others</span>
            </div>
          </div>

          <div className="space-y-4 py-2">
            {[
              { name: 'Deenesh Sabat', totalMin: 3120, label: '52h', aec: 45, nextgen: 30, fin: 15, oth: 10 },
              { name: 'Bharat Sharma', totalMin: 2880, label: '48h', aec: 50, nextgen: 25, fin: 15, oth: 10 },
              { name: 'Vivek Singh', totalMin: 2280, label: '38h', aec: 40, nextgen: 35, fin: 15, oth: 10 },
              { name: 'Priya Patel', totalMin: 1800, label: '30h', aec: 40, nextgen: 40, fin: 10, oth: 10 },
              { name: 'Ajay Kumar', totalMin: 3300, label: '55h', aec: 50, nextgen: 30, fin: 10, oth: 10 },
              { name: 'Neha Gupta', totalMin: 960, label: '16h', aec: 30, nextgen: 30, fin: 20, oth: 20 },
              { name: 'Alok Gupta', totalMin: 165, label: '2h 45m', aec: 60, nextgen: 20, fin: 10, oth: 10 },
            ].map(m => (
              <div key={m.name} className="space-y-1.5">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-semibold text-slate-800">{m.name}</span>
                  <span className="font-mono font-bold text-[#7B3AEC]">{m.label} ({m.totalMin}m)</span>
                </div>
                <div className="w-full h-4 bg-slate-100 rounded-full overflow-hidden flex">
                  <div style={{ width: `${m.aec}%` }} className="h-full bg-[#7B3AEC]" title={`AEC: ${m.aec}%`} />
                  <div style={{ width: `${m.nextgen}%` }} className="h-full bg-[#00A6FC]" title={`NextGen: ${m.nextgen}%`} />
                  <div style={{ width: `${m.fin}%` }} className="h-full bg-[#A3D801]" title={`FinServe: ${m.fin}%`} />
                  <div style={{ width: `${m.oth}%` }} className="h-full bg-[#D946EF]" title={`Others: ${m.oth}%`} />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* VIEW 4: DB-4 LEAVES / WFH */}
      {activeTab === 'leaves' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-[#191627]">Leaves / WFH Distribution Overview</h3>
            <div className="space-y-4 py-3 text-xs">
              <div className="p-3.5 bg-violet-50/60 rounded-2xl border border-violet-100 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-[#7B3AEC]" />
                  <span className="font-semibold text-slate-800">Planned Leave</span>
                </div>
                <span className="font-mono font-bold text-[#7B3AEC] text-sm">28 days</span>
              </div>

              <div className="p-3.5 bg-lime-50/60 rounded-2xl border border-lime-100 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-[#A3D801]" />
                  <span className="font-semibold text-slate-800">WFH Approved</span>
                </div>
                <span className="font-mono font-bold text-[#4D6B00] text-sm">14 days</span>
              </div>

              <div className="p-3.5 bg-amber-50/60 rounded-2xl border border-amber-100 flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-amber-500" />
                  <span className="font-semibold text-slate-800">Unplanned Leave</span>
                </div>
                <span className="font-mono font-bold text-amber-700 text-sm">1 day</span>
              </div>
            </div>
          </div>

          <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-[#191627]">Leave &amp; WFH Monthly Summary</h3>
            <p className="text-xs text-slate-500">Breakdown of absence types during the active sprint window.</p>
            <div className="py-6 space-y-4">
              <div className="flex items-center justify-between text-xs">
                <span>Total Absence Days</span>
                <span className="font-mono font-bold text-slate-900">43 days</span>
              </div>
              <div className="w-full h-4 bg-slate-100 rounded-full overflow-hidden flex">
                <div className="h-full bg-[#7B3AEC]" style={{ width: '65%' }} title="Planned Leave: 28 days" />
                <div className="h-full bg-[#A3D801]" style={{ width: '32.5%' }} title="WFH Approved: 14 days" />
                <div className="h-full bg-amber-500" style={{ width: '2.5%' }} title="Unplanned Leave: 1 day" />
              </div>
              <div className="flex items-center justify-between text-[11px] text-slate-500 pt-2">
                <span>Planned: 65%</span>
                <span>WFH: 32.5%</span>
                <span>Unplanned: 2.5%</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
