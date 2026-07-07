# API Fundamentals — Notes

The foundation the rest of these notes build on: what an API is, why we use one,
and the client–server / REST model behind web APIs.

## What is an API

**API = Application Programming Interface** — a defined set of rules that lets two
pieces of software talk to each other. It exposes *what* you can do (operations
and data) while hiding *how* it's done inside.

- **Contract, not implementation** — the API is the agreed interface; the caller
  doesn't need to know the internal code, language, or database behind it.
- **Analogy:** a restaurant menu. You order from the menu (the API) without
  knowing how the kitchen (the server's internals) prepares the dish. The waiter
  carries your request in and the food back out.

## Why use an API

- **Abstraction** — hide complexity; callers use a simple interface instead of
  internal details.
- **Reuse & integration** — one service can be consumed by many clients (web,
  mobile, third parties) without rewriting logic.
- **Decoupling** — client and server evolve independently as long as the contract
  holds (change the DB or language without breaking callers).
- **Interoperability** — different platforms/languages communicate over a shared,
  standard format (usually JSON over HTTP).
- **Security & control** — expose only what's intended, behind auth, validation,
  rate limits, and versioning.

## The client–server model

Web APIs follow a **request–response** pattern:

1. The **client** (browser, mobile app, another service) sends a **request**.
2. The **server** processes it and returns a **response**.

The client asks; the server answers. They communicate over **HTTP**, and are
otherwise independent — the same server can serve many kinds of clients.

## HTTP methods (verbs)

An HTTP request names a **method** (the action) and a **URL** (the resource):

| Method | Purpose | Example |
|---|---|---|
| **GET** | read data | `GET /users/42` |
| **POST** | create a new resource | `POST /users` |
| **PUT** | replace/update a resource | `PUT /users/42` |
| **PATCH** | partially update | `PATCH /users/42` |
| **DELETE** | remove a resource | `DELETE /users/42` |

Each response carries a **status code**: `2xx` success, `3xx` redirect, `4xx`
client error (e.g. `404` not found, `401` unauthorized), `5xx` server error.

## REST in brief

**REST** (Representational State Transfer) is the most common style for web APIs:

- **Resources** are identified by URLs (`/users`, `/users/42/orders`).
- **HTTP methods** act on those resources (the verbs above).
- **Stateless** — each request carries everything the server needs; the server
  keeps no client session between requests.
- Data is exchanged in a standard format — usually **JSON** (see
  [Serialization](./serialization-notes.md)).

A web API built this way is called **RESTful**.

## How this connects to the other notes

Everything else here refines this base:
[versioning](./api-versioning-notes.md) manages change to the contract,
[serialization](./serialization-notes.md) is the data format,
[caching](./caching-notes.md) / [rate limiting](./rate-limiting-throttling-notes.md)
keep it fast and fair, [async APIs](./async-apis-notes.md) handle non-blocking
flows, and [performance metrics](./performance-metrics-notes.md) tell you how well
it all runs.