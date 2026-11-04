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
  HelpCircle,
  ChevronLeft,
  ChevronRight,
  ArrowRight
} from 'lucide-react';
import { MascotPenguin } from '../mascot/MascotPenguin';
import { MyMitraaLogo } from '../common/MyMitraaLogo';

interface SidebarProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
  collapsed: boolean;
  onToggleCollapse: () => void;
  openTasksCount?: number;
  userRole?: string;
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
  openTasksCount = 24
}) => {
  const menuGroups: MenuGroup[] = [
    {
      group: 'Work',
      items: [
        { title: 'Dashboard', route: '/', icon: LayoutDashboard },
        { title: 'Task Tracker', route: '/task-tracker', icon: CheckSquare, badge: openTasksCount },
        { title: 'Calendar View', route: '/users/task-calendar-view', icon: Calendar },
        { title: 'Timeline View', route: '/users/timeline-view', icon: GanttChartSquare },
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
      group: 'Insights and admin',
      items: [
        { title: 'Reports', route: '/task-dashboard', icon: BarChart3 },
        { title: 'User Management', route: '/users/user-management', icon: Shield, badge: 'Admin' },
        { title: 'Dropdown Management', route: '/users/dropdown-management', icon: Settings2 },
      ]
    }
  ];

  return (
    <aside
      className={`bg-white border-r border-slate-200/80 flex flex-col justify-between transition-all duration-300 relative ${
        collapsed ? 'w-20' : 'w-64'
      } min-h-screen shrink-0`}
    >
      {/* Top Branding Header with MyMitraaLogo */}
      <div>
        <div className="h-16 px-4 flex items-center justify-between border-b border-slate-100">
          <div
            onClick={() => onNavigate('/')}
            className="flex items-center cursor-pointer overflow-hidden select-none"
          >
            <MyMitraaLogo
              size={collapsed ? 'sm' : 'md'}
              variant={collapsed ? 'mark-only' : 'full'}
            />
          </div>

          {/* Collapse Toggle Arrow */}
          <button
            type="button"
            onClick={onToggleCollapse}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors shrink-0"
            title={collapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
          >
            {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
          </button>
        </div>

        {/* Navigation Menu Groups */}
        <nav className="p-3 space-y-4 overflow-y-auto max-h-[calc(100vh-280px)]">
          {menuGroups.map(group => (
            <div key={group.group} className="space-y-1">
              {!collapsed && (
                <p className="px-3 text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1">
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
                    className={`w-full flex items-center justify-between px-3 py-2.5 rounded-2xl text-xs font-semibold transition-all group ${
                      isActive
                        ? 'bg-[#7B3AEC] text-white shadow-sm shadow-[#7B3AEC]/25'
                        : 'text-slate-600 hover:bg-slate-100/80 hover:text-slate-900'
                    }`}
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <Icon
                        className={`w-4 h-4 shrink-0 transition-colors ${
                          isActive ? 'text-white' : 'text-slate-500 group-hover:text-slate-900'
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
                        className={`text-[11px] font-bold px-2 py-0.5 rounded-full shrink-0 ${
                          isActive
                            ? 'bg-white/20 text-white'
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

          {/* Help & Support Entry */}
          <div className="pt-2 border-t border-slate-100">
            <button
              type="button"
              onClick={() => onNavigate('/help-support')}
              title={collapsed ? 'Help & Support' : undefined}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-2xl text-xs font-semibold transition-all ${
                currentRoute === '/help-support'
                  ? 'bg-[#7B3AEC] text-white shadow-sm'
                  : 'text-slate-600 hover:bg-slate-100/80 hover:text-slate-900'
              }`}
            >
              <HelpCircle className="w-4 h-4 text-slate-500 shrink-0" />
              {!collapsed && <span>Help & Support</span>}
            </button>
          </div>
        </nav>
      </div>

      {/* Mascot Card at bottom of sidebar (hidden when collapsed) */}
      {!collapsed && (
        <div className="p-3 mb-2">
          <div className="relative bg-[#191627] rounded-3xl p-4 text-white overflow-hidden shadow-lg border border-slate-800">
            {/* Subtle violet decorative background radial glow */}
            <div className="absolute -top-6 -right-6 w-24 h-24 bg-[#7B3AEC]/30 rounded-full blur-xl pointer-events-none" />

            {/* Peeking Mascot */}
            <div className="flex items-center justify-between mb-2">
              <div className="w-12 h-12 shrink-0">
                <MascotPenguin pose="sidebar-peek" size={48} />
              </div>
              <span className="text-[10px] font-bold text-[#A3D801] tracking-wider uppercase bg-[#A3D801]/10 px-2 py-0.5 rounded-full border border-[#A3D801]/30">
                PROD v3.0
              </span>
            </div>

            <p className="text-xs font-bold leading-tight mb-1 text-slate-100">
              Let&apos;s achieve more together!
            </p>
            <p className="text-[11px] text-slate-400 mb-3 leading-snug">
              Boost sprint productivity with unified telemetry.
            </p>

            <button
              type="button"
              onClick={() => onNavigate('/task-dashboard')}
              className="w-full flex items-center justify-center gap-1.5 py-2 px-3 bg-white/10 hover:bg-white/20 active:bg-white/30 text-white rounded-xl text-xs font-semibold transition-colors border border-white/10"
            >
              <span>Explore Ecosystem</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      )}
    </aside>
  );
};
