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
    var isExpanded: Boolean) {
    constructor(name: String, code: Int): this(0, name, code, mutableListOf(), mutableListOf(), false)
    constructor(parentCode: Int, name: String, code: Int): this(parentCode, name, code, mutableListOf(), mutableListOf(), false)


    constructor(dtoDpt: com.sjk.yoram.model.dto.Department): this(dtoDpt.parent, dtoDpt.name, dtoDpt.code) { loadUsersForDepartment() }

    constructor(dtoPos: Position): this(dtoPos.name, dtoPos.code) {
        if (dtoPos.cat != dtoPos.code)
            this.parentCode = dtoPos.cat
        loadUsersForPosition()
    }

    constructor(dptCode: Int): this("", dptCode) {
        CoroutineScope(Dispatchers.IO).launch {
            val dpt = MyRetrofit.getMyApi().loadDepartmentbyCode(dptCode)
            name = dpt.name
            parentCode = dpt.parent
        }
    }

    private fun loadChildsForDepartment() {
        CoroutineScope(Dispatchers.IO).async {
            val list = MyRetrofit.getMyApi().getChildDepartments(code)
            list.forEach { childDepartment.add(Department(it)) }
        }
    }

    private fun loadUsersForDepartment() {
        CoroutineScope(Dispatchers.IO).async {
            users = MyRetrofit.getMyApi().getSimpleUsersDepartment(code)
        }
    }

    private fun loadUsersForPosition() {
        CoroutineScope(Dispatchers.IO).async {
            users = MyRetrofit.getMyApi().getSimpleUsersPosition(code)
        }
    }
}