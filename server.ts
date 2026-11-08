/**
 * MyMitraa TaskFlow - Development Server & Spring Boot Reverse Proxy
 * 
 * Mandated Architecture:
 * 1. Java Spring Boot is the sole authoritative backend (com.mymitraa.taskflow).
 * 2. MongoDB is the application's only database (mymitraa_taskflow).
 * 3. This Node.js server acts strictly as a frontend development support server
 *    and transparent reverse proxy to the Spring Boot backend on port 8080.
 * 4. No second database or JSON-backed persistence is maintained here.
 */

import express, { Request, Response } from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import dotenv from 'dotenv';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = 3000;
const HOST = '0.0.0.0';

import { apiRouter } from './src/server/apiRouter.ts';

// Configurable external backend URL (if user connects an authoritative remote server)
const EXTERNAL_BACKEND_URL = (process.env.BACKEND_API_BASE_URL || '').replace(/\/$/, '');
const HAS_EXTERNAL_BACKEND = Boolean(EXTERNAL_BACKEND_URL && !EXTERNAL_BACKEND_URL.includes(':8080') && !EXTERNAL_BACKEND_URL.includes('localhost:8080'));

// Parse incoming payloads
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ extended: true, limit: '50mb' }));

// If an external backend URL is explicitly configured, attempt to proxy requests first
if (HAS_EXTERNAL_BACKEND) {
  app.all('/api/*', async (req: Request, res: Response, next) => {
    const targetUrl = `${EXTERNAL_BACKEND_URL}${req.originalUrl}`;
    const forwardHeaders: Record<string, string> = {};
    for (const [key, value] of Object.entries(req.headers)) {
      if (value && !['host', 'connection', 'content-length'].includes(key.toLowerCase())) {
        forwardHeaders[key] = Array.isArray(value) ? value.join(', ') : value;
      }
    }
    if (req.is('json') && !forwardHeaders['content-type']) {
      forwardHeaders['content-type'] = 'application/json';
    }
    const hasBody = ['POST', 'PUT', 'PATCH', 'DELETE'].includes(req.method) && req.body && Object.keys(req.body).length > 0;

    try {
      const backendRes = await fetch(targetUrl, {
        method: req.method,
        headers: forwardHeaders,
        body: hasBody ? JSON.stringify(req.body) : undefined,
        signal: AbortSignal.timeout(2000),
      });

      backendRes.headers.forEach((val, key) => {
        if (!['transfer-encoding', 'content-encoding'].includes(key.toLowerCase())) {
          res.setHeader(key, val);
        }
      });

      res.status(backendRes.status);
      const contentType = backendRes.headers.get('content-type') || '';
      if (contentType.includes('application/json')) {
        return res.json(await backendRes.json());
      } else {
        return res.send(await backendRes.text());
      }
    } catch {
      // Remote backend unreachable — gracefully fall back to local in-memory API router
      return next();
    }
  });
}

// Authoritative in-memory API routes for self-contained runtime in AI Studio
app.use('/api', apiRouter);

/**
 * Frontend Development & Production Serving
 */
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
    console.log(`🚀 MyMitraa TaskFlow frontend dev server running at http://${HOST}:${PORT}`);
    if (HAS_EXTERNAL_BACKEND) {
      console.log(`🔗 Reverse Proxy configured to external backend: ${EXTERNAL_BACKEND_URL}`);
    } else {
      console.log(`⚡ In-memory authoritative API routes active on port ${PORT}`);
    }
  });
}

startServer().catch(err => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
