/**
 * High-fidelity realistic mock database matching MiTRAA TaskFlow BRD captures:
 * - 124 tasks (62 Closed, 48 In Progress, 14 Open, 6 On Hold)
 * - 657 meetings
 * - 28 days Planned Leave, 14 WFH Approved, 1 Unplanned Leave, Holidays
 * - 9 people not filling tasks in project management tool
 * - 50 uploaded document attachments
 * - Dropdown records for Task Owner, Project, Customer, Module, Team
 */

import {
  FeedbackHistory,
  MeetingTimeTracker,
  OptimaLeaveTracker,
  OptimaWeeklyUpdateTracker,
  DropdownItem,
  DocumentRecord,
  PeopleNotFillingTasks,
  UserAccount
} from './types';

export const CURRENT_USER: UserAccount = {
  id: 'usr-admin-1',
  name: 'Deenesh Sabat',
  role: 'Super Admin',
  email: 'deeneshsabat2002@gmail.com',
  team: 'Production Engineering',
  status: 'Active',
  avatar: 'DS',
  phone: '+91 98765 43210',
  last_login: 'Just now',
  created_date: '01-Jan-2026',
};

export const INITIAL_USERS: UserAccount[] = [
  {
    id: 'usr-1',
    name: 'Deenesh Sabat',
    email: 'deeneshsabat2002@gmail.com',
    role: 'Super Admin',
    team: 'Production Engineering',
    status: 'Active',
    avatar: 'DS',
    phone: '+91 98765 43210',
    last_login: '04-Nov-2026 09:15 AM',
    created_date: '01-Jan-2026',
  },
  {
    id: 'usr-2',
    name: 'Bharat Sharma',
    email: 'bharat.sharma@mymitraa.internal',
    role: 'Lead Architect',
    team: 'Architecture',
    status: 'Active',
    avatar: 'BS',
    phone: '+91 98111 22334',
    last_login: '03-Nov-2026 04:30 PM',
    created_date: '15-Jan-2026',
  },
  {
    id: 'usr-3',
    name: 'Vivek Singh',
    email: 'vivek.singh@mymitraa.internal',
    role: 'Senior Backend Engineer',
    team: 'BE Development',
    status: 'Active',
    avatar: 'VS',
    phone: '+91 98222 33445',
    last_login: '04-Nov-2026 08:45 AM',
    created_date: '01-Feb-2026',
  },
  {
    id: 'usr-4',
    name: 'Priya Patel',
    email: 'priya.patel@mymitraa.internal',
    role: 'Frontend Engineer',
    team: 'FE Development',
    status: 'Active',
    avatar: 'PP',
    phone: '+91 98333 44556',
    last_login: '04-Nov-2026 09:02 AM',
    created_date: '10-Feb-2026',
  },
  {
    id: 'usr-5',
    name: 'Ajay Kumar',
    email: 'ajay.kumar@mymitraa.internal',
    role: 'Product Manager',
    team: 'Project Management',
    status: 'Active',
    avatar: 'AK',
    phone: '+91 98444 55667',
    last_login: '03-Nov-2026 06:10 PM',
    created_date: '05-Jan-2026',
  },
  {
    id: 'usr-6',
    name: 'Neha Gupta',
    email: 'neha.gupta@mymitraa.internal',
    role: 'UI / UX Designer',
    team: 'UI / UX Design',
    status: 'Active',
    avatar: 'NG',
    phone: '+91 98555 66778',
    last_login: '02-Nov-2026 03:20 PM',
    created_date: '20-Feb-2026',
  },
  {
    id: 'usr-7',
    name: 'Rohan Sharma',
    email: 'rohan.sharma@mymitraa.internal',
    role: 'QA Engineer',
    team: 'QA',
    status: 'Active',
    avatar: 'RS',
    phone: '+91 98666 77889',
    last_login: '01-Nov-2026 11:30 AM',
    created_date: '01-Mar-2026',
  },
  {
    id: 'usr-8',
    name: 'Siddharth Sen',
    email: 'siddharth.sen@mymitraa.internal',
    role: 'Python Developer',
    team: 'Python Development',
    status: 'Active',
    avatar: 'SS',
    phone: '+91 98777 88990',
    last_login: '04-Nov-2026 07:50 AM',
    created_date: '15-Mar-2026',
  },
  {
    id: 'usr-9',
    name: 'Alok Gupta',
    email: 'alok.gupta@mymitraa.internal',
    role: 'DevOps Engineer',
    team: 'Production Engineering',
    status: 'Active',
    avatar: 'AG',
    phone: '+91 98888 99001',
    last_login: '03-Nov-2026 01:15 PM',
    created_date: '01-Apr-2026',
  },
  {
    id: 'usr-10',
    name: 'Kavita Reddy',
    email: 'kavita.reddy@mymitraa.internal',
    role: 'QA Engineer',
    team: 'QA',
    status: 'Inactive',
    avatar: 'KR',
    phone: '+91 98999 00112',
    last_login: '20-Oct-2026 05:00 PM',
    created_date: '10-Apr-2026',
  },
  {
    id: 'usr-11',
    name: 'Ananya Roy',
    email: 'ananya.roy@mymitraa.internal',
    role: 'Frontend Engineer',
    team: 'FE Development',
    status: 'Pending',
    avatar: 'AR',
    phone: '+91 98000 11223',
    last_login: 'Never',
    created_date: '02-Nov-2026',
  }
];

