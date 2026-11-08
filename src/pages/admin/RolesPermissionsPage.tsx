import React, { useState } from 'react';
import {
  ShieldCheck,
  Users,
  Lock,
  KeyRound,
  Check,
  Search,
  ChevronRight,
  ShieldAlert,
  Info
} from 'lucide-react';
import { ALL_ROLES, ROLE_PERMISSIONS, RoleType, Permission } from '../../utils/rbac';
import { useAppTheme } from '../../context/ThemeContext';
import { useToast } from '../../components/common/Toast';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';

const ROLE_DESCRIPTIONS: Record<RoleType, string> = {
  'Super Admin': 'Highest authority across platform settings, security configurations, database schemas, role assignments, and all organizational units.',
  Admin: 'Operational administrator managing employees, departments, projects, global tasks, leaves, meetings, and audit access.',
  'Project Manager': 'Responsible for project health, milestones, assigning developers and QA testers, tracking blockers, and sprint delivery.',
  Developer: 'Dedicated development workspace: My Tasks, code reviews, pull requests, blockers, work hours logged, and bug resolution.',
  'Sales Executive': 'Direct sales CRM workspace: Leads, customer meetings, proposals, quotes, deal pipeline, and monthly sales quota.',
  'Sales Manager': 'Team-wide sales pipeline visibility, lead assignments, conversion metrics, team quota performance, and deal reviews.',
  'QA Tester': 'Quality assurance workspace: Test plans, test suites, test case execution, defect logging, and retest verification.',
  'HR Manager': 'People operations: Employee directory, department assignments, onboarding, attendance, and leave approvals.',
  Employee: 'Standard staff member: Personal assigned tasks, calendar, leave applications, daily/weekly updates, and profile.',
  Client: 'External stakeholder: Access restricted strictly to approved milestones, shared progress reports, and deliverable files.'
};

export const RolesPermissionsPage: React.FC = () => {
  const { isGlass } = useAppTheme();
  const { showToast } = useToast();

  const [selectedRole, setSelectedRole] = useState<RoleType>('Super Admin');
  const [permSearch, setPermSearch] = useState('');

  const permissions = ROLE_PERMISSIONS[selectedRole] || [];
  const filteredPerms = permSearch
    ? permissions.filter(p => p.toLowerCase().includes(permSearch.toLowerCase()))
    : permissions;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="pb-2">
        <h1 className={`text-xl font-extrabold tracking-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
          Roles &amp; Permissions Matrix
        </h1>
        <p className={`text-xs mt-0.5 ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
          Centralized Role-Based Access Control (RBAC) definitions enforcing module and action-level authorization.
        </p>
      </div>

      {/* Grid: Role selector left, Permission breakdown right */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Left column: 10 Roles */}
        <div className={`lg:col-span-5 rounded-3xl p-5 border shadow-xs space-y-2 transition-all ${
          isGlass ? 'bg-white/[0.03] border-white/10' : 'bg-white border-slate-200/80'
        }`}>
          <div className="flex items-center justify-between pb-2 border-b border-slate-100">
            <h2 className="text-xs font-bold uppercase tracking-wider text-slate-400">
              System Roles (10)
            </h2>
            <span className="text-[10px] font-bold text-[#7B3AEC] bg-[#7B3AEC]/10 px-2 py-0.5 rounded-full">
              Enterprise RBAC
            </span>
          </div>

          <div className="space-y-1.5 pt-1">
            {ALL_ROLES.map(role => {
              const isSelected = selectedRole === role;
              const count = ROLE_PERMISSIONS[role]?.length || 0;

              return (
                <button
                  key={role}
                  type="button"
                  onClick={() => setSelectedRole(role)}
                  className={`w-full flex items-center justify-between p-3 rounded-2xl text-left transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#7B3AEC] text-white shadow-md shadow-[#7B3AEC]/25'
                      : isGlass
                      ? 'hover:bg-white/[0.06] text-zinc-300'
                      : 'hover:bg-slate-50 text-slate-700'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <div className={`w-7 h-7 rounded-xl flex items-center justify-center text-xs font-bold shrink-0 ${
                      isSelected
                        ? 'bg-white/20 text-white'
                        : 'bg-[#7B3AEC]/15 text-[#7B3AEC]'
                    }`}>
                      <ShieldCheck className="w-3.5 h-3.5" />
                    </div>
                    <div>
                      <p className="text-xs font-bold leading-tight">{role}</p>
                      <p className={`text-[10px] leading-tight ${isSelected ? 'text-white/80' : 'text-slate-400'}`}>
                        {count} permissions
                      </p>
                    </div>
                  </div>

                  <ChevronRight className={`w-3.5 h-3.5 ${isSelected ? 'text-white' : 'text-slate-400'}`} />
                </button>
              );
            })}
          </div>
        </div>

        {/* Right column: Role Details & Effective Permissions */}
        <div className={`lg:col-span-7 rounded-3xl p-6 border shadow-xs space-y-4 transition-all ${
          isGlass ? 'bg-white/[0.03] border-white/10' : 'bg-white border-slate-200/80'
        }`}>
          {/* Header of selected role */}
          <div className="pb-3 border-b border-slate-100 flex flex-wrap items-center justify-between gap-2">
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base font-extrabold text-[#191627] tracking-tight">
                  {selectedRole}
                </h3>
                <span className="text-[10px] font-bold bg-[#A3D801]/30 text-[#4D6B00] px-2 py-0.5 rounded-full">
                  {permissions.length} Authorized Operations
                </span>
              </div>
              <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                {ROLE_DESCRIPTIONS[selectedRole]}
              </p>
            </div>
          </div>

          {/* Search permissions inside role */}
          <div className="relative">
            <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={permSearch}
              onChange={e => setPermSearch(e.target.value)}
              placeholder={`Filter ${selectedRole} permissions...`}
              className="w-full pl-9 pr-3 py-2 rounded-xl text-xs border border-slate-200 focus:outline-none focus:border-[#7B3AEC]"
            />
          </div>

          {/* Permissions Tag Grid */}
          <div className="space-y-2">
            <h4 className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
              Granted Permissions ({filteredPerms.length})
            </h4>

            {filteredPerms.length === 0 ? (
              <p className="text-xs text-slate-400 py-4 italic text-center">No permissions matching query.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {filteredPerms.map((perm: Permission) => (
                  <div
                    key={perm}
                    className="flex items-center gap-2 p-2.5 rounded-xl border border-slate-100 bg-slate-50/70 hover:border-[#7B3AEC]/30 transition-all text-xs"
                  >
                    <div className="w-4 h-4 rounded-full bg-[#A3D801] text-[#191627] flex items-center justify-center shrink-0">
                      <Check className="w-2.5 h-2.5 stroke-[3]" />
                    </div>
                    <code className="font-mono text-[11px] font-semibold text-slate-800 truncate" title={perm}>
                      {perm}
                    </code>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Security Notice */}
          <div className="p-3 bg-violet-50/80 rounded-2xl border border-violet-100 flex items-start gap-2.5 text-xs text-violet-900 leading-relaxed">
            <Info className="w-4 h-4 text-[#7B3AEC] shrink-0 mt-0.5" />
            <span>
              All permissions are enforced authoritatively by the Spring Boot backend using Spring Security annotations and JWT session claims.
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
