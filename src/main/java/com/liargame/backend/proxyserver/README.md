# Liar Game Proxy Server
이 프로젝트는 라이어 게임에서 **프록시 서버** 역할을 담당하며, WebSocket을 통해 웹 클라이언트로부터 메시지를 받고, 이를 TCP 서버로 중개하는 기능을 수행합니다.
## 구현할 기능 목록
- [x] 기본 WebSocket 서버 설정 및 클라이언트 연결 수락
    - WebSocket 서버 엔드포인트를 설정하고 클라이언트가 연결할 수 있도록 구성합니다.

- [x] TCP 서버 연결 설정
    - WebSocket 서버에서 TCP 서버와 연결을 설정하여 클라이언트 메시지를 중개할 준비를 완료합니다.

- [x] TCP 서버에서 수신한 메시지 WebSocket으로 전송
    - TCP 서버로부터 메시지를 수신하여 연결된 WebSocket 클라이언트에게 브로드캐스트합니다.

- [x] WebSocket 클라이언트의 메시지를 TCP 서버로 중개 (`@OnMessage` 메서드)
    - WebSocket 클라이언트로부터 전송된 메시지를 TCP 서버로 전달합니다.

- [x] WebSocket 클라이언트 연결 종료 처리 (`@OnClose` 메서드)
    - WebSocket 클라이언트가 연결을 종료할 때, TCP 서버와의 연결도 종료하고 자원을 해제합니다.

- [x] WebSocket 에러 처리 (`@OnError` 메서드)
    - WebSocket 연결에서 발생하는 에러를 처리하고, 관련 정보를 로그에 기록합니다.

- [x] 메시지 클래스 작성
  - 클라이언트와 서버 사이의 메시지를 타입별 클래스로 작성합니다.