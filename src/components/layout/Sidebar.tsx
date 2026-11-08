import React from 'react';
import {
  LayoutDashboard,
  CheckSquare,
  Calendar,
  GanttChartSquare,
  Users,
  Palmtree,
  KanbanSquare,
  FolderOpen,
  BarChart3,
  Settings2,
  Shield,
  Mail,
  HelpCircle,
  ChevronLeft,
  ChevronRight,
  ArrowRight,
  FolderKanban,
  TrendingUp,
  Bug,
  ShieldAlert,
  KeyRound
} from 'lucide-react';
import { MascotPenguin } from '../mascot/MascotPenguin';
import { MyMitraaLogo } from '../common/MyMitraaLogo';
import { useAppTheme } from '../../context/ThemeContext';
import { UserAccount } from '../../api/types';
import { CURRENT_USER } from '../../api/mockData';
import { canAccessRoute } from '../../utils/rbac';

interface SidebarProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
  collapsed: boolean;
  onToggleCollapse: () => void;
  openTasksCount?: number;
  currentUser?: UserAccount;
}

interface MenuItem {
  title: string;
  route: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: number | string;
}

interface MenuGroup {
  group: string;
  items: MenuItem[];
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentRoute,
  onNavigate,
  collapsed,
  onToggleCollapse,
  openTasksCount = 24,
  currentUser = CURRENT_USER
}) => {
  const { isGlass } = useAppTheme();

  const allMenuGroups: MenuGroup[] = [
    {
      group: 'Work',
      items: [
        { title: 'Dashboard', route: '/', icon: LayoutDashboard },
        { title: 'Projects', route: '/projects', icon: FolderKanban },
        { title: 'Task Tracker', route: '/task-tracker', icon: CheckSquare, badge: openTasksCount },
        { title: 'Calendar View', route: '/users/task-calendar-view', icon: Calendar },
        { title: 'Timeline View', route: '/users/timeline-view', icon: GanttChartSquare },
      ]
    },
    {
      group: 'Commercial',
      items: [
        { title: 'Sales CRM', route: '/sales-crm', icon: TrendingUp },
      ]
    },
    {
      group: 'Quality',
      items: [
        { title: 'QA & Bug Tracker', route: '/qa-bugs', icon: Bug },
      ]
    },
    {
      group: 'Team',
      items: [
        { title: 'Meeting Tracker', route: '/meeting-tracker', icon: Users },
        { title: 'Leaves / WFH Tracker', route: '/leave-wfh-dashboard', icon: Palmtree },
        { title: 'Project Status', route: '/project-weekly-status', icon: KanbanSquare },
      ]
    },
    {
      group: 'Files and assets',
      items: [
        { title: 'View Documents', route: '/task-upload-files', icon: FolderOpen },
      ]
    },
    {
      group: 'Governance & Admin',
      items: [
        { title: 'Reports', route: '/task-dashboard', icon: BarChart3 },
        { title: 'User Management', route: '/users/user-management', icon: Shield, badge: 'Admin' },
        { title: 'Roles & Permissions', route: '/admin/roles-permissions', icon: KeyRound },
        { title: 'Audit Trail', route: '/admin/audit-logs', icon: ShieldAlert },
        { title: 'Zoho Mail & SMTP', route: '/admin/email-zoho', icon: Mail, badge: 'Zoho' },
        { title: 'Dropdown Management', route: '/users/dropdown-management', icon: Settings2 },
      ]
    }
  ];

  // Filter groups according to current user's effective permissions
  const menuGroups = allMenuGroups
    .map(grp => ({
      ...grp,
      items: grp.items.filter(item => canAccessRoute(currentUser as any, item.route))
    }))
    .filter(grp => grp.items.length > 0);


  return (
    <aside
      className={`flex flex-col justify-between transition-all duration-300 relative ${
        collapsed ? 'w-20' : 'w-64'
      } min-h-screen shrink-0 ${
        isGlass
          ? 'bg-[#0A1021]/90 backdrop-blur-2xl border-r border-white/10 text-white'
          : 'bg-white border-r border-slate-200/80 text-[#191627]'
      }`}
    >
      {/* Top Branding Header with MyMitraaLogo */}
      <div>
        <div className={`h-16 px-4 flex items-center justify-between border-b ${
          isGlass ? 'border-white/10' : 'border-slate-100'
        }`}>
          <div
            onClick={() => onNavigate('/')}
            className="flex items-center cursor-pointer overflow-hidden select-none"
          >
            <MyMitraaLogo
              size={collapsed ? 'sm' : 'md'}
              variant={collapsed ? 'mark-only' : 'full'}
              inverted={isGlass}
            />
          </div>

          {/* Collapse Toggle Arrow */}
          <button
            type="button"
            onClick={onToggleCollapse}
            className={`p-1.5 rounded-xl transition-colors shrink-0 cursor-pointer ${
              isGlass
                ? 'text-zinc-400 hover:text-white hover:bg-white/10'
                : 'text-slate-400 hover:text-slate-700 hover:bg-slate-100'
            }`}
            title={collapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
          >
            {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
          </button>
        </div>

        {/* Navigation Menu Groups - Fully visible without internal scroll clipping */}
        <nav className="p-3 space-y-3.5 flex-1">
          {menuGroups.map(group => (
            <div key={group.group} className="space-y-0.5">
              {!collapsed && (
                <p className={`px-3 text-[10px] font-bold uppercase tracking-wider mb-1 ${
                  isGlass ? 'text-zinc-400' : 'text-slate-400'
                }`}>
                  {group.group}
                </p>
              )}
              {group.items.map(item => {
                const isActive = currentRoute === item.route;
                const Icon = item.icon;
                return (
                  <button
                    key={item.route}
                    type="button"
                    onClick={() => onNavigate(item.route)}
                    title={collapsed ? item.title : undefined}
                    className={`w-full flex items-center justify-between px-3 py-2 rounded-xl text-xs font-semibold transition-all group cursor-pointer ${
                      isActive
                        ? isGlass
                          ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold shadow-md shadow-amber-500/25'
                          : 'bg-[#7B3AEC] text-white shadow-sm shadow-[#7B3AEC]/25'
                        : isGlass
                        ? 'text-zinc-300 hover:bg-white/[0.08] hover:text-white'
                        : 'text-slate-600 hover:bg-slate-100/80 hover:text-slate-900'
                    }`}
                  >
                    <div className="flex items-center gap-2.5 min-w-0">
                      <Icon
                        className={`w-4 h-4 shrink-0 transition-colors ${
                          isActive
                            ? isGlass ? 'text-black stroke-[2.2]' : 'text-white'
                            : isGlass ? 'text-zinc-400 group-hover:text-amber-400' : 'text-slate-500 group-hover:text-slate-900'
                        }`}
                      />
                      {!collapsed && (
                        <span className="truncate text-left" title={item.title}>
                          {item.title}
                        </span>
                      )}
                    </div>

                    {!collapsed && item.badge !== undefined && (
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full shrink-0 ${
                          isActive
                            ? 'bg-white/20 text-white'
                            : isGlass
                            ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                            : 'bg-violet-100 text-[#7B3AEC]'
                        }`}
                      >
                        {item.badge}
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          ))}

          {/* Help & Support Entry directly in navigation list */}
          <div className={`pt-2 border-t ${isGlass ? 'border-white/10' : 'border-slate-100'}`}>
            <button
              type="button"
              onClick={() => onNavigate('/help-support')}
              title={collapsed ? 'Help & Support' : undefined}
              className={`w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                currentRoute === '/help-support'
                  ? isGlass
                    ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-black font-extrabold shadow-sm'
                    : 'bg-[#7B3AEC] text-white shadow-sm'
                  : isGlass
                  ? 'text-zinc-300 hover:bg-white/[0.08] hover:text-white'
                  : 'text-slate-600 hover:bg-slate-100/80 hover:text-slate-900'
              }`}
            >
              <HelpCircle className="w-4 h-4 text-slate-500 shrink-0" />
              {!collapsed && <span>Help &amp; Support</span>}
            </button>
          </div>
        </nav>
      </div>

      {/* Sleek Compact Footer (Visible in viewport without pushing items out) */}
      {!collapsed && (
        <div className="p-3 border-t border-slate-100/10">
          <button
            type="button"
            onClick={() => onNavigate('/task-dashboard')}
            className={`w-full flex items-center justify-between p-2.5 rounded-2xl border transition-all text-left group cursor-pointer ${
              isGlass
                ? 'bg-white/[0.03] border-white/10 hover:border-white/25 hover:bg-white/[0.06]'
                : 'bg-slate-50 border-slate-200/80 hover:bg-slate-100 hover:border-slate-300'
            }`}
          >
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg bg-[#7B3AEC]/15 flex items-center justify-center shrink-0">
                <MascotPenguin pose="sidebar-peek" size={26} />
              </div>
              <div>
                <p className={`text-xs font-bold leading-tight ${isGlass ? 'text-white' : 'text-[#191627]'}`}>
                  Ecosystem v3.0
                </p>
                <p className={`text-[10px] leading-none ${isGlass ? 'text-zinc-400' : 'text-slate-500'}`}>
                  PROD Active
                </p>
              </div>
            </div>
            <ArrowRight className={`w-3.5 h-3.5 transition-transform group-hover:translate-x-0.5 ${
              isGlass ? 'text-amber-400' : 'text-[#7B3AEC]'
            }`} />
          </button>
        </div>
      )}
    </aside>
  );
};
