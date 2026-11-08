import React, { useState, useEffect } from 'react';
import {
  Bug,
  CheckCircle2,
  AlertTriangle,
  Clock,
  Plus,
  Search,
  Filter,
  Check,
  RotateCcw,
  ExternalLink,
  ChevronRight,
  ShieldCheck,
  FlaskConical,
  Edit,
  Trash2,
  FileCode2
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { BugRecord, TestCaseRecord, UserAccount } from '../../api/types';
import { Modal } from '../../components/common/Modal';
import { useToast } from '../../components/common/Toast';
import { useAppTheme } from '../../context/ThemeContext';
import { hasPermission } from '../../utils/rbac';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';
import { PROJECTS, MODULES } from '../../api/mockData';

interface QaBugTrackerPageProps {
  currentUser?: UserAccount;
}

const BUG_STATUSES: BugRecord['status'][] = [
  'Open',
  'Assigned',
  'In Progress',
  'Fixed',
  'Ready for Retest',
  'Retest Passed',
  'Reopened',
  'Closed'
];

export const QaBugTrackerPage: React.FC<QaBugTrackerPageProps> = ({ currentUser }) => {
  const { isGlass } = useAppTheme();
  const { showToast } = useToast();

  const [activeTab, setActiveTab] = useState<'bugs' | 'tests'>('bugs');
  const [bugs, setBugs] = useState<BugRecord[]>([]);
  const [testCases, setTestCases] = useState<TestCaseRecord[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [severityFilter, setSeverityFilter] = useState('All');
  const [statusFilter, setStatusFilter] = useState('All');
  const [search, setSearch] = useState('');

  // Bug Modal State
  const [bugModalOpen, setBugModalOpen] = useState(false);
  const [editingBug, setEditingBug] = useState<BugRecord | null>(null);

  // Bug Form fields
  const [title, setTitle] = useState('');
  const [project, setProject] = useState(PROJECTS[0]);
  const [module, setModule] = useState(MODULES[1]);
  const [severity, setSeverity] = useState<BugRecord['severity']>('Medium');
  const [priority, setPriority] = useState<BugRecord['priority']>('Normal P2');
  const [status, setStatus] = useState<BugRecord['status']>('Open');
  const [assignedTo, setAssignedTo] = useState('Bharat Sharma');
  const [linkedTaskId, setLinkedTaskId] = useState('');
  const [reproductionSteps, setReproductionSteps] = useState('');
  const [expectedResult, setExpectedResult] = useState('');
  const [actualResult, setActualResult] = useState('');
  const [environment, setEnvironment] = useState('Chrome 130 / macOS');

  // Test Case Modal State
  const [testModalOpen, setTestModalOpen] = useState(false);
  const [testTitle, setTestTitle] = useState('');
  const [testProject, setTestProject] = useState(PROJECTS[0]);
  const [testSuite, setTestSuite] = useState('Core Functionality');
  const [testStatus, setTestStatus] = useState<TestCaseRecord['status']>('Untested');
  const [testNotes, setTestNotes] = useState('');

  const canCreateBug = hasPermission(currentUser as any, 'qa.bugs.create');
  const canUpdateBug = hasPermission(currentUser as any, 'qa.bugs.update');
  const canCreateTest = hasPermission(currentUser as any, 'qa.tests.create');
  const canExecuteTest = hasPermission(currentUser as any, 'qa.tests.execute');

  const loadData = () => {
    setLoading(true);
    Promise.all([
      ApiClient.qa.getBugs({ severity: severityFilter, status: statusFilter, search }),
      ApiClient.qa.getTestCases()
    ]).then(([bRes, tRes]) => {
      setBugs(bRes.data);
      setTestCases(tRes.data);
    }).catch(() => showToast('Error', 'Failed to load QA records', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, [severityFilter, statusFilter, search]);

  const handleOpenCreateBug = () => {
    setEditingBug(null);
    setTitle('');
    setProject(PROJECTS[0]);
    setModule(MODULES[1]);
    setSeverity('Medium');
    setPriority('Normal P2');
    setStatus('Open');
    setAssignedTo('Bharat Sharma');
    setLinkedTaskId('AECKUWAIT-422');
    setReproductionSteps('');
    setExpectedResult('');
    setActualResult('');
    setEnvironment('Chrome 130 / Windows 11');
    setBugModalOpen(true);
  };

  const handleOpenEditBug = (bug: BugRecord) => {
    setEditingBug(bug);
    setTitle(bug.title);
    setProject(bug.project);
    setModule(bug.module);
    setSeverity(bug.severity);
    setPriority(bug.priority);
    setStatus(bug.status);
    setAssignedTo(bug.assigned_to);
    setLinkedTaskId(bug.linked_task_id || '');
    setReproductionSteps(bug.reproduction_steps);
    setExpectedResult(bug.expected_result);
    setActualResult(bug.actual_result);
    setEnvironment(bug.environment || '');
    setBugModalOpen(true);
  };

  const handleSaveBug = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      showToast('Validation Error', 'Bug summary is required', 'error');
      return;
    }

    if (editingBug) {
      ApiClient.qa.updateBug(editingBug.id, {
        title,
        project,
        module,
        severity,
        priority,
        status,
        assigned_to: assignedTo,
        linked_task_id: linkedTaskId,
        reproduction_steps: reproductionSteps,
        expected_result: expectedResult,
        actual_result: actualResult,
        environment,
      }).then(res => {
        setBugs(prev => prev.map(b => b.id === editingBug.id ? res.data : b));
        setBugModalOpen(false);
        showToast('Bug Updated', `${res.data.bug_code} updated`);
      });
    } else {
      const code = `BUG-${Math.floor(100 + Math.random() * 900)}`;
      ApiClient.qa.saveBug({
        bug_code: code,
        title,
        project,
        module,
        severity,
        priority,
        status,
        reported_by: currentUser?.name || 'Priya Singh',
        assigned_to: assignedTo,
        linked_task_id: linkedTaskId,
        reproduction_steps: reproductionSteps,
        expected_result: expectedResult,
        actual_result: actualResult,
        environment,
        created_at: '08-Nov-2026',
      }).then(res => {
        setBugs(prev => [res.data, ...prev]);
        setBugModalOpen(false);
        showToast('Bug Logged', `Logged ${res.data.bug_code} successfully`);
      });
    }
  };

  const handleRetestAction = (bug: BugRecord, passed: boolean) => {
    const newStatus = passed ? 'Retest Passed' : 'Reopened';
    ApiClient.qa.updateBug(bug.id, { status: newStatus }).then(res => {
      setBugs(prev => prev.map(b => b.id === bug.id ? res.data : b));
      showToast(passed ? 'Retest Passed' : 'Issue Reopened', `${bug.bug_code} marked as ${newStatus}`);
    });
  };

  const handleExecuteTest = (tc: TestCaseRecord, newStatus: TestCaseRecord['status']) => {
    ApiClient.qa.updateTestCase(tc.id, {
      status: newStatus,
      last_executed_by: currentUser?.name || 'Priya Singh',
      last_executed_at: 'Just now'
    }).then(res => {
      setTestCases(prev => prev.map(t => t.id === tc.id ? res.data : t));
      showToast('Test Executed', `${tc.test_code} marked as ${newStatus}`);
    });
  };

  // Metrics
  const criticalBugs = bugs.filter(b => b.severity === 'Critical' && b.status !== 'Closed').length;
  const readyForRetest = bugs.filter(b => b.status === 'Ready for Retest').length;
  const totalTests = testCases.length;
  const passedTests = testCases.filter(t => t.status === 'Passed').length;
  const passRate = totalTests > 0 ? Math.round((passedTests / totalTests) * 100) : 75;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 pb-2">
        <div>
          <h1 className={`text-xl font-extrabold tracking-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
            QA &amp; Bug Tracking Workspace
          </h1>
          <p className={`text-xs mt-0.5 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
            Quality assurance suites, defect lifecycle management, and release readiness verification.
          </p>
        </div>

        <div className="flex items-center gap-3">
          {activeTab === 'bugs' && canCreateBug && (
            <button
              type="button"
              onClick={handleOpenCreateBug}
              className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#7B3AEC] hover:bg-[#682ecf] text-white rounded-full text-xs font-semibold shadow-md shadow-[#7B3AEC]/25 transition-all cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              <span>Report Bug</span>
            </button>
          )}
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Open Defects */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Active Defects</span>
            <div className="p-2 bg-[#7B3AEC]/15 rounded-xl text-[#7B3AEC]">
              <Bug className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight">{bugs.filter(b => b.status !== 'Closed').length}</p>
          <p className="text-[11px] text-slate-500 mt-1">{bugs.length} total logged</p>
        </div>

        {/* Critical Defects */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Critical Blockers</span>
            <div className="p-2 bg-red-500/15 rounded-xl text-red-600">
              <AlertTriangle className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-red-600">{criticalBugs}</p>
          <p className="text-[11px] text-red-600/80 font-medium mt-1">Requires immediate hotfix</p>
        </div>

        {/* Ready for Retest */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Ready for Retest</span>
            <div className="p-2 bg-[#00A6FC]/15 rounded-xl text-[#00A6FC]">
              <RotateCcw className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-[#00A6FC]">{readyForRetest}</p>
          <p className="text-[11px] text-[#00A6FC] font-semibold mt-1">Fixed by developers</p>
        </div>

        {/* Test Pass Rate */}
        <div className={`rounded-3xl p-5 border shadow-xs transition-all ${
          isGlass
            ? 'bg-gradient-to-b from-white/[0.08] via-white/[0.03] to-black/50 border-white/15 text-white'
            : 'bg-white border-slate-200/80 text-[#191627]'
        }`}>
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-slate-500">Test Pass Rate</span>
            <div className="p-2 bg-[#A3D801]/20 rounded-xl text-[#4D6B00]">
              <ShieldCheck className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-black font-mono tracking-tight text-[#4D6B00]">{passRate}%</p>
          <p className="text-[11px] text-[#4D6B00] font-semibold mt-1">{passedTests} of {totalTests} test cases passed</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200/80 pb-2">
        <button
          type="button"
          onClick={() => setActiveTab('bugs')}
          className={`flex items-center gap-2 px-4 py-2 rounded-2xl text-xs font-bold transition-all cursor-pointer ${
            activeTab === 'bugs'
              ? 'bg-[#7B3AEC] text-white shadow-xs'
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
          }`}
        >
          <Bug className="w-4 h-4" />
          <span>Defect Tracking ({bugs.length})</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('tests')}
          className={`flex items-center gap-2 px-4 py-2 rounded-2xl text-xs font-bold transition-all cursor-pointer ${
            activeTab === 'tests'
              ? 'bg-[#7B3AEC] text-white shadow-xs'
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
          }`}
        >
          <FlaskConical className="w-4 h-4" />
          <span>Test Suites &amp; Cases ({testCases.length})</span>
        </button>
      </div>

      {/* Tab 1: Defects View */}
      {activeTab === 'bugs' && (
        <div className="space-y-4">
          {/* Filter Bar */}
          <div className={`p-4 rounded-3xl border flex flex-wrap items-center justify-between gap-4 transition-all ${
            isGlass ? 'bg-white/[0.04] border-white/10' : 'bg-white border-slate-200/80 shadow-xs'
          }`}>
            <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
              <span className="text-xs font-bold text-slate-500 mr-1">Severity:</span>
              {['All', 'Critical', 'High', 'Medium', 'Low'].map(sev => (
                <button
                  key={sev}
                  type="button"
                  onClick={() => setSeverityFilter(sev)}
                  className={`px-3 py-1 rounded-full text-xs font-semibold transition-all whitespace-nowrap cursor-pointer ${
                    severityFilter === sev
                      ? 'bg-[#7B3AEC] text-white shadow-xs'
                      : isGlass
                      ? 'text-zinc-400 hover:text-white hover:bg-white/10'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                  }`}
                >
                  {sev}
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
                placeholder="Search bugs, tickets, reporters..."
                className={`w-full pl-9 pr-3 py-1.5 rounded-full text-xs border focus:outline-none transition-all ${
                  isGlass
                    ? 'bg-black/30 border-white/15 text-white placeholder-zinc-500 focus:border-amber-400'
                    : 'bg-slate-50 border-slate-200 text-slate-800 placeholder-slate-400 focus:border-[#7B3AEC]'
                }`}
              />
            </div>
          </div>

          {/* Bug List */}
          <div className="space-y-3">
            {loading ? (
              [1, 2, 3].map(n => <div key={n} className="h-20 bg-slate-200/50 rounded-2xl animate-pulse" />)
            ) : bugs.length === 0 ? (
              <div className="p-12 text-center bg-white rounded-3xl border border-slate-200">
                <MascotPenguin pose="sleeping" size={80} />
                <h3 className="text-sm font-bold text-slate-800 mt-2">No defects match criteria</h3>
                <p className="text-xs text-slate-500 mt-0.5">All clear in this severity category.</p>
              </div>
            ) : (
              bugs.map(bug => {
                const sevBadge = bug.severity === 'Critical'
                  ? 'bg-red-500/20 text-red-700 border-red-500/30'
                  : bug.severity === 'High'
                  ? 'bg-fuchsia-500/20 text-fuchsia-700 border-fuchsia-500/30'
                  : bug.severity === 'Medium'
                  ? 'bg-sky-500/20 text-sky-700 border-sky-500/30'
                  : 'bg-slate-100 text-slate-700 border-slate-200';

                return (
                  <div
                    key={bug.id}
                    className={`p-4 rounded-3xl border shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4 transition-all hover:shadow-md ${
                      isGlass ? 'bg-white/[0.04] border-white/15 text-white' : 'bg-white border-slate-200/80 text-[#191627]'
                    }`}
                  >
                    <div className="space-y-1.5 min-w-0 flex-1">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="font-mono text-xs font-bold text-[#7B3AEC] bg-[#7B3AEC]/10 px-2.5 py-0.5 rounded-full">
                          {bug.bug_code}
                        </span>
                        <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${sevBadge}`}>
                          {bug.severity}
                        </span>
                        <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                          {bug.status}
                        </span>
                        <span className="text-[10px] text-slate-400 font-mono">
                          {bug.project} · {bug.module}
                        </span>
                      </div>

                      <h4 className="text-sm font-bold tracking-tight">
                        {bug.title}
                      </h4>

                      <p className="text-xs text-slate-500 line-clamp-1">
                        Steps: {bug.reproduction_steps}
                      </p>
                    </div>

                    <div className="flex items-center gap-3 shrink-0">
                      <div className="text-right text-xs">
                        <span className="text-slate-400 block text-[10px]">Assigned to</span>
                        <span className="font-semibold text-slate-700">{bug.assigned_to}</span>
                      </div>

                      {/* Retest Actions if Ready for Retest */}
                      {canExecuteTest && (bug.status === 'Ready for Retest' || bug.status === 'Fixed') && (
                        <div className="flex items-center gap-1.5">
                          <button
                            type="button"
                            onClick={() => handleRetestAction(bug, true)}
                            className="inline-flex items-center gap-1 px-3 py-1.5 bg-[#A3D801] hover:bg-[#8ec200] text-[#191627] rounded-xl text-xs font-bold transition-all shadow-xs"
                            title="Mark Retest Passed"
                          >
                            <Check className="w-3.5 h-3.5 stroke-[3]" />
                            <span>Pass</span>
                          </button>
                          <button
                            type="button"
                            onClick={() => handleRetestAction(bug, false)}
                            className="inline-flex items-center gap-1 px-3 py-1.5 bg-red-100 hover:bg-red-200 text-red-700 rounded-xl text-xs font-bold transition-all"
                            title="Reopen Defect"
                          >
                            <RotateCcw className="w-3.5 h-3.5" />
                            <span>Reopen</span>
                          </button>
                        </div>
                      )}

                      {canUpdateBug && (
                        <button
                          type="button"
                          onClick={() => handleOpenEditBug(bug)}
                          className="p-1.5 rounded-xl text-slate-400 hover:text-[#7B3AEC] hover:bg-slate-100 transition-colors"
                          title="Edit defect"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* Tab 2: Test Suites View */}
      {activeTab === 'tests' && (
        <div className="space-y-3">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {testCases.map(tc => {
              const statusColor = tc.status === 'Passed'
                ? 'bg-[#A3D801]/25 text-[#4D6B00]'
                : tc.status === 'Failed'
                ? 'bg-red-100 text-red-700'
                : tc.status === 'Blocked'
                ? 'bg-amber-100 text-amber-800'
                : 'bg-slate-100 text-slate-600';

              return (
                <div
                  key={tc.id}
                  className={`p-5 rounded-3xl border shadow-xs flex flex-col justify-between transition-all ${
                    isGlass ? 'bg-white/[0.04] border-white/10 text-white' : 'bg-white border-slate-200/80 text-[#191627]'
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="font-mono text-[11px] font-bold text-[#7B3AEC] bg-[#7B3AEC]/10 px-2 py-0.5 rounded-full">
                        {tc.test_code}
                      </span>
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${statusColor}`}>
                        {tc.status}
                      </span>
                    </div>

                    <h4 className="text-xs font-bold leading-snug mb-1">
                      {tc.title}
                    </h4>

                    <p className="text-[11px] text-slate-400 mb-3">
                      Suite: <span className="font-semibold text-slate-600">{tc.suite}</span> · Project: {tc.project}
                    </p>

                    {tc.execution_notes && (
                      <p className="text-[11px] bg-slate-50 p-2.5 rounded-xl text-slate-600 font-mono leading-relaxed mb-3">
                        {tc.execution_notes}
                      </p>
                    )}
                  </div>

                  {canExecuteTest && (
                    <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs">
                      <span className="text-[10px] text-slate-400 font-mono">
                        {tc.last_executed_at || 'Never run'}
                      </span>

                      <div className="flex items-center gap-1.5">
                        <button
                          type="button"
                          onClick={() => handleExecuteTest(tc, 'Passed')}
                          className="px-2.5 py-1 bg-[#A3D801]/20 hover:bg-[#A3D801]/40 text-[#4D6B00] rounded-lg font-bold text-[10px]"
                        >
                          Pass
                        </button>
                        <button
                          type="button"
                          onClick={() => handleExecuteTest(tc, 'Failed')}
                          className="px-2.5 py-1 bg-red-100 hover:bg-red-200 text-red-700 rounded-lg font-bold text-[10px]"
                        >
                          Fail
                        </button>
                        <button
                          type="button"
                          onClick={() => handleExecuteTest(tc, 'Blocked')}
                          className="px-2.5 py-1 bg-amber-100 hover:bg-amber-200 text-amber-800 rounded-lg font-bold text-[10px]"
                        >
                          Block
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Bug Report Modal */}
      {bugModalOpen && (
        <Modal
          isOpen={bugModalOpen}
          onClose={() => setBugModalOpen(false)}
          title={editingBug ? 'Edit Defect' : 'Report Defect'}
          subtitle="Document reproduction steps, severity, environment, and task linkage"
          maxWidth="lg"
        >
          <form onSubmit={handleSaveBug} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Defect Summary / Title *</label>
              <input
                type="text"
                required
                value={title}
                onChange={e => setTitle(e.target.value)}
                placeholder="e.g. Token expiry triggers duplicate API retries on slow network"
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Project</label>
                <select
                  value={project}
                  onChange={e => setProject(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {PROJECTS.map(p => <option key={p} value={p}>{p}</option>)}
                </select>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Module</label>
                <select
                  value={module}
                  onChange={e => setModule(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {MODULES.map(m => <option key={m} value={m}>{m}</option>)}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Severity</label>
                <select
                  value={severity}
                  onChange={e => setSeverity(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Critical">Critical</option>
                  <option value="High">High</option>
                  <option value="Medium">Medium</option>
                  <option value="Low">Low</option>
                </select>
              </div>

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
                <label className="text-xs font-semibold text-slate-700 block mb-1">Status</label>
                <select
                  value={status}
                  onChange={e => setStatus(e.target.value as any)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {BUG_STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Assign Developer</label>
                <input
                  type="text"
                  value={assignedTo}
                  onChange={e => setAssignedTo(e.target.value)}
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Linked Task ID</label>
                <input
                  type="text"
                  value={linkedTaskId}
                  onChange={e => setLinkedTaskId(e.target.value)}
                  placeholder="e.g. AECKUWAIT-422"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Reproduction Steps</label>
              <textarea
                rows={2}
                value={reproductionSteps}
                onChange={e => setReproductionSteps(e.target.value)}
                placeholder="1. Step one&#10;2. Step two&#10;3. Trigger issue"
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Expected Result</label>
                <input
                  type="text"
                  value={expectedResult}
                  onChange={e => setExpectedResult(e.target.value)}
                  placeholder="Expected system behavior..."
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Actual Result</label>
                <input
                  type="text"
                  value={actualResult}
                  onChange={e => setActualResult(e.target.value)}
                  placeholder="Observed error or failure..."
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2.5 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setBugModalOpen(false)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold transition-colors"
              >
                {editingBug ? 'Save Changes' : 'Submit Defect'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
