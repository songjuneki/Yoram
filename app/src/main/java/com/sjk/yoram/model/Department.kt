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
    constructor(name: String, code: Int): this(0, name, code, mutableListOf(), mutableListOf(), false, 0)
    constructor(parentCode: Int, name: String, code: Int): this(parentCode, name, code, mutableListOf(), mutableListOf(), false, 0)


    constructor(dtoDpt: com.sjk.yoram.model.dto.Department): this(dtoDpt.parent, dtoDpt.name, dtoDpt.code) { loadUsersForDepartment() }

    constructor(dtoPos: Position): this(dtoPos.name, dtoPos.code) {
        if (dtoPos.cat != dtoPos.code)
            this.parentCode = dtoPos.cat
        loadUsersForPosition()
    }

    constructor(dptCode: Int): this("", dptCode) {
        CoroutineScope(Dispatchers.IO).launch {
            val dpt = MyRetrofit.dptmentApi.loadDepartmentbyCode(dptCode)
            name = dpt.name
            parentCode = dpt.parent
        }
    }

    private fun loadChildsForDepartment() {
        CoroutineScope(Dispatchers.IO).async {
            val list = MyRetrofit.dptmentApi.getChildDepartments(code)
            list.forEach { childDepartment.add(Department(it)) }
            count = list.size
        }
    }

    private fun loadUsersForDepartment() {
        CoroutineScope(Dispatchers.IO).async {
//            users = MyRetrofit.userApi.getSimpleUsersDepartment(code)
            count = users.size
        }
    }

    private fun loadUsersForPosition() {
        CoroutineScope(Dispatchers.IO).async {
//            users = MyRetrofit.userApi.getSimpleUsersPosition(code)
            count = users.size
        }
    }
}