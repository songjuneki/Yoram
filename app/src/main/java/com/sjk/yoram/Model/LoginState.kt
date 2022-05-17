package com.sjk.yoram.Model

enum class LoginState {
    NONE,
    NAME_SUCCESS,
    NAME_SUCCESS_NEED_BD,
    BD_SUCCESS_NEED_PW,
    PW_SUCCESS,
    BD_N_PW_SUCCESS,
    NAME_FAIL,
    BD_FAIL,
    PW_FAIL,
    LOGIN
}