/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import { ToastProvider } from './components/common/Toast';
import { AppLayout } from './components/layout/AppLayout';
import { HomeDashboard } from './pages/home/HomeDashboard';
import { ReportsPage } from './pages/reports/ReportsPage';
import { MeetingTracker } from './pages/meetings/MeetingTracker';
import { LeaveWfhTracker } from './pages/leaves/LeaveWfhTracker';
import { ProjectStatus } from './pages/project-status/ProjectStatus';
import { ViewDocuments } from './pages/documents/ViewDocuments';
import { TaskCalendarView } from './pages/calendar/TaskCalendarView';
import { TimelineView } from './pages/timeline/TimelineView';
import { DropdownManagement } from './pages/dropdowns/DropdownManagement';
import { TaskTrackerPage } from './pages/tasks/TaskTrackerPage';
import { PlaceholderPage } from './pages/common/PlaceholderPage';
import { ApiClient } from './api/client';

import { AuthPage } from './pages/auth/AuthPage';
import { UserManagementPage } from './pages/admin/UserManagementPage';
import { CURRENT_USER } from './api/mockData';
import { UserAccount } from './api/types';

export default function App() {
  const [currentUser, setCurrentUser] = useState<UserAccount>(() => {
    return ApiClient.auth.getCurrentUserSync();
  });

  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
    const saved = localStorage.getItem('mitraa_is_auth');
    return saved !== null ? saved === 'true' : true;
  });

  const [currentRoute, setCurrentRoute] = useState<string>(() => {
    // Check hash or pathname
    const hash = window.location.hash.replace('#', '');
    if (hash && hash.startsWith('/')) return hash;
    return window.location.pathname || '/';
  });

  const [openTasksCount, setOpenTasksCount] = useState<number>(24);

  // Sync route changes
  const handleNavigate = (route: string) => {
    setCurrentRoute(route);
    window.location.hash = route;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleLoginSuccess = (user: UserAccount) => {
    setCurrentUser(user);
    setIsAuthenticated(true);
    localStorage.setItem('mitraa_is_auth', 'true');
  };

  const handleSignOut = () => {
    ApiClient.auth.logout();
    setIsAuthenticated(false);
    localStorage.setItem('mitraa_is_auth', 'false');
  };

  useEffect(() => {
    const handleHashChange = () => {
      const hash = window.location.hash.replace('#', '');
      if (hash && hash.startsWith('/')) {
        setCurrentRoute(hash);
      }
    };
    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  // Sync open tasks badge count with API client
  useEffect(() => {
    if (!isAuthenticated) return;
    ApiClient.tasks.getAll().then(res => {
      const myOpen = res.data.filter(t => 
        (t.task_owner === currentUser.name || t.assigned_by === currentUser.name) &&
        t.status !== 'Closed'
      ).length;
      if (myOpen > 0) setOpenTasksCount(myOpen);
    });
  }, [currentRoute, isAuthenticated, currentUser]);

  // Render current page component
  const renderPage = () => {
    switch (currentRoute) {
      case '/':
        return <HomeDashboard onNavigate={handleNavigate} />;
      case '/task-dashboard':
        return <ReportsPage />;
      case '/meeting-tracker':
        return <MeetingTracker />;
      case '/leave-wfh-dashboard':
        return <LeaveWfhTracker />;
      case '/project-weekly-status':
        return <ProjectStatus />;
      case '/task-upload-files':
        return <ViewDocuments />;
      case '/users/task-calendar-view':
        return <TaskCalendarView />;
      case '/users/timeline-view':
        return <TimelineView />;
      case '/users/dropdown-management':
        return <DropdownManagement />;
      case '/users/user-management':
        return <UserManagementPage />;
      case '/task-tracker':
        return <TaskTrackerPage />;
      case '/event-management':
        return (
          <PlaceholderPage
            title="Event Management"
            subtitle="Coordinate company-wide release ceremonies, hackathons, and quarterly technical reviews"
            onNavigate={handleNavigate}
          />
        );
      case '/device-inventory':
        return (
          <PlaceholderPage
            title="Device Inventory"
            subtitle="Track hardware assets, staging test devices, and server rack allocations"
            onNavigate={handleNavigate}
          />
        );
      case '/productivity-loss':
        return (
          <PlaceholderPage
            title="Productivity Loss"
            subtitle="Analyze blockers, test environment downtime, and external dependency delays"
            onNavigate={handleNavigate}
          />
        );
      case '/help-support':
        return (
          <PlaceholderPage
            title="Help & Support"
            subtitle="MY MiTRAA TaskFlow user guides, API documentation, and engineering support desk"
            onNavigate={handleNavigate}
          />
        );
      default:
        return <HomeDashboard onNavigate={handleNavigate} />;
    }
  };

  return (
    <ToastProvider>
      {!isAuthenticated ? (
        <AuthPage onSuccess={handleLoginSuccess} />
      ) : (
        <AppLayout
          currentRoute={currentRoute}
          onNavigate={handleNavigate}
          openTasksCount={openTasksCount}
          currentUser={currentUser}
          onSignOut={handleSignOut}
        >
          {renderPage()}
        </AppLayout>
      )}
    </ToastProvider>
  );
}
