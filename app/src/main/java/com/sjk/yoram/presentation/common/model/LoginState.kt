package com.sjk.yoram.presentation.common.model

enum class LoginState {
    NONE,
    NAME_SUCCESS_NEED_BD,
    NAME_FAIL,
    BD_FAIL,
    PW_FAIL,
    NAME_BD_OK_PW_FAIL,
    NETWORK_ERROR,
    LOGIN
}