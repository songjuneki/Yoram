package com.sjk.yoram.Model

import com.sjk.yoram.Model.dto.Position
import com.sjk.yoram.Model.dto.SimpleUser
import com.sjk.yoram.Model.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

data class Department (
    var parentCode: Int,
    val name: String,
    val code: Int,
    var users: MutableList<SimpleUser>,
    var childDepartment: MutableList<Department>,
    var isExpanded: Boolean) {
    constructor(name: String, code: Int): this(0, name, code, mutableListOf(), mutableListOf(), false)
    constructor(parentCode: Int, name: String, code: Int): this(parentCode, name, code, mutableListOf(), mutableListOf(), false)


    constructor(dtoDpt: com.sjk.yoram.Model.dto.Department): this(dtoDpt.parent, dtoDpt.name, dtoDpt.code) { loadUsersForDepartment() }

    constructor(dtoPos: Position): this(dtoPos.name, dtoPos.code) {
        if (dtoPos.cat != dtoPos.code)
            this.parentCode = dtoPos.cat
        loadUsersForPosition()
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