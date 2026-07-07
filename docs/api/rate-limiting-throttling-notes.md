# Rate Limiting & Throttling — Notes

Techniques that keep an API fast, stable, and **fair** by controlling how much
traffic each client can send. They pair with caching and CDNs (see
[Caching Strategies](./caching-notes.md)) as the core performance toolkit.

## The three concepts at a glance

| Concept | What it does |
|---|---|
| **CDN** | delivers cached content from servers near the user → lower latency (covered in [caching notes](./caching-notes.md)) |
| **Rate limiting** | caps *how many* requests a client may make in a time window → **blocks** the excess |
| **Throttling** | *slows or delays* excess requests instead of blocking them → smooths traffic |

Key difference: **rate limiting says "no"** (reject once over the limit);
**throttling says "wait"** (queue/delay so requests still succeed, just slower).

## Rate limiting

**Rate limiting caps how many requests one client may make in a given time
window.** The server counts each client's requests (usually keyed by API key, user
ID, or IP); once the count passes the limit, further requests are **rejected**
until the window resets — typically with HTTP `429 Too Many Requests`.

The point is protection: it stops any single client — whether a runaway script, a
buggy loop, or an abusive user — from flooding the server and degrading it for
everyone else.

The algorithms below are just **different ways of counting** the requests and
deciding when the limit is reached:

> **Burst** — a sudden cluster of many requests in a very short time, as opposed
> to a steady, evenly-spaced stream. Example: 50 requests in 1 second is a burst;
> 1 request per second for 50 seconds is the same total but *not* a burst. A key
> difference between the algorithms is whether they **allow** bursts (let a quiet
> client suddenly send many at once) or **smooth** them (spread them into a steady
> flow).

### Fixed window
Count requests in a fixed clock interval — e.g. **100 per minute**, resetting at
the top of each minute. Over the limit → block until the next window.
- Simplest to implement, but has an **edge-burst flaw**: a client can send 100 at
  11:00:59 and 100 more at 11:01:00 — 200 requests in ~1 second, because the
  counter resets between them.

### Sliding window
Count over a **moving** window — the last 60 seconds measured from *now*, not a
fixed clock interval. This spreads requests evenly and removes the
spike-at-the-reset problem the fixed window has.

### Token bucket
A bucket holds **tokens** that refill at a steady rate; each request spends one.
Requests pass **while tokens remain**; when the bucket is empty they're blocked
until it refills.
- Because unused tokens accumulate, a client can spend several at once → **short
  bursts are allowed** while the long-run average stays capped. Good when
  occasional spikes are fine.

### Leaky bucket
Requests pour into a bucket and are **let out at a steady, fixed rate** — like
water leaking from a hole in the bottom. Extra requests queue up and drain
gradually; if the bucket overflows, they're dropped.
- Forces a **smooth, constant** output rate (the opposite of token bucket, which
  permits bursts). Good for protecting a downstream service that needs steady load.

| Algorithm | Allows bursts? | Best for |
|---|---|---|
| Fixed window | yes (at window edges) | the simplest cap |
| Sliding window | no | even distribution, no reset spikes |
| Token bucket | **yes**, controlled | allowing bursts while capping the average |
| Leaky bucket | no | forcing a steady, constant outflow |

## Throttling

Throttling controls the **flow** of requests rather than the raw count — when a
client goes over the acceptable rate, its requests aren't rejected outright, they
are **slowed, delayed, or queued** so the system stays stable and every client
gets a fair share. Think of it as *shaping* traffic instead of cutting it off.

Three strategies:

- **Request delays** — when a client exceeds the allowed rate, its extra requests
  are **queued or slowed** instead of blocked. The client still gets served, just
  later — this is why throttling feels gentler than a hard rate-limit rejection.
- **Dynamic limits** — the threshold **adapts to server load**. Under heavy load
  (peak traffic) the system *lowers* the limit to protect performance; when load
  eases, it raises it again. Static limits can't do this.
- **Fair usage** — ensures **equal access** by stopping any single client from
  hogging resources at everyone else's expense (e.g. one heavy user can't starve
  the rest).

### Rate limiting vs throttling — the mental model

- **Rate limiting** is a *hard ceiling*: over the limit → request rejected
  (often HTTP `429 Too Many Requests`).
- **Throttling** is a *speed governor*: over the limit → request delayed/queued so
  it still completes, just at a controlled pace.

They're often used **together**: throttle to smooth normal bursts, and rate-limit
as the hard backstop against abuse.

## Takeaway

CDNs cut latency by serving content close to users; **rate limiting** protects
servers from overload by capping request counts; **throttling** preserves fair,
stable access by slowing excess traffic instead of dropping it. Combined, they
keep APIs performant, stable, and fair under load.
