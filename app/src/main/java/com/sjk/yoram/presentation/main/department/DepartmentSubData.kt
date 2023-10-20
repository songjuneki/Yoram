package com.sjk.yoram.presentation.main.department

import com.sjk.yoram.data.entity.SimpleUser


data class DepartmentSubData(val childDepartment: Department?, val user: SimpleUser?, val type: DptSubDataType)
enum class DptSubDataType { CHILD, USER }
