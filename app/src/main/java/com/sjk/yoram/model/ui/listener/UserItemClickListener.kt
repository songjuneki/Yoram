package com.sjk.yoram.model.ui.listener

import com.sjk.yoram.model.Department
import com.sjk.yoram.model.dto.SimpleUser

interface UserItemClickListener {
    fun onClick(user: SimpleUser)
}