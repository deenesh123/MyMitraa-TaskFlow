import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Settings2, Download, Search } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { DropdownItem } from '../../api/types';
import { DataTable, Column } from '../../components/common/DataTable';
import { FilterBar } from '../../components/common/FilterBar';
import { DateRangeBox } from '../../components/common/DateRangeBox';
import { Modal } from '../../components/common/Modal';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { useToast } from '../../components/common/Toast';
import { ModuleUploadTrigger } from '../../components/common/ModuleUploadTrigger';

export const DropdownManagement: React.FC = () => {
  const { showToast } = useToast();
  const [dropdownItems, setDropdownItems] = useState<DropdownItem[]>([]);
  const [selectedList, setSelectedList] = useState<string>('Task Owner'); // DM-1

  // Filter state (DM-3: wrapping filter row with full labels)
  const [secondaryFilter, setSecondaryFilter] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [startDate, setStartDate] = useState('01-Aug-2026');
  const [endDate, setEndDate] = useState('31-Oct-2026');

  // Modals state
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<DropdownItem | null>(null);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  // Form fields
  const [formValue, setFormValue] = useState('');
  const [formRecordedDate, setFormRecordedDate] = useState('01-Oct-2026');
  const [formComments, setFormComments] = useState('');

  const loadItems = () => {
    ApiClient.dropdowns.getAll(selectedList).then(res => {
      setDropdownItems(res.data);
    });
  };

  useEffect(() => {
    loadItems();
  }, [selectedList]);

  const handleSearch = () => {
    ApiClient.dropdowns.getAll(selectedList).then(res => {
      let filtered = res.data;
      if (searchQuery) {
        filtered = filtered.filter(i => 
          i.value.toLowerCase().includes(searchQuery.toLowerCase()) ||
          (i.comments && i.comments.toLowerCase().includes(searchQuery.toLowerCase()))
        );
      }
      setDropdownItems(filtered);
      showToast('Search Applied', `Showing results for "${searchQuery || 'all'}"`);
    });
  };

  const handleDownload = () => {
    showToast('Export Started', `Downloading export for ${selectedList}...`);
  };

  const handleOpenAdd = () => {
    setEditingItem(null);
    setFormValue('');
    setFormRecordedDate('01-Oct-2026');
    setFormComments('');
    setModalOpen(true);
  };

  const handleOpenEdit = (item: DropdownItem) => {
    setEditingItem(item);
    setFormValue(item.value);
    setFormRecordedDate(item.recorded_date);
    setFormComments(item.comments || '');
    setModalOpen(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formValue.trim()) {
      showToast('Validation Error', 'Value cannot be blank', 'error');
      return;
    }

    if (editingItem) {
      ApiClient.dropdowns.updateItem(editingItem.id, {
        value: formValue,
        recorded_date: formRecordedDate,
        comments: formComments
      }).then(res => {
        setDropdownItems(prev => prev.map(i => (i.id === editingItem.id ? res.data : i)));
        setModalOpen(false);
        showToast('Updated', `Updated ${formValue} successfully`);
      });
    } else {
      ApiClient.dropdowns.saveItem({
        type: selectedList,
        value: formValue,
        recorded_date: formRecordedDate,
        comments: formComments
      }).then(res => {
        setDropdownItems(prev => [res.data, ...prev]);
        setModalOpen(false);
        showToast('Created', `Added ${formValue} to ${selectedList}`);
      });
    }
  };

  const handleDelete = () => {
    if (!deleteTargetId) return;
    ApiClient.dropdowns.deleteItem(deleteTargetId).then(() => {
      setDropdownItems(prev => prev.filter(i => i.id !== deleteTargetId));
      showToast('Deleted', 'Entry removed successfully');
      setDeleteTargetId(null);
    });
  };

  // DM-5: S.No, Actions (edit, delete), Recorded Date (DD-Mon-YYYY), Task Owner/Value, Comments
  const columns: Column<DropdownItem>[] = [
    {
      key: 'actions',
      header: 'Actions',
      width: 'w-24',
      render: row => (
        <div className="flex items-center gap-1.5">
          <button
            type="button"
            onClick={() => handleOpenEdit(row)}
            className="p-1.5 text-slate-500 hover:text-[#7B3AEC] hover:bg-[#7B3AEC]/10 rounded-lg transition-colors"
            title="Edit Entry"
          >
            <Edit2 className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTargetId(row.id)}
            className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            title="Delete Entry"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      )
    },
    {
      key: 'sno',
      header: 'S.No',
      width: 'w-16',
      render: (_, index) => <span className="font-mono text-slate-400">{index + 1}</span>
    },
    {
      key: 'recorded_date',
      header: 'Recorded Date',
      render: row => <span className="font-mono text-slate-700 font-medium">{row.recorded_date}</span>
    },
    {
      key: 'value',
      header: selectedList,
      render: row => <span className="font-bold text-[#191627]">{row.value}</span>
    },
    {
      key: 'comments',
      header: 'Comments',
      render: row => <span className="text-slate-600">{row.comments || '—'}</span>
    }
  ];

  return (
    <div className="space-y-6">
      {/* Page Header (DM-2: Main action button is named after chosen list, e.g. Add Task Owner) */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">
            Dropdown Management
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Maintain master metadata and dropdown value lists picked by forms across MiTRAA TaskFlow.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <ModuleUploadTrigger
            moduleName={`Dropdown Master (${selectedList})`}
            buttonLabel="Import / Upload List"
          />
          {/* Charcoal Pill Button named dynamically (DM-2) */}
          <button
            type="button"
            onClick={handleOpenAdd}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-xs transition-colors"
          >
            <Plus className="w-4 h-4" />
            <span>Add {selectedList}</span>
          </button>
        </div>
      </div>

      {/* DM-1: Dropdown Details selector */}
      <div className="bg-[#FAF5FF] border border-violet-100 rounded-3xl p-4 px-6 flex flex-wrap items-center justify-between gap-3 shadow-2xs">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-[#7B3AEC] text-white rounded-xl">
            <Settings2 className="w-4 h-4" />
          </div>
          <div>
            <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider block">
              Manage Master List
            </span>
            <span className="text-sm font-extrabold text-[#191627]">
              Current Dropdown: {selectedList}
            </span>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-600">Switch List:</label>
          <select
            value={selectedList}
            onChange={e => setSelectedList(e.target.value)}
            className="bg-white border border-slate-200 rounded-xl px-3 py-1.5 text-xs font-bold text-[#7B3AEC] shadow-2xs focus:outline-none focus:border-[#7B3AEC]"
          >
            <option value="Task Owner">Task Owner</option>
            <option value="Customer">Customer</option>
            <option value="Project">Project</option>
            <option value="Module">Module</option>
            <option value="Team">Team</option>
            <option value="Category">Category</option>
          </select>
        </div>
      </div>

      {/* Filter Row (DM-3: wrapping cleanly, DM-4: date range + download) */}
      <FilterBar onSearch={handleSearch} onDownload={handleDownload} downloadTooltip={`Download ${selectedList} Export`}>
        <div className="flex items-center gap-2">
          <label className="text-xs font-semibold text-slate-500 whitespace-nowrap">
            Search In {selectedList}:
          </label>
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder={`Search ${selectedList.toLowerCase()} name...`}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-800 focus:outline-none focus:border-[#7B3AEC] min-w-[200px]"
          />
        </div>

        <DateRangeBox startDate={startDate} endDate={endDate} onChange={(s, e) => { setStartDate(s); setEndDate(e); }} />
      </FilterBar>

      {/* DataTable (DM-6: Defaults to 25 items per page) */}
      <DataTable
        columns={columns}
        data={dropdownItems}
        defaultPageSize={25}
        keyExtractor={item => item.id}
        emptyTitle="Data Not Available"
        emptyDescription={`No entries configured for ${selectedList}.`}
        onEmptyAction={handleOpenAdd}
        emptyActionText={`Add ${selectedList}`}
      />

      {/* Add / Edit Modal */}
      {modalOpen && (
        <Modal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          title={editingItem ? `Edit ${selectedList}` : `Add ${selectedList}`}
          subtitle={`Configure master list entry for ${selectedList}`}
          maxWidth="md"
        >
          <form onSubmit={handleSave} className="space-y-4">
            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">
                {selectedList} Value *
              </label>
              <input
                type="text"
                required
                value={formValue}
                onChange={e => setFormValue(e.target.value)}
                placeholder={`e.g. ${selectedList === 'Task Owner' ? 'Arun Patel' : 'Enterprise Core'}`}
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Recorded Date</label>
              <input
                type="text"
                value={formRecordedDate}
                onChange={e => setFormRecordedDate(e.target.value)}
                placeholder="DD-Mon-YYYY"
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-mono focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-700 block mb-1">Comments / Notes</label>
              <textarea
                rows={2}
                value={formComments}
                onChange={e => setFormComments(e.target.value)}
                placeholder="Optional description or domain notes..."
                className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs focus:outline-none focus:border-[#7B3AEC]"
              />
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setModalOpen(false)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-full text-xs font-medium hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold transition-colors"
              >
                {editingItem ? 'Save Changes' : `Add ${selectedList}`}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Confirm Delete */}
      <ConfirmDialog
        isOpen={!!deleteTargetId}
        onClose={() => setDeleteTargetId(null)}
        onConfirm={handleDelete}
        title={`Delete ${selectedList}`}
        message={`Are you sure you want to remove this ${selectedList} entry? Screens referencing this value will retain historic records.`}
      />
    </div>
  );
};
