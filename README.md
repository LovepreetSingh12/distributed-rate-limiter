# ⚡ Distributed Rate Limiter

A high-performance, distributed rate limiting system designed to handle **10K+ requests/sec** with **sub-10ms latency**.

---

## 📌 Overview

This system enforces rate limits across distributed services using Redis and Kafka, ensuring scalability, fault tolerance, and real-time configurability.

---

## 🏗️ Architecture
Client → API Gateway / Filter → Rate Limiter Service → Redis
↓
Kafka (Async Events)


---

## ⚙️ Tech Stack

- **Backend:** Java, Spring Boot
- **Caching / Storage:** Redis
- **Streaming:** Apache Kafka
- **DevOps:** Docker

---

## 🔥 Features

- 🚀 Handles **10K+ requests/sec**
- ⏱️ Sub-10ms response latency
- 🔁 Supports multiple algorithms:
  - Token Bucket
  - Sliding Window
- 🎯 Fine-grained rate limiting:
  - Per user
  - Per IP
  - Per API key
- 🔄 Dynamic configuration updates
- 📩 Kafka-based async event logging
- 🧵 Thread-safe using Redis atomic operations

---

## ⚡ Rate Limiting Algorithms

### 1. Token Bucket
- Allows burst traffic
- Refills tokens over time

### 2. Sliding Window
- Smooth rate limiting
- Prevents sudden spikes

---

## 🧠 How It Works

1. Request hits filter/interceptor
2. Redis checks current usage
3. Decision:
   - ✅ Allow request
   - ❌ Reject with 429
4. Event sent to Kafka asynchronously

---

## 🧪 How to Run

### Prerequisites
- Java 17+
- Redis
- Kafka
- Docker

### Steps

```bash
# Clone repo
git clone <repo-url>

# Start dependencies
docker-compose up -d

# Run service
./mvnw spring-boot:run