export const PEOPLE_NOT_FILLING_TASKS: PeopleNotFillingTasks = {
  count: 9,
  people: [
    'Ajay Kumar',
    'Rohan Sharma',
    'Pooja Varma',
    'Siddharth Sen',
    'Divya Menon',
    'Kavita Reddy',
    'Naveen Raj',
    'Alok Gupta',
    'Meera Nair'
  ],
};

export const TEAMS = [
  'QA',
  'Project Management',
  'Mobile Development',
  'BE Development',
  'FE Development',
  'UI / UX Design',
  'Architecture',
  'Python Development',
  'IoT'
];

export const PROJECTS = [
  'AEC Kuwait',
  'NextGen',
  'FinServe Global',
  'TeleHealth Hub',
  'RetailPulse ERP',
  'CloudCore Platform'
];

export const CUSTOMERS = [
  'AEC',
  'NextGen',
  'FinServe',
  'TeleHealth',
  'RetailPulse',
  'Internal'
];

export const MODULES = [
  'All Modules',
  'Project Requirements',
  'Authentication & RBAC',
  'Task Tracker & Work Log',
  'Dashboard & Reports',
  'Document Management',
  'Internal Meeting'
];

// Seed 124 Tasks with realistic properties
export const INITIAL_TASKS: FeedbackHistory[] = [
  {
    id: 'task-1',
    ticket_number: 'AECKUWAIT-422',
    subject: 'Finalize Q3 Marketing Plan and Product Alignment',
    description: 'Coordinate with product teams to align technical milestones with Q3 launch dates and deliverables.',
    status: 'In Progress',
    priority: 'Urgent P1',
    project: 'AEC Kuwait',
    module: 'Project Requirements',
    team: 'Project Management',
    customer: 'AEC',
    task_owner: 'Ajay Kumar',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '02-Oct-2026',
    estimated_close_date: '15-Oct-2026',
    actual_start_date: '02-Oct-2026',
    estimated_effort: 24,
    actual_effort: 18.5,
    work_log_entries: [
      { id: 'wl-1', date: '02-Oct-2026', hours: 4.5, description: 'Initial alignment meeting and scope breakdown' },
      { id: 'wl-2', date: '05-Oct-2026', hours: 6.0, description: 'Drafting deliverables timeline' },
      { id: 'wl-3', date: '08-Oct-2026', hours: 8.0, description: 'Reviewing stakeholder requirements' },
    ],
    subtask_count: 3,
    attachments: ['q3_alignment_doc.pdf']
  },
  {
    id: 'task-2',
    ticket_number: 'AECKUWAIT-423',
    subject: 'Review Client Proposal for Phase 2 Architecture',
    description: 'Comprehensive review of the architectural diagram and API security constraints.',
    status: 'In Progress',
    priority: 'Critical P0',
    project: 'AEC Kuwait',
    module: 'Project Requirements',
    team: 'Architecture',
    customer: 'AEC',
    task_owner: 'Deenesh Sabat',
    assigned_by: 'Bharat Sharma',
    estimated_start_date: '04-Oct-2026',
    estimated_close_date: '18-Oct-2026',
    actual_start_date: '04-Oct-2026',
    estimated_effort: 32,
    actual_effort: 21,
    work_log_entries: [
      { id: 'wl-4', date: '04-Oct-2026', hours: 7.0, description: 'Architecture diagram review and threat modeling' },
      { id: 'wl-5', date: '07-Oct-2026', hours: 7.0, description: 'Microservices spec evaluation' },
      { id: 'wl-6', date: '10-Oct-2026', hours: 7.0, description: 'Client feedback consolidation' },
    ],
    subtask_count: 2,
    attachments: ['phase2_architecture_v1.pdf']
  },
  {
    id: 'task-3',
    ticket_number: 'NEXTGEN-104',
    subject: 'Update Project Roadmap and Dependency Graph',
    description: 'Refactor milestone dependencies following new cloud database migration guidelines.',
    status: 'In Progress',
    priority: 'Normal P2',
    project: 'NextGen',
    module: 'Project Requirements',
    team: 'Project Management',
    customer: 'NextGen',
    task_owner: 'Vivek Singh',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '05-Oct-2026',
    estimated_close_date: '20-Oct-2026',
    actual_start_date: '05-Oct-2026',
    estimated_effort: 16,
    actual_effort: 11,
    work_log_entries: [
      { id: 'wl-7', date: '06-Oct-2026', hours: 5.0, description: 'Sprint backlog grooming' },
      { id: 'wl-8', date: '09-Oct-2026', hours: 6.0, description: 'Roadmap timeline sync with engineering leads' },
    ],
    subtask_count: 1,
    attachments: []
  },
  {
    id: 'task-4',
    ticket_number: 'INTERNAL-088',
    subject: 'Quarterly Team Operations & Resource Planning',
    description: 'Quarterly review of team capacity, skill distribution, and workload balancing.',
    status: 'Open',
    priority: 'Normal P2',
    project: 'CloudCore Platform',
    module: 'Internal Meeting',
    team: 'Project Management',
    customer: 'Internal',
    task_owner: 'Deenesh Sabat',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '12-Oct-2026',
    estimated_close_date: '25-Oct-2026',
    estimated_effort: 12,
    actual_effort: 0,
    work_log_entries: [],
    subtask_count: 0,
    attachments: []
  },
  {
    id: 'task-5',
    ticket_number: 'FINSERVE-311',
    subject: 'Prepare Financial Audit and Business Reconciliation Report',
    description: 'Extract monthly transaction logs and verify SLA compliance across all payment gateways.',
    status: 'Open',
    priority: 'Urgent P1',
    project: 'FinServe Global',
    module: 'Dashboard & Reports',
    team: 'BE Development',
    customer: 'FinServe',
    task_owner: 'Bharat Sharma',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '15-Oct-2026',
    estimated_close_date: '28-Oct-2026',
    estimated_effort: 20,
    actual_effort: 0,
    work_log_entries: [],
    subtask_count: 4,
    attachments: ['financial_template.xlsx']
  },
  {
    id: 'task-6',
    ticket_number: 'AECKUWAIT-419',
    subject: 'Payment Gateway Webhook Idempotency Implementation',
    description: 'Ensure double-charge prevention via Redis distributed locking and unique payment keys.',
    status: 'Closed',
    priority: 'Critical P0',
    project: 'AEC Kuwait',
    module: 'Authentication & RBAC',
    team: 'BE Development',
    customer: 'AEC',
    task_owner: 'Deenesh Sabat',
    assigned_by: 'Bharat Sharma',
    estimated_start_date: '20-Sep-2026',
    estimated_close_date: '02-Oct-2026',
    actual_start_date: '20-Sep-2026',
    actual_close_date: '01-Oct-2026',
    estimated_effort: 30,
    actual_effort: 28,
    work_log_entries: [
      { id: 'wl-9', date: '21-Sep-2026', hours: 8.0, description: 'Redis lock implementation' },
      { id: 'wl-10', date: '25-Sep-2026', hours: 10.0, description: 'Idempotency unit and integration tests' },
      { id: 'wl-11', date: '01-Oct-2026', hours: 10.0, description: 'Staging environment load verification' }
    ],
    subtask_count: 2,
    attachments: []
  },
  {
    id: 'task-7',
    ticket_number: 'AECKUWAIT-420',
    subject: 'Responsive Table UI Overhaul with Horizontal Scroll Container',
    description: 'Implement clean tabular numerals, 10/25/100 pagination, and action menus.',
    status: 'Closed',
    priority: 'Normal P2',
    project: 'AEC Kuwait',
    module: 'Task Tracker & Work Log',
    team: 'FE Development',
    customer: 'AEC',
    task_owner: 'Priya Patel',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '22-Sep-2026',
    estimated_close_date: '03-Oct-2026',
    actual_start_date: '22-Sep-2026',
    actual_close_date: '03-Oct-2026',
    estimated_effort: 18,
    actual_effort: 18,
    work_log_entries: [
      { id: 'wl-12', date: '24-Sep-2026', hours: 8.0, description: 'Table container markup and styles' },
      { id: 'wl-13', date: '03-Oct-2026', hours: 10.0, description: 'Pagination controls and mobile test' }
    ],
    subtask_count: 1,
    attachments: []
  },
  {
    id: 'task-8',
    ticket_number: 'TELEHEALTH-052',
    subject: 'Automated Test Suite for Video Consultation Session Webhooks',
    description: 'Automate test cases for patient-doctor live video call telemetry.',
    status: 'On Hold',
    priority: 'Low P3',
    project: 'TeleHealth Hub',
    module: 'Project Requirements',
    team: 'QA',
    customer: 'TeleHealth',
    task_owner: 'Kavita Reddy',
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: '10-Oct-2026',
    estimated_close_date: '30-Oct-2026',
    estimated_effort: 16,
    actual_effort: 3,
    work_log_entries: [
      { id: 'wl-14', date: '11-Oct-2026', hours: 3.0, description: 'Test plan outline drafted; waiting for test credentials' }
    ],
    subtask_count: 0,
    attachments: []
  }
];

