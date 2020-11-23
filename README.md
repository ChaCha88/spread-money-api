
# 뿌리기 기능 API 

## 요구 사항
* 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
  * 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로 전달됩니다.
  * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는 HTTP Header로 전달됩니다.
  * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않습니다.
* 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록 설계되어야 합니다.
* 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

## 상세 구현 요건 및 제약사항
1. 뿌리기 API
* 다음 조건을 만족하는 뿌리기 API를 만들어 주세요.
  * 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
  * 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.
  * 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. (분배 로직은 자유롭게 구현해 주세요.)
  * token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다.
2. 받기 API
* 다음 조건을 만족하는 받기 API를 만들어 주세요.
  * 뿌리기 시 발급된 token을 요청값으로 받습니다.
  * token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다.
  * 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
  * 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
  * 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.
  * 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.
3. 조회 API
* 다음 조건을 만족하는 조회 API를 만들어 주세요.
  * 뿌리기 시 발급된 token을 요청값으로 받습니다.
  * token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재 상태는 다음의 정보를 포함합니다.
  * 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트)
  * 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다.
  * 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.

## 프로젝트 환경 구성
* Java 8
* Spring boot 2.3.7.BUILD
* H2 Embedded
* JPA
* Swagger2 ( API Docs )

## 핵심문제 해결 전략
### 1. 뿌리기시 3자리 문자열 token
* 토큰 생성 경우의 수을 늘리기 위해 대문자, 소문자, 숫자, 특수문자를 포함하여 token을 생성 (단, 문제가 발생될 수 있는 특수문자는 제거)
  * 조회를 GET 방식으로 사용 시 일부 특수문자가 치환되어 POST 방식으로 사용처리
  * 최초 구현 당시 줍기, 조회 시 token과 방으로 조회 조건을 주어 경우의 수를 늘렸으나, 받기 API 다른 방에선 주울 수 없는 제약조건으로 제거
### 2. 다수의 서버에 다수의 인스턴스로 동작

* 3자리 토큰 중복 가능성
  * 앞서 제시된 1번) 문자열 token을 생성 방식으로 토큰의 경우의 수를 늘려 중복방지
  * 이후 적은 확률의 토큰 중복 가능성을 고려하여 token을 Unique key 처리하여 중복 불가하도록 처리
* 줍기 중 문제 발생 가능성
  * Pickup Entitiy에 JPA 낙관적 잠금(Version check)로 미연의 사고를 방지 [Optimisstic Lock, @Version]
   
### 3. 뿌릴 금액을 인원수에 맞게 분배하여 저장
 * 금액 분배 시, 1인당 최소 1원의 금액을 분배 받을 수 있도록 구현

## API 명세 
### Swagger2 (API Docs)
* http://localhost:8080/swagger-ui.html#
#

## Response Error 응답
| 이름 |타입| 설명 |
|--|--|--|
| message | String | 에러 메세지 | 
| status | int | HTTP 응답 코드|
| code | String| 에러 코드 |

## Response Success 응답
| 이름 |타입| 설명 |
|--|--|--|
| status | int| HTTP 응답상태 | 
| data | Object| 각 API Response| 
  
##  뿌리기 API ( POST - api/v1/spread )

### Header 
| 항목 | 타입|설명| 
|--|--|--|
| X-ROOM-ID | String |대화방 ID| 
| X-USER-ID | long | 사용자 ID| 
| Content-Type | - | application/json |
### Request
| 이름 |타입| 설명 |
|--|--|--|
| person | int|인원수| 
| amount | long|뿌릴 금액| 

### Request HTTP
```
POST /api/v1/spread HTTP/1.1
Host: localhost:8080
X-USER-ID: 30
X-ROOM-ID: _A3
Content-Type: application/json
Content-Length: 35

{
    "person":2, "amount":1500
}
```
### Response 
| 이름 |타입| 설명 |
|--|--|--|
| token | String | 발급된 3자리 토큰| 

```
{
    "status": 201,
    "data": {
        "token": ".Dy"
    }
}
```

#
##  조회 API ( POST - api/v1/info )
### Header 
| 항목 | 타입|설명| 
|--|--|--|
| X-ROOM-ID | String |대화방 ID| 
| X-USER-ID | long | 사용자 ID| 
| Content-Type | - | application/json |
### Request
| 이름 |타입| 설명 |
|--|--|--|
| token | String | 3자리 토큰|
### Request HTTP
```
POST /api/v1/info HTTP/1.1
Host: localhost:8080
X-USER-ID: 30
X-ROOM-ID: A
Content-Type: application/json
Content-Length: 24

{
    "token": ".Dy"
}
```
### Response 
| 이름 |타입| 설명 |
|--|--|--|
| spreadTime| LocalDateTime | 뿌린 시각| 
| amount| long| 뿌린 금액| 
| pickupAmount| long | 받기 완료된 금액| 
| pickupList.amount| long | 받은금액| 
| pickupList.pickupId| long | 받은 사용자 아이디| 
```
{
    "status": 200,
    "data": {
        "spreadTime": "2020-12-25T12:25:25.25",
        "amount": 1500,
        "pickupAmount": 666,
        "pickupList": [
            {
                "amount": 666,
                "pickupId": 3034
            }
        ]
    }
}
```

