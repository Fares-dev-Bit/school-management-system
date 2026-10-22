# Backend API Reference

**Base URL:** `http://<server-ip>:8040`

No authentication required. CORS open to all origins (`*`).  
All requests and responses are **JSON** with `Content-Type: application/json`.

---

## Endpoints

### Candidates CRUD

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/candidates` | List all candidates |
| `GET` | `/api/v1/candidates/{id}` | Get a single candidate by ID |
| `POST` | `/api/v1/candidates` | Create a candidate |
| `POST` | `/api/v1/candidates/batch` | Create multiple candidates |
| `PUT` | `/api/v1/candidates/{id}` | Update a candidate |
| `PUT` | `/api/v1/candidates/batch` | Batch update candidates |
| `DELETE` | `/api/v1/candidates/{id}` | Delete a candidate |
| `DELETE` | `/api/v1/candidates/batch` | Delete multiple candidates |
| `GET` | `/api/v1/candidates/search` | Search / filter candidates |

### Utility

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/network-info` | Get server IP and port |
| `POST` | `/api/v1/shutdown` | Gracefully stop the server |

---

## Request / Response Examples

### List all candidates

```
GET /api/v1/candidates
```

**Response `200`**
```json
[
  {
    "id": 1,
    "firstName": "ahmed",
    "lastName": "benali",
    "phone": "0612345678",
    "stage": "CODE",
    "category": "B",
    "createdAt": "2026-07-30",
    "infoModel": {
      "id": 1,
      "birthDate": "2000-05-15",
      "codeDate": "2026-07-30",
      "crneauDate": null,
      "circulationDate": null
    }
  }
]
```

### Get candidate by ID

```
GET /api/v1/candidates/1
```

**Response `200`** — same shape as a single item above.

**Response `404`**
```json
{ "success": false, "error": "Candidate not found with ID: 1" }
```

### Create a candidate

```
POST /api/v1/candidates
```

**Request body**
```json
{
  "firstName": "Sara",
  "lastName": "Amrani",
  "phone": "0655557777",
  "stage": "CODE",
  "category": "B",
  "infoModel": {
    "birthDate": "2002-03-10"
  }
}
```

`stage` determines which date gets auto-set:
- `CODE` → `infoModel.codeDate` = today
- `CRNEAU` → `infoModel.crneauDate` = today
- `CIRCULATION` or `COMPLETED` → `infoModel.circulationDate` = today

**Response `201`** — the created candidate object with auto-generated `id`, `createdAt`, and stage dates.

**Response `400`** — validation error
```json
{
  "details": {
    "phone": "Phone must contain exactly 10 digits",
    "firstName": "must not be blank"
  }
}
```

**Response `409`** — duplicate (same firstName + lastName + phone)
```json
{ "success": false, "error": "user already exists" }
```

### Batch create candidates

```
POST /api/v1/candidates/batch
```

**Request body**
```json
[
  {
    "firstName": "Ali",
    "lastName": "Slimani",
    "phone": "0611111111",
    "stage": "CRNEAU",
    "category": "A",
    "infoModel": { "birthDate": "1999-01-01" }
  },
  {
    "firstName": "Omar",
    "lastName": "Bennis",
    "phone": "0622222222",
    "stage": "CIRCULATION",
    "category": "B",
    "infoModel": { "birthDate": "2001-06-15" }
  }
]
```

**Response `201`** — array of created candidates.

### Update a candidate

```
PUT /api/v1/candidates/1
```

**Request body** (only include fields you want to change)
```json
{
  "firstName": "Ahmed Updated",
  "stage": "CIRCULATION"
}
```

- If `stage` changes, the corresponding date in `infoModel` is auto-set.
- `firstName` and `lastName` are trimmed and lowercased.

**Response `200`** — the updated candidate object.

### Batch update candidates

```
PUT /api/v1/candidates/batch
```

**Request body** — each object **must** include `id`
```json
[
  { "id": 1, "stage": "COMPLETED", "category": "A1" },
  { "id": 2, "phone": "0699999999" }
]
```

**Response `200`** — array of updated candidates.

### Delete a candidate

```
DELETE /api/v1/candidates/1
```

**Response `200`**
```
Deleted
```

**Response `404`**
```json
{ "success": false, "error": "Candidate not found with ID 1" }
```

### Batch delete candidates

```
DELETE /api/v1/candidates/batch
```

**Request body**
```json
[1, 3, 5]
```

**Response `200`** — empty body.

### Search / filter candidates

```
GET /api/v1/candidates/search?q=ahmed&stage=CODE&category=B&dataField=createdAt&start=2026-01-01&end=2026-07-30
```

All query parameters are **optional**.

| Param | Type | Description |
|-------|------|-------------|
| `q` | string | Search keyword (matches firstName, lastName, phone — partial, case-insensitive) |
| `stage` | string | Filter by stage: `CODE`, `CRNEAU`, `CIRCULATION`, `COMPLETED` |
| `category` | string | Filter by category: `A1`, `A`, `B` |
| `dataField` | string | Date field to filter on: `createdAt`, `birthDate`, `codeDate`, `crneauDate`, `circulationDate` |
| `start` | date (ISO) | Start date (inclusive). Requires `dataField`. |
| `end` | date (ISO) | End date (inclusive). Requires `dataField`. |

**Usage notes for date filters:**
- `dataField` is **required** if `start` or `end` is provided.
- If only `start` → results with dates >= start.
- If only `end` → results with dates <= end.
- If both → results between start and end (inclusive).

**Example — search by name with stage filter:**
```
GET /api/v1/candidates/search?q=ben&stage=CODE
```

**Example — filter by creation date range:**
```
GET /api/v1/candidates/search?dataField=createdAt&start=2026-06-01&end=2026-06-30
```

**Response `200`** — array of matching candidates.

### Get network info

```
GET /api/v1/network-info
```

**Response `200`**
```json
{
  "ip": "192.168.1.100",
  "port": "8040"
}
```

### Shutdown server

```
POST /api/v1/shutdown
```

**Response `200`** — server stops after ~500ms.

---

## Validation Rules

| Field | Rule |
|-------|------|
| `firstName` | Required, not blank. Auto-trimmed and lowercased. |
| `lastName` | Required, not blank. Auto-trimmed and lowercased. |
| `phone` | Required. Pattern: `^0[5-7][0-9]{8}$` (starts with `05`, `06`, or `07`, exactly 10 digits). |
| `stage` | Required. One of: `CODE`, `CRNEAU`, `CIRCULATION`, `COMPLETED`. |
| `category` | Required. One of: `A1`, `A`, `B`. |
| `infoModel.birthDate` | Must be in the past. Candidate must be at least 18 years old. |
| Unique constraint | Combination of `firstName` + `lastName` + `phone` must be unique. Returns `409` on duplicate. |

---

## Error Summary

| Status | Meaning | Response Shape |
|--------|---------|----------------|
| `400` | Validation error | `{ "details": { "field": "message" } }` |
| `404` | Candidate not found | `{ "success": false, "error": "message" }` |
| `409` | Duplicate entry | `{ "success": false, "error": "user already exists" }` |

---

## cURL Quick Tests

```bash
# List all
curl http://localhost:8040/api/v1/candidates

# Create
curl -X POST http://localhost:8040/api/v1/candidates \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "phone": "0612345678",
    "stage": "CODE",
    "category": "B",
    "infoModel": { "birthDate": "2000-01-01" }
  }'

# Search
curl "http://localhost:8040/api/v1/candidates/search?q=test&stage=CODE"
```
