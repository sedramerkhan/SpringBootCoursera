# Key Performance Metrics for APIs — Notes

The measures that show how well an API performs — across **speed**,
**reliability**, and **stability** — connecting technical performance to user
satisfaction and business value.

## Why measure

- **Purpose** — APIs need measurable indicators to track behavior accurately.
- **User experience** — speed and reliability directly shape how users feel about
  the app.
- **System efficiency** — metrics guide optimization and scaling; they show where
  to improve and how to manage resources.
- **Business value** — tracking metrics ensures SLA compliance and helps cut costs
  while raising reliability.

## Speed: latency vs response time

Both affect how responsive the API *feels*, but they measure different spans:

| Metric | What it measures |
|---|---|
| **Latency** | the short delay between a request being **sent** and the server **beginning** to process it |
| **Response time** | the **full** duration from request sent → complete response received by the client |

Latency is one slice; response time is end-to-end (includes latency +
processing + transfer). Even small latency increases make an app feel noticeably
slower.

## Reliability under load: throughput & error rate

| Metric | What it measures | Signal |
|---|---|---|
| **Throughput** | requests processed **per second** | higher = handles more load; key when scaling to more users |
| **Error rate** | failed requests ÷ total requests | a rising rate flags reliability issues needing attention |

Related lenses:
- **Capacity** — how well the API holds up under **heavy load**, and whether it
  scales without slowing or failing.
- **Reliability check** — consistency of performance **over time**, so results
  stay stable as traffic grows.

## Stability: availability & uptime

Shows how consistently the API can be accessed and trusted:

- **High availability** — stays accessible with minimal downtime even during
  failures, hardware issues, or traffic spikes.
- **System stability** — maintains steady performance across varying traffic,
  from light usage to peak demand.
- **SLA compliance** — uptime and reliability meet the **Service Level Agreements**
  promised to customers; essential for business credibility and user confidence.

> **SLA (Service Level Agreement)** — a formal, often contractual promise from a
> service provider to its customers about the level of service to expect, stated
> as measurable targets: e.g. **99.9% uptime** (~43 min downtime/month), "95% of
> requests under 200ms", or "<0.1% errors". Miss it and there are usually
> consequences (service credits, refunds). "SLA compliance" = actually hitting
> those numbers, which is exactly why the metrics above are tracked. Related:
> **SLO** (the internal *objective* you aim for, often stricter) and **SLI** (the
> *indicator* — the value actually measured).

## Collecting & analyzing metrics: tools

Knowing *what* to measure is half of it — you also need tools to **collect, store,
visualize, and alert** on these metrics. A typical monitoring pipeline has four
stages:

1. **Instrument / expose** — the app emits its metrics.
   - **Spring Boot Actuator** — exposes health and metrics at `/actuator/metrics`
     out of the box (see [Actuator notes](../spring-boot/actuator-notes.md)).
   - **Micrometer** — the metrics facade Actuator uses; a vendor-neutral API (think
     "SLF4J for metrics") that ships the data to many backends.
2. **Scrape / store** — a time-series database pulls and retains the metrics.
   - **Prometheus** — scrapes a `/actuator/prometheus` endpoint at intervals,
     stores time-series data, and supports queries (PromQL) and alert rules.
3. **Visualize / alert** — turn stored metrics into dashboards and notifications.
   - **Grafana** — dashboards over Prometheus (and other sources): latency graphs,
     error-rate panels, throughput, uptime; fires alerts on thresholds.
4. **Cloud-native alternatives** — managed stacks that do all of the above:
   AWS **CloudWatch**, Google Cloud **Operations**, Azure **Monitor**, or
   third-party **Datadog** / **New Relic**. Less to run yourself; often the default
   in cloud deployments.

**Typical Spring Boot flow:** Actuator + Micrometer expose metrics →
Prometheus scrapes them → Grafana dashboards + alerts. Swap Prometheus/Grafana for
a cloud-native stack when you'd rather not self-host.

Beyond metrics, mature setups add **logging** (aggregated, searchable) and
**distributed tracing** (following one request across services) — the "three
pillars of observability": metrics, logs, traces.

## Takeaway

Track **latency & response time** (speed), **throughput & error rate**
(reliability), and **availability & uptime** (stability) — then **collect and act
on them** with a monitoring pipeline (Actuator/Micrometer → Prometheus → Grafana,
or a cloud-native stack). Together they let teams optimize systems, keep users
satisfied, and deliver real business value.