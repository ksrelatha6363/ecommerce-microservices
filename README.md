# E-Commerce Microservices (Spring Boot + Spring Cloud)

A ready-to-run reference microservices project demonstrating service discovery,
API gateway routing, interservice communication via Feign, external payment
gateway integration, centralized monitoring, and distributed tracing.

No Docker, Kubernetes, Kafka, or JUnit are used — everything runs as plain
Spring Boot jars on your local JVM, and each service uses an in-memory H2
database so there's nothing to install or configure before you run it.

## Architecture

```
                        ┌─────────────────────┐
                        │   API Gateway :8080  │  (Spring Cloud Gateway)
                        └──────────┬───────────┘
                     ┌─────────────┴─────────────┐
                     ▼                            ▼
          ┌─────────────────────┐      ┌──────────────────────┐
          │ product-service:8081│◄─────┤  order-service :8082  │
          │  (CRUD, H2 DB)      │Feign │  (CRUD, H2 DB)        │
          └─────────────────────┘      └───────────┬───────────┘
                     ▲                              │ RestTemplate
                     │  register/discover           ▼
          ┌─────────────────────┐      ┌──────────────────────┐
          │ service-registry    │      │ payment-gateway-      │
          │ (Eureka) :8761      │      │ service :8090 (mock   │
          └─────────────────────┘      │ external provider)    │
                     ▲                  └──────────────────────┘
                     │ register/discover
          ┌─────────────────────┐      ┌──────────────────────┐
          │ admin-server :8085  │      │ zipkin-server :9411   │
          │ (Spring Boot Admin) │      │ (distributed tracing) │
          └─────────────────────┘      └──────────────────────┘
```

| # | Module | Port | Role |
|---|--------|------|------|
| 1 | `service-registry` | 8761 | Eureka server — every service below registers here |
| 2 | `zipkin-server` | 9411 | Embedded Zipkin — collects distributed traces from all services |
| 3 | `admin-server` | 8085 | Spring Boot Admin — health/metrics dashboard, auto-discovers via Eureka |
| 4 | `api-gateway` | 8080 | Spring Cloud Gateway — single entry point, routes `/api/products/**` and `/api/orders/**` |
| 5 | `product-service` | 8081 | Product catalog CRUD REST API |
| 6 | `order-service` | 8082 | Order CRUD REST API; calls product-service via **Feign**, and the payment gateway via **RestTemplate** |
| 7 | `payment-gateway-service` | 8090 | Mock external payment gateway (Stripe/Razorpay-style). Deliberately **not** registered with Eureka — it stands in for a real third-party API reached over a plain base URL |

### Why Feign for one call and RestTemplate for the other?

- **product-service ↔ order-service** is *interservice* communication between two
  services you own, both registered in Eureka — a perfect fit for a declarative
  **Feign client** with load-balancing and service-name resolution.
- **order-service → payment-gateway-service** simulates calling a genuine
  **external** provider (Stripe, Razorpay, PayU, etc.). Real payment gateways
  aren't in your service registry, so this uses a plain, non-load-balanced
  `RestTemplate` against a configured base URL — exactly how you'd wire up a
  real gateway SDK/API key call in production.

## Prerequisites

- JDK 17+
- Maven 3.8+
- Nothing else — H2 is embedded, Zipkin is embedded, no external DB/broker needed.

## Build

From the project root:

```bash
mvn clean install
```

This builds all 7 modules and installs the parent POM. Each module produces
its own executable jar under `<module>/target/`.

## Run order (each in its own terminal)

Start infrastructure first, then the business services, in this order:

```bash
# 1. Service Registry (wait until it's up before starting anything else)
cd service-registry && mvn spring-boot:run

# 2. Zipkin tracing server
cd zipkin-server && mvn spring-boot:run

# 3. Admin server
cd admin-server && mvn spring-boot:run

# 4. Payment gateway (mock external provider)
cd payment-gateway-service && mvn spring-boot:run

# 5. Product service
cd product-service && mvn spring-boot:run

# 6. Order service
cd order-service && mvn spring-boot:run

# 7. API Gateway
cd api-gateway && mvn spring-boot:run
```

Or, after `mvn clean install`, run any module directly from its jar:

```bash
java -jar service-registry/target/service-registry.jar
java -jar product-service/target/product-service.jar
# ...etc
```

