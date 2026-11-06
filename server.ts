/**
 * MyMitraa TaskFlow - Full-Stack Express Server
 * Serves complete REST endpoints migrated from Spring Boot PM-TOOL backend
 * Integrated with Zoho Mail SMTP delivery, persistent database (mymitraa_taskflow),
 * and Vite SPA middleware.
 */

import express, { Request, Response, NextFunction } from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import dotenv from 'dotenv';
import { loadDatabase, saveDatabase, TaskOwnerEmailRecord } from './server/db.js';
import { EmailService } from './server/emailService.js';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = Number(process.env.PORT || 3000);
const HOST = process.env.HOST || '0.0.0.0';

// Global middleware
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ extended: true, limit: '50mb' }));

// Helper to send standard Spring Boot CustomResponse
function sendCustomResponse(
  res: Response,
  successCode: string,
  successMessage: string,
  data: any,
  metaInfo: Record<string, any> = {}
) {
  res.status(200).json({
    successCode,
    successMessage,
    data,
    metaInfo: { SERVICE_NAME: successCode, ...metaInfo },
    code: 200,
    message: successCode,
  });
}

// --------------------------------------------------------------------------
// DATABASE STATUS & HEALTH
// --------------------------------------------------------------------------
app.get('/api/health', (req: Request, res: Response) => {
  const db = loadDatabase();
  res.json({
    status: 'UP',
    application: 'MyMitraa TaskFlow',
    database: db.database_name,
    version: db.version,
    emailService: EmailService.getConfig().isConfigured ? 'ZOHO_CONFIGURED' : 'ZOHO_SIMULATED',
    counts: {
      tasks: db.tasks.length,
      meetings: db.meetings.length,
      leaves: db.leaves.length,
      weeklyUpdates: db.weeklyUpdates.length,
      documents: db.documents.length,
      users: db.users.length,
      dropdowns: db.dropdowns.length,
    },
  });
});

// --------------------------------------------------------------------------
// AUTHENTICATION & USERS (WebSecurityConfiguration & User Management)
// --------------------------------------------------------------------------
app.post('/api/auth/login', (req: Request, res: Response) => {
  const { email } = req.body;
  const db = loadDatabase();
  const normalized = (email || '').toLowerCase().trim();
  const user = db.users.find(u => u.email.toLowerCase() === normalized) || {
    id: `usr-${Date.now()}`,
    name: email ? email.split('@')[0].replace(/[._]/g, ' ') : 'TaskFlow User',
    email: email || 'user@mymitraa.com',
    role: 'Standard User',
    team: 'Engineering',
    status: 'Active',
    avatar: 'U',
    created_date: '04-Nov-2026',
    last_login: 'Just now',
  };

  sendCustomResponse(res, 'LOGIN_SUCCESS', 'User authenticated successfully', {
    token: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mymitraa-taskflow-jwt-token',
    user,
  });
});

app.post('/api/auth/register', (req: Request, res: Response) => {
  const { name, email, role, team } = req.body;
  const db = loadDatabase();
  const initials = (name || 'User')
    .split(' ')
    .map((n: string) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);

  const newUser = {
    id: `usr-${Date.now()}`,
    name: name || 'New User',
    email: email || 'new.user@mymitraa.com',
    role: role || 'Standard User',
    team: team || 'General',
    status: 'Active',
    avatar: initials || 'U',
    created_date: '06-Nov-2026',
    last_login: 'Just now',
  };

  db.users.unshift(newUser);
  saveDatabase(db);

  sendCustomResponse(res, 'REGISTRATION_SUCCESS', 'User registered successfully', newUser);
});

app.get(['/api/users/getAll', '/api/users'], (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'USERS_FETCHED_SUCCESSFULLY', 'Users fetched successfully', db.users, {
    totalCount: db.users.length,
  });
});

app.post('/api/users/save', (req: Request, res: Response) => {
  const db = loadDatabase();
  const user = req.body;
  const initials = (user.name || 'User')
    .split(' ')
    .map((n: string) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);

  const newUser = {
    ...user,
    id: `usr-${Date.now()}`,
    avatar: initials || 'U',
    created_date: '06-Nov-2026',
    last_login: 'Never',
    status: user.status || 'Active',
  };
  db.users.unshift(newUser);
  saveDatabase(db);
  sendCustomResponse(res, 'USER_CREATED_SUCCESSFULLY', 'User created successfully', newUser);
});

