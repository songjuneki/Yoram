package com.sjk.yoram.model

import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentListItem(
    val type: DepartmentListItemType,
    val department: ExpandableDepartment?,
    val user: SimpleUser?,
    var isHide: Boolean,
    var isExpanded: Boolean
) {
    constructor(nodeDpt: DepartmentNode,
                isHide: Boolean = false):
            this(DepartmentListItemType.DEPARTMENT,
                ExpandableDepartment(nodeDpt),
                null,
                isHide,
                nodeDpt.isExpanded)
    constructor(dpt: ExpandableDepartment,
                isHide: Boolean = false):
            this(DepartmentListItemType.DEPARTMENT,
                dpt,
                null,
                isHide,
                dpt.isExpanded)

    constructor(user: SimpleUser,
                isHide: Boolean = false,
                isExpanded: Boolean = true):
            this(DepartmentListItemType.USER,
                null,
                user,
                isHide,
                isExpanded)


    override fun equals(other: Any?): Boolean {
        return if (other is DepartmentListItem) {
            when (this.type) {
                DepartmentListItemType.DEPARTMENT ->
                    this.department?.code == other.department?.code
                            && this.department?.parent == other.department?.parent
                DepartmentListItemType.USER ->
                    this.user?.id == other.user?.id
                            && this.user?.position == other.user?.position
                            && this.user?.department == other.user?.department
            }
        } else
            super.equals(other)
    }
}
