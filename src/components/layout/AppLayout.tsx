import React, { useState, useEffect, ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import { TopBar } from './TopBar';
import { SearchModal } from './SearchModal';

import { UserAccount } from '../../api/types';
import { useAppTheme } from '../../context/ThemeContext';

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
  const { isGlass } = useAppTheme();
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
    <div className={`flex min-h-screen transition-colors duration-300 relative ${
      isGlass ? 'bg-[#080D1A] text-white' : 'bg-[#F8F9FE] text-[#191627]'
    }`}>
      {/* Ambient Glassmorphic Background Orbs (Rendered when Glass theme is active) */}
      {isGlass && (
        <>
          <div className="fixed top-[-10%] right-[-5%] w-[600px] h-[600px] bg-amber-500/10 rounded-full blur-[140px] pointer-events-none z-0" />
          <div className="fixed bottom-[-10%] left-[10%] w-[650px] h-[550px] bg-[#7B3AEC]/15 rounded-full blur-[160px] pointer-events-none z-0" />
          <div className="fixed top-[40%] left-[-5%] w-[500px] h-[500px] bg-[#00A6FC]/10 rounded-full blur-[140px] pointer-events-none z-0" />
          <div className="fixed inset-0 bg-[radial-gradient(#ffffff_1px,transparent_1px)] [background-size:32px_32px] opacity-[0.03] pointer-events-none z-0" />
        </>
      )}
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