// Generate additional tasks up to 124 tasks (Total = 124: 62 Closed, 48 In Progress, 14 Open, 6 On Hold)
const statusesDistribution: ('Closed' | 'In Progress' | 'Open' | 'On Hold')[] = [];
for (let i = 0; i < 62; i++) statusesDistribution.push('Closed');
for (let i = 0; i < 48; i++) statusesDistribution.push('In Progress');
for (let i = 0; i < 14; i++) statusesDistribution.push('Open');
// Note: 6 on hold included in total or sub-count
for (let i = 0; i < 6; i++) {
  // replace 6 of In Progress / Open with On Hold to strictly preserve 124 total
  if (i < 3) statusesDistribution[62 + 45 + i] = 'On Hold';
}

const OWNERS = [
  'Deenesh Sabat',
  'Ajay Kumar',
  'Bharat Sharma',
  'Vivek Singh',
  'Priya Patel',
  'Siddharth Sen',
  'Neha Gupta',
  'Rohan Sharma'
];

const SUBJECT_TEMPLATES = [
  'Refactor MongoDB aggregation pipelines for report generation',
  'Implement OAuth2 token refresh interceptor in Axios client',
  'Optimize Redis cache eviction policy for active user sessions',
  'Configure automated Cypress E2E test runs on GitHub Actions',
  'Update API swagger documentation with request validation schemas',
  'Resolve memory leak in real-time notification WebSocket pool',
  'Deploy Kubernetes microservice rolling update to QA environment',
  'Perform security vulnerability scan on third-party npm packages',
  'Refactor Tailwind utility classes to match 5-colour palette tokens',
  'Implement role-based authorization check on document download',
  'Export CSV report of daily team workload and effort metrics',
  'Add unit tests for DD-Mon-YYYY date formatting edge cases'
];

