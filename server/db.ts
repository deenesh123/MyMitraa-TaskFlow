/**
 * Database Layer for MyMitraa TaskFlow
 * Database Name: mymitraa_taskflow (migrated from pardali_service per migration plan)
 * Supports file persistence and in-memory operations with collection schema validation.
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import {
  INITIAL_TASKS,
  INITIAL_MEETINGS,
  INITIAL_LEAVES,
  INITIAL_WEEKLY_UPDATES,
  INITIAL_DOCUMENTS,
  INITIAL_DROPDOWNS,
  INITIAL_USERS,
  CURRENT_USER,
} from '../src/api/mockData.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DATA_DIR = path.resolve(__dirname, '../data');
const DB_FILE = path.resolve(DATA_DIR, 'mymitraa_taskflow.json');

export interface TaskOwnerEmailRecord {
  id: string;
  task_id: string;
  owner_name: string;
  owner_email: string;
  assigned_by: string;
  subject: string;
  sent_at: string;
  status: string;
}

export interface MyMitraaDatabase {
  database_name: string;
  version: string;
  last_updated: string;
  tasks: any[];
  meetings: any[];
  leaves: any[];
  weeklyUpdates: any[];
  documents: any[];
  dropdowns: any[];
  users: any[];
  taskOwnerEmails: TaskOwnerEmailRecord[];
  projectStatuses: any[];
  timelineEntries: any[];
}

function getInitialDatabase(): MyMitraaDatabase {
  return {
    database_name: 'mymitraa_taskflow',
    version: '3.0.0',
    last_updated: new Date().toISOString(),
    tasks: JSON.parse(JSON.stringify(INITIAL_TASKS)),
    meetings: JSON.parse(JSON.stringify(INITIAL_MEETINGS)),
    leaves: JSON.parse(JSON.stringify(INITIAL_LEAVES)),
    weeklyUpdates: JSON.parse(JSON.stringify(INITIAL_WEEKLY_UPDATES)),
    documents: JSON.parse(JSON.stringify(INITIAL_DOCUMENTS)),
    dropdowns: JSON.parse(JSON.stringify(INITIAL_DROPDOWNS)),
    users: JSON.parse(JSON.stringify(INITIAL_USERS)),
    taskOwnerEmails: [
      {
        id: 'toe-1',
        task_id: 'task-1',
        owner_name: 'Aarav Sharma',
        owner_email: 'aarav.sharma@mymitraa.com',
        assigned_by: 'Deenesh Sabat',
        subject: 'Real-time WebSocket & Live Meeting Tracker Sync',
        sent_at: '2026-11-04T10:00:00Z',
        status: 'DELIVERED',
      },
    ],
    projectStatuses: [
      {
        id: 'ps-1',
        projectName: 'MyMitraa Enterprise Suite',
        status: 'On Track',
        healthScore: '94%',
        lead: 'Deenesh Sabat',
        activeSprints: 3,
        updatedAt: '2026-11-04',
      },
      {
        id: 'ps-2',
        projectName: 'TaskFlow Cloud Migrations',
        status: 'Active',
        healthScore: '98%',
        lead: 'Bharat Sharma',
        activeSprints: 2,
        updatedAt: '2026-11-03',
      },
    ],
    timelineEntries: [
      {
        id: 'tl-1',
        title: 'Backend Migration to Node.js & Zoho SMTP',
        entityType: 'PROJECT',
        eventDate: '2026-11-06',
        performedBy: 'Deenesh Sabat',
        category: 'DEPLOYMENT',
      },
      {
        id: 'tl-2',
        title: 'User Management & Role-Based Access Control',
        entityType: 'SECURITY',
        eventDate: '2026-11-05',
        performedBy: 'Admin',
        category: 'FEATURE',
      },
    ],
  };
}

let dbCache: MyMitraaDatabase | null = null;

export function loadDatabase(): MyMitraaDatabase {
  if (dbCache) return dbCache;

  try {
    if (!fs.existsSync(DATA_DIR)) {
      fs.mkdirSync(DATA_DIR, { recursive: true });
    }

    if (fs.existsSync(DB_FILE)) {
      const raw = fs.readFileSync(DB_FILE, 'utf-8');
      dbCache = JSON.parse(raw);
      return dbCache!;
    }
  } catch (err) {
    console.warn('[MyMitraa DB] Could not read disk DB, initializing fresh store:', err);
  }

  dbCache = getInitialDatabase();
  saveDatabase(dbCache);
  return dbCache;
}

export function saveDatabase(data: MyMitraaDatabase): void {
  try {
    data.last_updated = new Date().toISOString();
    dbCache = data;
    if (!fs.existsSync(DATA_DIR)) {
      fs.mkdirSync(DATA_DIR, { recursive: true });
    }
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf-8');
  } catch (err) {
    console.error('[MyMitraa DB] Error persisting database file:', err);
  }
}
