# Console Group API Challenge

This is a Kotlin & Spring Boot 5.0 solution the the Console Group API Challenge.

To run unit/integration tests:

```
mvn test
```

To run demo server:

```
mvn spring-boot:run
```

## API

### Create new tenant

```
POST /tenants/ HTTP/1.1
Content-Length: 53
Host: localhost:8080
Content-Type: application/json

{
  "name": "Tom Atkins",
  "weeklyRentAmount": 300
}

HTTP/1.1 201 Created
transfer-encoding: chunked
Location: /tenants/7f8a162a-375a-4fe6-acc8-f1beaafe848c
Content-Type: application/json;charset=UTF-8

{"id":"7f8a162a-375a-4fe6-acc8-f1beaafe848c","name":"Tom Atkins","weeklyRentAmount":300.0,"paidToDate":"2017-11-27","credit":0.0}
```

### Create rent receipt

```
POST /tenants/7f8a162a-375a-4fe6-acc8-f1beaafe848c/receipts/ HTTP/1.1
Content-Length: 19
Host: localhost:8080
Content-Type: application/json

{
  "amount": 350
}

HTTP/1.1 201 Created
transfer-encoding: chunked
Location: /tenants/7f8a162a-375a-4fe6-acc8-f1beaafe848c/receipts/2084cd31-b828-459f-95d6-39e9605fd705
Content-Type: application/json;charset=UTF-8

{"id":"2084cd31-b828-459f-95d6-39e9605fd705","amount":350.0}
```

### Get tenant

```
GET /tenants/7f8a162a-375a-4fe6-acc8-f1beaafe848c/ HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK
transfer-encoding: chunked
Content-Type: application/json;charset=UTF-8

{"id":"7f8a162a-375a-4fe6-acc8-f1beaafe848c","name":"Tom Atkins","weeklyRentAmount":300.0,"paidToDate":"2017-12-04","credit":50.0}
```

### Get all rent receipts for a single tenant

```
GET /tenants/7f8a162a-375a-4fe6-acc8-f1beaafe848c/receipts/ HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK
transfer-encoding: chunked
Content-Type: application/json;charset=UTF-8

[{"id":"2084cd31-b828-459f-95d6-39e9605fd705","amount":350.0},{"id":"55b9a585-9a04-4950-9c7e-812d54848c13","amount":100.0},{"id":"ea3c7bc2-8094-4536-9b26-8c5d3bb6595e","amount":5.5}]
```

### List all tenants

(This was not required by the specification, but is included for debugging purposes.)

```
GET /tenants/ HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK
transfer-encoding: chunked
Content-Type: application/json;charset=UTF-8

[{"id":"7f8a162a-375a-4fe6-acc8-f1beaafe848c","name":"Tom Atkins","weeklyRentAmount":300.0,"paidToDate":"2017-12-04","credit":50.0},{"id":"4b9509cf-f536-4984-99ea-c4f3e60a01cb","name":"Ted Jones","weeklyRentAmount":275.0,"paidToDate":"2017-11-27","credit":0.0}]
```

### List all tenants with a rent receipt within the last 2 hours

Argument is specified as a [IS08601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), meeting the "last N hours" specification requirement, but also allowing greater control.

```
GET /tenants/with-rent-receipt?withinLast=PT2H HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK
transfer-encoding: chunked
content-type: application/json;charset=UTF-8

[{"id":"7f8a162a-375a-4fe6-acc8-f1beaafe848c","name":"Tom Atkins","weeklyRentAmount":300.0,"paidToDate":"2017-12-04","credit":50.0}]
```

## Design decisions

### Single source of truth

The epoch (initial calculation date) is stored, with paid-to-date and credit being calculated from the rent receipts of each tenant. While involving slightly more processing, this removes the possibility of internal database inconsistency.

### Dates are just the API view of timestamps

At any given time, the world will generally accept one of two dates to be "today". The API specification requires solely date output, and does not accommodate time zone specification. Internally however, all calculations and data storage use UTC timestamps. This allows the API to easily be modified to a more global-friendly design at a later stage.

### No updates or mutability

Domain objects are immutable, and database records are append-only. This makes it easier to reason about the possible system state.
