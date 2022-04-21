package com.sjk.yoram.Model

import com.sjk.yoram.Model.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

data class Department (
    var parentCode: Int,
    val name: String,
    val code: Int,
    var users: MutableList<User>,
    var childDepartment: MutableList<Department>,
    var isExpanded: Boolean) {
    constructor(name: String, code: Int): this(0, name, code, mutableListOf(), mutableListOf(), false)
    constructor(parentCode: Int, name: String, code: Int): this(parentCode, name, code, mutableListOf(), mutableListOf(), false)
    constructor(dtoDpt: com.sjk.yoram.Model.dto.Department): this(dtoDpt.parent, dtoDpt.name, dtoDpt.code) { loadUsers() }

    private fun loadChilds() {
        CoroutineScope(Dispatchers.IO).async {
            val list = MyRetrofit.getMyApi().getChildDepartments(code)
            list.forEach { childDepartment.add(Department(it)) }
        }
    }

    private fun loadUsers() {
        CoroutineScope(Dispatchers.IO).async {
            users = MyRetrofit.getMyApi().getUsersDepartment(code)
        }
    }
}