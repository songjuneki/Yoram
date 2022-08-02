package com.sjk.yoram.model

import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentSubData(val childDepartment: Department?, val user: SimpleUser?, val type: DptSubDataType)
enum class DptSubDataType { CHILD, USER }
