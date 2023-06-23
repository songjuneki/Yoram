package com.sjk.yoram.model

import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentListItem(
    val type: DepartmentNodeSubDataType,
    val department: ExpandableDepartment?,
    val user: SimpleUser?,
    val isHide: Boolean,
    var isExpanded: Boolean
) {
    constructor(nodeDpt: DepartmentNode,
                isHide: Boolean = false,
                isExpanded: Boolean = true):
            this(DepartmentNodeSubDataType.DEPARTMENT,
                ExpandableDepartment(nodeDpt),
                null,
                isHide,
                isExpanded)
    constructor(dpt: ExpandableDepartment,
                isHide: Boolean = false,
                isExpanded: Boolean = true):
            this(DepartmentNodeSubDataType.DEPARTMENT,
                dpt,
                null,
                isHide,
                isExpanded)

    constructor(user: SimpleUser,
                isHide: Boolean = false,
                isExpanded: Boolean = true):
            this(DepartmentNodeSubDataType.USER,
                null,
                user,
                isHide,
                isExpanded)


    override fun equals(other: Any?): Boolean {
        return if (other is DepartmentListItem) {
            when (this.type) {
                DepartmentNodeSubDataType.DEPARTMENT ->
                    this.department?.code == other.department?.code
                            && this.department?.parent == other.department?.parent
                DepartmentNodeSubDataType.USER ->
                    this.user?.id == other.user?.id
                            && this.user?.position == other.user?.position
                            && this.user?.department == other.user?.department
            }
        } else
            super.equals(other)
    }
}
