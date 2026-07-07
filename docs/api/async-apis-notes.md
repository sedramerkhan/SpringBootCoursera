# Asynchronous APIs — Notes

APIs that enable **non-blocking** communication — the client doesn't wait around
for an immediate response, making real-time, scalable applications possible.

## Key features

- **Non-blocking communication** — the client doesn't pause for the server; it
  keeps working and receives the response later, staying responsive.
- **Event-driven model** — responses arrive via events, callbacks, or webhooks;
  ideal for real-time updates (notifications, live data).
- **Improved performance** — avoiding blocking calls lets the API handle many
  requests at once, supporting high concurrency.

## How it works

1. **Client sends the request** — but, unlike a synchronous call, doesn't wait for
   an immediate response, so it can keep doing other work.
2. **Server processes in the background** — handled asynchronously (event loop or
   worker process) so other requests aren't blocked.
3. **Event notification is triggered** — when the result is ready, the server
   notifies the client via a callback, webhook, or message queue.
4. **Client receives the response** — processed as soon as it arrives, without
   blocking ongoing operations.

## WebSockets

A popular way to build real-time async communication:

- **Persistent connection** — one open channel is kept between client and server
  instead of opening/closing per request, saving time and resources.
- **Instant data exchange** — messages move back and forth in real time with very
  low latency; crucial for chat, gaming, and live dashboards.
- **Bidirectional** — unlike traditional HTTP (client always initiates), both
  client and server can send messages whenever needed.

## Message queues

The backbone of scalable async systems:

The **producer** sends work; the **consumer** does the work; the **queue** sits
between them holding messages until they're processed. That middle layer buys
three things:

### 1. Decoupling

The producer and consumer don't need to know about — or wait for — each other.
Instead of Service A calling Service B directly, A drops a message in the queue
and moves on; B picks it up when it's ready.

- **Why it matters:** with a direct call, A depends on B being up *right now* — if
  B is down or slow, A is stuck or fails. With a queue between them, the message
  waits safely until B can handle it. The two are connected only through the
  queue, not to each other.
- You can change, redeploy, or even replace B without touching A, as long as the
  message format stays the same — they evolve independently.
- **Analogy:** leaving a voicemail instead of needing the person to pick up live.
  You leave the message and carry on; they listen when free. Neither has to be
  available at the same moment.

### 2. Scaling

Because work sits in the queue waiting to be consumed, you can run **multiple
consumers** pulling from the same queue and processing in parallel. Workload grows
→ add consumer instances; shrinks → remove some (**horizontal scaling**).

- The queue is also a **buffer** that absorbs spikes. If 10,000 orders arrive in
  one second but consumers handle 1,000/sec, without a queue the surge crashes the
  service. With a queue, all 10,000 wait in line and drain steadily — the system
  stays stable, it just takes a little longer. This is **load leveling**:
  smoothing bursty demand into a steady flow.
- **Analogy:** a supermarket line. When it gets busy you open more checkout
  counters (add consumers); the line makes sure no customer is turned away during
  the rush — they just wait their turn.

### 3. Reliable delivery

The queue guarantees a message isn't lost even if something fails midway, via:

- **Durable storage** — messages are persisted to disk, so if the queue or a
  service crashes and restarts, they're still there.
- **Acknowledgements (acks)** — a consumer tells the queue "done, delete it" only
  *after* processing successfully. If it crashes before acking, the queue assumes
  the work wasn't done and hands the message to another consumer to retry.
- **Retries & dead-letter queues (DLQ)** — a repeatedly failing message is retried
  automatically; if it still fails, it's moved to a special dead-letter queue for
  later inspection instead of being silently dropped.

Used at the core of large-scale systems: e-commerce, cloud services, financial apps.

## Takeaway

Async APIs (step-by-step non-blocking flow) + **WebSockets** for real-time
communication + **message queues** for scalability and reliability = applications
that are fast, scalable, and ready for real-time interaction.