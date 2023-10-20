package com.sjk.yoram.presentation.common.listener

import com.sjk.yoram.data.entity.SimpleUser

interface UserItemClickListener {
    fun onClick(user: SimpleUser)
}