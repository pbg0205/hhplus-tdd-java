# [2] 유저 포인트 충전 API

## (1) 행동 분석

1. 충전 포인트, 유저 식별자를 파라미터로 입력 받는다.
2. 충전 포인트를 검증한다.
    - 충전 포인트는 양수이다.
3. 유저 식별자를 검증한다.
    - 유저는 가입된 사용자여야 한다.
    - 가입되지 않는 사용자는 충전할 수 없다.
4. 충전 포인트를 충전한다.
    - 충전 포인트는 최대 10만점이다.
    - 최대 충전 포인트를 초과할 경우 충전할 수 없다.
5. 충전 결과를 응답한다.

## (2) 단위 테스트 케이스

### 1. PointService

| Pass/Fail |      type      |         input condition         | expected result                       |
|:---------:|:--------------:|:-------------------------------:|---------------------------------------|
|   Fail    |     point      |          충전 포인트 == 음수           | 예외 반환 (InvalidChargingPointException) |
|   Fail    |     point      |           충전 포인트 == 0           | 예외 반환 (InvalidChargingPointException) |
|   Fail    |     point      |       충전 포인트 > 최대 충전 포인트        | 예외 반환 (InvalidChargingPointException) |
|   Fail    |     userId     |          사용자 식별자 == 음수          | 예외 반환 (InvalidUserIdException)        |
|   Fail    |     userId     |          사용자 식별자 == 0           | 예외 반환 (InvalidUserIdException)        |
|   Fail    |     userId     |        양수 && 사용자 식별자 불일치        | 예외 반환 (UserNotFoundException)         |
|   Fail    | userId & point | (양수 && 사용자 식별자 일치) + 유효한 충전 포인트 | 포인트를 충전한다.                            |

### 2. PointController

| Pass/Fail |        |          user input           | expected status code | expected response body                                                 |
|:---------:|:------:|:-----------------------------:|:--------------------:|:-----------------------------------------------------------------------|
|   Fail    | point  |         충전 포인트 == 음수          |   bad request(400)   | {"code" : "CHARGING_POINT01", "message" : "충전 포인트는 음수일 수 없습니다."}       |
|   Fail    | point  |          충전 포인트 == 0          |   bad request(400)   | {"code" : "CHARGING_POINT02", "message" : "충전 포인트는 0일 수 없습니다."}        |
|   Fail    | point  |      충전 포인트 > 최대 충전 포인트       |   bad request(400)   | {"code" : "CHARGING_POINT03", "message" : "단일 포인트 충전은 최대 10만점 가능합니다."} |
|   Fail    | userId |         사용자 식별자 == 음수         |   bad request(400)   | {"code" : "MEMBER01", "message" : "유효하지 않는 유저 식별자 입니다."}               |
|   Fail    | userId |         사용자 식별자 == 0          |   bad request(400)   | {"code" : "MEMBER01", "message" : "유효하지 않는 유저 식별자 입니다."}               |
|   Fail    | userId |       양수 && 미등록 사용자 식별자       |   bad request(400)   | {"code" : "MEMBER02", "message" : "일치하는 유저가 존재하지 않습니다."}               |
|   Pass    | userId | 양수 && 사용자 식별자 일치 + 유효한 충전 포인트 |       ok(200)        | {"id" : "1", "point" : 200}                                            |

## (3) 시퀀스 다이어그램

### 1. 응답 성공 시퀀스

```mermaid
sequenceDiagram
actor user
participant controller as PointController 
participant service as PointService
participant repository as PointRepository

user ->>+ controller: PATCH /point/{id}/charge
controller ->>+ service: charge(id, chargingAmount)
service ->>+ repository: selectById(id)
repository -->>- service: UserPoint
service ->>+ repository: insertOrUpdate(id, amount)
repository -->>- service: UserPoint
service -->>- controller: UserPointSelectDTO
controller -->>- user: UserPointChargeResponse
```


### 2. 응답 실패 시퀀스 : PointService 예외 발생

```mermaid
sequenceDiagram
actor user
participant controller as PointController 
participant service as PointService
participant controllerAdvice as PointControllerAdvice 

user ->>+ controller: POST /point/{id}/charge
activate controller
controller ->>+ service: charge(id, chargingAmount)
deactivate controller
activate service
service ->>+ controllerAdvice: handleInvalidChargingPointException(exception)
deactivate service
controllerAdvice -->>- user: ErrorResponse
```


### 3. 응답 실패 시퀀스 : PointController 예외 발생

```mermaid
sequenceDiagram
actor user
participant controller as PointController
participant controllerAdvice as PointControllerAdvice

user ->>+ controller: POST /point/{id}/charge
activate controller
controller ->>+ controllerAdvice: handleInvalidChargingPointException(exception)
deactivate controller
activate controllerAdvice
controllerAdvice -->> user: ErrorResponse
deactivate controllerAdvice
```
