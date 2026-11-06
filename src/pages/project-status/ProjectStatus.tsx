import React, { useState, useEffect } from 'react';
import { Plus, Download, Globe, Users, Server, FileText, ChevronDown, Check, ExternalLink } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { OptimaWeeklyUpdateTracker } from '../../api/types';
import { FilterBar } from '../../components/common/FilterBar';
import { Modal } from '../../components/common/Modal';
import { MascotEmpty } from '../../components/common/MascotEmpty';
import { useToast } from '../../components/common/Toast';
import { PROJECTS, CUSTOMERS } from '../../api/mockData';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const ProjectStatus: React.FC = () => {
  const { showToast } = useToast();
  const [updates, setUpdates] = useState<OptimaWeeklyUpdateTracker[]>([]);
  const [selectedProject, setSelectedProject] = useState('All');
  const [selectedStatus, setSelectedStatus] = useState('All');
  const [selectedPriority, setSelectedPriority] = useState('All');
  const [selectedGeo, setSelectedGeo] = useState('All');
  const [selectedWeek, setSelectedWeek] = useState('Mon, 26-Oct – Fri, 30-Oct-2026');

  const [addModalOpen, setAddModalOpen] = useState(false);

  // Form state (PS-1)
  const [formProject, setFormProject] = useState(PROJECTS[0]);
  const [formCustomer, setFormCustomer] = useState(CUSTOMERS[0]);
  const [formGeo, setFormGeo] = useState('Middle East');
  const [formStatus, setFormStatus] = useState('On Track');
  const [formCurrentStatus, setFormCurrentStatus] = useState('');
  const [formLastWeek, setFormLastWeek] = useState('');
  const [formDevUrl, setFormDevUrl] = useState('https://dev.mitraa.internal');
  const [formQaUrl, setFormQaUrl] = useState('https://qa.mitraa.internal');

  const loadUpdates = () => {
    ApiClient.weeklyUpdates.getAll().then(res => setUpdates(res.data));
  };

  useEffect(() => {
    loadUpdates();
  }, []);

  const handleDownload = () => {
    showToast('Weekly Report Export', 'Downloading weekly status compilation document...');
  };

  const handleSaveUpdate = (e: React.FormEvent) => {
    e.preventDefault();
    ApiClient.weeklyUpdates.saveUpdate({
      project_name: formProject,
      customer_name: formCustomer,
      customer_geography: formGeo,
      industry: 'Technology & Enterprise',
      state: formStatus,
      current_state: 'Active Sprint',
      current_status: formCurrentStatus || 'Sprint objectives on schedule; test cycles passing.',
      project_status: 'Green',
      last_week_status: formLastWeek || 'Completed initial requirements grooming.',
      priority: 'High',
      risk: 'Low',
      project_summery: `Weekly update for ${formProject}`,
      start_date: '01-Jan-2026',
      go_live_date: '15-Dec-2026',
      weekly_start_date: '26-Oct-2026',
      weekly_end_date: '30-Oct-2026',
      testers: ['Rohan Sharma'],
      fe_developers: ['Deenesh Sabat'],
      be_developers: ['Vivek Singh'],
      python_developers: ['Siddharth Sen'],
      ux_designers: ['Neha Gupta'],
      shared_resources: ['Alok Gupta'],
      totalEnv: 2,
      dev_url: formDevUrl,
      qa_url: formQaUrl,
      la_url: '',
      other_url: '',
      dev_arrow: true,
      qa_arrow: true,
      la_arrow: false,
      other_arrow: false
    }).then(res => {
      setUpdates(prev => [res.data, ...prev]);
      setAddModalOpen(false);
      showToast('Status Added', `Saved weekly update for ${formProject}`);
    });
  };

  const filteredUpdates = updates.filter(u => {
    if (selectedProject !== 'All' && u.project_name !== selectedProject) return false;
    if (selectedGeo !== 'All' && u.customer_geography !== selectedGeo) return false;
    return true;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Project Weekly Status
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Weekly board where each project reports its team allocation, environment links, and execution progress.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <ModuleUploadTrigger
            moduleName="Project Status"
            defaultProject={formProject}
            buttonLabel="Upload Project Status Document"
          />

          {/* Add New Charcoal Pill (PS-1) */}
          <button
            type="button"
            onClick={() => setAddModalOpen(true)}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>Add Weekly Update</span>
          </button>
        </div>
      </div>

      {/* Filters (PS-2) */}
      <FilterBar onDownload={handleDownload} downloadTooltip="Download Weekly Status Report">
        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Project:</label>
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
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Customer Geography:</label>
          <select
            value={selectedGeo}
            onChange={e => setSelectedGeo(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Regions</option>
            <option value="Middle East">Middle East</option>
            <option value="North America">North America</option>
            <option value="APAC">APAC</option>
          </select>
        </div>

        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Select Week:</label>
          <select
            value={selectedWeek}
            onChange={e => setSelectedWeek(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-700 font-medium focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="Mon, 26-Oct – Fri, 30-Oct-2026">Mon, 26-Oct – Fri, 30-Oct-2026</option>
            <option value="Mon, 19-Oct – Fri, 23-Oct-2026">Mon, 19-Oct – Fri, 23-Oct-2026</option>
            <option value="Mon, 12-Oct – Fri, 16-Oct-2026">Mon, 12-Oct – Fri, 16-Oct-2026</option>
          </select>
        </div>
      </FilterBar>

      {/* Week Banner (PS-3: Names the week Monday to Friday) */}
      <div className="bg-[#FAF5FF] border border-violet-100 rounded-2xl p-4 px-6 flex items-center justify-between shadow-2xs">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-[#7B3AEC] text-white rounded-xl">
            <Globe className="w-4 h-4" />
          </div>
          <div>
            <h2 className="text-sm font-bold text-[#191627]">
              Current Sprint Week: {selectedWeek}
            </h2>
            <p className="text-[11px] text-slate-500">
              Active engineering deliverables, environment links, and deployment readiness.
            </p>
          </div>
        </div>
        <span className="text-xs font-mono font-bold text-[#7B3AEC] bg-white px-3 py-1 rounded-full border border-violet-200 shadow-2xs">
          Week 44
        </span>
      </div>

      {/* Grouped Board (PS-4) or Mascot Empty (PS-6) */}
      {filteredUpdates.length === 0 ? (
        <div className="bg-white rounded-3xl p-8 border border-slate-200 shadow-xs">
          <MascotEmpty
            title="Data Not Available"
            description="No weekly updates logged for the selected project and geography filter."
            actionText="Add Weekly Update"
            onAction={() => setAddModalOpen(true)}
          />
        </div>
      ) : (
        <div className="space-y-6">
          {filteredUpdates.map(update => (
            <div
              key={update.id}
              className="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden"
            >
              {/* Project Card Header */}
              <div className="px-6 py-4 bg-slate-50/70 border-b border-slate-100 flex flex-wrap items-center justify-between gap-3">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-xl bg-[#7B3AEC] text-white flex items-center justify-center font-bold text-xs">
                    {update.project_name.substring(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-[#191627]">{update.project_name}</h3>
                    <p className="text-[11px] text-slate-500">
                      Customer: {update.customer_name} · Geography: {update.customer_geography}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className="px-3 py-1 rounded-full text-xs font-semibold bg-[#A3D801]/20 text-[#4D6B00] border border-[#A3D801]/40">
                    {update.state}
                  </span>
                  <span className="text-xs font-mono text-slate-400 bg-white px-2.5 py-1 rounded-lg border border-slate-200">
                    {update.weekly_start_date} – {update.weekly_end_date}
                  </span>
                </div>
              </div>

              {/* Grouped 3-column Grid (PS-4: People, Environments, Status) */}
              <div className="p-6 grid grid-cols-1 md:grid-cols-3 gap-6 divide-y md:divide-y-0 md:divide-x divide-slate-100">
                {/* 1. PEOPLE */}
                <div className="space-y-3 md:pr-4">
                  <div className="flex items-center gap-2 text-xs font-bold text-[#191627] uppercase tracking-wider">
                    <Users className="w-4 h-4 text-[#7B3AEC]" />
                    <span>People &amp; Allocation</span>
                  </div>

                  <div className="space-y-2 text-xs">
                    <div>
                      <span className="text-[11px] text-slate-400 block font-medium">UX Designers:</span>
                      <div className="flex flex-wrap gap-1 mt-0.5">
                        {update.ux_designers.map((p, i) => (
                          <span key={i} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[11px]">
                            {p}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div>
                      <span className="text-[11px] text-slate-400 block font-medium">BE Developers:</span>
                      <div className="flex flex-wrap gap-1 mt-0.5">
                        {update.be_developers.map((p, i) => (
                          <span key={i} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[11px]">
                            {p}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div>
                      <span className="text-[11px] text-slate-400 block font-medium">Python Developers:</span>
                      <div className="flex flex-wrap gap-1 mt-0.5">
                        {update.python_developers.map((p, i) => (
                          <span key={i} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[11px]">
                            {p}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div>
                      <span className="text-[11px] text-slate-400 block font-medium">Testers / QA:</span>
                      <div className="flex flex-wrap gap-1 mt-0.5">
                        {update.testers.map((p, i) => (
                          <span key={i} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[11px]">
                            {p}
                          </span>
                        ))}
                      </div>
                    </div>

                    <div>
                      <span className="text-[11px] text-slate-400 block font-medium">Shared Resources:</span>
                      <div className="flex flex-wrap gap-1 mt-0.5">
                        {update.shared_resources.map((p, i) => (
                          <span key={i} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[11px]">
                            {p}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>

                {/* 2. ENVIRONMENTS (with blue arrow toggles) */}
                <div className="space-y-3 pt-4 md:pt-0 md:px-4">
                  <div className="flex items-center gap-2 text-xs font-bold text-[#191627] uppercase tracking-wider">
                    <Server className="w-4 h-4 text-[#00A6FC]" />
                    <span>Total Environments ({update.totalEnv})</span>
                  </div>

                  <div className="space-y-2 text-xs">
                    {/* Dev URL */}
                    <div className="p-2.5 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-between gap-2">
                      <div className="min-w-0">
                        <span className="text-[10px] font-bold text-slate-400 block uppercase">DEV</span>
                        <a
                          href={update.dev_url}
                          target="_blank"
                          rel="noreferrer"
                          className="text-[#00A6FC] hover:underline font-mono truncate block text-[11px]"
                        >
                          {update.dev_url}
                        </a>
                      </div>
                      <span
                        className={`w-6 h-6 rounded-full flex items-center justify-center shrink-0 text-white ${
                          update.dev_arrow ? 'bg-[#00A6FC]' : 'bg-slate-300'
                        }`}
                        title={update.dev_arrow ? 'Environment active' : 'Environment inactive'}
                      >
                        <ExternalLink className="w-3 h-3" />
                      </span>
                    </div>

                    {/* QA URL */}
                    <div className="p-2.5 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-between gap-2">
                      <div className="min-w-0">
                        <span className="text-[10px] font-bold text-slate-400 block uppercase">QA</span>
                        <a
                          href={update.qa_url}
                          target="_blank"
                          rel="noreferrer"
                          className="text-[#00A6FC] hover:underline font-mono truncate block text-[11px]"
                        >
                          {update.qa_url}
                        </a>
                      </div>
                      <span
                        className={`w-6 h-6 rounded-full flex items-center justify-center shrink-0 text-white ${
                          update.qa_arrow ? 'bg-[#00A6FC]' : 'bg-slate-300'
                        }`}
                        title={update.qa_arrow ? 'Environment active' : 'Environment inactive'}
                      >
                        <ExternalLink className="w-3 h-3" />
                      </span>
                    </div>

                    {/* LA URL */}
                    {update.la_url && (
                      <div className="p-2.5 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-between gap-2">
                        <div className="min-w-0">
                          <span className="text-[10px] font-bold text-slate-400 block uppercase">LA</span>
                          <span className="text-slate-600 font-mono truncate block text-[11px]">
                            {update.la_url}
                          </span>
                        </div>
                        <span className="w-6 h-6 rounded-full flex items-center justify-center shrink-0 text-white bg-slate-300">
                          <ExternalLink className="w-3 h-3" />
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                {/* 3. STATUS (Last week, Current, Next Steps) */}
                <div className="space-y-3 pt-4 md:pt-0 md:pl-4">
                  <div className="flex items-center gap-2 text-xs font-bold text-[#191627] uppercase tracking-wider">
                    <FileText className="w-4 h-4 text-[#A3D801]" />
                    <span>Progress &amp; Next Steps</span>
                  </div>

                  <div className="space-y-2.5 text-xs">
                    <div>
                      <span className="text-[11px] font-bold text-slate-400 block uppercase">Last Week Status:</span>
                      <p className="text-slate-600 leading-snug mt-0.5">{update.last_week_status}</p>
                    </div>

                    <div>
                      <span className="text-[11px] font-bold text-slate-700 block uppercase">Current Status:</span>
                      <p className="text-slate-800 font-medium leading-snug mt-0.5">{update.current_status}</p>
                    </div>

                    <div>
                      <span className="text-[11px] font-bold text-[#7B3AEC] block uppercase">Next Steps:</span>
                      <p className="text-slate-600 leading-snug mt-0.5">
                        Conduct load validation, execute regression tests, and prepare release candidate package.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Weekly Update Modal (PS-1) */}
      {addModalOpen && (
        <Modal
          isOpen={addModalOpen}
          onClose={() => setAddModalOpen(false)}
          title="Add Weekly Project Update"
          subtitle="Submit weekly progress status, team roster, and staging environments"
          maxWidth="lg"
        >
          <form onSubmit={handleSaveUpdate} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
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
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Geography</label>
                <input
                  type="text"
                  value={formGeo}
                  onChange={e => setFormGeo(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">State / Status</label>
                <select
                  value={formStatus}
                  onChange={e => setFormStatus(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="On Track">On Track</option>
                  <option value="In Progress">In Progress</option>
                  <option value="Under Review">Under Review</option>
                  <option value="Blocked">Blocked</option>
                </select>
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Current Status *</label>
              <textarea
                rows={2}
                required
                value={formCurrentStatus}
                onChange={e => setFormCurrentStatus(e.target.value)}
                placeholder="Key accomplishments and test outcomes this week..."
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Last Week Status</label>
              <textarea
                rows={2}
                value={formLastWeek}
                onChange={e => setFormLastWeek(e.target.value)}
                placeholder="Recap of milestones from the previous week..."
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Dev URL</label>
                <input
                  type="text"
                  value={formDevUrl}
                  onChange={e => setFormDevUrl(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">QA URL</label>
                <input
                  type="text"
                  value={formQaUrl}
                  onChange={e => setFormQaUrl(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
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
                Save Weekly Update
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
