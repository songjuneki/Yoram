package com.sjk.yoram.model

import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentNodeSubData(
    val childDepartmentNode: DepartmentNode?,
    val user: SimpleUser?,
    val type: DepartmentNodeSubDataType
)
