package com.sjk.yoram.presentation.main

enum class FragmentType(val title: String, val tag: String) {
    Fragment_HOME("홈", "homeFrag"),
    Fragment_DPTMENT("조직도", "departmentFrag"),
    Fragment_ID("교인증", "idFrag"),
    Fragment_BOARD("게시판", "boardFrag"),
    Fragment_MY("내 계정", "myFrag")
}