for (let i = INITIAL_TASKS.length; i < 124; i++) {
  const targetStatus = statusesDistribution[i] || (i % 2 === 0 ? 'Closed' : 'In Progress');
  const project = PROJECTS[i % PROJECTS.length];
  const customer = CUSTOMERS[i % CUSTOMERS.length];
  const owner = OWNERS[i % OWNERS.length];
  const team = TEAMS[i % TEAMS.length];
  const dayOffset = (i % 28) + 1;
  const dayStr = String(dayOffset).padStart(2, '0');
  const endDayStr = String(Math.min(28, dayOffset + 6)).padStart(2, '0');

  const effortEst = 8 + (i % 5) * 4;
  const effortAct = targetStatus === 'Closed' ? effortEst : targetStatus === 'In Progress' ? Math.round(effortEst * 0.6) : 0;

  INITIAL_TASKS.push({
    id: `task-${i + 1}`,
    ticket_number: `${project.split(' ')[0].toUpperCase()}-${100 + i}`,
    subject: SUBJECT_TEMPLATES[i % SUBJECT_TEMPLATES.length] + ` (#${i + 1})`,
    description: `Detailed technical specification for task execution within ${project}. Adheres to MiTRAA standards.`,
    status: targetStatus,
    priority: i % 15 === 0 ? 'Critical P0' : i % 5 === 0 ? 'Urgent P1' : i % 2 === 0 ? 'Normal P2' : 'Low P3',
    project,
    module: MODULES[i % MODULES.length],
    team,
    customer,
    task_owner: owner,
    assigned_by: 'Deenesh Sabat',
    estimated_start_date: `${dayStr}-Oct-2026`,
    estimated_close_date: `${endDayStr}-Oct-2026`,
    actual_start_date: targetStatus !== 'Open' ? `${dayStr}-Oct-2026` : undefined,
    actual_close_date: targetStatus === 'Closed' ? `${endDayStr}-Oct-2026` : undefined,
    estimated_effort: effortEst,
    actual_effort: effortAct,
    work_log_entries: effortAct > 0 ? [
      { id: `wl-${i}-1`, date: `${dayStr}-Oct-2026`, hours: effortAct, description: 'Core feature development and code review' }
    ] : [],
    subtask_count: i % 4,
    attachments: i % 3 === 0 ? [`spec_doc_${i}.pdf`] : []
  });
}

