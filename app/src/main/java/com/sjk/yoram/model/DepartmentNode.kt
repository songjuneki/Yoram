package com.sjk.yoram.model

import android.util.Log
import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentNode(
    val code: Int,
    val name: String,
    val parent: Int = 0,
    var child: MutableList<DepartmentNode> = mutableListOf(),
    var users: MutableList<SimpleUser> = mutableListOf(),
    var isExpanded: Boolean = true,
    var count: Int = 0
) {
    constructor(expandableDepartment: ExpandableDepartment):
            this(expandableDepartment.code, expandableDepartment.name, expandableDepartment.parent,
            isExpanded = expandableDepartment.isExpanded, count = expandableDepartment.count)
    fun copy(): DepartmentNode {
        val dest = DepartmentNode(this.code, this.name, this.parent, isExpanded = this.isExpanded, count = this.count)
        dest.child.addAll(this.child)
        dest.users.addAll(this.users)
        return dest
    }
}

/**
 * 자신을 제외한 [DepartmentNode] 의 모든 하위 부서들을 1차원 List로 반환합니다.
 * 반환된 List의 모든 원소([DepartmentNode])는 하위 부서를 가지지 않습니다.
 * @return 하위 부서들의 1차원 List
 */
fun DepartmentNode.flattenOnlyChildren(): List<DepartmentNode> {
    val list = mutableListOf<DepartmentNode>()

    this.child.forEach {
        list.add(it)
        if (it.child.isNotEmpty())
            list.addAll(it.flattenOnlyChildren())
    }

    return list.toList()
}

/**
 * 나타내는 [DepartmentNode]의 code 를 Key로 가지는 HashMap을 반환합니다. Value 는 [ExpandableDepartment] 입니다.
 * @return HashMap<Key: Code of current [DepartmentNode], Value: Current [ExpandableDepartment]>
 */
fun List<DepartmentNode>.toExpandableDepartmentHashMap(): HashMap<Int, ExpandableDepartment> {
    val map = hashMapOf<Int, ExpandableDepartment>()
    this.forEach {
        if (!map.contains(it.code))
            map[it.code] = ExpandableDepartment(it)

        if (it.child.isNotEmpty()) {
            val childList = it.flattenOnlyChildren()
            map.putAll(childList.toExpandableDepartmentHashMap())
        }
    }
    return map
}

/**
 * @return HashMap<Key: id of current [SimpleUser], Value: Current [SimpleUser]>
 */
fun List<DepartmentNode>.toSimpleUserHashMap(): HashMap<Int, SimpleUser> {
    val map = hashMapOf<Int, SimpleUser>()
    this.forEach {
        map.putAll(it.users.associateBy { user -> user.id })

        if (it.child.isNotEmpty()) {
            val childList = it.flattenOnlyChildren()
            map.putAll(childList.toSimpleUserHashMap())
        }
    }
    return map
}


/**
 * @return HashMap<Key: Code of current [DepartmentNode], Value: Relation of current [DepartmentNode]>
 */
fun List<DepartmentNode>.toDepartmentRelationHashMap(): HashMap<Int, DepartmentRelation> {
    val map = hashMapOf<Int, DepartmentRelation>()

    val list = this.toMutableList()

    this.forEach {
        val add = it.flattenOnlyChildren()
        list.addAll(add)
    }

    list.forEach {
        if (!map.contains(it.code))
            map[it.code] = DepartmentRelation()
        if (!map.contains(it.parent))
            map[it.parent] = DepartmentRelation()

        map[it.code]?.parentList?.add(it.parent)
        map[it.parent]?.childList?.add(it.code)
    }

    list.reversed().forEach {
        map[it.code]?.parentList?.addAll(map[it.parent]?.parentList ?: mutableSetOf())
    }

    Log.d("JKJK", "relation=$map")
    return map
}

/**
 * @return HashMap<Key: Code of Current [DepartmentNode], Value: Set of current DepartmentNode's [SimpleUser]'s id>
 */
fun List<DepartmentNode>.toUserRelationHashMap(): HashMap<Int, MutableSet<Int>> {
    val map = hashMapOf<Int, MutableSet<Int>>()

    this.forEach {
        if (!map.contains(it.code))
            map[it.code] = mutableSetOf()
        map[it.code]?.addAll(it.users.map { user -> user.id })

        if (it.child.isNotEmpty()) {
            val childList = it.flattenOnlyChildren()
            map.putAll(childList.toUserRelationHashMap())
        }
    }

    return map
}
