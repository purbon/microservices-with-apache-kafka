# Microservices with Apache Kafka and Play Framework

TBA

## Actions

* Start docker images with:

```bash
 $> ./up
```

this will start all internal necessary containers, including:
* an api-gateway app
* a customer service
* and a payments services

To create new customers you can run

```bash
curl -d "@data/customer0.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/customers/new
curl -d "@data/customer1.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/customers/new
```

and see how data flows
