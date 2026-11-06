import React, { useState } from 'react';
import {
  PieChart,
  BarChart2,
  Clock,
  Palmtree,
  Calendar,
  AlertTriangle,
  Users,
  Sparkles,
  Layers,
  Activity,
  Flame,
  CheckCircle2,
  TrendingUp,
  TrendingDown,
  Gauge,
  Briefcase,
  AlertCircle,
  ShieldCheck,
  Sun,
  Moon
} from 'lucide-react';
import { FilterBar } from '../../components/common/FilterBar';
import { DateRangeBox } from '../../components/common/DateRangeBox';
import { PROJECTS, TEAMS, PEOPLE_NOT_FILLING_TASKS } from '../../api/mockData';
import { useToast } from '../../components/common/Toast';
import { useAppTheme } from '../../context/ThemeContext';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const ReportsPage: React.FC = () => {
  const { showToast } = useToast();
  const { theme: themeMode, setTheme: setThemeMode, isGlass } = useAppTheme();

  // Active Tab State (Original 4 + 3 New Dashboards Requested by User)
  const [activeTab, setActiveTab] = useState<
    'status' | 'effort' | 'meetings' | 'leaves' | 'taskType' | 'workload' | 'sprintHealth'
  >('taskType');

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

  // Dynamic CSS classes depending on Glassmorphism toggle
  const cardBgClass = isGlass
    ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 backdrop-blur-2xl border border-white/15 shadow-[0_20px_50px_rgba(0,0,0,0.6),inset_0_1px_1px_rgba(255,255,255,0.25)] text-white'
    : 'bg-white border border-slate-200/80 shadow-xs text-[#191627]';

  const headingTextClass = isGlass ? 'text-white' : 'text-[#191627]';
  const subTextClass = isGlass ? 'text-zinc-400' : 'text-slate-500';
  const subtleBorderClass = isGlass ? 'border-white/10' : 'border-slate-100';
  const pillActiveClass = isGlass
    ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold shadow-md shadow-amber-500/30'
    : 'bg-[#7B3AEC] text-white shadow-xs';
  const pillInactiveClass = isGlass
    ? 'text-zinc-300 hover:text-white hover:bg-white/10'
    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50';

  return (
    <div className={`space-y-6 transition-all duration-300 ${isGlass ? 'p-6 -m-6 rounded-3xl bg-[#090D1A] min-h-screen text-white' : ''}`}>
      {/* 
        ====================================================================
        PAGE HEADER & GLASSMORPHISM TOGGLE SWITCH
        ====================================================================
      */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className={`text-xl font-extrabold tracking-tight ${headingTextClass}`}>
              Reports &amp; Analytics
            </h1>
            {isGlass && (
              <span className="text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30 px-2 py-0.5 rounded-full flex items-center gap-1">
                <Sparkles className="w-3 h-3 text-amber-400" />
                <span>Cinematic Glass</span>
              </span>
            )}
          </div>
          <p className={`text-xs mt-0.5 ${subTextClass}`}>
            Cross-module operational reporting, task type splits, engineering capacity, and sprint SLA telemetry.
          </p>
        </div>

        {/* Theme Switcher Toggle & Module Upload Trigger */}
        <div className="flex flex-wrap items-center gap-3">
          <ModuleUploadTrigger
            moduleName="Reports & Analytics"
            defaultProject={selectedProject === 'All Projects' ? 'AEC Kuwait' : selectedProject}
            buttonLabel="Upload Analytics / Telemetry Data"
          />

          <div className={`flex items-center p-1 rounded-2xl border transition-colors ${isGlass ? 'bg-black/60 border-white/20' : 'bg-slate-100 border-slate-200'}`}>
            <button
              type="button"
              onClick={() => {
                setThemeMode('crisp');
                showToast('Light Theme', 'Switched to crisp light dashboard mode', 'info');
              }}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                !isGlass ? 'bg-white text-slate-900 shadow-sm' : 'text-zinc-400 hover:text-white'
              }`}
            >
              <Sun className="w-3.5 h-3.5 text-amber-500" />
              <span>Crisp Light</span>
            </button>
            <button
              type="button"
              onClick={() => {
                setThemeMode('glass');
                showToast('Glassmorphism', 'Switched to frosted luxury glass mode', 'info');
              }}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                isGlass ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-black shadow-md shadow-amber-500/30' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <Moon className="w-3.5 h-3.5 text-black" />
              <span>Frosted Glass</span>
            </button>
          </div>
        </div>
      </div>

      {/* 
        ====================================================================
        NAVIGATION TABS: 7 ADVANCED DASHBOARDS
        Including 3 New Ones Requested by User:
        - Task Type & Velocity Analytics (Bug/Feature/Refactor split)
        - Engineering Effort & Workload Telemetry (Hours & capacity)
        - Sprint Health & SLA Quality Dashboard (Blockers & P0/P1 aging)
        ====================================================================
      */}
      <div className={`p-1.5 rounded-2xl border overflow-x-auto flex items-center gap-1.5 text-xs ${
        isGlass ? 'bg-white/[0.04] border-white/15 backdrop-blur-xl' : 'bg-white border-slate-200/80 shadow-2xs'
      }`}>
        {/* NEW DASHBOARD 1: Task Type & Velocity */}
        <button
          type="button"
          onClick={() => setActiveTab('taskType')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-bold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'taskType' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <Layers className="w-3.5 h-3.5" />
          <span>Task Type &amp; Velocity</span>
          <span className={`text-[9px] px-1.5 py-0.2 rounded-full font-bold ${
            isGlass ? 'bg-black/40 text-amber-200' : 'bg-white/30 text-white'
          }`}>New</span>
        </button>

        {/* NEW DASHBOARD 2: Effort & Workload */}
        <button
          type="button"
          onClick={() => setActiveTab('workload')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-bold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'workload' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <Briefcase className="w-3.5 h-3.5" />
          <span>Effort &amp; Workload Telemetry</span>
          <span className={`text-[9px] px-1.5 py-0.2 rounded-full font-bold ${
            isGlass ? 'bg-black/40 text-amber-200' : 'bg-white/30 text-white'
          }`}>New</span>
        </button>

        {/* NEW DASHBOARD 3: Sprint Health & SLA */}
        <button
          type="button"
          onClick={() => setActiveTab('sprintHealth')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-bold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'sprintHealth' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <ShieldCheck className="w-3.5 h-3.5" />
          <span>Sprint Health &amp; SLA</span>
          <span className={`text-[9px] px-1.5 py-0.2 rounded-full font-bold ${
            isGlass ? 'bg-black/40 text-amber-200' : 'bg-white/30 text-white'
          }`}>New</span>
        </button>

        <div className={`h-4 w-px mx-1 ${isGlass ? 'bg-white/20' : 'bg-slate-200'}`} />

        {/* Original DB-1: Status Overview */}
        <button
          type="button"
          onClick={() => setActiveTab('status')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'status' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <PieChart className="w-3.5 h-3.5" />
          <span>Status Overview</span>
        </button>

        {/* Original DB-2: Daily Effort */}
        <button
          type="button"
          onClick={() => setActiveTab('effort')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'effort' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <BarChart2 className="w-3.5 h-3.5" />
          <span>Daily Effort</span>
        </button>

        {/* Original DB-3: Meeting Time */}
        <button
          type="button"
          onClick={() => setActiveTab('meetings')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'meetings' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <Clock className="w-3.5 h-3.5" />
          <span>Meeting Time</span>
        </button>

        {/* Original DB-4: Leaves / WFH */}
        <button
          type="button"
          onClick={() => setActiveTab('leaves')}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl font-semibold transition-all whitespace-nowrap cursor-pointer ${
            activeTab === 'leaves' ? pillActiveClass : pillInactiveClass
          }`}
        >
          <Palmtree className="w-3.5 h-3.5" />
          <span>Leaves / WFH</span>
        </button>
      </div>

      {/* 
        ====================================================================
        FILTER ROW
        ====================================================================
      */}
      <FilterBar onSearch={handleSearch} onClear={handleClear} showClear>
        <div className="flex items-center gap-2">
          <label className={`text-xs font-semibold ${subTextClass}`}>Project:</label>
          <select
            value={selectedProject}
            onChange={e => setSelectedProject(e.target.value)}
            className={`border rounded-xl px-3 py-1.5 text-xs font-medium focus:outline-none ${
              isGlass
                ? 'bg-black/50 border-white/20 text-white focus:border-amber-400'
                : 'bg-slate-50 border-slate-200 text-slate-700 focus:border-[#7B3AEC]'
            }`}
          >
            <option value="All Projects">All Projects</option>
            {PROJECTS.map(p => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </div>

        {activeTab === 'effort' && (
          <div className="flex items-center gap-2">
            <label className={`text-xs font-semibold ${subTextClass}`}>Task Owner:</label>
            <select
              value={selectedOwner}
              onChange={e => setSelectedOwner(e.target.value)}
              className={`border rounded-xl px-3 py-1.5 text-xs font-medium focus:outline-none ${
                isGlass
                  ? 'bg-black/50 border-white/20 text-white focus:border-amber-400'
                  : 'bg-slate-50 border-slate-200 text-slate-700 focus:border-[#7B3AEC]'
              }`}
            >
              <option value="Deenesh Sabat">Deenesh Sabat</option>
              <option value="Ajay Kumar">Ajay Kumar</option>
              <option value="Bharat Sharma">Bharat Sharma</option>
              <option value="Vivek Singh">Vivek Singh</option>
            </select>
          </div>
        )}

        <div className="flex items-center gap-2">
          <label className={`text-xs font-semibold ${subTextClass}`}>Team:</label>
          <select
            value={selectedTeam}
            onChange={e => setSelectedTeam(e.target.value)}
            className={`border rounded-xl px-3 py-1.5 text-xs font-medium focus:outline-none ${
              isGlass
                ? 'bg-black/50 border-white/20 text-white focus:border-amber-400'
                : 'bg-slate-50 border-slate-200 text-slate-700 focus:border-[#7B3AEC]'
            }`}
          >
            <option value="All Teams">All Teams</option>
            {TEAMS.map(t => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <DateRangeBox startDate={startDate} endDate={endDate} onChange={(s, e) => { setStartDate(s); setEndDate(e); }} />
      </FilterBar>

      {/* 
        ====================================================================
        NEW DASHBOARD 1: TASK TYPE & VELOCITY ANALYTICS
        Features Bug / Feature / Tech Debt / Maintenance Breakdown
        ====================================================================
      */}
      {activeTab === 'taskType' && (
        <div className="space-y-6">
          {/* 4 Summary Stat Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className={`${cardBgClass} rounded-3xl p-5 relative overflow-hidden`}>
              <div className="flex items-center justify-between mb-2">
                <span className={`text-xs font-semibold ${subTextClass}`}>Features &amp; Enhancements</span>
                <span className="p-1.5 bg-sky-500/20 text-sky-400 rounded-xl">
                  <Sparkles className="w-4 h-4" />
                </span>
              </div>
              <div className="flex items-baseline justify-between">
                <span className="text-2xl font-black font-mono">148</span>
                <span className="text-xs font-bold text-sky-400 font-mono">46.2% share</span>
              </div>
              <div className="w-full bg-white/10 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-[#00A6FC] h-full rounded-full" style={{ width: '46.2%' }} />
              </div>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 relative overflow-hidden`}>
              <div className="flex items-center justify-between mb-2">
                <span className={`text-xs font-semibold ${subTextClass}`}>Bugs &amp; Hotfixes</span>
                <span className="p-1.5 bg-rose-500/20 text-rose-400 rounded-xl">
                  <Flame className="w-4 h-4" />
                </span>
              </div>
              <div className="flex items-baseline justify-between">
                <span className="text-2xl font-black font-mono">84</span>
                <span className="text-xs font-bold text-rose-400 font-mono">26.3% share</span>
              </div>
              <div className="w-full bg-white/10 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-rose-500 h-full rounded-full" style={{ width: '26.3%' }} />
              </div>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 relative overflow-hidden`}>
              <div className="flex items-center justify-between mb-2">
                <span className={`text-xs font-semibold ${subTextClass}`}>Architecture &amp; Refactor</span>
                <span className="p-1.5 bg-violet-500/20 text-violet-400 rounded-xl">
                  <Layers className="w-4 h-4" />
                </span>
              </div>
              <div className="flex items-baseline justify-between">
                <span className="text-2xl font-black font-mono">56</span>
                <span className="text-xs font-bold text-violet-400 font-mono">17.5% share</span>
              </div>
              <div className="w-full bg-white/10 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-[#7B3AEC] h-full rounded-full" style={{ width: '17.5%' }} />
              </div>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 relative overflow-hidden`}>
              <div className="flex items-center justify-between mb-2">
                <span className={`text-xs font-semibold ${subTextClass}`}>Security &amp; Compliance</span>
                <span className="p-1.5 bg-emerald-500/20 text-emerald-400 rounded-xl">
                  <ShieldCheck className="w-4 h-4" />
                </span>
              </div>
              <div className="flex items-baseline justify-between">
                <span className="text-2xl font-black font-mono">32</span>
                <span className="text-xs font-bold text-emerald-400 font-mono">10.0% share</span>
              </div>
              <div className="w-full bg-white/10 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-[#A3D801] h-full rounded-full" style={{ width: '10%' }} />
              </div>
            </div>
          </div>

          {/* Central Visualization: Sprint Velocity & Task Type Distribution */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className={`lg:col-span-7 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <div className="flex items-center justify-between">
                <div>
                  <h3 className={`text-sm font-bold ${headingTextClass}`}>Weekly Sprint Velocity by Task Type</h3>
                  <p className={`text-xs ${subTextClass}`}>Completed story points delivered per week over Sprint 14 to 17.</p>
                </div>
                <span className="text-xs font-mono font-bold px-2.5 py-1 rounded-full bg-sky-500/20 text-sky-400">
                  Avg 42 pts/wk
                </span>
              </div>

              {/* Stacked Bar Velocity Chart */}
              <div className="h-60 flex items-end justify-around gap-6 pt-6 pb-2 border-b border-white/10">
                {[
                  { sprint: 'Sprint 14', feature: 22, bug: 12, refactor: 8, sec: 4, total: 46 },
                  { sprint: 'Sprint 15', feature: 26, bug: 8, refactor: 6, sec: 5, total: 45 },
                  { sprint: 'Sprint 16', feature: 18, bug: 14, refactor: 10, sec: 6, total: 48 },
                  { sprint: 'Sprint 17 (Current)', feature: 28, bug: 6, refactor: 12, sec: 4, total: 50 },
                ].map(v => (
                  <div key={v.sprint} className="flex-1 flex flex-col items-center gap-1.5 group relative">
                    <span className="text-xs font-mono font-bold text-sky-400">{v.total} pts</span>
                    <div className="w-14 sm:w-16 flex flex-col justify-end h-44 rounded-xl overflow-hidden bg-white/5 border border-white/10">
                      <div style={{ height: `${(v.sec / v.total) * 100}%` }} className="bg-[#A3D801] w-full" title={`Security: ${v.sec}`} />
                      <div style={{ height: `${(v.refactor / v.total) * 100}%` }} className="bg-[#7B3AEC] w-full" title={`Refactor: ${v.refactor}`} />
                      <div style={{ height: `${(v.bug / v.total) * 100}%` }} className="bg-rose-500 w-full" title={`Bugs: ${v.bug}`} />
                      <div style={{ height: `${(v.feature / v.total) * 100}%` }} className="bg-[#00A6FC] w-full" title={`Features: ${v.feature}`} />
                    </div>
                    <span className={`text-[11px] font-semibold ${subTextClass} text-center`}>{v.sprint}</span>
                  </div>
                ))}
              </div>

              <div className="flex flex-wrap items-center justify-between gap-3 text-xs pt-1">
                <div className="flex items-center gap-4">
                  <span className="flex items-center gap-1.5"><span className="w-3 h-3 rounded bg-[#00A6FC]" /> Features</span>
                  <span className="flex items-center gap-1.5"><span className="w-3 h-3 rounded bg-rose-500" /> Bugs</span>
                  <span className="flex items-center gap-1.5"><span className="w-3 h-3 rounded bg-[#7B3AEC]" /> Refactor</span>
                  <span className="flex items-center gap-1.5"><span className="w-3 h-3 rounded bg-[#A3D801]" /> Security</span>
                </div>
                <span className={`font-mono ${subTextClass}`}>Total Velocity: 189 Story Points</span>
              </div>
            </div>

            {/* Task Type Donut Distribution */}
            <div className={`lg:col-span-5 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <div className="flex items-center justify-between">
                <h3 className={`text-sm font-bold ${headingTextClass}`}>Work Type Proportion</h3>
                <span className="text-xs font-mono text-zinc-400">Total: 320 Tasks</span>
              </div>

              <div className="flex flex-col items-center justify-center py-4">
                <div className="relative w-40 h-40 flex items-center justify-center">
                  <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                    {/* Features: 46.2% = 110.3 */}
                    <circle cx="50" cy="50" r="38" stroke="#00A6FC" strokeWidth="12" strokeDasharray="110.3 128.4" strokeDashoffset="0" fill="transparent" />
                    {/* Bugs: 26.3% = 62.8 */}
                    <circle cx="50" cy="50" r="38" stroke="#F43F5E" strokeWidth="12" strokeDasharray="62.8 175.9" strokeDashoffset="-110.3" fill="transparent" />
                    {/* Refactor: 17.5% = 41.8 */}
                    <circle cx="50" cy="50" r="38" stroke="#7B3AEC" strokeWidth="12" strokeDasharray="41.8 196.9" strokeDashoffset="-173.1" fill="transparent" />
                    {/* Security: 10% = 23.8 */}
                    <circle cx="50" cy="50" r="38" stroke="#A3D801" strokeWidth="12" strokeDasharray="23.8 214.9" strokeDashoffset="-214.9" fill="transparent" />
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                    <span className="text-2xl font-black font-mono">320</span>
                    <span className="text-[10px] text-zinc-400 uppercase font-semibold">Logged Tasks</span>
                  </div>
                </div>

                <div className="w-full space-y-2 mt-4 text-xs">
                  <div className="flex items-center justify-between">
                    <span className="flex items-center gap-2"><span className="w-2.5 h-2.5 rounded-full bg-[#00A6FC]" /> Features &amp; New APIs</span>
                    <span className="font-mono font-bold">148 (46.2%)</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="flex items-center gap-2"><span className="w-2.5 h-2.5 rounded-full bg-rose-500" /> Bugs &amp; Regression</span>
                    <span className="font-mono font-bold">84 (26.3%)</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="flex items-center gap-2"><span className="w-2.5 h-2.5 rounded-full bg-[#7B3AEC]" /> Architecture &amp; Refactor</span>
                    <span className="font-mono font-bold">56 (17.5%)</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="flex items-center gap-2"><span className="w-2.5 h-2.5 rounded-full bg-[#A3D801]" /> SecOps &amp; Compliance</span>
                    <span className="font-mono font-bold">32 (10.0%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 
        ====================================================================
        NEW DASHBOARD 2: ENGINEERING EFFORT & WORKLOAD TELEMETRY
        Capacity utilization, logged hours vs baseline, and overtime trends
        ====================================================================
      */}
      {activeTab === 'workload' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className={`${cardBgClass} rounded-3xl p-5 space-y-2`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>Total Hours Logged (Month)</span>
              <div className="flex items-baseline justify-between">
                <span className="text-3xl font-black font-mono">2,975.8h</span>
                <span className="text-xs font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full">
                  94.2% Capacity
                </span>
              </div>
              <p className={`text-[11px] ${subTextClass}`}>Target capacity: 3,160 hours across 20 working days.</p>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 space-y-2`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>Average Daily Output</span>
              <div className="flex items-baseline justify-between">
                <span className="text-3xl font-black font-mono">7.8h</span>
                <span className="text-xs font-bold text-sky-400 bg-sky-500/10 px-2 py-0.5 rounded-full">
                  Healthy Zone
                </span>
              </div>
              <p className={`text-[11px] ${subTextClass}`}>Standard 8h working model with 1.2h daily meeting load.</p>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 space-y-2`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>Engineering Overtime (Spillover)</span>
              <div className="flex items-baseline justify-between">
                <span className="text-3xl font-black font-mono">46.5h</span>
                <span className="text-xs font-bold text-amber-400 bg-amber-500/10 px-2 py-0.5 rounded-full">
                  1.5% of Total
                </span>
              </div>
              <p className={`text-[11px] ${subTextClass}`}>Concentrated during release deployment on 18-Oct-2026.</p>
            </div>
          </div>

          {/* Engineer Capacity Grid with Utilization Meters */}
          <div className={`${cardBgClass} rounded-3xl p-6 space-y-4`}>
            <div className="flex items-center justify-between">
              <div>
                <h3 className={`text-sm font-bold ${headingTextClass}`}>Engineer Capacity &amp; Utilization Matrix</h3>
                <p className={`text-xs ${subTextClass}`}>Actual logged hours compared against standard 160h monthly capacity.</p>
              </div>
              <span className="text-xs font-mono font-bold text-emerald-400">11 Active Engineers</span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 py-2">
              {[
                { name: 'Deenesh Sabat', role: 'Super Admin / Lead', logged: 168.5, capacity: 160, project: 'AEC Kuwait', status: 'Optimal' },
                { name: 'Bharat Sharma', role: 'Lead Architect', logged: 162.0, capacity: 160, project: 'NextGen Core', status: 'Optimal' },
                { name: 'Vivek Singh', role: 'Senior Backend', logged: 156.0, capacity: 160, project: 'FinServe', status: 'Balanced' },
                { name: 'Priya Patel', role: 'Frontend Engineer', logged: 152.5, capacity: 160, project: 'TeleHealth Hub', status: 'Balanced' },
                { name: 'Ajay Kumar', role: 'Product Manager', logged: 172.0, capacity: 160, project: 'Operations', status: 'High' },
                { name: 'Neha Gupta', role: 'UI/UX Lead', logged: 144.0, capacity: 160, project: 'Design System', status: 'Balanced' },
                { name: 'Siddharth Sen', role: 'Python Developer', logged: 158.0, capacity: 160, project: 'ML Telemetry', status: 'Balanced' },
                { name: 'Rohan Sharma', role: 'QA Automation Lead', logged: 164.0, capacity: 160, project: 'E2E Suites', status: 'Optimal' },
                { name: 'Kavita Joshi', role: 'DevOps Specialist', logged: 150.0, capacity: 160, project: 'Infra & Cloud', status: 'Balanced' },
              ].map(eng => {
                const util = Math.round((eng.logged / eng.capacity) * 100);
                const isOver = util > 105;
                return (
                  <div key={eng.name} className={`p-4 rounded-2xl border transition-all ${
                    isGlass ? 'bg-white/[0.03] border-white/10 hover:border-white/20' : 'bg-slate-50 border-slate-100'
                  }`}>
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-xs font-bold">{eng.name}</span>
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        isOver ? 'bg-amber-500/20 text-amber-300' : 'bg-emerald-500/20 text-emerald-300'
                      }`}>
                        {eng.status} ({util}%)
                      </span>
                    </div>
                    <p className={`text-[11px] ${subTextClass} mb-2`}>{eng.role} · {eng.project}</p>
                    <div className="space-y-1">
                      <div className="flex justify-between text-[10px] font-mono">
                        <span className={subTextClass}>Logged: {eng.logged}h</span>
                        <span className={subTextClass}>Cap: {eng.capacity}h</span>
                      </div>
                      <div className="w-full h-2 bg-white/10 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full ${isOver ? 'bg-amber-400' : 'bg-[#00A6FC]'}`}
                          style={{ width: `${Math.min(util, 100)}%` }}
                        />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* 
        ====================================================================
        NEW DASHBOARD 3: SPRINT HEALTH & SLA QUALITY DASHBOARD
        Blocker tracking, SLA breach risk, P0/P1 aging, and QA Pass Rates
        ====================================================================
      */}
      {activeTab === 'sprintHealth' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <div className={`${cardBgClass} rounded-3xl p-5 space-y-1 border-l-4 border-l-emerald-500`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>Sprint Health Score</span>
              <div className="text-3xl font-black font-mono text-emerald-400">92 / 100</div>
              <p className={`text-[11px] ${subTextClass}`}>Quality index based on velocity, test passes, and zero P0 blockers.</p>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 space-y-1 border-l-4 border-l-sky-500`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>QA Test Pass Rate</span>
              <div className="text-3xl font-black font-mono text-sky-400">98.4%</div>
              <p className={`text-[11px] ${subTextClass}`}>1,248 automated regression tests passed in CI/CD pipeline.</p>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 space-y-1 border-l-4 border-l-amber-500`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>Active Blockers</span>
              <div className="text-3xl font-black font-mono text-amber-400">2 Blockers</div>
              <p className={`text-[11px] ${subTextClass}`}>External client staging credentials awaiting approvals.</p>
            </div>

            <div className={`${cardBgClass} rounded-3xl p-5 space-y-1 border-l-4 border-l-rose-500`}>
              <span className={`text-xs font-semibold ${subTextClass}`}>SLA At-Risk Tickets</span>
              <div className="text-3xl font-black font-mono text-rose-400">1 Ticket</div>
              <p className={`text-[11px] ${subTextClass}`}>AECKUWAIT-423 approaching 48h resolution SLA threshold.</p>
            </div>
          </div>

          {/* SLA Aging Matrix Table */}
          <div className={`${cardBgClass} rounded-3xl p-6 space-y-4`}>
            <div className="flex items-center justify-between">
              <div>
                <h3 className={`text-sm font-bold ${headingTextClass}`}>Critical SLA Resolution Aging Matrix</h3>
                <p className={`text-xs ${subTextClass}`}>Monitoring turnaround times for high-priority production defects.</p>
              </div>
              <span className="text-xs font-mono font-bold text-rose-400">0 Overdue Breaches</span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className={`border-b ${subtleBorderClass} ${subTextClass} font-semibold uppercase tracking-wider text-[10px]`}>
                    <th className="py-2.5 px-3">Ticket ID</th>
                    <th className="py-2.5 px-3">Subject</th>
                    <th className="py-2.5 px-3">Priority</th>
                    <th className="py-2.5 px-3">Assigned Owner</th>
                    <th className="py-2.5 px-3">Age</th>
                    <th className="py-2.5 px-3">SLA Status</th>
                  </tr>
                </thead>
                <tbody className={`divide-y ${subtleBorderClass}`}>
                  {[
                    { id: 'AEC-109', title: 'Payment Gateway Webhook Timeout', prio: 'Critical P0', owner: 'Vivek Singh', age: '14 hrs', sla: 'Within SLA (24h target)', statusColor: 'text-emerald-400 bg-emerald-500/10' },
                    { id: 'NEXT-312', title: 'OAuth Token Invalidation on Refresh', prio: 'Urgent P1', owner: 'Deenesh Sabat', age: '22 hrs', sla: 'Within SLA (48h target)', statusColor: 'text-emerald-400 bg-emerald-500/10' },
                    { id: 'FIN-504', title: 'Export PDF Table Overflow on iPad', prio: 'Normal P2', owner: 'Priya Patel', age: '36 hrs', sla: 'Within SLA (72h target)', statusColor: 'text-sky-400 bg-sky-500/10' },
                    { id: 'AEC-423', title: 'Staging Environment CORS Handshake', prio: 'Urgent P1', owner: 'Bharat Sharma', age: '41 hrs', sla: 'At Risk (Approaching 48h)', statusColor: 'text-amber-400 bg-amber-500/10' },
                  ].map(row => (
                    <tr key={row.id} className={isGlass ? 'hover:bg-white/[0.04]' : 'hover:bg-slate-50'}>
                      <td className="py-3 px-3 font-mono font-bold text-[#7B3AEC]">{row.id}</td>
                      <td className="py-3 px-3 font-medium">{row.title}</td>
                      <td className="py-3 px-3">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                          row.prio.includes('P0') ? 'bg-rose-500/20 text-rose-400' :
                          row.prio.includes('P1') ? 'bg-fuchsia-500/20 text-fuchsia-400' : 'bg-sky-500/20 text-sky-400'
                        }`}>
                          {row.prio}
                        </span>
                      </td>
                      <td className="py-3 px-3">{row.owner}</td>
                      <td className="py-3 px-3 font-mono">{row.age}</td>
                      <td className="py-3 px-3">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${row.statusColor}`}>
                          {row.sla}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* 
        ====================================================================
        ORIGINAL DB-1: STATUS OVERVIEW
        ====================================================================
      */}
      {activeTab === 'status' && (
        <div className="space-y-6">
          <div className={`p-4 rounded-2xl border flex items-center justify-between gap-4 ${
            isGlass ? 'bg-amber-500/10 border-amber-500/30 text-amber-200' : 'bg-amber-50 border-amber-200'
          }`}>
            <div className="flex items-center gap-3">
              <div className="p-2 bg-amber-500 text-white rounded-xl">
                <AlertTriangle className="w-4 h-4" />
              </div>
              <div>
                <p className={`text-xs font-bold ${isGlass ? 'text-amber-300' : 'text-amber-900'}`}>
                  People not Filling Tasks in Project Management Tool ({PEOPLE_NOT_FILLING_TASKS.count})
                </p>
                <p className={`text-[11px] mt-0.5 ${isGlass ? 'text-amber-200/80' : 'text-amber-800/80'}`}>
                  {PEOPLE_NOT_FILLING_TASKS.people.join(', ')}
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className={`lg:col-span-6 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <div className="flex items-center justify-between">
                <h3 className={`text-sm font-bold ${headingTextClass}`}>Status Overview (Total 320 items)</h3>
                <span className="text-xs font-mono text-slate-400">Oct 2026</span>
              </div>

              <div className="flex flex-col sm:flex-row items-center justify-around gap-6 py-4">
                <div className="relative w-44 h-44 shrink-0 flex items-center justify-center">
                  <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="38" stroke="#A3D801" strokeWidth="12" strokeDasharray="238.7" strokeDashoffset="0" fill="transparent" />
                    <circle cx="50" cy="50" r="38" stroke="#00A6FC" strokeWidth="12" strokeDasharray="23.8 214.9" strokeDashoffset="-214" fill="transparent" />
                    <circle cx="50" cy="50" r="38" stroke="#7B3AEC" strokeWidth="12" strokeDasharray="2 236.7" strokeDashoffset="-236" fill="transparent" />
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                    <span className="text-2xl font-black font-mono">320</span>
                    <span className={`text-[10px] uppercase font-semibold ${subTextClass}`}>Total Items</span>
                  </div>
                </div>

                <div className="space-y-3 text-xs w-full sm:w-auto">
                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#A3D801]" />
                      <span className="font-semibold">Closed</span>
                    </div>
                    <span className="font-mono font-bold">287 (89.7%)</span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#00A6FC]" />
                      <span className="font-semibold">In Progress</span>
                    </div>
                    <span className="font-mono font-bold">32 (10.0%)</span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full bg-[#7B3AEC]" />
                      <span className="font-semibold">Open</span>
                    </div>
                    <span className="font-mono font-bold">1 (0.3%)</span>
                  </div>
                </div>
              </div>
            </div>

            <div className={`lg:col-span-6 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <div className="flex items-center justify-between">
                <h3 className={`text-sm font-bold ${headingTextClass}`}>Priority Breakdown</h3>
                <span className="text-xs font-mono text-slate-400">Total: 320</span>
              </div>

              <div className="space-y-4 py-2">
                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-red-400 font-bold">Critical P0</span>
                    <span className="font-mono">4 tasks (1.2%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-red-600 rounded-full" style={{ width: '4%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-fuchsia-400 font-bold">Urgent P1</span>
                    <span className="font-mono">112 tasks (35.0%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-[#D946EF] rounded-full" style={{ width: '35%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-sky-400 font-bold">Normal P2</span>
                    <span className="font-mono">203 tasks (63.4%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-[#00A6FC] rounded-full" style={{ width: '63.4%' }} />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-zinc-400 font-bold">Low P3</span>
                    <span className="font-mono">1 task (0.3%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-white/10 rounded-full overflow-hidden">
                    <div className="h-full bg-zinc-500 rounded-full" style={{ width: '2%' }} />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 
        ====================================================================
        ORIGINAL DB-2: DAILY EFFORT
        ====================================================================
      */}
      {activeTab === 'effort' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className={`lg:col-span-8 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <div className="flex items-center justify-between">
                <div>
                  <h3 className={`text-sm font-bold ${headingTextClass}`}>Daily Effort Hours: {selectedOwner}</h3>
                  <p className={`text-[11px] ${subTextClass}`}>Split by Project and Meeting Time (136.00 hrs for the month)</p>
                </div>
                <span className="text-xs font-mono font-bold bg-violet-500/20 text-[#7B3AEC] px-2.5 py-1 rounded-full">
                  136.00 hrs
                </span>
              </div>

              <div className="h-56 flex items-end gap-2 pt-6 pb-2 border-b border-white/10 px-2 overflow-x-auto">
                {Array.from({ length: 20 }).map((_, i) => {
                  const day = i + 1;
                  const projectHours = (i % 3 === 0 ? 5 : i % 2 === 0 ? 6 : 7);
                  const meetingHours = (i % 4 === 0 ? 2 : 1);
                  const total = projectHours + meetingHours;
                  return (
                    <div key={day} className="flex-1 min-w-[20px] flex flex-col items-center gap-1 group relative">
                      <div className="w-full flex flex-col justify-end h-40 bg-white/5 rounded-t-lg overflow-hidden">
                        <div className="w-full bg-[#00A6FC]" style={{ height: `${meetingHours * 10}%` }} title="Meeting Time" />
                        <div className="w-full bg-[#7B3AEC]" style={{ height: `${projectHours * 10}%` }} title="Project Work" />
                      </div>
                      <span className={`text-[10px] font-mono ${subTextClass}`}>{day}</span>
                    </div>
                  );
                })}
              </div>

              <div className="flex items-center justify-between text-xs pt-2">
                <div className="flex items-center gap-4">
                  <span className="flex items-center gap-1"><span className="w-2.5 h-2.5 rounded-full bg-[#7B3AEC]" /> Project Tasks</span>
                  <span className="flex items-center gap-1"><span className="w-2.5 h-2.5 rounded-full bg-[#00A6FC]" /> Meeting Time</span>
                </div>
                <div className="font-semibold">
                  Total Month Effort: <span className="font-mono font-bold text-[#7B3AEC]">2975.78 hrs</span>
                </div>
              </div>
            </div>

            <div className={`lg:col-span-4 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
              <h3 className={`text-sm font-bold ${headingTextClass}`}>Effort by Project</h3>
              <div className="space-y-3 py-2 text-xs">
                {[
                  { name: 'AEC Kuwait', hours: '1240.50 hrs', pct: 42, color: 'bg-[#7B3AEC]' },
                  { name: 'NextGen', hours: '820.25 hrs', pct: 28, color: 'bg-[#00A6FC]' },
                  { name: 'FinServe Global', hours: '450.00 hrs', pct: 15, color: 'bg-[#A3D801]' },
                  { name: 'TeleHealth Hub', hours: '280.03 hrs', pct: 9, color: 'bg-[#D946EF]' },
                  { name: 'Others', hours: '185.00 hrs', pct: 6, color: 'bg-zinc-600' },
                ].map(p => (
                  <div key={p.name} className="space-y-1">
                    <div className="flex justify-between font-semibold">
                      <span>{p.name}</span>
                      <span className="font-mono">{p.hours}</span>
                    </div>
                    <div className="w-full h-2 bg-white/10 rounded-full overflow-hidden">
                      <div className={`h-full ${p.color} rounded-full`} style={{ width: `${p.pct}%` }} />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 
        ====================================================================
        ORIGINAL DB-3: MEETING TIME
        ====================================================================
      */}
      {activeTab === 'meetings' && (
        <div className={`${cardBgClass} rounded-3xl p-6 space-y-6`}>
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div>
              <h3 className={`text-sm font-bold ${headingTextClass}`}>Meeting Time per Engineer (Minutes &amp; Hours)</h3>
              <p className={`text-xs ${subTextClass}`}>Stacked duration by project with ordered 5-colour palette.</p>
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
                  <span className="font-semibold">{m.name}</span>
                  <span className="font-mono font-bold text-[#7B3AEC]">{m.label} ({m.totalMin}m)</span>
                </div>
                <div className="w-full h-4 bg-white/10 rounded-full overflow-hidden flex">
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

      {/* 
        ====================================================================
        ORIGINAL DB-4: LEAVES / WFH
        ====================================================================
      */}
      {activeTab === 'leaves' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className={`lg:col-span-6 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
            <h3 className={`text-sm font-bold ${headingTextClass}`}>Leaves / WFH Distribution Overview</h3>
            <div className="space-y-4 py-3 text-xs">
              <div className={`p-3.5 rounded-2xl border flex items-center justify-between ${
                isGlass ? 'bg-violet-500/10 border-violet-500/30' : 'bg-violet-50/60 border-violet-100'
              }`}>
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-[#7B3AEC]" />
                  <span className="font-semibold">Planned Leave</span>
                </div>
                <span className="font-mono font-bold text-[#7B3AEC] text-sm">28 days</span>
              </div>

              <div className={`p-3.5 rounded-2xl border flex items-center justify-between ${
                isGlass ? 'bg-lime-500/10 border-lime-500/30' : 'bg-lime-50/60 border-lime-100'
              }`}>
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-[#A3D801]" />
                  <span className="font-semibold">WFH Approved</span>
                </div>
                <span className="font-mono font-bold text-[#A3D801] text-sm">14 days</span>
              </div>

              <div className={`p-3.5 rounded-2xl border flex items-center justify-between ${
                isGlass ? 'bg-amber-500/10 border-amber-500/30' : 'bg-amber-50/60 border-amber-100'
              }`}>
                <div className="flex items-center gap-2.5">
                  <div className="w-3.5 h-3.5 rounded-full bg-amber-500" />
                  <span className="font-semibold">Unplanned Leave</span>
                </div>
                <span className="font-mono font-bold text-amber-400 text-sm">1 day</span>
              </div>
            </div>
          </div>

          <div className={`lg:col-span-6 ${cardBgClass} rounded-3xl p-6 space-y-4`}>
            <h3 className={`text-sm font-bold ${headingTextClass}`}>Leave &amp; WFH Monthly Summary</h3>
            <p className={`text-xs ${subTextClass}`}>Breakdown of absence types during the active sprint window.</p>
            <div className="py-6 space-y-4">
              <div className="flex items-center justify-between text-xs">
                <span>Total Absence Days</span>
                <span className="font-mono font-bold">43 days</span>
              </div>
              <div className="w-full h-4 bg-white/10 rounded-full overflow-hidden flex">
                <div className="h-full bg-[#7B3AEC]" style={{ width: '65%' }} title="Planned Leave: 28 days" />
                <div className="h-full bg-[#A3D801]" style={{ width: '32.5%' }} title="WFH Approved: 14 days" />
                <div className="h-full bg-amber-500" style={{ width: '2.5%' }} title="Unplanned Leave: 1 day" />
              </div>
              <div className={`flex items-center justify-between text-[11px] ${subTextClass} pt-2`}>
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