app.put('/api/users/update/:id', (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  const idx = db.users.findIndex(u => u.id === id);
  if (idx === -1) {
    res.status(404).json({ code: 404, message: 'User not found' });
    return;
  }
  db.users[idx] = { ...db.users[idx], ...req.body };
  saveDatabase(db);
  sendCustomResponse(res, 'USER_UPDATED_SUCCESSFULLY', 'User updated successfully', db.users[idx]);
});

app.delete('/api/users/delete/:id', (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.users = db.users.filter(u => u.id !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'USER_DELETED_SUCCESSFULLY', 'User deleted successfully', true);
});

// --------------------------------------------------------------------------
// MODULE 1: TASK TRACKER & FEEDBACK HISTORY (TaskController & FeedbackHistoryController)
// --------------------------------------------------------------------------
app.get('/api/feedback/feedback_numerical_count', (req: Request, res: Response) => {
  const db = loadDatabase();
  const total = db.tasks.length;
  const closed = db.tasks.filter(t => t.status === 'Closed').length;
  const in_progress = db.tasks.filter(t => t.status === 'In Progress').length;
  const open = db.tasks.filter(t => t.status === 'Open').length;
  const on_hold = db.tasks.filter(t => t.status === 'On Hold').length;
  const overdue = db.tasks.filter(t => t.status === 'Open' || t.status === 'In Progress').slice(0, 3).length;

  sendCustomResponse(res, 'FEEDBACK_NUMERICAL_COUNT_FETCHED_SUCCESSFULLY', 'Counts fetched successfully', {
    total,
    closed,
    in_progress,
    open,
    on_hold,
    overdue,
    total_trend: '+8%',
    in_progress_trend: '+5%',
    closed_trend: '+15%',
    open_trend: '-3%',
  });
});

app.all(['/api/tasks/getAll', '/api/feedback/get_all_feedback'], (req: Request, res: Response) => {
  const db = loadDatabase();
  const searchVM = req.body || {};
  let list = [...db.tasks];

  if (searchVM.customer) {
    list = list.filter(t => (t.customer || '').toLowerCase().includes(searchVM.customer.toLowerCase()));
  }
  if (searchVM.project) {
    list = list.filter(t => (t.project || '').toLowerCase().includes(searchVM.project.toLowerCase()));
  }
  if (searchVM.status) {
    list = list.filter(t => (t.status || '').toLowerCase() === searchVM.status.toLowerCase());
  }
  if (searchVM.task_owner) {
    list = list.filter(t => (t.task_owner || '').toLowerCase().includes(searchVM.task_owner.toLowerCase()));
  }

  sendCustomResponse(res, 'TASKS_FETCHED_SUCCESSFULLY', 'Tasks fetched successfully', list, {
    totalCount: list.length,
  });
});

app.get(['/api/tasks/get/:id', '/api/feedback/get_feedback_by_id/:id'], (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  const task = db.tasks.find(t => t.id === id || t.ticket_number === id);
  if (!task) {
    res.status(404).json({ code: 404, message: 'Task not found' });
    return;
  }
  sendCustomResponse(res, 'TASK_FETCHED_SUCCESSFULLY', 'Task fetched successfully', task);
});

app.post(['/api/tasks/save', '/api/feedback/save_feedback'], async (req: Request, res: Response) => {
  const db = loadDatabase();
  const task = req.body;
  const id = `task-${Date.now()}`;
  const actual_effort = (task.work_log_entries || []).reduce((sum: number, w: any) => sum + (Number(w.hours) || 0), 0);

  const newTask = {
    ...task,
    id,
    actual_effort,
    subtask_count: task.subtask_count || 0,
    work_log_entries: task.work_log_entries || [],
  };

  db.tasks.unshift(newTask);
  saveDatabase(db);

  // Automatically notify task owner if email or owner is provided
  if (newTask.task_owner) {
    try {
      await EmailService.sendTaskOwnerEmail({
        ticket_number: newTask.ticket_number,
        subject: newTask.subject,
        task_owner: newTask.task_owner,
        assigned_by: newTask.assigned_by,
        priority: newTask.priority,
        eta: newTask.eta,
        description: newTask.description,
      });
    } catch (e) {
      console.warn('[Task Save] Email dispatch warning:', e);
    }
  }

  sendCustomResponse(res, 'TASK_SAVED_SUCCESS', 'Task saved successfully', newTask);
});

