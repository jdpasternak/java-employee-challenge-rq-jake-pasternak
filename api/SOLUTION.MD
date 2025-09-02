# ReliaQuest Coding Challenge Solution

## Architecture
```
Controller → Service → ReadService (cached) → Client/Gateway → Mapper (wire→domain)
                                    ↑
                                    Caffeine
```

### Responsibilities

- Controller: Receives requests and calls service methods to return expected results.
- Service: Performs validation on inputs, business logic, calls cached findAll to get all employees.
- ReadService: Snapshot read (findAll) with Caffeine cache.
- Client (Gateway): Downstream communication to mock server.
- Mapper: Strict translation of wire models to domain models.
- Global @ControllerAdvice produces RFC 7807 Problem Details.

## Error Handling

### Mapping

- 400: Jakarta validation failures (request DTOs, path vars)
- 404: EmployeeNotFoundException(id)
- 409: EmployeeWithNameAlreadyExistsException(name)
- 429: DownstreamUnavailableException(retryAfter) → include Retry-After
- 502/503: Downstream client/server errors (BadGatewayException, DownstreamUnavailableException)

### Problem fields

- type, title, status, detail, instance
- correlation_id (from header/MDC)
- error.code (stable enum)

## Logging & Correlation

### Ingress filter (CorrelationFilter):

- Reads or creates X-Correlation-Id, sets on response, stores in MDC.
- Logs request_received and request_completed with method/path/status/duration_ms.

### Egress interceptor (CorrelationInterceptor):

- Propagates correlation header to downstream.
- Logs downstream_request_sent / downstream_request_completed.
- Log format: JSON lines via Logback.

## Caching Strategy (Caffeine)

- Cache name: employees:all
- findAll() is @Cacheable (no key → single snapshot entry).
- create / delete are @CacheEvict(allEntries = true).
- TTL and max size are configurable (default 30s / 1 entry).
- All derived reads (findById, search, topTen, highestSalary) operate on the cached snapshot.
- Why: Reduces downstream calls, absorbs rate limiting bursts, keeps reads O(n) over in-memory list.

## Validation

- Request DTOs (e.g., CreateEmployeeInput) use Jakarta annotations; @Validated on service/client enables method validation.
- Path vars (e.g., id) validated as @UUID at the service boundary.
- Downstream payloads validated in the mapper → throw BadGatewayException with clear messages.

## Testing Strategy (TDD)

- Service tests: search, top-10 order/ties, max salary, duplicate-name prevention, delete-by-id → name mapping.
- ReadService tests: cache hit/miss, eviction on writes, exception not cached.\
- Client tests (@RestClientTest): happy path mapping, 404→domain exception, 5xx/429→domain exception with Retry-After, timeouts.
- Mapper tests: valid mapping; invalid wire → BadGatewayException.
- Filter/Interceptor tests: correlation ID echo/propagate; header behavior on success/error.
- Controller tests (@WebMvcTest): status codes (200/204/404/409), Problem Details shape, headers (e.g., Retry-After).


## Project Layout
```
controller/           REST endpoints + advice
service/              business rules; read snapshot service (cached)
gateway/              RestTemplate client + error handler + interceptor
http/                 wire models, envelope, mapper
cache/                Caffeine config + props
config/               @ConfigurationProperties
log/                  correlation constants/filter
model/                domain & request DTOs
exception/            domain exceptions (mapped to PD)
```