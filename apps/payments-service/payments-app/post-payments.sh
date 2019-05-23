#!/usr/bin/env bash

curl -d "@data/payments-customer0-0.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/0/payments/new
curl -d "@data/payments-customer0-1.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/0/payments/new

curl -d "@data/payments-customer1-0.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/1/payments/new
curl -d "@data/payments-customer1-1.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/1/payments/new