app.put(['/api/tasks/update/:id', '/api/feedback/edit_feedback'], (req: Request, res: Response) => {
  const id = req.params.id || req.body.id || req.body.ticket_number;
  const db = loadDatabase();
  const idx = db.tasks.findIndex(t => t.id === id || t.ticket_number === id);
  if (idx === -1) {
    res.status(404).json({ code: 404, message: 'Task not found' });
    return;
  }

  const current = db.tasks[idx];
  const updates = req.body;
  let actual_effort = current.actual_effort;
  if (updates.work_log_entries) {
    actual_effort = updates.work_log_entries.reduce((sum: number, w: any) => sum + (Number(w.hours) || 0), 0);
  }

  db.tasks[idx] = { ...current, ...updates, actual_effort };
  saveDatabase(db);

  sendCustomResponse(res, 'TASK_UPDATED_SUCCESSFULLY', 'Task updated successfully', db.tasks[idx]);
});

app.delete(['/api/tasks/delete/:id', '/api/feedback/delete_feedback/:id'], (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.tasks = db.tasks.filter(t => t.id !== id && t.ticket_number !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'TASK_DELETED_SUCCESSFULLY', 'Task deleted successfully', true);
});

app.post('/api/feedback/add_worklog_hours', (req: Request, res: Response) => {
  const { taskId, entry } = req.body;
  const db = loadDatabase();
  const idx = db.tasks.findIndex(t => t.id === taskId || t.ticket_number === taskId);
  if (idx === -1) {
    res.status(404).json({ code: 404, message: 'Task not found' });
    return;
  }

  const current = db.tasks[idx];
  const newEntry = { id: `wl-${Date.now()}`, ...entry };
  const entries = [...(current.work_log_entries || []), newEntry];
  const actual_effort = entries.reduce((s, w) => s + (Number(w.hours) || 0), 0);

  current.work_log_entries = entries;
  current.actual_effort = actual_effort;
  db.tasks[idx] = current;
  saveDatabase(db);

  sendCustomResponse(res, 'WORKLOG_ADDED_SUCCESSFULLY', 'Worklog recorded successfully', current);
});

// --------------------------------------------------------------------------
// MODULE 2: MEETING TIME TRACKER (MeetingTimeTrackerController)
// --------------------------------------------------------------------------
app.all(['/api/meeting_time_tracker/getAll', '/api/meetings/getAll'], (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'MEETING_TIME_TRACKER_FETCHED_SUCCESSFULLY', 'Meetings fetched successfully', db.meetings, {
    totalCount: db.meetings.length,
  });
});

app.post(['/api/meeting_time_tracker/save', '/api/meetings/save'], (req: Request, res: Response) => {
  const db = loadDatabase();
  const newMeeting = { ...req.body, id: `meeting-${Date.now()}` };
  db.meetings.unshift(newMeeting);
  saveDatabase(db);
  sendCustomResponse(res, 'MEETING_TIME_TRACKER_DATA_SAVED_SUCCESS', 'Meeting saved successfully', newMeeting);
});

app.put(['/api/meeting_time_tracker/update/:id', '/api/meetings/update/:id'], (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  const idx = db.meetings.findIndex(m => m.id === id);
  if (idx === -1) {
    res.status(404).json({ code: 404, message: 'Meeting not found' });
    return;
  }
  db.meetings[idx] = { ...db.meetings[idx], ...req.body };
  saveDatabase(db);
  sendCustomResponse(res, 'MEETING_TIME_TRACKER_UPDATED_SUCCESSFULLY', 'Meeting updated successfully', db.meetings[idx]);
});

