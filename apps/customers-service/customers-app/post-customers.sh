#!/usr/bin/env bash

curl -d "@data/customer0.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/new
curl -d "@data/customer1.json" -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/customers/new