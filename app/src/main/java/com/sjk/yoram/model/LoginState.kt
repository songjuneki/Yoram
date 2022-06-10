package com.sjk.yoram.model

enum class LoginState {
    NONE,
    NAME_SUCCESS,
    NAME_SUCCESS_NEED_BD,
    BD_SUCCESS_NEED_PW,
    NAME_FAIL,
    BD_FAIL,
    PW_FAIL,
    LOGIN
}