app.delete(['/api/meeting_time_tracker/delete/:id', '/api/meetings/delete/:id'], (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.meetings = db.meetings.filter(m => m.id !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'MEETING_TIME_TRACKER_DELETED_SUCCESSFULLY', 'Meeting deleted successfully', true);
});

// --------------------------------------------------------------------------
// MODULE 3: LEAVE & WFH TRACKER (OptimaLeaveTrackerController & LeaveRequestController)
// --------------------------------------------------------------------------
app.all(['/api/optima_leave_tracker/getAll', '/api/leave-requests/getAll'], (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'OPTIMA_LEAVE_TRACKER_FETCHED_SUCCESSFULLY', 'Leaves fetched successfully', db.leaves);
});

app.post(['/api/optima_leave_tracker/save', '/api/leave-requests/save'], (req: Request, res: Response) => {
  const db = loadDatabase();
  const newLeave = { ...req.body, id: `leave-${Date.now()}` };
  db.leaves.unshift(newLeave);
  saveDatabase(db);
  sendCustomResponse(res, 'OPTIMA_LEAVE_TRACKER_DATA_SAVED_SUCCESS', 'Leave saved successfully', newLeave);
});

app.delete(['/api/optima_leave_tracker/delete/:id', '/api/leave-requests/delete/:id'], (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.leaves = db.leaves.filter(l => l.id !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'OPTIMA_LEAVE_TRACKER_DELETED_SUCCESSFULLY', 'Leave deleted successfully', true);
});

// --------------------------------------------------------------------------
// MODULE 4: WEEKLY UPDATE TRACKER & SEND WEEKLY MAIL (OptimaWeeklyUpdateTrackerController)
// --------------------------------------------------------------------------
app.all('/api/optima_weekly_update_tracker/getAll', (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'OPTIMA_WEEKLY_UPDATE_TRACKER_FETCHED_SUCCESSFULLY', 'Updates fetched successfully', db.weeklyUpdates);
});

app.post('/api/optima_weekly_update_tracker/save', (req: Request, res: Response) => {
  const db = loadDatabase();
  const newUpdate = { ...req.body, id: `update-${Date.now()}` };
  db.weeklyUpdates.unshift(newUpdate);
  saveDatabase(db);
  sendCustomResponse(res, 'OPTIMA_WEEKLY_UPDATE_TRACKER_DATA_SAVED_SUCCESS', 'Weekly update saved', newUpdate);
});

app.post('/api/optima_weekly_update_tracker/update', (req: Request, res: Response) => {
  const db = loadDatabase();
  const id = req.body.id;
  const idx = db.weeklyUpdates.findIndex(u => u.id === id);
  if (idx !== -1) {
    db.weeklyUpdates[idx] = { ...db.weeklyUpdates[idx], ...req.body };
    saveDatabase(db);
  }
  sendCustomResponse(res, 'OPTIMA_WEEKLY_UPDATE_TRACKER_UPDATED_SUCCESSFULLY', 'Weekly update updated', db.weeklyUpdates[idx] || req.body);
});

/**
 * End-to-end Weekly Report Email Dispatch via Zoho Mail SMTP
 */
app.put('/api/optima_weekly_update_tracker/send-weekly-mail', async (req: Request, res: Response) => {
  const { email, projectName, weekNumber, milestonesCompleted, highlights, nextWeekGoals } = req.body;
  const targetEmail = email || 'management@mymitraa.com';

  const result = await EmailService.sendWeeklyReportEmail({
    to: targetEmail,
    projectName: projectName || 'MyMitraa Enterprise Cloud',
    weekNumber: weekNumber || 'Current Sprint',
    milestonesCompleted,
    highlights,
    nextWeekGoals,
  });

  sendCustomResponse(
    res,
    'OPTIMA_WEEKLY_REPORT_MAIL_SENT_SUCCESSFULLY',
    result.message,
    { emailSent: result.success, logId: result.logId, simulated: result.simulated }
  );
});