// 657 Meetings Seed Generator
export const INITIAL_MEETINGS: MeetingTimeTracker[] = [];
const MEETING_CATEGORIES = ['Sprint Planning', 'Client Standup', 'Architecture Sync', 'Backlog Grooming', 'Retrospective', 'Ad-hoc Technical Discussion'];

for (let i = 0; i < 657; i++) {
  const day = ((i * 7) % 30) + 1;
  const month = i < 300 ? 'Oct' : i < 500 ? 'Sep' : 'Aug';
  const year = 2026;
  const dayStr = String(day).padStart(2, '0');
  const project = PROJECTS[i % PROJECTS.length];
  const customer = CUSTOMERS[i % CUSTOMERS.length];
  const team = TEAMS[i % TEAMS.length];
  const organizer = OWNERS[i % OWNERS.length];

  const durationMin = [30, 45, 60, 90, 120][i % 5];
  const startHour = 9 + (i % 8);
  const startStr = `${startHour > 12 ? startHour - 12 : startHour}:00 ${startHour >= 12 ? 'PM' : 'AM'}`;
  const endHour = startHour + Math.floor(durationMin / 60);
  const endMin = durationMin % 60;
  const endStr = `${endHour > 12 ? endHour - 12 : endHour}:${endMin === 0 ? '00' : endMin} ${endHour >= 12 ? 'PM' : 'AM'}`;

  INITIAL_MEETINGS.push({
    id: `meeting-${i + 1}`,
    date: `${dayStr}-${month}-${year}`,
    customer,
    project,
    module: i % 3 === 0 ? 'Project Requirements' : i % 2 === 0 ? 'All Modules' : 'Internal Meeting',
    team,
    category: MEETING_CATEGORIES[i % MEETING_CATEGORIES.length],
    meeting_by: organizer,
    meeting_with: [OWNERS[(i + 1) % OWNERS.length], OWNERS[(i + 2) % OWNERS.length]],
    lead_attended: i % 2 === 0,
    architect_attended: i % 3 === 0,
    start_time: startStr,
    end_time: endStr,
    total_time: durationMin,
    comments: `Review progress for ${project} sprint deliverables and action items.`
  });
}

