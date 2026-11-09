# MyMitraa TaskFlow — Authentication and JWT Security Guide

This document specifies the authentication lifecycle, JWT token generation, claims, client-side session management, and authorization flows in MyMitraa TaskFlow.

---

## 1. Authentication Lifecycle & Protocol

MyMitraa TaskFlow enforces stateless JSON Web Token (JWT) authentication for all protected API endpoints under `/api/**`.

```
User (Browser)               Spring Boot Backend                 MongoDB
     |                               |                              |
     | 1. POST /api/auth/login       |                              |
     |    { username, password }     |                              |
     |------------------------------>|                              |
     |                               | 2. Fetch User & BCrypt Hash  |
     |                               |----------------------------->|
     |                               |    Return User Document      |
     |                               |<-----------------------------|
     |                               |                              |
     |                               | 3. Generate HMAC-SHA256 JWT  |
     |                               |    - sub: username           |
     |                               |    - roles: [ROLE_USER, ...] |
     |                               |    - exp: +86,400,000 ms     |
     | 4. Return CustomResponse      |                              |
     |    metaInfo.token = <jwt>     |                              |
     |<------------------------------|                              |
     |                               |                              |
     | 5. Store in sessionStorage    |                              |
     |    Key: 'mitraa_jwt_token'    |                              |
     |                               |                              |
     | 6. GET /api/tasks             |                              |
     |    Header: "Bearer <jwt>"     |                              |
     |------------------------------>|                              |
     |                               | 7. JwtAuthenticationFilter:  |
     |                               |    Verify Signature & Claims |
     |                               |    Populate SecurityContext  |
     | 8. 200 OK + Data              |                              |
     |<------------------------------|                              |
```

---

## 2. JWT Configuration & Millisecond Expiration Standard

### 2.1 The Millisecond Standard
In earlier builds, a mismatch occurred where `JwtTokenUtil.java` multiplied the configured expiration by 1,000, assuming input in seconds, while the environment variable supplied milliseconds (e.g. `86400000`).

**Standardization Rule**:
- The expiration property `jwt.expiration` is defined strictly in **milliseconds**.
- Required 24-hour value: `86400000` (86.4 million ms = 24 * 60 * 60 * 1000).
- `JwtTokenUtil.java` now uses:
  ```java
  @Value("${jwt.expiration:86400000}")
  private Long expirationMs;

  Date createdDate = new Date();
  Date expirationDate = new Date(createdDate.getTime() + expirationMs);
  ```
- No extra `* 1000` multiplier is applied.
- Token lifetime is precisely 24 hours.

### 2.2 Token Claims
Tokens issued by `JwtTokenUtil.java` contain:
- `sub`: Principal username.
- `created`: Issuance timestamp (Date).
- `exp`: Expiration timestamp (`created + 86400000 ms`).
- Custom claims: Role list (`roles`).
- Signing Algorithm: `HMAC-SHA256` (`SignatureAlgorithm.HS256`).

---

## 3. Client-Side Session Security

### 3.1 sessionStorage Storage Policy
- **No localStorage**: JWT tokens are **never** placed into `localStorage`. `sessionStorage` limits token lifespan to the browser tab lifecycle, mitigating persistence across untrusted shared machines.
- **Key Name**: `mitraa_jwt_token`.
- **Zero Display**: Tokens are never rendered in DOM nodes, console logs, alert dialogs, or URL parameters.

### 3.2 Single Bearer Formatting
In `src/api/client.ts`, requests format the header strictly once:
```typescript
const token = sessionStorage.getItem('mitraa_jwt_token');
if (token) {
  const cleanToken = token.startsWith('Bearer ') ? token.slice(7) : token;
  headers['Authorization'] = `Bearer ${cleanToken}`;
}
```
This guarantees duplicate `Bearer Bearer ...` prefixes are never transmitted.

### 3.3 HTTP 401 Unauthorized Interceptor
When any API endpoint responds with HTTP 401:
1. `src/api/client.ts` catches the response.
2. `sessionStorage.removeItem('mitraa_jwt_token')` is executed immediately.
3. The custom event `mitraa:unauthorized` is dispatched to the window.
4. `src/App.tsx` listens to this event, clears React authentication state, and redirects the user to `/auth`.

### 3.4 Startup Verification Flow
On application boot (`src/App.tsx`):
1. Client checks if `mitraa_jwt_token` exists in `sessionStorage`.
2. If token is absent, display the login screen.
3. If token is present, perform a live verification request:
   ```
   GET /api/auth/current
   ```
4. If backend returns 200 OK with the authenticated `UserAccount`, dashboard is rendered.
5. If backend returns 401 Unauthorized (expired/revoked token), the session is purged and login is displayed.
6. The client **never** trusts client-side boolean flags alone.

---

## 4. Login, Registration & Logout Contracts

### 4.1 Login Contract (`POST /api/auth/login`)
- **Request Body**:
  ```json
  {
    "username": "admin",
    "password": "SecurePassword123"
  }
  ```
- **Response**:
  ```json
  {
    "status": "SUCCESS",
    "message": "User authenticated successfully",
    "data": {
      "id": "6730a1b2c3d4e5f678901234",
      "username": "admin",
      "email": "admin@mitratechgroup.com",
      "roles": ["ROLE_ADMIN"]
    },
    "metaInfo": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
  }
  ```
- The frontend extracts the token from `res.metaInfo.token` (or `res.meta.token`).

### 4.2 Registration Contract (`POST /api/auth/register`)
- Registration accepts account details and writes a pending or active user record in MongoDB.
- Registration **does not** issue an authenticated session token.
- After successful registration, the client transitions the user to the login form to authenticate explicitly.

### 4.3 Logout Contract (`POST /api/auth/logout`)
- The client dispatches a logout request to the server.
- Regardless of whether the server request succeeds or fails, a `finally` block purges local `sessionStorage` and resets all in-memory user states.

---

## 5. Automated JWT Tests
Automated tests in `JwtTokenExpirationTest.java` verify:
1. `tokenExpirationIsExactly24Hours`: Validates that `exp - iat` equals `86,400,000 ms` (+/- 1,000 ms clock tolerance).
2. `expiredTokenFailsValidation`: Verifies rejection of simulated expired tokens.
3. `validTokenPassesValidation`: Verifies successful username and claim resolution.
