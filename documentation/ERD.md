
Point ERDiagram
---

```mermaid
erDiagram
    UserPoint ||--|{ PointHistory : has
    UserPoint {
        long id
        long point
        long updateMillis
    }
    PointHistory {
        long id
        long userId
        TransactionType type
        long updateMillis
    }
```
