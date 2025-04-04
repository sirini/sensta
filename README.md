# SENSTA

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=fff&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=fff&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge" />
  <img src="https://img.shields.io/badge/TSBOARD-000000.svg?&style=for-the-badge&&logoColor=3178C6"/>
</p>

1. SENSTA란 무엇인가요?
2. TSBOARD와는 무슨 관계인가요?
3. 어떻게 활용하면 좋을까요?

## SENSTA란 무엇인가요?

SENSTA는 https://sensta.me 사이트에서 제공하고 있는 사진 공유 서비스를 안드로이드 스마트폰에서
보다 편리하게 이용 하실 수 있도록 개발하고 있는 앱입니다. SENSTA 앱과 웹사이트 모두 사용자분들이 올려주신
고품질의 사진들을 로그인 없이도 편하게 만나 보실 수 있습니다. (사진 업로드 및 좋아요/댓글 기능 등에는 로그인이 필요합니다)

> SENSTA 이름은 `Share Elegant, Noble Shots That Amaze` 라는 나름 거창한 풀네임의 줄임말로 지었습니다만,
> 실상은 짧은 도메인을 고민하다가 뭔가 마음에 들어서 선택 하였습니다.

이 SENSTA 프로젝트는 안드로이드 14 버전 기준으로 제작되고 있으며, 제가 테스트하고 있는 기기는 갤럭시 S23 울트라입니다.
안드로이드 스마트폰을 사용하시는 분들께서는 25년 6월 전후로 구글 플레이를 통해서 무료로 설치하여 사용해 보실 수 있습니다.

## TSBOARD와는 무슨 관계인가요?

SENSTA 서비스는 TSBOARD 기반으로 운영되고 있고, TSBOARD의 GOAPI에서 제공하는 각종 API들을 그대로 활용하고 있습니다.
따라서 이 SENSTA 안드로이드 앱은 약간의 설정 변경만으로도 TSBOARD 기반의 커뮤니티 사이트에 딱 맞춘 안드로이드 앱을
빠르게 제작 하실 수 있습니다.

- 즉, TSBOARD 기반으로 커뮤니티를 운영하시는 분들은 처음부터 새로 앱을 제작 하실 필요가 없습니다.
- 본 프로젝트를 따로 포크 하신 후, 자신의 커뮤니티에 맞게 조금만 수정 하시면 바로 앱이 완성 됩니다.

## 어떻게 활용하면 좋을까요?

아래와 같은 분들에게 도움이 되실 것 같습니다.

- 이미 TSBOARD 기반으로 웹사이트를 운영중이거나, 운영할 생각이 있으신 개발자/관리자님
    - 이분들은 이 SENSTA 프로젝트를 이용해서 빠르게 운영중인 사이트의 전용 앱을 개발 하실 수 있습니다.
    - 안드로이드 앱 개발을 전혀 해본 적이 없거나, 약간의 수정에 어려움이 있으시다면? https://tsboard.dev 사이트에서 도움을 요청해 주세요!
    - TSBOARD 사용을 고려중이시라면? 이 SENSTA 앱을 참고해 주세요! 웹 뿐만 아니라 앱도 빠르게 제작 가능합니다.
- SENSTA 앱의 개선에 기여하고자 하는 개발자님
    - 발견하신 버그, 개선해주신 코드 모두 환영합니다! 안드로이드 앱 개발을 주로 하신 베테랑 개발자님들의 조언도 환영합니다!

# 참고

- SENSTA.ME 웹사이트 : https://sensta.me
- TSBOARD 공식 홈페이지 : https://tsboard.dev
    - TSBOARD GitHub : https://github.com/sirini/tsboard
    - GOAPI GitHub : https://github.com/sirini/goapi

> SENSTA 서비스는 비상업적 목적으로 운영되고 있으며, 사진을 좋아하는 모든 분들에게 열려 있습니다.