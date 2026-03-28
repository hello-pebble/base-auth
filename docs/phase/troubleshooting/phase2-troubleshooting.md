## Phase 2 트러블슈팅

### 1. jjwt 0.12.x 버전 API 변경 사항 대응
- **문제**: 기존에 사용하던 `Jwts.parser().setSigningKey(key).parseClaimsJws(token)` 방식이 0.12.x 버전부터 deprecated 됨.
- **원인**: JJWT 라이브러리의 보안 및 가독성 개선을 위한 인터페이스 변경.
- **해결**: 최신 API인 `Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)` 형식을 적용하여 해결.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// JwtProvider.java - jjwt 0.12.6 API 적용
public Claims getClaims(String token) {
    return Jwts.parser()
            .verifyWith(secretKey) // setSigningKey -> verifyWith
            .build()               // builder 패턴 도입
            .parseSignedClaims(token) // parseClaimsJws -> parseSignedClaims
            .getPayload();         // getBody -> getPayload
}
```

### 2. JWT 서명 키 길이 부족 이슈
- **문제**: `Keys.hmacShaKeyFor(secret.getBytes())` 호출 시 `WeakKeyException` 발생.
- **원인**: HMAC-SHA256 알고리즘은 보안상 최소 256비트(32바이트) 이상의 키를 요구함.
- **해결**: `application.yaml`의 시크릿 키를 32자 이상의 문자열로 교체하고 `StandardCharsets.UTF_8`을 명시하여 해결.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// JwtProvider.java - 안전한 키 생성
@PostConstruct
protected void init() {
    // 32바이트(256비트) 미만의 키 사용 시 WeakKeyException 발생 방지
    byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
}
```

### 3. Stateless 전환 후 권한 체크 실패 (Forbidden)
- **이슈**: JWT 필터 통과 후에도 `@PreAuthorize` 권한 체크에서 403 Forbidden 에러 발생.
- **원인**: `JwtAuthenticationFilter`에서 인증 객체 생성 시 권한(Authorities) 목록을 제대로 담지 않았거나, 권한 이름 포맷(ROLE_ 접두사 등)이 불일치함.
- **해결**: 토큰의 `roles` 클레임을 기반으로 `SimpleGrantedAuthority` 리스트를 생성하여 `UsernamePasswordAuthenticationToken`에 명시적으로 전달함으로써 해결.

#### 🛠️ 해결 근거 (Code Snippet)
```java
// JwtAuthenticationFilter.java - 권한 포함 인증 객체 생성
private Authentication getAuthentication(String token) {
    Claims claims = jwtProvider.getClaims(token);
    String role = claims.get("roles", String.class); // "ROLE_USER" 등

    // [핵심] Authorities 리스트를 생성하여 SecurityContext에 주입해야 인가(@PreAuthorize)가 작동함
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    UserDetails principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
}
```
