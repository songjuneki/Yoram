# 교회 스마트 요람

![Banner](art/prj_banner.webp)

<a href="https://play.google.com/store/apps/details?id=com.sjk.yoram" target="_blank"><img src="art/Google Play Badge.png" alt="Play Store로 이동" width=200></a>

## 요람이란?

**교회 소개** 및 **정보**, **교인 인사 정보**가 담겨있는 **책자**로  
*대외적으로는* **교회를 알리고** *내부적으로는* **교인들의 활발한 교류**를 위한 **역할**을 합니다.

---

## 기능 소개

### 기존 요람 책자 대체

요람 책자의 역할을 하기 위해 등록된 **교인의 인사 정보**를 *이름*, *부서*, *직책* 순으로 **열람**할 수 있습니다.  

### 교회 소개를 위해 익명으로도 사용 가능

**익명으로도 제한된 기능**으로 사용 가능해 교회의 정보를 알 수 있습니다.  
**개인 설정**으로 **익명에게 나의 정보를 공개하거나 가릴 수 있습니다.**  

### QR 교인증 / 출석 및 헌금

**교인증**을 통해 **출석 체크**를 할 수 있습니다.  
*재정 관리자*는 **헌금을 등록**할 수 있습니다.  
나의 **출석과 헌금 기록 열람**이 가능합니다. 

### 커뮤니티

**게시판**을 통해 *교회의 소식*을 볼 수 있습니다.

### 관리자 기능

이 외에도 **관리자 전용 메뉴**를 통해  
*부서*, *직책 변경*과 같은 **교인 인사 관리**와  
*부서*, *직책*, *예배 종류*, *헌금 종류 관리*와 같은 **운영 정보**를 **관리**할 수 있습니다.

--- 

## 프로젝트 구조

```
app
├─ data
│   ├─ entity
│   └─ repository
│       └─ retrofit
│
├─ presentation
│   ├─ common
│   │   ├─ adapter
│   │   ├─ listener
│   │   └─ model
│   ├─ init
│   └─ main
│       ├─ home
│       ├─ department
│       │   └─ manager
│       ├─ identification
│       │   └─ scanner
│       ├─ board
│       │   └─ detail
│       └─ my
│           ├─ attend
│           ├─ edit
│           ├─ give
│           └─ preference
│               ├─ admin
│               │   ├─ banner
│               │   ├─ board
│               │   ├─ department
│               │   ├─ give
│               │   ├─ newuser
│               │   └─ worship
│               └─ delete
└─ util
```

---

## 구현 방법

### 설계

초기 **MVVM 디자인 패턴**을 사용하여 설계했습니다.  
Model - View - ViewModel 로 나누어 프로젝트를 구조화했고  
각 View 와 ViewModel 끼리 관찰 가능한 데이터 홀더 클래스인 **LiveData**를 **바인딩**하여 여러 값과 유저 액션, 이벤트들을 관리했습니다. 

Play Store 출시 이후엔 **Clean Architecture**를 따르고 안드로이드에서 지향하고 가이드하는 아키텍처로 **리팩토링**을 진행 중에 있습니다.  
안드로이드에 종속되지 않는 **Kotlin Flow** 를 이용하여 **관심사 분리**를 따르고  
각 Data, Presentation 레이어 사이를 **한 방향으로만 흐르는 데이터 흐름**으로 **코드 가독성**, **유지보수성**을 높였습니다.  
최종적으로는 **테스트 가능한 코드**를 만들어내고 *유닛, UI 테스트*를 익히고 적용까지 하려는 *계획*이 있습니다.

앱 동작에 필요한 데이터는 백엔드와 서버와 **Retorfit2** 라이브러리를 사용하여 **REST 통신**으로 상호작용을 합니다.

### 디자인 및 레이아웃

**디자이너와 협업**으로 제시된 디자인 요구사항에 따라 **커스텀 View**를 만들고  
**ConstraintLayout**을 활용하여 비율을 이용한 배치로 화면 크기가 달라도 **일관된 레이아웃**을 보여줌으로 **기기 호환성**을 높였습니다.  


### 로직 및 기능

 1. 앱 시작시 SplashScreen 에서 서버 및 기기의 네트워크 상태와 기기에 저장된 계정 정보를 확인합니다.
 
 
 2. 저장된 계정 정보가 없다면(앱 구동 초기시) 로그인/회원가입 화면으로 이동합니다.
    - 회원가입시에는 이름, 패스워드, 성별, 생년월일, 전화번호, 주소, 차번호(선택사항)을 질의합니다.
    - 익명으로 로그인이 가능합니다. 익명일시 교인들의 개인정보를 가리고 기능을 제한적으로 서비스합니다.
    - 회원가입이 된 유저는 관리자에게 승인을 받아야 앱의 기능을 모두 제약없이 이용할 수 있습니다.
    - 로그인시 메인 화면으로 이동하고 기기에 계정 정보를 저장하여 다음 앱 구동시에 자동 로그인을 진행합니다.
 
 
 3. 하단 네비게이션 바에 따라 홈, 조직도, 교인증, 게시판, 내 정보의 기능이 있습니다.
 
 4. 권한이 주어진 관리자는 각 기능들의 항목을 관리할 수 있습니다.(교인 권한 부여, 부서 관리 등)

> - 홈 : 배너와 각 정보(내 정보, 조직도 검색, 게시글)를 간단하게 볼 수 있는 대시보드 입니다.
> 
> 
> - 조직도 : 등록된 교인들의 정보를 볼 수 있습니다.  
>   이름 순, 조직 별, 직분 별로 정렬하여 볼 수 있습니다.  
>   이름을 검색하여 교인을 찾을 수 있습니다.
> 
> 
> - 교인증 : QR 코드로 발급된 교인증입니다.   
>   관리자는 스캐너를 실행할 수 있고 이를 통해 출석 체크를 할 수 있습니다.
> 
> 
> - 게시판 : 교회 홈페이지와 연결된 게시판입니다.  
>   관리자는 보여질 게시판 항목을 관리할 수 있습니다.
> 
> 
> - 내 정보 : 내 정보(이름, 부서, 직분), 내 헌금 및 출석 정보를 볼 수 있고  
>   개인정보(프로필 사진, 전화번호, 주소, 차번호)를 수정할 수 있습니다.  
>   개인정보 익명 공개 여부를 설정할 수 있습니다.  
>   이 곳에서 설정으로 이동할 수 있습니다.  
>   설정에서는 로그아웃 및 계정 삭제를 할 수 있고 관리자에게는 관리 메뉴가 보입니다.

---

## 사용한 기술 및 라이브러리

- Kotlin
- MVVM 패턴
- ViewModel
- DataBinding
- ConstraintLayout
- LiveData
- Navigation Component
- RecyclerView
- Paging3
- Flow
- Retrofit2
- Coil
- etc..

---

## 차후 개발 목표

- [ ] Clean Architecture 리팩토링
- [ ] 새 게시글 푸시 알림 기능 구현
- [ ] 게시글 이미지 뷰어 구현
- [ ] 비밀번호 찾기 기능 구현
- [ ] 의존성 주입 도입
- [ ] 유닛/UI 테스트 도입
- [ ] 외부 라이브러리 사용 최소화

---