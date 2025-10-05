# 🎯 Chaos Engineering

A distributed e-commerce system built with Spring Boot microservices architecture, featuring user management, product catalog, shopping cart, order processing, and review system.

---

## 📋 Table of Contents

- [Architecture](#-Architecture)
- [Prerequisites](#-prerequisites)
- [Java Version Configuration](#-java-version-configuration)
- [Quick Start](#-quick-start)
- [Monitoring Stack](#-monitoring-stack)
- [Load Testing with JMeter](#-load-testing-with-jmeter)
- [Chaos Engineering with Pumba](#-chaos-engineering-with-pumba)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      API GATEWAY (:9080)                    │
│              Routes requests to microservices               │
│         • CORS Configuration • Load Balancing               │
│         • Request/Response Filtering                        │
└────────┬────────────────────────────────────────────────────┘
         │
         ├──────────┬──────────┬──────────┬──────────┐
         ↓          ↓          ↓          ↓          ↓
   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
   │  User   │ │ Product │ │  Cart   │ │  Order  │ │ Review  │
   │ Service │ │ Service │ │ Service │ │ Service │ │ Service │
   │  :9081  │ │  :9082  │ │  :9083  │ │  :9084  │ │  :9085  │
   └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘
        │           │           │           │           │
        └───────────┴───────────┴───────────┴───────────┘
                              │
                              ↓
                   ┌──────────────────────┐
                   │  PostgreSQL Database │
                   │ ecommerce_shared_db  │
                   │      (:5432)         │
                   └──────────────────────┘
```

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

### Required Software
- **Docker** (version 20.0+) and **Docker Compose** (version 2.0+)
- **Java 17** (for local development)
- **Gradle** (version 7.0+)
- **PostgreSQL** (version 15+) - if running locally without Docker
- **Git** for cloning the repository

### Optional Tools
- **Bruno** or **Postman** for API testing
- **IntelliJ IDEA** or **VS Code** for development

---

## ☕ Java Version Configuration

This project supports both **Java 17** and **Java 21**. By default, it uses **Java 17**.

### How to Override Java Version

If you're working with Java 21, you can override the version without modifying the shared `build.gradle` file:

#### **Option 1: Using gradle.properties file**

Create a `gradle.properties` file in the project root:

```properties
javaVersion=21
```

#### **Option 2: Using command line**

```bash
./gradlew build -PjavaVersion=21
```

---

## 🚀 Quick Start

### 1️⃣ Clone the Repository
```bash
git clone <repository-url>
cd ecommerce-microservices
```

### 2️⃣ Run with Docker (Recommended)
```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

### 3️⃣ Verify Services
Once all containers are running, verify the services:

- **API Gateway**: http://localhost:9080
- **User Service**: http://localhost:9081
- **Product Service**: http://localhost:9082
- **Cart Service**: http://localhost:9083
- **Order Service**: http://localhost:9084
- **Review Service**: http://localhost:9085
- **Grafana Dashboard**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9091

### 4️⃣ Test the APIs
Use the Bruno collection in `bruno-collection/` directory or access Swagger UI:
- http://localhost:9080/swagger-ui.html (via API Gateway)

---

## 📊 Monitoring Stack

### Overview

Comprehensive monitoring solution providing visibility into both application performance and infrastructure health using Prometheus, Grafana, and cAdvisor.

### Components

#### **cAdvisor (Container Advisor)**
An open-source tool by Google that monitors Docker container resource usage and performance.

**What it provides:**
- CPU, memory, network, and disk I/O metrics per container
- Container lifecycle tracking (start/stop/restart events)
- Real-time resource consumption data

**Key metrics:**
- `container_cpu_usage_seconds_total` - CPU consumption
- `container_memory_usage_bytes` - Memory usage
- `container_network_receive/transmit_bytes_total` - Network traffic
- `container_fs_reads/writes_bytes_total` - Disk I/O

**Access:** http://localhost:8092

---

#### **Prometheus**
Industry-standard open-source monitoring system that collects, stores, and queries time-series metrics.

**How it works:**
1. **Scrapes** metrics from targets every 15 seconds via HTTP
2. **Stores** data in efficient time-series database
3. **Queries** data using PromQL (Prometheus Query Language)
4. **Exposes** data via API for visualization tools

**What it monitors:**
- Spring Boot applications via `/actuator/prometheus` endpoints
- Docker containers via cAdvisor
- Itself (self-monitoring for system health)

**Configuration:** `monitoring/prometheus.yml`

**Access:** http://localhost:9091

**Example PromQL queries:**
```promql
# Request rate over 5 minutes
rate(http_requests_total[5m])

# 95th percentile response time
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

# Container memory usage in MB
container_memory_usage_bytes / 1024 / 1024
```

---

#### **Grafana**
Open-source visualization platform that creates interactive dashboards from Prometheus metrics.

**Features:**
- Beautiful, customizable dashboards
- Real-time graph updates
- Interactive data exploration (zoom, filter, drill-down)
- Alert configuration and notifications
- Multiple visualization types (graphs, tables, gauges, heatmaps)

**Access:** http://localhost:3000 (default credentials: `admin`/`admin`)

---

### Pre-configured Dashboards

Located in `monitoring/dashboards/`:

1. **`Application Metrics.json`**
    - Service uptime
    - HTTP request rates per service
    - Request latency (p95)
    - JVM heap memory usage

2. **`Container Metrics Dashboard.json`**
    - CPU usage per container
    - Memory usage per container
    - Network I/O (receive/transmit)
    - Disk I/O (read/write)

3. **`Product Service – Application & Container.json`**
    - Combined view of application and infrastructure metrics for Product Service
    - Response time, error rates, throughput
    - Container resource consumption

---

### Setup Monitoring

#### 1. Access Grafana
Open http://localhost:3000
- Username: `admin`
- Password: `admin`

#### 2. Add Prometheus Data Source
1. Go to **Configuration** → **Data Sources**
2. Click **Add data source**
3. Select **Prometheus**
4. Set URL: `http://prometheus:9090`
5. Click **Save & Test**

#### 3. Import Dashboards
1. Click **+** → **Import Dashboard**
2. Click **Upload JSON file**
3. Select a dashboard file from `monitoring/dashboards/`
4. Choose **Prometheus** as data source
5. Click **Import**
6. Repeat for all three dashboard files

#### 4. View Metrics
- Browse imported dashboards
- Adjust time ranges (top-right corner)
- Explore different panels and services

---

### What You'll Monitor

| Metric Type | Source | Examples |
|-------------|--------|----------|
| **Application Performance** | Spring Boot Actuator | Request rate, latency, error rate, throughput |
| **Application Health** | Spring Boot Actuator | Service uptime, JVM memory, active threads |
| **Container Resources** | cAdvisor | CPU usage, memory consumption, network I/O |
| **Infrastructure** | cAdvisor | Disk I/O, container restarts, resource limits |

---

### Monitoring Endpoints

| Service | Metrics Endpoint |
|---------|-----------------|
| User Service | http://localhost:9081/actuator/prometheus |
| Product Service | http://localhost:9082/actuator/prometheus |
| Cart Service | http://localhost:9083/actuator/prometheus |
| Order Service | http://localhost:9084/actuator/prometheus |
| Review Service | http://localhost:9085/actuator/prometheus |
| API Gateway | http://localhost:9080/actuator/prometheus |
| cAdvisor | http://localhost:8092/metrics |

---

### Troubleshooting

**No data in Grafana:**
- Verify Prometheus is scraping targets: http://localhost:9091/targets
- Check all targets show status "UP"
- Verify data source connection in Grafana

**Missing metrics:**
- Ensure services expose `/actuator/prometheus` endpoint
- Check `management.endpoints.web.exposure.include` in `application.yml`
- Verify `micrometer-registry-prometheus` dependency is included

**High resource usage:**
- Adjust scrape interval in `prometheus.yml` (increase from 15s to 30s)
- Reduce metric retention period
- Disable unused cAdvisor metrics

---

## 🚇 Load Testing with JMeter

### What is JMeter?

**Apache JMeter** is an open-source Java application designed to load test and measure performance of applications.

**Key Features:**
- **Load Testing** - Simulate multiple users/threads
- **Performance Testing** - Measure response times and throughput
- **Stress Testing** - Find breaking points
- **Functional Testing** - Validate API responses
- **Multi-protocol Support** - HTTP, HTTPS, REST, SOAP, FTP, JDBC

**How it works:**
1. **Thread Groups** - Define number of virtual users
2. **Samplers** - Define requests to send (HTTP, JDBC, etc.)
3. **Listeners** - Collect and display results
4. **Assertions** - Validate responses
5. **Timers** - Add delays between requests

---

### Test Suite Overview

The project includes comprehensive JMeter test plans to validate performance and reliability under various load conditions:

#### 1️⃣ User Service Load Test (`user-service-load-test.jmx`)

**Purpose**: Tests user registration and management endpoints under concurrent load.

**Scenario**:
- 50 concurrent users performing registration and retrieval operations
- 30-second ramp-up period with 10 iterations each
- Tests user creation, address creation, and user listing endpoints

**What it validates**:
- User registration performance under load
- Database write operations efficiency
- Unique constraint handling (username/email duplicates)
- Response times for user management operations

#### 2️⃣ Product Service Load Test (`product-service-load-test.jmx`)

**Purpose**: Validates catalog browsing and search functionality performance.

**Scenario**:
- 100 concurrent users browsing products
- 60-second ramp-up period with 20 iterations each
- Simulates realistic e-commerce browsing patterns

**What it validates**:
- Product catalog listing performance
- Search functionality responsiveness
- Category-based filtering efficiency
- Individual product detail view performance

#### 3️⃣ End-to-End Shopping Flow (`e2e-shopping-flow.jmx`)

**Purpose**: Tests complete user journey from browsing to checkout.

**Scenario**:
- 20 concurrent users completing full shopping workflows
- 60-second ramp-up period with 5 iterations each
- Complete flow: Browse → View Details → Add to Cart → Checkout

**What it validates**:
- Cross-service integration performance
- Real-world user workflow efficiency
- Database transaction handling across services
- API gateway routing under load

### 📁 Test Results Location

All test results are automatically saved to the `jmeter-results/` directory:
- Summary reports in `.jtl` format
- Performance metrics and response times
- Error rates and throughput data

### ▶️ Running Load Tests

1. **Prerequisites**: Ensure all services are running via Docker Compose
2. **Execute tests**: Open JMeter GUI and load the desired `.jmx` file
3. **Monitor**: Use the built-in Summary Report and View Results Tree listeners
4. **Analyze**: Review results in the `jmeter-results/` folder for detailed metrics

### 📈 Performance Expectations

- **Response Times**: < 500ms for read operations, < 2s for write operations
- **Throughput**: 100+ requests/second per service
- **Error Rate**: < 1% under normal load conditions
- **Concurrent Users**: Supports 100+ simultaneous users per service

---

## 🧪 Chaos Engineering with Pumba

### 📋 Overview

Pumba is a chaos testing tool that injects failures into Docker containers to test system resilience. When you run a Pumba command, it creates a **temporary container** that executes chaos experiments on your target services.

### How Pumba Works

```bash
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  <chaos-command>
```

**What happens:**
1. Creates a temporary Pumba container (with random name like `magical_newton`)
2. Connects to Docker daemon via socket mount
3. Executes chaos on target containers
4. Runs until you press `Ctrl+C`
5. Auto-removes itself (due to `--rm` flag)

**Note for Windows PowerShell:** Use `` ` `` for line continuation instead of `\`

---

## 🎯 Chaos Scenarios

### 1️⃣ Container Stop (Temporary Pause)

**Windows PowerShell:**
```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  stop --duration 30s re2:^product-service$
```

**Linux/Mac:**
```bash
docker run -it --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --network ecommerce-microservices_ecommerce-network \
  gaiaadm/pumba:0.9.0 \
  stop --duration 30s re2:^product-service$
```

**Expected Impact:**
- `product-service` unavailable for 30s
- API Gateway returns `500 Internal Server Error`
- After 30s, container remains stopped
- Requires manual restart: `docker start product-service`

---

### 2️⃣ Container Kill (Crash Simulation)

**SIGTERM (Graceful):**
```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  kill --signal=SIGTERM re2:^product-service$
```

**SIGKILL (Forced):**
```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  kill --signal=SIGKILL re2:^product-service$
```

**Expected Impact:**
- Container exits immediately (status `Exited (143)` or `(137)`)
- Gateway returns `500` while service is down
- Service stays down until manually restarted
- Requires: `docker start product-service`

---

### 3️⃣ Network Latency Injection

```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  netem --tc-image gaiadocker/iproute2 `
  --duration 60s delay --time 300 --jitter 50 re2:^product-service$
```

**Parameters:**
- `--time 300`: Adds 300ms base delay
- `--jitter 50`: Random variation ±50ms
- `--duration 60s`: Applies for 60 seconds

**Expected Impact:**
- Responses still return `200 OK`
- Response times increase by ~300ms
- Affects p50/p90/p99 latency percentiles
- Service remains available but slower

---

### 4️⃣ Packet Loss Injection

```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  netem --tc-image gaiadocker/iproute2 `
  --duration 60s loss --percent 40 re2:^product-service$
```

**Parameters:**
- `--percent 40`: Drops 40% of packets

**Expected Impact:**
- TCP retransmissions occur automatically
- No immediate `500` errors
- Responses still return `200` but slower
- Increased latency due to retransmissions

---

### 5️⃣ Combined Latency + Packet Loss

**Terminal 1 - Add Delay:**
```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  netem --tc-image gaiadocker/iproute2 `
  --duration 90s delay --time 250 --jitter 50 "re2:^product-service$"
```

**Terminal 2 - Add Packet Loss:**
```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  netem --tc-image gaiadocker/iproute2 `
  --duration 90s loss --percent 20 "re2:^product-service$"
```

**Expected Impact:**
- Response times > 2 seconds
- Risk of timeouts depending on client configuration
- May result in `500` errors if timeouts are short
- Tests system behavior under severe network degradation

---

### 6️⃣ Multi-Service Chaos

```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  netem --tc-image gaiadocker/iproute2 `
  --duration 90s delay --time 300 --jitter 50 "re2:^(product-service|order-service)$"
```

**Expected Impact:**
- `/api/products` → `200` but slow (~2.8s)
- `/api/users/1/cart` → `200` with increased latency
- `/api/users/1/orders` → `200`
- Cascading delays across dependent services

---

### 7️⃣ Random Service Killing (Continuous Chaos)

```powershell
docker run -it --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  --network ecommerce-microservices_ecommerce-network `
  gaiaadm/pumba:0.9.0 `
  --interval=30s --random --log-level=info kill --signal=SIGKILL "re2:.*-service"
```

**Parameters:**
- `--interval=30s`: Repeats every 30 seconds
- `--random`: Selects random matching container
- `--log-level=info`: Shows what's being killed

**Expected Impact:**
- Kills one random service every 30s
- Tests automatic restart policies
- Validates system recovery mechanisms
- Runs continuously until stopped with `Ctrl+C`

### 8️⃣ CPU Limitation (Resource Starvation)

Sometimes services fail not because of network issues but due to **lack of CPU resources**.  
We can simulate this by limiting the CPU quota of a running container.

**Command:**

```bash
# Limit product-service to 0.25 CPU (25% of a core)
docker update --cpus 0.25 product-service

# Check that the limit is applied
docker inspect -f '{{json .HostConfig.NanoCpus}}' product-service

# Monitor live stats
docker stats --no-stream product-service
```

**Expected Impact:**
- Responses still return 200 OK but **latency increases**
- Under load testing (e.g., JMeter), requests may time out if CPU is too constrained
- API Gateway may start returning 500s if retries/circuit breakers trigger

**Restore Normal State:**
```bash
# Give back 1 full CPU (or unlimited)
docker update --cpus 1 product-service

# Confirm with:
docker stats --no-stream product-service
```

**Alternative: CPU Stress Container**
```bash
docker run --rm -it --name stressor --cpus=2 polinux/stress \
  --cpu 2 --timeout 60
```

- `--cpu 2` → start 2 CPU workers
- `--timeout 60` → runs for 60 seconds then exits
- While active, other containers (like product-service) will suffer higher latency

##  📊 Microservices Monitoring with Prometheus, Grafana, cAdvisor, Actuator, and Micrometer

Monitoring distributed microservices is critical for ensuring reliability, performance, and availability.  
This project integrates the following tools:
- **Prometheus** → Metrics collection & storage  
- **Grafana** → Visualization & alerting  
- **cAdvisor** → Container-level monitoring  
- **Spring Boot Actuator** → Application monitoring endpoints  
- **Micrometer** → Metrics instrumentation  

---

### Components

### Prometheus
- Open-source monitoring toolkit  
- Scrapes metrics at intervals from defined targets  
- Stores data as **time-series**  
- Supports **PromQL** for querying  

📌 In this setup, Prometheus scrapes:
- Spring Boot services (`/actuator/prometheus`)  
- cAdvisor for Docker container metrics  

---

### Grafana
- Visualization platform for metrics from Prometheus  
- Features: dashboards, alerts, filters, and time-based queries  
- Used here to create **two dashboards**:
  - Application metrics (via Actuator + Micrometer)  
  - Container metrics (via cAdvisor)  

---

### cAdvisor
- Collects **container-level resource usage & performance**  
- Metrics: CPU, memory, disk I/O, network, uptime  
- Exposes metrics in Prometheus format  

---

### Spring Boot Actuator
- Provides endpoints for monitoring & managing apps  
- Example:  
  - `/actuator/health` → health status  
  - `/actuator/prometheus` → Prometheus metrics  

---

### Micrometer
- Metrics instrumentation library  
- Integrated with Spring Boot  
- Exposes JVM, HTTP, and custom metrics  
- Acts as a **bridge** between Actuator and Prometheus  


