package com.sjk.yoram.Model

import kotlinx.coroutines.*

data class MyLoginData(
    var id: Int,
    var avatar: String,
    var fname: String,
    var lname: String,
    var sex: Boolean,
    var department: Int,
    var position: Int,
    var permission: Int
) {
    constructor(): this(-1, "", "익", "명", true, -1, -1, 0)
    suspend fun getDepartmentName() = CoroutineScope(Dispatchers.IO).async {
        MyRetrofit.getMyApi().loadDepartmentbyCode(department).name
    }
}