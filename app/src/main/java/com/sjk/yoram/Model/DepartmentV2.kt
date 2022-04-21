package com.sjk.yoram.Model

import com.sjk.yoram.Model.dto.Department
import com.sjk.yoram.Model.dto.User

data class DepartmentV2 (
    var parentCode: Int,
    val name: String,
    val code: Int,
    var isExpanded: Boolean) {
    constructor(name: String, code: Int): this(0, name, code, false)
    constructor(parent: Int, name: String, code: Int): this(parent, name, code, false)
    constructor(dtoDpt: Department) : this(dtoDpt.name, dtoDpt.code) {
        this.isExpanded = false
        if (dtoDpt.parent != 0)
            this.parentCode = dtoDpt.parent
    }
}