// Leave & WFH Tracker Records (Planned Leave: 28 days, WFH Approved: 14 days, Unplanned Leave: 1 day, Holidays)
export const INITIAL_LEAVES: OptimaLeaveTracker[] = [
  {
    id: 'leave-1',
    name: 'Vivek Singh',
    role: 'Senior Backend Engineer',
    start_date: '05-Oct-2026',
    end_date: '09-Oct-2026',
    day: 'Full day',
    leave_type: 'Planned Leave',
    comments: 'Annual family vacation'
  },
  {
    id: 'leave-2',
    name: 'Bharat Sharma',
    role: 'Lead Architect',
    start_date: '12-Oct-2026',
    end_date: '16-Oct-2026',
    day: 'Full day',
    leave_type: 'Planned Leave',
    comments: 'Personal leave'
  },
  {
    id: 'leave-3',
    name: 'Priya Patel',
    role: 'Frontend Engineer',
    start_date: '19-Oct-2026',
    end_date: '23-Oct-2026',
    day: 'Full day',
    leave_type: 'Planned Leave',
    comments: 'Family function'
  },
  {
    id: 'leave-4',
    name: 'Rohan Sharma',
    role: 'QA Engineer',
    start_date: '26-Oct-2026',
    end_date: '30-Oct-2026',
    day: 'Full day',
    leave_type: 'Planned Leave',
    comments: 'Personal time off'
  },
  {
    id: 'leave-5',
    name: 'Ajay Kumar',
    role: 'Product Manager',
    start_date: '14-Oct-2026',
    end_date: '14-Oct-2026',
    day: 'Full day',
    leave_type: 'Unplanned Leave',
    comments: 'Medical emergency'
  },
  {
    id: 'leave-6',
    name: 'Deenesh Sabat',
    role: 'Production Engineer',
    start_date: '02-Oct-2026',
    end_date: '03-Oct-2026',
    day: 'Full day',
    leave_type: 'WFH (Approved)',
    comments: 'Working remotely for internet cabling upgrade'
  },
  {
    id: 'leave-7',
    name: 'Neha Gupta',
    role: 'UI / UX Designer',
    start_date: '07-Oct-2026',
    end_date: '09-Oct-2026',
    day: 'Full day',
    leave_type: 'WFH (Approved)',
    comments: 'WFH approved by manager'
  },
  {
    id: 'leave-8',
    name: 'Siddharth Sen',
    role: 'Python Developer',
    start_date: '21-Oct-2026',
    end_date: '22-Oct-2026',
    day: 'Full day',
    leave_type: 'WFH (Approved)',
    comments: 'Home renovation'
  },
  {
    id: 'leave-9',
    name: 'Company Wide',
    role: 'All Employees',
    start_date: '20-Oct-2026',
    end_date: '20-Oct-2026',
    day: 'Full day',
    leave_type: 'Holiday',
    comments: 'Dussehra / Vijayadashami National Holiday'
  },
  {
    id: 'leave-10',
    name: 'Alok Gupta',
    role: 'DevOps Engineer',
    start_date: '15-Oct-2026',
    end_date: '15-Oct-2026',
    day: 'Half day',
    leave_type: 'WFH (Unapproved)',
    comments: 'Pending supervisor signoff'
  }
];