// --------------------------------------------------------------------------
// MODULE 5: TASK OWNER EMAIL CONTROLLER (TaskOwnerEmailController.java)
// --------------------------------------------------------------------------
app.post('/api/task_owner_email/save', async (req: Request, res: Response) => {
  const { task_id, owner_name, owner_email, assigned_by, subject, description, priority, eta } = req.body;
  const db = loadDatabase();

  const record: TaskOwnerEmailRecord = {
    id: `toe-${Date.now()}`,
    task_id: task_id || 'task-gen',
    owner_name: owner_name || 'Task Owner',
    owner_email: owner_email || 'owner@mymitraa.com',
    assigned_by: assigned_by || 'Admin',
    subject: subject || 'Task Assigned Notification',
    sent_at: new Date().toISOString(),
    status: 'DISPATCHED',
  };

  db.taskOwnerEmails.unshift(record);
  saveDatabase(db);

  // Send real Zoho email
  const mailResult = await EmailService.sendTaskOwnerEmail({
    ticket_number: task_id,
    subject: subject || 'Task Assignment',
    task_owner: owner_name,
    owner_email,
    assigned_by,
    priority,
    eta,
    description,
  });

  sendCustomResponse(
    res,
    'TASK_OWNER_EMAIL_DATA_SAVED_SUCCESS',
    `Task Owner Email processed: ${mailResult.message}`,
    { record, emailResult: mailResult }
  );
});

app.get('/api/tasks/owner-email/:id', async (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  const task = db.tasks.find(t => t.id === id || t.ticket_number === id);

  if (!task) {
    res.status(404).json({ code: 404, message: 'Task not found' });
    return;
  }

  const result = await EmailService.sendTaskOwnerEmail({
    ticket_number: task.ticket_number,
    subject: task.subject,
    task_owner: task.task_owner,
    assigned_by: task.assigned_by,
    priority: task.priority,
    eta: task.eta,
    description: task.description,
  });

  sendCustomResponse(res, 'TASK_OWNER_EMAIL_SENT', result.message, { taskId: id, result });
});

// --------------------------------------------------------------------------
// MODULE 6: DROPDOWN MANAGEMENT (DropdownManagementController.java)
// --------------------------------------------------------------------------
app.all(['/api/dropdown_management/getAll', '/api/dropdowns/getAll'], (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'DROPDOWN_MANAGEMENT_FETCHED_SUCCESSFULLY', 'Dropdown items fetched successfully', db.dropdowns);
});

app.post(['/api/dropdown_management/save', '/api/dropdowns/save'], (req: Request, res: Response) => {
  const db = loadDatabase();
  const newItem = { ...req.body, id: `dm-${Date.now()}` };
  db.dropdowns.unshift(newItem);
  saveDatabase(db);
  sendCustomResponse(res, 'DROPDOWN_MANAGEMENT_DATA_SAVED_SUCCESS', 'Dropdown saved successfully', newItem);
});

app.put('/api/dropdown_management/update/:id', (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  const idx = db.dropdowns.findIndex(d => d.id === id);
  if (idx !== -1) {
    db.dropdowns[idx] = { ...db.dropdowns[idx], ...req.body };
    saveDatabase(db);
  }
  sendCustomResponse(res, 'DROPDOWN_MANAGEMENT_UPDATED_SUCCESSFULLY', 'Dropdown updated', db.dropdowns[idx] || req.body);
});

app.delete('/api/dropdown_management/delete/:id', (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.dropdowns = db.dropdowns.filter(d => d.id !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'DROPDOWN_MANAGEMENT_DELETED_SUCCESSFULLY', 'Dropdown deleted successfully', true);
});

// --------------------------------------------------------------------------
// MODULE 7: DOCUMENTS & GLOBAL FILE UPLOADS (DocumentController.java)
// --------------------------------------------------------------------------
app.get(['/api/documents', '/api/feedback/get_uploaded_files'], (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'DOCUMENTS_FETCHED_SUCCESSFULLY', 'Documents fetched successfully', db.documents);
});

app.post(['/api/documents/upload', '/api/feedback/upload'], (req: Request, res: Response) => {
  const db = loadDatabase();
  const newDoc = {
    ...req.body,
    id: `doc-${Date.now()}`,
    upload_date: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }),
  };
  db.documents.unshift(newDoc);
  saveDatabase(db);
  sendCustomResponse(res, 'GLOBAL_FILE_UPLOADED_SUCCESSFULLY', 'Document uploaded successfully', newDoc);
});

