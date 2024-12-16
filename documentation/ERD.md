
Point ERDiagram
---

```mermaid
erDiagram
    User ||--|| UserPoint: contains
    User {
        long id
    }
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