#
## 줍기 API ( POST - api/v1/pickup )
### Header 
| 항목 | 타입|설명| 
|--|--|--|
| X-ROOM-ID | String |대화방 ID| 
| X-USER-ID | long | 사용자 ID|
| Content-Type | - | application/json | 
### Request
| 이름 |타입| 설명 |
|--|--|--|
| token | String | 3자리 토큰|
### Request HTTP
```
PUT /api/v1/pickup HTTP/1.1
Host: localhost:8080
Content-Type: application/json
X-ROOM-ID: _A3
X-USER-ID: 3034
Content-Length: 24

{
    "token": ".Dy"
}
``` 
### Response 
| 이름 |타입| 설명 |
|--|--|--|
| amount| long | 주운 금액| 
```
{
    "status": 201,
    "data": {
        "amount": 666
    }
}
```

## Exception

### 공통
|CODE| STATUS | MESSAGE|
|--|--|--|
| MISSING_REQUEST| 400| Header 정보가 누락되었습니다.| 
| TYPE_MISMATCH| 400| Header 정보가 올바르지 않습니다.| 
| METHOD_ARGUMENT_NOT_VALID| 400| Body 정보가 올바르지 않습니다.| 
| INVALID_FORMAT| 400| 입력 형식이 올바르지 않습니다.| 
### 뿌리기
|CODE| STATUS | MESSAGE|
|--|--|--|
| SP001| 500| 인원수 보다 많은 금액을 입력하세요.| 
### 조회
|CODE| STATUS | MESSAGE|
|--|--|--|
| SI001| 404| 뿌리기 정보가 존재하지 않습니다.(조회 결과 없음)|
| SI002| 404| 뿌리기 정보가 존재하지 않습니다.(방 정보가 다름)|  
| SI003| 500| 뿌린 사람 자신만 조회를 할 수 있습니다.| 
| SI004| 500| 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.| 
###  줍기
|CODE| STATUS | MESSAGE|
|--|--|--|
| PICK001| 404| 뿌리기 정보가 존재 하지 않습니다.| 
| PICK002| 400| 다른 방의 사용자는 받을 수 없습니다.| 
| PICK003| 500| 뿌린 건은 10분간만 유효합니다.| 
| PICK004| 500| 자신이 뿌리기한 건은 자신이 받을 수 없습니다.| 
| PICK005| 500| 뿌리기 당 한번만 받을 수 있습니다.| 
| PICK006| 500| 받기가 마감 되었습니다.| 

## 테스트
### API
* Mockmvc 를 통하여 뿌리기, 조회, 줍기를 테스트

|항목| 요청 클래스 | 정상 | 설명|
|--|--|--|--|
|공통| CommonApiTest| Exception| 헤더정보 타입 변경| 
|| CommonApiTest| Exception| 헤더 정보 누락| 
|| CommonApiTest| Exception| 요청 값 검증 | 
|뿌리기| SpreadApiTest| Success| 뿌리기 요청 후 토큰 3자리 받기 | 
|조회| SpreadApiTest| Success| 뿌리기 조회 후 응답값 결과 받기 | 
|줍기| PickupApiTest| Success| 줍기 후 응답값 금액 받기 | 

### Service
* AssertThat을 사용하여 정상으로 서비스가 동작 되는지 확인

|항목 |요청 클래스 | 정상 | 설명|
|--|--|--|--|
|뿌리기| SpreadServiceTest| Success| 뿌리기 요청 후 토큰 3자리 받기 | 
|| SpreadServiceTest| Exception| 7일이 지난 뿌리기 조회 불가능| 
|| SpreadServiceTest| Exception| 인원수 보다 적은 금액을 입력한 케이스 | 
|조회| SpreadServiceTest| Success| 뿌리기 조회 후 응답값 결과 받기 | 
|| SpreadServiceTest| Exception| 뿌리기 조회 결과가 존재 하지 않음 - 조회결과 없음 |
|| SpreadServiceTest| Exception| 뿌리기 조회 결과가 존재 하지 않음2 - 방정보가 다른 케이스 | 
|| SpreadServiceTest| Exception| 뿌린 유저가 아닌 유저가 조회하는 케이스 | 
|줍기| PickupServiceTest| Success| 줍기 후 응답값 금액 받기 | 
|| PickupServiceTest| Exception| 자신이 뿌리기한거 줍기 시도 실패 케이스 | 
|| PickupServiceTest| Exception| 마감된 뿌리기 줍기 시도 실패 | 
|| PickupServiceTest| Exception| 다른방의 사용자 줍기 시도 실패| 
|| PickupServiceTest| Exception| 두번 줍기 시도 실패| 
|| PickupServiceTest| Exception| 10분 유효기간 만료후 시도 실패 케이스 | 