### Dashboards

| Dashboard | URL |
|---|---|
| Eureka registry | http://localhost:8761 |
| Spring Boot Admin | http://localhost:8085 |
| Zipkin UI | http://localhost:9411/zipkin |
| Product service H2 console | http://localhost:8081/h2-console (JDBC URL `jdbc:h2:mem:productdb`) |
| Order service H2 console | http://localhost:8082/h2-console (JDBC URL `jdbc:h2:mem:orderdb`) |

## Trying it out

product-service seeds 3 demo products on startup, so you can place an order
immediately. All calls below go through the **API Gateway on port 8080**.

**List products**
```bash
curl http://localhost:8080/api/products
```

**Get one product**
```bash
curl http://localhost:8080/api/products/1
```

**Create a product**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"USB-C Hub","description":"7-in-1 hub","price":34.99,"quantity":100,"category":"Electronics"}'
```

**Place an order (successful payment)**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
        "productId": 1,
        "quantity": 2,
        "customerName": "Priya Sharma",
        "cardNumber": "4111111111111111",
        "cardExpiry": "12/29",
        "cardCvv": "123"
      }'
```
Response `status` will be `CONFIRMED`, with a `paymentTransactionId` from the
mock gateway, and product-service's stock for that product will be reduced.

**Place an order that gets declined** (the mock gateway declines any card
number ending in `0000`):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
        "productId": 2,
        "quantity": 1,
        "customerName": "Alex Kim",
        "cardNumber": "4111111111110000",
        "cardExpiry": "12/29",
        "cardCvv": "123"
      }'
```
Response `status` will be `PAYMENT_FAILED` and stock is left untouched.

**List orders / get one**
```bash
curl http://localhost:8080/api/orders
curl http://localhost:8080/api/orders/1
```

**Call the payment gateway directly** (bypassing order-service, just to see it standalone):
```bash
curl -X POST http://localhost:8090/gateway/v1/payments/charge \
  -H "Content-Type: application/json" \
  -d '{"orderId":"TEST-1","amount":50.00,"currency":"USD","cardNumber":"4111111111111111","cardExpiry":"12/29","cardCvv":"123","customerName":"Test User"}'
```

After running a few requests, open **http://localhost:9411/zipkin** and search
for recent traces — you'll see a trace that spans api-gateway → order-service
→ product-service and → payment-gateway-service for a single order placement.

## REST API summary

### product-service (via gateway: `/api/products`)
| Method | Path | Description |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | List all products (optional `?category=`) |
| GET | `/api/products/{id}` | Get one product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |
| PUT | `/api/products/{id}/reduce-stock?quantity=` | Internal — used by order-service |

### order-service (via gateway: `/api/orders`)
| Method | Path | Description |
|---|---|---|
| POST | `/api/orders` | Place an order (fetches product, charges payment gateway, reduces stock) |
| GET | `/api/orders` | List all orders (optional `?customerName=`) |
| GET | `/api/orders/{id}` | Get one order |
| PUT | `/api/orders/{id}/cancel` | Cancel an order |

### payment-gateway-service (called only by order-service; direct access shown above for testing)
| Method | Path | Description |
|---|---|---|
| POST | `/gateway/v1/payments/charge` | Process a charge |
| GET | `/gateway/v1/payments/{transactionId}` | Look up a transaction |

## Configuration notes

- **Databases**: each of product-service/order-service uses an in-memory H2
  database (`DB_CLOSE_DELAY=-1` keeps data alive for the app's lifetime — it
  resets on restart). To switch to MySQL/Postgres for a real deployment,
  add the driver dependency and replace the `spring.datasource.*` /
  `spring.jpa.database-platform` properties in that module's `application.yml`.
- **Payment gateway URL**: `order-service/src/main/resources/application.yml`
  → `payment.gateway.base-url`. Point this at a real provider's base URL and
  add your API key (e.g. as a header in `PaymentGatewayClient`) to go live.
- **Tracing sample rate**: `management.tracing.sampling.probability: 1.0` in
  every service means 100% of requests are traced — good for a demo, dial
  this down (e.g. `0.1`) in production to reduce overhead.
- **Security**: none of these services have authentication enabled, to keep
  the project immediately runnable. Add Spring Security (and secure the
  Eureka dashboard, Admin server, and Actuator endpoints) before any real
  deployment.
