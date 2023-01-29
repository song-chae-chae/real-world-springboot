# real-world-springboot

리얼 월드 백엔드 코드를 위한 저장소

## Real World Back-end spec
- https://realworld-docs.netlify.app/docs/specs/backend-specs/endpoints
---
## 개발 환경

- SpringBoot 2.7.6
- Java 11
- Gradle 7.6
- H2 Memory DB
- Spring Data JPA
- QueryDSL
- Spring Security
- OAuth2 client
- Spring Validation
- Lombok
- jjwt 0.9.1
---
## Real World 구조

![realworld](https://user-images.githubusercontent.com/113492037/215315343-828c38e5-9445-4a31-9c4d-725e9893f827.png)
---
## 주요 구현
* Spring Security + Kakao Social Login + JWT
    * 사용자가 카카오 로그인을 성공해서 인가 코드를 받아 Real World API에 로그인 요청을 보낸다.
    * Real World API에서 인가코드로 카카오 서버에 토큰 요청을 보내서 토큰이 제대로 발급되면 토큰으로 사용자 정보를 가져와서 회원가입 or 로그인을 진행한다.
    * 회원가입 / 로그인을 하면 사용자에게 자체 JWT를 발급하고, Front에서 토큰과 함께 API 요청한다.
    * Spring Security의 필터에서 토큰 검증, access token 만료된 경우 refresh token 있으면 토큰을 재발급해서 response header에 담아 사용자에게 보낸다.
* Controller Advice를 활용한 예외 처리
    * 예외 상황마다 동일한 포맷의 예외 메시지를 Front로 전달해주기 위해 Controller단에서 예외를 캐치해주었다.
    * 예외 타입이 추가될 때마다 예외 컨트롤러에 추가하는 건 비효율적이므로 CustomException들을 추상화하는 부모 클래스 RealWorldException을 만들었다.
  ``` java
  @Slf4j
  @RestControllerAdvice
  public class ExceptionController {
    @ExceptionHandler(RealWorldException.class)
    public ResponseEntity<ErrorResponse> realWorldExceptionHandler(RealWorldException e) {
        int statusCode = e.getExceptionType().getStatus().value();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getExceptionType().getMessage())
                .validation(e.getValidation()).build();

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
  }
  ```
* QueryDSL을 활용한 글 목록 페이지네이션 처리
  * 총 게시글이 몇 건인지 알기 위해 PageableExecutionUtils.getPage 사용
    * 토탈카운트가 페이지사이즈 보다 적거나 , 마지막페이지 일 경우 해당 함수를 실행하지 않는 장점이 있음
  ``` java
    @Override
    public Page<Article> getFeed(Long authId, ArticleSearch articleSearch) {
        List<Follow> byFollowerId = followRepository.findByFollowerId(authId);
        List<Long> followedIdList = byFollowerId.stream().map(i -> i.getFollowed().getId()).collect(Collectors.toList());

        followedIdList.add(authId);
        List<Article> articles =jpaQueryFactory.selectFrom(article)
                        .where(user.id.in(followedIdList))
                        .limit(articleSearch.getSize())
                        .offset(articleSearch.getOffset())
                        .orderBy(article.createdAt.desc())
                        .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(article.count())
                .from(article)
                .where(user.id.in(followedIdList));

        return getPageArticle(totalCount, articles, articleSearch);
    }

    private Page<Article> getPageArticle(JPAQuery<Long> jpaQuery, List<Article> articles, ArticleSearch articleSearch) {
        PageRequest of = PageRequest.of(articleSearch.getPage() - 1, articleSearch.getSize());

        return PageableExecutionUtils.getPage(articles, of, jpaQuery::fetchOne);
    }
  ```
---
## 요구사항 및 API 정리
* https://mulberry-fragrance-0aa.notion.site/API-e8cb5756926640ada0e03f26e9425ebc
---
## ERD

![realworld-erd](https://user-images.githubusercontent.com/113492037/215315531-e4f6a709-724d-415a-bc7e-93a8ee4cc5e6.png)