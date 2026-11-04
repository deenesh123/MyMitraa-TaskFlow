import React, { useState, useEffect, ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import { TopBar } from './TopBar';
import { SearchModal } from './SearchModal';

import { UserAccount } from '../../api/types';

interface AppLayoutProps {
  currentRoute: string;
  onNavigate: (route: string) => void;
  openTasksCount?: number;
  currentUser?: UserAccount;
  onSignOut?: () => void;
  children: ReactNode;
}

export const AppLayout: React.FC<AppLayoutProps> = ({
  currentRoute,
  onNavigate,
  openTasksCount = 24,
  currentUser,
  onSignOut,
  children
}) => {
  const [collapsed, setCollapsed] = useState(false);
  const [searchModalOpen, setSearchModalOpen] = useState(false);

  // Keyboard shortcut Ctrl+K / Cmd+K listener
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setSearchModalOpen(prev => !prev);
      }
    }
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <div className="flex min-h-screen bg-[#F8F9FE] text-[#191627]">
      {/* Grouped Sidebar */}
      <Sidebar
        currentRoute={currentRoute}
        onNavigate={onNavigate}
        collapsed={collapsed}
        onToggleCollapse={() => setCollapsed(prev => !prev)}
        openTasksCount={openTasksCount}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-x-hidden">
        <TopBar
          onOpenSearch={() => setSearchModalOpen(true)}
          onNavigate={onNavigate}
          currentUser={currentUser}
          onSignOut={onSignOut}
        />

        <main className="flex-1 p-6 lg:p-8 max-w-[1600px] w-full mx-auto">
          {children}
        </main>
      </div>

      {/* Global Search Dialog */}
      <SearchModal
        isOpen={searchModalOpen}
        onClose={() => setSearchModalOpen(false)}
        onNavigate={onNavigate}
      />
    </div>
  );
};
