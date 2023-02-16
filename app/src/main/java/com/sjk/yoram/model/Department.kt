package com.sjk.yoram.model

import com.sjk.yoram.model.dto.Position
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.repository.retrofit.MyRetrofit
import io.github.bangjunyoung.KoreanTextMatcher
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
    }

    constructor(dptCode: Int): this("", dptCode) {
        CoroutineScope(Dispatchers.IO).async {
            val dpt = MyRetrofit.dptmentApi.loadDepartmentbyCode(dptCode)
            name = dpt.name
            parentCode = dpt.parent
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

@JvmName("addListDtoDepartment")
fun MutableList<Department>.addList(dtoDptList: MutableList<com.sjk.yoram.model.dto.Department>) {
    dtoDptList.forEach { this.add(Department(it)) }
}

@JvmName("addListPosition")
fun MutableList<Department>.addList(dtoPosList: MutableList<Position>) {
    dtoPosList.forEach { this.add(Department(it)) }
}

fun MutableList<Department>.findUser(name: String): MutableList<SimpleUser> {
    val list = mutableListOf<SimpleUser>()

    this.forEach { dpt ->
        val matcher = KoreanTextMatcher(name)
        list.addAll(dpt.users.filter { matcher.match(it.name).success() }.toMutableList())
        if (dpt.childDepartment.isNotEmpty())
            list.addAll(dpt.childDepartment.findUser(name))
    }
    return list
}