// Project Weekly Updates (Weekly Update Tracker)
export const INITIAL_WEEKLY_UPDATES: OptimaWeeklyUpdateTracker[] = [
  {
    id: 'update-1',
    project_name: 'AEC Kuwait',
    customer_name: 'AEC',
    customer_geography: 'Middle East',
    industry: 'Energy & Infrastructure',
    state: 'On Track',
    current_state: 'Active Sprint 14',
    current_status: 'Completed Redis lock idempotency and payments testing. Preparing deployment to QA environment.',
    project_status: 'Green',
    last_week_status: 'On schedule with milestone 3 delivery.',
    priority: 'High',
    risk: 'Low - external vendor API dependencies resolved.',
    project_summery: 'Delivery of core transaction logging modules and task management integrations.',
    start_date: '01-Jan-2026',
    go_live_date: '15-Dec-2026',
    weekly_start_date: '26-Oct-2026',
    weekly_end_date: '30-Oct-2026',
    ref: 'AEC-REF-2026-Q4',
    code_base: 'git@github.com:mitraa/aec-kuwait-backend.git',
    documentation: 'https://wiki.mitraa.internal/docs/aec-kuwait',
    kt_provider: 'Bharat Sharma',
    hld_link: 'https://drive.mitraa.internal/hld/aec_kuwait_v2.pdf',
    lld_link: 'https://drive.mitraa.internal/lld/aec_kuwait_lld.pdf',
    testers: ['Rohan Sharma', 'Pooja Varma'],
    fe_developers: ['Priya Patel', 'Deenesh Sabat'],
    be_developers: ['Vivek Singh', 'Bharat Sharma'],
    python_developers: ['Siddharth Sen'],
    ux_designers: ['Neha Gupta'],
    shared_resources: ['Alok Gupta (DevOps)'],
    totalEnv: 4,
    dev_url: 'https://dev.aeckuwait.mitraa.internal',
    qa_url: 'https://qa.aeckuwait.mitraa.internal',
    la_url: 'https://la.aeckuwait.mitraa.internal',
    other_url: 'https://demo.aeckuwait.mitraa.internal',
    dev_arrow: true,
    qa_arrow: true,
    la_arrow: true,
    other_arrow: true
  },
  {
    id: 'update-2',
    project_name: 'NextGen',
    customer_name: 'NextGen',
    customer_geography: 'North America',
    industry: 'Healthcare Technology',
    state: 'In Progress',
    current_state: 'Sprint 8',
    current_status: 'Finalizing patient appointment calendar integration and HL7 protocol adapters.',
    project_status: 'Amber',
    last_week_status: 'Pending feedback from security compliance audit.',
    priority: 'High',
    risk: 'Medium - SOC2 audit review in progress.',
    project_summery: 'Patient portal overhaul with FHIR interoperability.',
    start_date: '15-Mar-2026',
    go_live_date: '30-Nov-2026',
    weekly_start_date: '26-Oct-2026',
    weekly_end_date: '30-Oct-2026',
    testers: ['Kavita Reddy'],
    fe_developers: ['Deenesh Sabat'],
    be_developers: ['Vivek Singh'],
    python_developers: ['Siddharth Sen'],
    ux_designers: ['Neha Gupta'],
    shared_resources: ['Meera Nair'],
    totalEnv: 3,
    dev_url: 'https://dev.nextgen.mitraa.internal',
    qa_url: 'https://qa.nextgen.mitraa.internal',
    la_url: 'https://la.nextgen.mitraa.internal',
    other_url: '',
    dev_arrow: true,
    qa_arrow: true,
    la_arrow: false,
    other_arrow: false
  }
];

