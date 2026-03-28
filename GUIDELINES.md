# 🌳 Project Guidelines & Git Strategy

이 문서는 `base-auth` 프로젝트의 협업 규칙과 브랜치 전략을 정의합니다. 모든 팀원과 AI 에이전트는 이 규칙을 준수해야 합니다.

---

## 🚀 Git 브랜치 전략 (Simplified Git Flow)

우리는 빠른 개발과 안정성을 위해 **기능별 브랜치 전략**을 사용합니다.

### 1. 주요 브랜치 구성
*   **`main`**: 운영 환경(Production) 배포용 브랜치입니다.
    *   보호된 브랜치이며, 직접 커밋이 금지됩니다.
    *   `develop`에서 `PR(Pull Request)`을 통해서만 머지됩니다.
*   **`develop`**: 다음 출시 버전을 개발하는 통합 브랜치입니다.
    *   모든 기능 개발의 기준점이 됩니다.
*   **`feature/{기능명}`**: 새로운 기능 개발 또는 리팩토링용 브랜치입니다.
    *   `develop`에서 분기하며, 완료 후 다시 `develop`으로 머지합니다.
    *   예: `feature/jwt-auth`, `feature/redis-integration`
*   **`hotfix/{이슈명}`**: 운영 환경의 긴급 버그 수정용 브랜치입니다.
    *   `main`에서 분기하며, 수정 후 `main`과 `develop` 모두에 반영합니다.

### 2. 커밋 메시지 규칙 (Conventional Commits)
커밋 메시지는 다음 형식을 따릅니다: `<type>: <description>`

*   `feat`: 새로운 기능 추가
*   `fix`: 버그 수정
*   `docs`: 문서 수정 (README, TDR 등)
*   `style`: 코드 포맷팅, 세미콜론 누락 (로직 변경 없음)
*   `refactor`: 코드 리팩토링
*   `test`: 테스트 코드 추가 및 수정
*   `chore`: 빌드 업무 수정, 패키지 매니저 설정 등

---

## 🛠️ 작업 프로세스

1.  **브랜치 생성**: 최신 `develop`에서 `feature/` 브랜치를 생성합니다.
2.  **기능 개발**: 작은 단위로 커밋하며 개발을 진행합니다.
3.  **검증**: 로컬 테스트 및 빌드를 완료합니다.
4.  **머지**: `develop` 브랜치로 PR을 생성하고, 코드 리뷰 후 머지합니다. (권장: Squash & Merge)
5.  **정리**: 머지된 `feature/` 브랜치는 삭제합니다.
