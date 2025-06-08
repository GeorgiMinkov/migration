# RMI to REST Migration Register

> Last updated: 2025-06-08

This document maintains a register of all RMI methods that have been migrated to REST endpoints.

## Migration Status

| RMI Method | REST Endpoint | Feature Flag | Status | Target Removal Date |
|------------|---------------|--------------|--------|---------------------|
| `OrderRmiService.findOrderById(Integer id)` | `GET /api/orders/{id}` | `useRest_order` | In Progress | T + 60 days |

## Migration Details

### OrderRmiService.findOrderById

* **Original RMI Signature**: `Order findOrderById(Integer id) throws RemoteException`
* **New REST Endpoint**: `GET /api/orders/{id}`
* **Response Format**: JSON representing OrderDto
* **Feature Flag**: `useRest_order=true` to use repository, `false` to use RMI
* **Migration Start Date**: 2025-06-08
* **Current Status**: In Progress - Implementation and testing
* **Removal Target Date**: 2025-08-07 (T + 60 days)

### Performance Metrics

* P95 Latency Target: ≤ 120ms
* Error Rate Target: ≤ 0.1% (HTTP 5xx)
* Traffic Migration Target: ≥ 95% of calls via REST

## Verification Process

1. Unit tests for the REST controller are passing
2. Parity tests confirm REST and RMI return identical data
3. Performance tests meet the latency and error rate targets
4. Feature flag enabled in production for 7 consecutive days without issues
5. Traffic monitoring confirms migration targets are met
