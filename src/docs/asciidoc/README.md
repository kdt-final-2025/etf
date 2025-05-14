1. 📝 **문서 추가**

    * `src/docs/asciidoc` 폴더에 아래 템플릿 형식으로 `.adoc` 파일 생성

      ```adoc
      = <API 문서 제목>
      :doctype: book
      :source-highlighter: highlightjs
      :toc: left
      :toclevels: 2
      :seclinks:
 
      == Table of Contents
      * 1. 섹션1
      * 2. 섹션2
 
      == 1. 섹션1
      include::{snippets}/<snippet-path>/http-request.adoc[]
 
      == 2. 섹션2
      include::{snippets}/<snippet-path>/http-response.adoc[]
      ```

2. 🔧 **테스트 작성**

    * MockMvc(Spring REST Docs) 테스트에 `build/generated-snippets` 경로로 스니펫 생성

3. 🚀 **빌드 & 서버 실행**

   ```bash
   # 1) 테스트 + Asciidoctor 빌드
   ./gradlew test asciidoctor

   # 2) 애플리케이션 실행
   ./gradlew bootRun
   ```

    * 이 과정에서 `index.adoc`가 자동 갱신
    * 실행 후 브라우저에서 → [http://localhost:8080/docs/index.html](http://localhost:8080/docs/index.html)

---