app.delete('/api/documents/:id', (req: Request, res: Response) => {
  const { id } = req.params;
  const db = loadDatabase();
  db.documents = db.documents.filter(d => d.id !== id);
  saveDatabase(db);
  sendCustomResponse(res, 'DOCUMENT_DELETED_SUCCESSFULLY', 'Document deleted successfully', true);
});

// --------------------------------------------------------------------------
// MODULE 8: PROJECT STATUS & TIMELINE (ProjectStatusController & TimelineEntryController)
// --------------------------------------------------------------------------
app.get('/api/project-status', (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'PROJECT_STATUS_FETCHED_SUCCESSFULLY', 'Project statuses fetched successfully', db.projectStatuses);
});

app.get('/api/timeline', (req: Request, res: Response) => {
  const db = loadDatabase();
  sendCustomResponse(res, 'TIMELINE_ENTRIES_FETCHED_SUCCESSFULLY', 'Timeline entries fetched successfully', db.timelineEntries);
});

// --------------------------------------------------------------------------
// ZOHO MAIL & SMTP EMAIL INTEGRATION CONTROL & LOGS
// --------------------------------------------------------------------------
app.get('/api/email/config', (req: Request, res: Response) => {
  const config = EmailService.getConfig();
  sendCustomResponse(res, 'EMAIL_CONFIG_FETCHED', 'Email configuration status retrieved', config);
});

app.post('/api/email/config', (req: Request, res: Response) => {
  const updated = EmailService.updateConfig(req.body);
  sendCustomResponse(res, 'EMAIL_CONFIG_UPDATED', 'Email configuration updated', updated);
});

app.get('/api/email/logs', (req: Request, res: Response) => {
  const logs = EmailService.getLogs();
  sendCustomResponse(res, 'EMAIL_LOGS_FETCHED', 'Email activity logs retrieved', logs);
});

app.post('/api/email/test', async (req: Request, res: Response) => {
  const { to, subject, message } = req.body;
  const recipient = to || 'test@mymitraa.com';

  const result = await EmailService.sendMail({
    to: recipient,
    subject: subject || 'Zoho Mail Integration Test - MyMitraa TaskFlow',
    html: `
      <div style="font-family: sans-serif; padding: 24px; max-width: 500px; border: 1px solid #e2e8f0; border-radius: 8px;">
        <h2 style="color: #7B3AEC; margin-top: 0;">Zoho Mail SMTP Verification</h2>
        <p>This is a live test email sent from <strong>MyMitraa TaskFlow</strong>.</p>
        <div style="background: #f1f5f9; padding: 12px; border-radius: 4px; font-size: 14px; color: #334155;">
          ${message || 'Zoho SMTP connection handshake verified successfully.'}
        </div>
        <p style="font-size: 12px; color: #94a3b8; margin-top: 16px;">Timestamp: ${new Date().toISOString()}</p>
      </div>
    `,
    category: 'TEST',
  });

  sendCustomResponse(res, result.success ? 'TEST_EMAIL_SENT' : 'TEST_EMAIL_FAILED', result.message, result);
});

// --------------------------------------------------------------------------
// SPA & VITE INTEGRATION
// --------------------------------------------------------------------------
async function startServer() {
  if (process.env.NODE_ENV !== 'production') {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    app.use(express.static(path.resolve(__dirname, 'dist')));
    app.get('*', (req: Request, res: Response) => {
      res.sendFile(path.resolve(__dirname, 'dist/index.html'));
    });
  }

  app.listen(PORT, HOST, () => {
    console.log(`🚀 MyMitraa TaskFlow server running at http://${HOST}:${PORT}`);
    console.log(`📦 Database: mymitraa_taskflow (JSON-backed store active)`);
    console.log(`✉️  Zoho Mail SMTP: ${EmailService.getConfig().host}:${EmailService.getConfig().port}`);
  });
}

startServer().catch(err => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
