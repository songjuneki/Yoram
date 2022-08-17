package com.sjk.yoram.model

import com.sjk.yoram.model.dto.Position
import com.sjk.yoram.model.dto.SimpleUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Department (
    var parentCode: Int,
    var name: String,
    val code: Int,
    var users: MutableList<SimpleUser>,
    var childDepartment: MutableList<Department>,
    var isExpanded: Boolean,
    var count: Int) {
    constructor(name: String, code: Int): this(0, name, code, mutableListOf(), mutableListOf(), true, 0)
    constructor(parentCode: Int, name: String, code: Int): this(parentCode, name, code, mutableListOf(), mutableListOf(), true, 0)
    constructor(dtoDpt: com.sjk.yoram.model.dto.Department): this(dtoDpt.parent, dtoDpt.name, dtoDpt.code)

    constructor(dtoPos: Position): this(dtoPos.name, dtoPos.code) {
        if (dtoPos.cat != dtoPos.code)
            this.parentCode = dtoPos.cat
        loadUsersForPosition()
    }

    constructor(dptCode: Int): this("", dptCode) {
        CoroutineScope(Dispatchers.IO).async {
            val dpt = MyRetrofit.dptmentApi.loadDepartmentbyCode(dptCode)
            name = dpt.name
            parentCode = dpt.parent
        }
    }

    private fun loadChildsForDepartment() =
        CoroutineScope(Dispatchers.IO).launch {
            val list = MyRetrofit.dptmentApi.getChildDepartments(this@Department.code)
            list.forEach {
                childDepartment.add(Department(it))
            }
        }


    private fun loadUsersForDepartment() =
        CoroutineScope(Dispatchers.IO).launch {
            val users = MyRetrofit.userApi.getSimpleUsersDepartment(this@Department.code)
            users.forEach {
                this@Department.users.add(it)
            }
        }


    private fun loadUsersForPosition() {
        CoroutineScope(Dispatchers.IO).async {
//            users = MyRetrofit.userApi.getSimpleUsersPosition(code)
            count = users.size
        }
    }

    fun getAllUserSize() =
        CoroutineScope(Dispatchers.IO).launch {
            count = 0
            count += users.size
            childDepartment.forEach {
                count += it.users.size
            }
        }

}

fun MutableList<Department>.addList(dtoDptList: MutableList<com.sjk.yoram.model.dto.Department>) {
    dtoDptList.forEach { this.add(Department(it)) }
}