import React, { useState, useEffect } from 'react';
import {
  UserPlus,
  Users,
  Shield,
  CheckCircle,
  AlertCircle,
  Clock,
  Edit2,
  Trash2,
  Lock,
  Search,
  Filter,
  Mail,
  Building2,
  Briefcase,
  KeyRound,
  RotateCcw
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { UserAccount, UserRole, UserStatus } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { useToast } from '../../components/common/Toast';
import { TEAMS } from '../../api/mockData';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const UserManagementPage: React.FC = () => {
  const { showToast } = useToast();
  const [users, setUsers] = useState<UserAccount[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRole, setSelectedRole] = useState('All');
  const [selectedTeam, setSelectedTeam] = useState('All');
  const [selectedStatus, setSelectedStatus] = useState('All');

  // Modals state
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserAccount | null>(null);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);
  const [passwordResetUser, setPasswordResetUser] = useState<UserAccount | null>(null);
  const [newPassword, setNewPassword] = useState('');

  // Create / Edit User Form State
  const [formName, setFormName] = useState('');
  const [formEmail, setFormEmail] = useState('');
  const [formRole, setFormRole] = useState<UserRole>('Production Engineer');
  const [formTeam, setFormTeam] = useState(TEAMS[0]);
  const [formPhone, setFormPhone] = useState('');
  const [formStatus, setFormStatus] = useState<UserStatus>('Active');
  const [formTempPassword, setFormTempPassword] = useState('TempPass@2026');

  const loadUsers = () => {
    setLoading(true);
    ApiClient.users.getAll({
      search: searchQuery || undefined,
      role: selectedRole !== 'All' ? selectedRole : undefined,
      team: selectedTeam !== 'All' ? selectedTeam : undefined,
      status: selectedStatus !== 'All' ? selectedStatus : undefined,
    })
      .then(res => setUsers(res.data))
      .catch(() => showToast('Error', 'Failed to fetch user directory', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadUsers();
  }, [selectedRole, selectedTeam, selectedStatus]);

  const handleSearch = () => {
    loadUsers();
    showToast('Search Applied', `Filtered users list (${users.length} matching)`);
  };

  const handleClear = () => {
    setSearchQuery('');
    setSelectedRole('All');
    setSelectedTeam('All');
    setSelectedStatus('All');
    ApiClient.users.getAll().then(res => setUsers(res.data));
    showToast('Filters Cleared', 'Displaying all users', 'info');
  };

  const handleOpenCreate = () => {
    setEditingUser(null);
    setFormName('');
    setFormEmail('');
    setFormRole('Production Engineer');
    setFormTeam(TEAMS[0]);
    setFormPhone('');
    setFormStatus('Active');
    setFormTempPassword('TempPass@2026');
    setCreateModalOpen(true);
  };

  const handleOpenEdit = (user: UserAccount) => {
    setEditingUser(user);
    setFormName(user.name);
    setFormEmail(user.email);
    setFormRole(user.role);
    setFormTeam(user.team);
    setFormPhone(user.phone || '');
    setFormStatus(user.status);
    setCreateModalOpen(true);
  };

  const handleSaveUser = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formName.trim() || !formEmail.trim()) {
      showToast('Validation Error', 'Name and Email are required', 'error');
      return;
    }

    if (editingUser) {
      ApiClient.users.updateUser(editingUser.id, {
        name: formName,
        email: formEmail,
        role: formRole,
        team: formTeam,
        phone: formPhone,
        status: formStatus,
      }).then(res => {
        setUsers(prev => prev.map(u => (u.id === editingUser.id ? res.data : u)));
        setCreateModalOpen(false);
        showToast('User Updated', `Updated profile for ${res.data.name}`);
      });
    } else {
      ApiClient.users.saveUser({
        name: formName,
        email: formEmail,
        role: formRole,
        team: formTeam,
        status: formStatus,
        phone: formPhone,
      }).then(res => {
        setUsers(prev => [res.data, ...prev]);
        setCreateModalOpen(false);
        showToast('User Created', `Created account for ${res.data.name}`);
      });
    }
  };

  const handleDeleteUser = () => {
    if (!deleteTargetId) return;
    ApiClient.users.deleteUser(deleteTargetId).then(() => {
      setUsers(prev => prev.filter(u => u.id !== deleteTargetId));
      showToast('User Removed', 'User account permanently removed from directory');
      setDeleteTargetId(null);
    });
  };

  const handleResetPassword = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPassword || newPassword.length < 6) {
      showToast('Validation Error', 'Password must be at least 6 characters', 'error');
      return;
    }
    setPasswordResetUser(null);
    setNewPassword('');
    showToast('Password Reset', 'New credentials saved and emailed to user');
  };

  // Metrics summary
  const totalUsers = users.length;
  const activeCount = users.filter(u => u.status === 'Active').length;
  const pendingCount = users.filter(u => u.status === 'Pending').length;
  const adminCount = users.filter(u => u.role.includes('Admin')).length;

  const getRoleBadgeStyle = (role: string) => {
    if (role.includes('Admin')) {
      return 'bg-purple-50 text-[#7B3AEC] border-[#7B3AEC]/30 font-bold';
    }
    if (role.includes('Architect')) {
      return 'bg-sky-50 text-[#0082CA] border-[#00A6FC]/30 font-semibold';
    }
    if (role.includes('Backend') || role.includes('Python')) {
      return 'bg-emerald-50 text-emerald-700 border-emerald-200 font-semibold';
    }
    if (role.includes('Frontend')) {
      return 'bg-lime-50 text-[#4D6B00] border-[#A3D801]/40 font-semibold';
    }
    if (role.includes('Product')) {
      return 'bg-fuchsia-50 text-[#B024C5] border-[#D946EF]/30 font-semibold';
    }
    if (role.includes('QA')) {
      return 'bg-amber-50 text-amber-800 border-amber-200 font-semibold';
    }
    return 'bg-slate-100 text-slate-700 border-slate-200 font-medium';
  };

  const getStatusBadge = (status: UserStatus) => {
    switch (status) {
      case 'Active':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
            <span>Active</span>
          </span>
        );
      case 'Pending':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-800 border border-amber-200">
            <span className="w-1.5 h-1.5 rounded-full bg-amber-500" />
            <span>Pending</span>
          </span>
        );
      case 'Inactive':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
            <span className="w-1.5 h-1.5 rounded-full bg-slate-400" />
            <span>Inactive</span>
          </span>
        );
    }
  };

  const columns: Column<UserAccount>[] = [
    {
      key: 'actions',
      header: 'Actions',
      width: 'w-28',
      render: row => (
        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(row)}
            className="p-1.5 text-slate-500 hover:text-[#7B3AEC] hover:bg-violet-50 rounded-lg transition-colors"
            title="Edit User"
          >
            <Edit2 className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setPasswordResetUser(row)}
            className="p-1.5 text-slate-500 hover:text-[#00A6FC] hover:bg-sky-50 rounded-lg transition-colors"
            title="Reset Password"
          >
            <KeyRound className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTargetId(row.id)}
            className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            title="Delete User"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      ),
    },
    {
      key: 'user',
      header: 'User Profile',
      render: row => (
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-2xl bg-gradient-to-tr from-[#7B3AEC] to-[#D946EF] text-white font-bold text-xs flex items-center justify-center shadow-xs shrink-0">
            {row.avatar}
          </div>
          <div className="min-w-0">
            <div className="flex items-center gap-2">
              <span className="font-bold text-[#191627] text-xs truncate">{row.name}</span>
              {row.role.includes('Super Admin') && (
                <span className="text-[10px] font-bold text-amber-600 bg-amber-50 px-1.5 py-0.2 rounded border border-amber-200">
                  SUPER
                </span>
              )}
            </div>
            <p className="text-[11px] text-slate-500 truncate">{row.email}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'role',
      header: 'Assigned Role',
      render: row => (
        <span className={`inline-block px-2.5 py-1 rounded-full text-xs border ${getRoleBadgeStyle(row.role)}`}>
          {row.role}
        </span>
      ),
    },
    {
      key: 'team',
      header: 'Department / Team',
      render: row => <span className="text-xs font-medium text-slate-700">{row.team}</span>,
    },
    {
      key: 'status',
      header: 'Status',
      render: row => getStatusBadge(row.status),
    },
    {
      key: 'last_login',
      header: 'Last Active',
      render: row => <span className="font-mono text-[11px] text-slate-500">{row.last_login || 'Never'}</span>,
    },
    {
      key: 'created_date',
      header: 'Joined Date',
      render: row => <span className="font-mono text-[11px] text-slate-500">{row.created_date}</span>,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
              User Management
            </h1>
            <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-violet-100 text-[#7B3AEC] border border-violet-200">
              Admin Console
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-0.5">
            Create, configure, and manage staff user accounts, role authorizations, and departmental access for MY MiTRAA.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <ModuleUploadTrigger
            moduleName="User Management"
            defaultProject="Admin Operations"
            buttonLabel="Import Users / Upload Roster"
          />

          {/* Charcoal Pill Button: Add New User */}
          <button
            type="button"
            onClick={handleOpenCreate}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] active:bg-[#12101c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors cursor-pointer"
          >
            <UserPlus className="w-4 h-4" />
            <span>Add New User</span>
          </button>
        </div>
      </div>

      {/* 4 Stat Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white border border-slate-200/80 rounded-3xl p-5 shadow-xs flex items-center justify-between">
          <div>
            <span className="text-xs font-semibold text-slate-500 block">Total Accounts</span>
            <span className="text-2xl font-black text-[#191627] font-mono mt-0.5 block">{totalUsers}</span>
            <span className="text-[11px] text-slate-400">All registered profiles</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-[#7B3AEC]/10 text-[#7B3AEC] flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white border border-slate-200/80 rounded-3xl p-5 shadow-xs flex items-center justify-between">
          <div>
            <span className="text-xs font-semibold text-slate-500 block">Active Users</span>
            <span className="text-2xl font-black text-emerald-600 font-mono mt-0.5 block">{activeCount}</span>
            <span className="text-[11px] text-slate-400">With system access</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <CheckCircle className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white border border-slate-200/80 rounded-3xl p-5 shadow-xs flex items-center justify-between">
          <div>
            <span className="text-xs font-semibold text-slate-500 block">Admins &amp; Leads</span>
            <span className="text-2xl font-black text-[#7B3AEC] font-mono mt-0.5 block">{adminCount}</span>
            <span className="text-[11px] text-slate-400">Elevated privileges</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-violet-50 text-[#7B3AEC] flex items-center justify-center">
            <Shield className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white border border-slate-200/80 rounded-3xl p-5 shadow-xs flex items-center justify-between">
          <div>
            <span className="text-xs font-semibold text-slate-500 block">Pending Invites</span>
            <span className="text-2xl font-black text-amber-600 font-mono mt-0.5 block">{pendingCount}</span>
            <span className="text-[11px] text-slate-400">Awaiting onboarding</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <Clock className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Filter Row */}
      <FilterBar onSearch={handleSearch} onClear={handleClear} showClear>
        <div className="flex items-center gap-1.5 flex-1 min-w-[220px]">
          <Search className="w-3.5 h-3.5 text-slate-400 shrink-0" />
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Search by name or email..."
            className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-[#7B3AEC]"
          />
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Role:</label>
          <select
            value={selectedRole}
            onChange={e => setSelectedRole(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Roles</option>
            <option value="Super Admin">Super Admin</option>
            <option value="Admin">Admin</option>
            <option value="Production Engineer">Production Engineer</option>
            <option value="Lead Architect">Lead Architect</option>
            <option value="Senior Backend Engineer">Senior Backend Engineer</option>
            <option value="Frontend Engineer">Frontend Engineer</option>
            <option value="Product Manager">Product Manager</option>
            <option value="QA Engineer">QA Engineer</option>
            <option value="UI / UX Designer">UI / UX Designer</option>
            <option value="Python Developer">Python Developer</option>
            <option value="DevOps Engineer">DevOps Engineer</option>
          </select>
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Team:</label>
          <select
            value={selectedTeam}
            onChange={e => setSelectedTeam(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Departments</option>
            {TEAMS.map(t => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <div className="flex items-center gap-1.5">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">Status:</label>
          <select
            value={selectedStatus}
            onChange={e => setSelectedStatus(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-1.5 text-xs text-slate-700 focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="All">All Statuses</option>
            <option value="Active">Active</option>
            <option value="Inactive">Inactive</option>
            <option value="Pending">Pending</option>
          </select>
        </div>
      </FilterBar>

      {/* Data Table */}
      <DataTable
        columns={columns}
        data={users}
        defaultPageSize={10}
        keyExtractor={u => u.id}
        emptyTitle="No Users Found"
        emptyDescription="No employee records found matching your selected filters."
        onEmptyAction={handleOpenCreate}
        emptyActionText="Create New User"
      />

      {/* Create / Edit User Modal */}
      {createModalOpen && (
        <Modal
          isOpen={createModalOpen}
          onClose={() => setCreateModalOpen(false)}
          title={editingUser ? `Edit User: ${editingUser.name}` : 'Create New User Account'}
          subtitle="Provision new employee credentials and access permissions for MY MiTRAA"
          maxWidth="lg"
        >
          <form onSubmit={handleSaveUser} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">
                  Full Name *
                </label>
                <input
                  type="text"
                  required
                  value={formName}
                  onChange={e => setFormName(e.target.value)}
                  placeholder="e.g. Arun Kumar"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">
                  Company Email *
                </label>
                <input
                  type="email"
                  required
                  value={formEmail}
                  onChange={e => setFormEmail(e.target.value)}
                  placeholder="arun.kumar@mymitraa.internal"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Assigned Role</label>
                <select
                  value={formRole}
                  onChange={e => setFormRole(e.target.value as UserRole)}
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Production Engineer">Production Engineer</option>
                  <option value="Admin">Admin</option>
                  <option value="Lead Architect">Lead Architect</option>
                  <option value="Senior Backend Engineer">Senior Backend Engineer</option>
                  <option value="Frontend Engineer">Frontend Engineer</option>
                  <option value="Product Manager">Product Manager</option>
                  <option value="QA Engineer">QA Engineer</option>
                  <option value="UI / UX Designer">UI / UX Designer</option>
                  <option value="Python Developer">Python Developer</option>
                  <option value="DevOps Engineer">DevOps Engineer</option>
                  <option value="Super Admin">Super Admin</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Department / Team</label>
                <select
                  value={formTeam}
                  onChange={e => setFormTeam(e.target.value)}
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  {TEAMS.map(t => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Contact Phone</label>
                <input
                  type="text"
                  value={formPhone}
                  onChange={e => setFormPhone(e.target.value)}
                  placeholder="+91 98765 00000"
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Account Status</label>
                <select
                  value={formStatus}
                  onChange={e => setFormStatus(e.target.value as UserStatus)}
                  className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
                >
                  <option value="Active">Active (Permit Login)</option>
                  <option value="Pending">Pending (Invitation Sent)</option>
                  <option value="Inactive">Inactive (Suspended)</option>
                </select>
              </div>
            </div>

            {!editingUser && (
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">
                  Temporary Password (User will change on first login)
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    value={formTempPassword}
                    onChange={e => setFormTempPassword(e.target.value)}
                    className="w-full pl-9 pr-3.5 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
                  />
                </div>
              </div>
            )}

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setCreateModalOpen(false)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold transition-colors"
              >
                {editingUser ? 'Save User Changes' : 'Create User Account'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Password Reset Modal */}
      {passwordResetUser && (
        <Modal
          isOpen={!!passwordResetUser}
          onClose={() => setPasswordResetUser(null)}
          title={`Reset Password: ${passwordResetUser.name}`}
          subtitle={`Set a new temporary password for ${passwordResetUser.email}`}
          maxWidth="sm"
        >
          <form onSubmit={handleResetPassword} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">
                New Temporary Password
              </label>
              <input
                type="text"
                required
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
                placeholder="At least 6 characters"
                className="w-full px-3.5 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>
            <div className="flex justify-end gap-2 pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setPasswordResetUser(null)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2 bg-[#7B3AEC] hover:bg-[#6828d9] text-white rounded-full text-xs font-semibold"
              >
                Update Password
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Confirm Delete */}
      <ConfirmDialog
        isOpen={!!deleteTargetId}
        onClose={() => setDeleteTargetId(null)}
        onConfirm={handleDeleteUser}
        title="Delete User Account"
        message="Are you sure you want to remove this user from the directory? Any open tasks assigned to them will require reassignment."
      />
    </div>
  );
};
