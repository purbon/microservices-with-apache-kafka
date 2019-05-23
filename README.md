# Microservices with Apache Kafka and Play Framework

TBA

## Actions

* Start docker-compose:

```bash
 $> docker-compose up -d
```

* start the customer-services app
* start the payment-services app

post customers

```bash
curl -d "@data/customer0.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/new
curl -d "@data/customer1.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/new
```

and see how data flows
