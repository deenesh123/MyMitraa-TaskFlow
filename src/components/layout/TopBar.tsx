import React, { useState, useRef, useEffect } from 'react';
import { Search, Bell, ChevronDown, KeyRound, LogOut, Check, SlidersHorizontal, Shield, UserCheck, Sun, Moon, Sparkles, Mail, ShieldCheck } from 'lucide-react';
import { CURRENT_USER, INITIAL_USERS } from '../../api/mockData';
import { UserAccount } from '../../api/types';
import { useToast } from '../common/Toast';
import { useAppTheme } from '../../context/ThemeContext';

interface TopBarProps {
  onOpenSearch: () => void;
  onNavigate: (route: string) => void;
  currentUser?: UserAccount;
  onSignOut?: () => void;
  onSwitchUser?: (user: UserAccount) => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  onOpenSearch,
  onNavigate,
  currentUser = CURRENT_USER,
  onSignOut,
  onSwitchUser
}) => {
  const { theme, setTheme, isGlass } = useAppTheme();
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [roleMenuOpen, setRoleMenuOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [unreadCount, setUnreadCount] = useState(3);
  const userMenuRef = useRef<HTMLDivElement>(null);
  const roleMenuRef = useRef<HTMLDivElement>(null);
  const notifRef = useRef<HTMLDivElement>(null);
  const { showToast } = useToast();

  const [notifications, setNotifications] = useState([
    { id: 1, title: 'Task Assigned', text: 'You were assigned to AECKUWAIT-423 (Review Client Proposal)', time: '10m ago', unread: true },
    { id: 2, title: 'Upcoming Meeting', text: 'Sprint Planning at 10:00 AM with Architecture team', time: '1h ago', unread: true },
    { id: 3, title: 'Leave Approved', text: 'Your WFH request for 02-Oct-2026 was approved', time: '1d ago', unread: true },
  ]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (userMenuRef.current && !userMenuRef.current.contains(e.target as Node)) {
        setUserMenuOpen(false);
      }
      if (roleMenuRef.current && !roleMenuRef.current.contains(e.target as Node)) {
        setRoleMenuOpen(false);
      }
      if (notifRef.current && !notifRef.current.contains(e.target as Node)) {
        setNotificationOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const markAllRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, unread: false })));
    setUnreadCount(0);
    showToast('Notifications cleared', 'All notifications marked as read', 'info');
  };

  const handleChangePassword = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPassword || newPassword.length < 6) {
      showToast('Validation Error', 'Password must be at least 6 characters', 'error');
      return;
    }
    setPasswordModalOpen(false);
    setNewPassword('');
    showToast('Password Updated', 'Your security credentials have been updated successfully.');
  };

  return (
    <header className={`h-16 px-6 flex items-center justify-between gap-6 sticky top-0 z-40 transition-colors ${
      isGlass
        ? 'bg-[#0A1021]/80 backdrop-blur-xl border-b border-white/10 text-white'
        : 'bg-white border-b border-slate-200/80 text-[#191627]'
    }`}>
      {/* Search Input Box with Ctrl+K shortcut and field hint */}
      <div className="flex-1 max-w-xl">
        <button
          type="button"
          onClick={onOpenSearch}
          className={`w-full flex items-center justify-between px-4 py-2 border rounded-full text-xs transition-all group focus:outline-none focus:ring-2 ${
            isGlass
              ? 'bg-white/[0.06] hover:bg-white/[0.1] border-white/15 text-zinc-300 focus:ring-amber-400/40'
              : 'bg-slate-50 hover:bg-slate-100/80 border-slate-200/80 text-slate-500 focus:ring-[#7B3AEC]/30'
          }`}
        >
          <div className="flex items-center gap-2.5">
            <Search className={`w-4 h-4 transition-colors ${
              isGlass ? 'text-zinc-400 group-hover:text-amber-400' : 'text-slate-400 group-hover:text-[#7B3AEC]'
            }`} />
            <span>Search tasks, projects, tickets...</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className={`hidden sm:inline-flex items-center gap-1 text-[11px] font-mono px-2 py-0.5 rounded-full border shadow-2xs ${
              isGlass ? 'bg-black/40 text-zinc-400 border-white/15' : 'bg-white text-slate-400 border-slate-200'
            }`}>
              Ctrl + K
            </span>
          </div>
        </button>
      </div>

      {/* Right Action Icons: Role Switcher + Global Theme Switcher + Notification Bell + User Avatar Dropdown */}
      <div className="flex items-center gap-3">
        {/* Role Demo Switcher */}
        <div className="relative" ref={roleMenuRef}>
          <button
            type="button"
            onClick={() => setRoleMenuOpen(!roleMenuOpen)}
            className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold border transition-all cursor-pointer ${
              isGlass
                ? 'bg-amber-500/15 border-amber-500/30 text-amber-300 hover:bg-amber-500/25'
                : 'bg-[#7B3AEC]/10 border-[#7B3AEC]/25 text-[#7B3AEC] hover:bg-[#7B3AEC]/20'
            }`}
            title="Switch Persona / Role for Demo & Evaluation"
          >
            <ShieldCheck className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Role:</span>
            <span>{currentUser.role}</span>
            <ChevronDown className="w-3 h-3 ml-0.5 opacity-70" />
          </button>
          {roleMenuOpen && (
            <div className="absolute right-0 mt-2 w-60 bg-white border border-slate-200 rounded-3xl shadow-xl p-2 z-50">
              <div className="px-3 py-1.5 border-b border-slate-100 mb-1">
                <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Preview 10 Roles</p>
                <p className="text-[10px] text-slate-500">Test role-specific views &amp; permissions</p>
              </div>
              <div className="max-h-72 overflow-y-auto space-y-0.5">
                {INITIAL_USERS.map(u => (
                  <button
                    key={u.id}
                    type="button"
                    onClick={() => {
                      setRoleMenuOpen(false);
                      if (onSwitchUser) {
                        onSwitchUser(u);
                        showToast('Role Switched', `Active persona: ${u.name} (${u.role})`, 'info');
                      }
                    }}
                    className={`w-full flex items-center justify-between px-2.5 py-1.5 rounded-xl text-left text-xs transition-colors cursor-pointer ${
                      currentUser.role === u.role
                        ? 'bg-[#7B3AEC] text-white font-bold'
                        : 'text-slate-700 hover:bg-slate-100'
                    }`}
                  >
                    <div>
                      <p className="leading-tight font-semibold">{u.role}</p>
                      <p className={`text-[10px] ${currentUser.role === u.role ? 'text-white/80' : 'text-slate-400'}`}>
                        {u.name}
                      </p>
                    </div>
                    {currentUser.role === u.role && <Check className="w-3.5 h-3.5" />}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* GLOBAL GLASSMORPHISM THEME TOGGLE (User can select for the whole screen) */}
        <div className={`flex items-center p-0.5 rounded-full border transition-all ${
          isGlass ? 'bg-black/60 border-white/20' : 'bg-slate-100 border-slate-200'
        }`}>
          <button
            type="button"
            title="Switch to Crisp Light"
            onClick={() => {
              setTheme('crisp');
              showToast('Crisp Light', 'Enabled light mode for entire application', 'info');
            }}
            className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold transition-all cursor-pointer ${
              !isGlass
                ? 'bg-white text-slate-900 shadow-xs'
                : 'text-zinc-400 hover:text-white'
            }`}
          >
            <Sun className="w-3 h-3 text-amber-500" />
            <span className="hidden sm:inline">Light</span>
          </button>

          <button
            type="button"
            title="Switch to Frosted Glass"
            onClick={() => {
              setTheme('glass');
              showToast('Frosted Glass', 'Enabled cinematic glassmorphism for entire application', 'info');
            }}
            className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold transition-all cursor-pointer ${
              isGlass
                ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-black shadow-md shadow-amber-500/30'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Sparkles className="w-3 h-3 text-black" />
            <span className="hidden sm:inline">Glass</span>
          </button>
        </div>

        {/* Notifications Popover */}
        <div className="relative" ref={notifRef}>
          <button
            type="button"
            onClick={() => setNotificationOpen(!notificationOpen)}
            className={`relative p-2.5 rounded-full transition-colors ${
              isGlass
                ? 'text-zinc-300 hover:text-amber-400 hover:bg-white/10'
                : 'text-slate-600 hover:text-[#7B3AEC] hover:bg-[#7B3AEC]/10'
            }`}
            title="Notifications"
          >
            <Bell className="w-4 h-4" />
            {unreadCount > 0 && (
              <span className="absolute top-1.5 right-1.5 w-4 h-4 bg-red-500 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                {unreadCount}
              </span>
            )}
          </button>

          {notificationOpen && (
            <div className="absolute right-0 mt-2 w-80 bg-white border border-slate-200 rounded-3xl shadow-xl p-3 z-50">
              <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-100 px-2">
                <span className="text-xs font-bold text-slate-800">Notifications ({unreadCount} new)</span>
                {unreadCount > 0 && (
                  <button
                    type="button"
                    onClick={markAllRead}
                    className="text-[11px] text-[#7B3AEC] hover:underline font-medium"
                  >
                    Mark all read
                  </button>
                )}
              </div>
              <div className="space-y-1 max-h-64 overflow-y-auto">
                {notifications.map(n => (
                  <div
                    key={n.id}
                    className={`p-2.5 rounded-2xl transition-colors text-left ${
                      n.unread ? 'bg-[#7B3AEC]/5' : 'hover:bg-slate-50'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-0.5">
                      <span className="text-xs font-semibold text-slate-800">{n.title}</span>
                      <span className="text-[10px] text-slate-400">{n.time}</span>
                    </div>
                    <p className="text-[11px] text-slate-600 leading-snug">{n.text}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* User Menu */}
        <div className="relative" ref={userMenuRef}>
          <button
            type="button"
            onClick={() => setUserMenuOpen(!userMenuOpen)}
            className="flex items-center gap-2 p-1.5 hover:bg-slate-50 rounded-full border border-slate-200/80 transition-all focus:outline-none"
          >
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-[#7B3AEC] to-[#D946EF] text-white flex items-center justify-center text-xs font-bold shadow-xs">
              {currentUser.avatar || currentUser.name.slice(0, 2).toUpperCase()}
            </div>
            <div className="hidden md:flex flex-col text-left pr-1">
              <span className="text-xs font-bold text-[#191627] leading-tight">{currentUser.name}</span>
              <span className="text-[10px] text-slate-500 font-medium">{currentUser.role}</span>
            </div>
            <ChevronDown className="w-3.5 h-3.5 text-slate-400 mr-1" />
          </button>

          {userMenuOpen && (
            <div className="absolute right-0 mt-2 w-60 bg-white border border-slate-200 rounded-3xl shadow-xl p-2 z-50">
              <div className="px-3 py-2 border-b border-slate-100 mb-1">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-bold text-[#191627]">{currentUser.name}</p>
                  {currentUser.role === 'Super Admin' && (
                    <span className="text-[9px] bg-[#7B3AEC]/15 text-[#7B3AEC] font-bold px-1.5 py-0.5 rounded-full">
                      Admin
                    </span>
                  )}
                </div>
                <p className="text-[11px] text-[#7B3AEC] font-medium">{currentUser.role}</p>
                <p className="text-[10px] text-slate-400 mt-0.5 truncate">{currentUser.email}</p>
              </div>

              <button
                type="button"
                onClick={() => {
                  setUserMenuOpen(false);
                  onNavigate('/users/user-management');
                }}
                className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-700 hover:bg-[#7B3AEC]/10 hover:text-[#7B3AEC] rounded-2xl transition-colors"
              >
                <div className="flex items-center gap-2.5">
                  <Shield className="w-3.5 h-3.5 text-[#7B3AEC]" />
                  <span>User Management</span>
                </div>
                <span className="text-[9px] font-bold bg-[#A3D801]/30 text-emerald-800 px-1.5 py-0.5 rounded-full">
                  Admin
                </span>
              </button>

              <button
                type="button"
                onClick={() => {
                  setUserMenuOpen(false);
                  onNavigate('/admin/email-zoho');
                }}
                className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-700 hover:bg-[#7B3AEC]/10 hover:text-[#7B3AEC] rounded-2xl transition-colors"
              >
                <div className="flex items-center gap-2.5">
                  <Mail className="w-3.5 h-3.5 text-[#00A6FC]" />
                  <span>Zoho Mail &amp; SMTP</span>
                </div>
                <span className="text-[9px] font-bold bg-[#00A6FC]/20 text-[#00A6FC] px-1.5 py-0.5 rounded-full">
                  Config
                </span>
              </button>

              <button
                type="button"
                onClick={() => {
                  setUserMenuOpen(false);
                  setPasswordModalOpen(true);
                }}
                className="w-full flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-50 hover:text-[#7B3AEC] rounded-2xl transition-colors"
              >
                <KeyRound className="w-3.5 h-3.5" />
                <span>Change Password</span>
              </button>

              <button
                type="button"
                onClick={() => {
                  setUserMenuOpen(false);
                  onNavigate('/task-tracker');
                }}
                className="w-full flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-slate-700 hover:bg-slate-50 hover:text-[#7B3AEC] rounded-2xl transition-colors"
              >
                <SlidersHorizontal className="w-3.5 h-3.5" />
                <span>My Preferences</span>
              </button>

              <div className="border-t border-slate-100 my-1" />

              <button
                type="button"
                onClick={() => {
                  setUserMenuOpen(false);
                  if (onSignOut) {
                    onSignOut();
                  } else {
                    showToast('Signed Out', 'You have been safely signed out.', 'info');
                  }
                }}
                className="w-full flex items-center gap-2.5 px-3 py-2 text-xs font-medium text-red-600 hover:bg-red-50 rounded-2xl transition-colors"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span>Sign out</span>
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Change Password Dialog */}
      {passwordModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
          <div className="w-full max-w-sm bg-white rounded-3xl shadow-xl p-6 border border-slate-100 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-slate-100">
              <h3 className="text-sm font-bold text-[#191627]">Change Password</h3>
              <button
                type="button"
                onClick={() => setPasswordModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 text-xs"
              >
                Cancel
              </button>
            </div>

            <form onSubmit={handleChangePassword} className="space-y-3">
              <div>
                <label className="text-xs font-medium text-slate-700 block mb-1">Current Password</label>
                <input
                  type="password"
                  placeholder="••••••••"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
              <div>
                <label className="text-xs font-medium text-slate-700 block mb-1">New Password</label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={e => setNewPassword(e.target.value)}
                  placeholder="At least 6 characters"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setPasswordModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 rounded-full text-xs text-slate-600 hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold"
                >
                  Save Password
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </header>
  );
};