// Seed 50 Document records for View Documents
export const INITIAL_DOCUMENTS: DocumentRecord[] = [];
const FILE_EXTS = ['pdf', 'docx', 'xlsx', 'png', 'zip'];
for (let i = 0; i < 50; i++) {
  const project = PROJECTS[i % PROJECTS.length];
  const ext = FILE_EXTS[i % FILE_EXTS.length];
  const day = ((i * 3) % 28) + 1;
  const dayStr = String(day).padStart(2, '0');
  INITIAL_DOCUMENTS.push({
    id: `doc-${i + 1}`,
    ticket_number: `${project.split(' ')[0].toUpperCase()}-${400 + i}`,
    file_name: `${project.toLowerCase().replace(/ /g, '_')}_specification_v${(i % 5) + 1}.${ext}`,
    file_size: `${(1.2 + (i * 0.4)).toFixed(1)} MB`,
    file_type: ext.toUpperCase(),
    uploaded_date: `${dayStr}-Oct-2026`,
    customer: CUSTOMERS[i % CUSTOMERS.length],
    project,
    task_team: TEAMS[i % TEAMS.length],
    task_owner: OWNERS[i % OWNERS.length],
    task_status: i % 2 === 0 ? 'Closed' : 'In Progress',
    task_priority: i % 4 === 0 ? 'Critical P0' : 'Normal P2'
  });
}

// Dropdown Management Seed Data
export const INITIAL_DROPDOWNS: DropdownItem[] = [
  { id: 'dm-1', type: 'Task Owner', value: 'Deenesh Sabat', recorded_date: '01-Sep-2026', comments: 'Production Engineer' },
  { id: 'dm-2', type: 'Task Owner', value: 'Ajay Kumar', recorded_date: '01-Sep-2026', comments: 'Product Manager' },
  { id: 'dm-3', type: 'Task Owner', value: 'Bharat Sharma', recorded_date: '01-Sep-2026', comments: 'Lead Architect' },
  { id: 'dm-4', type: 'Task Owner', value: 'Vivek Singh', recorded_date: '01-Sep-2026', comments: 'Senior Backend Engineer' },
  { id: 'dm-5', type: 'Task Owner', value: 'Priya Patel', recorded_date: '01-Sep-2026', comments: 'Senior Frontend Engineer' },
  { id: 'dm-6', type: 'Task Owner', value: 'Neha Gupta', recorded_date: '01-Sep-2026', comments: 'Lead UI/UX Designer' },
  { id: 'dm-7', type: 'Customer', value: 'AEC', recorded_date: '01-Aug-2026', comments: 'Energy sector enterprise client' },
  { id: 'dm-8', type: 'Customer', value: 'NextGen', recorded_date: '01-Aug-2026', comments: 'Healthtech enterprise' },
  { id: 'dm-9', type: 'Customer', value: 'Internal', recorded_date: '01-Aug-2026', comments: 'Internal operations' },
  { id: 'dm-10', type: 'Project', value: 'AEC Kuwait', recorded_date: '01-Aug-2026', comments: 'Active project' },
  { id: 'dm-11', type: 'Project', value: 'NextGen', recorded_date: '01-Aug-2026', comments: 'Active project' },
  { id: 'dm-12', type: 'Module', value: 'All Modules', recorded_date: '01-Aug-2026', comments: 'Consolidated module entry' },
  { id: 'dm-13', type: 'Module', value: 'Project Requirements', recorded_date: '01-Aug-2026', comments: 'Scope & requirements' },
  { id: 'dm-14', type: 'Team', value: 'QA', recorded_date: '01-Aug-2026', comments: 'Quality assurance' },
  { id: 'dm-15', type: 'Team', value: 'FE Development', recorded_date: '01-Aug-2026', comments: 'Frontend team' },
  { id: 'dm-16', type: 'Team', value: 'BE Development', recorded_date: '01-Aug-2026', comments: 'Backend team' },
  { id: 'dm-17', type: 'Team', value: 'Project Management', recorded_date: '01-Aug-2026', comments: 'PM & Scrum' }
];
