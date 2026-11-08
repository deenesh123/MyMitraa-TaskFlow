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
const PORT = Number(process.env.PORT || 3000);
const HOST = process.env.HOST || '0.0.0.0';

// Configurable backend URL pointing to the authoritative Spring Boot server
const BACKEND_URL = (process.env.BACKEND_API_BASE_URL || process.env.VITE_BACKEND_URL || 'http://127.0.0.1:8080').replace(/\/$/, '');

// Parse incoming payloads
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ extended: true, limit: '50mb' }));

/**
 * Health check endpoint: Checks and reports the live status of the Spring Boot backend
 */
app.get('/api/health', async (req: Request, res: Response) => {
  try {
    const backendRes = await fetch(`${BACKEND_URL}/api/health`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
      },
      signal: AbortSignal.timeout(3000),
    });

    const data = await backendRes.json();
    return res.status(backendRes.status).json(data);
  } catch (err: any) {
    return res.status(503).json({
      code: 503,
      status: 'DOWN',
      application: 'MyMitraa TaskFlow',
      backend: 'Spring Boot (Offline)',
      backendUrl: BACKEND_URL,
      database: 'mymitraa_taskflow (MongoDB)',
      databaseConnected: false,
      message: `Authoritative Spring Boot backend is unreachable at ${BACKEND_URL}. Start the Spring Boot application (mvn spring-boot:run) with MongoDB to connect all services.`,
    });
  }
});

/**
 * Transparent Reverse Proxy to Spring Boot:
 * Forwards HTTP methods, request bodies, query parameters, authorization headers,
 * response status codes, and response bodies to the authoritative backend.
 */
app.all('/api/*', async (req: Request, res: Response) => {
  const targetUrl = `${BACKEND_URL}${req.originalUrl}`;

  // Forward client request headers, filtering out host-specific headers
  const forwardHeaders: Record<string, string> = {};
  for (const [key, value] of Object.entries(req.headers)) {
    if (value && !['host', 'connection', 'content-length'].includes(key.toLowerCase())) {
      forwardHeaders[key] = Array.isArray(value) ? value.join(', ') : value;
    }
  }

  // Ensure JSON content-type if client sent JSON
  if (req.is('json') && !forwardHeaders['content-type']) {
    forwardHeaders['content-type'] = 'application/json';
  }

  const hasBody = ['POST', 'PUT', 'PATCH', 'DELETE'].includes(req.method) && req.body && Object.keys(req.body).length > 0;
  const fetchOptions: RequestInit = {
    method: req.method,
    headers: forwardHeaders,
    body: hasBody ? JSON.stringify(req.body) : undefined,
    signal: AbortSignal.timeout(2500),
  };

  try {
    const backendRes = await fetch(targetUrl, fetchOptions);

    // Forward backend response headers
    backendRes.headers.forEach((val, key) => {
      if (!['transfer-encoding', 'content-encoding'].includes(key.toLowerCase())) {
        res.setHeader(key, val);
      }
    });

    res.status(backendRes.status);
    const contentType = backendRes.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      const data = await backendRes.json();
      return res.json(data);
    } else {
      const text = await backendRes.text();
      return res.send(text);
    }
  } catch (err: any) {
    // Honest reporting when Spring Boot backend is offline
    return res.status(503).json({
      code: 503,
      status: 'BACKEND_UNAVAILABLE',
      error: 'Spring Boot Backend Unreachable',
      backendUrl: BACKEND_URL,
      database: 'mymitraa_taskflow (MongoDB)',
      targetEndpoint: req.originalUrl,
      message: `The authoritative Java Spring Boot backend is unreachable at ${BACKEND_URL}. Please start the Spring Boot service with MongoDB (see LOCAL-SETUP.md).`,
    });
  }
});

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
    console.log(`🔗 Reverse Proxy configured to authoritative Spring Boot backend: ${BACKEND_URL}`);
    console.log(`🍃 Target Database: MongoDB 'mymitraa_taskflow'`);
  });
}

startServer().catch(err => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
