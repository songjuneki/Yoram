package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DptHeaderItemBinding
import com.sjk.yoram.databinding.DptUserItemBinding
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.ui.listener.UserItemClickListener

class ExpandableDepartmentNodeListAdapter(nodeList: MutableList<DepartmentNode>,
                                          private val userClickListener: UserItemClickListener): ListAdapter<DepartmentListItem, RecyclerView.ViewHolder>(diffUtil) {

    private lateinit var dptList: HashMap<Int, ExpandableDepartment>
    private lateinit var userList: HashMap<Int, SimpleUser>
    private lateinit var dptRelation: HashMap<Int, DepartmentRelation>
    private lateinit var userRelation: HashMap<Int, MutableSet<Int>>

    init {
        submitNodeList(nodeList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            DepartmentListItemType.DEPARTMENT.ordinal -> {
                val binding: DptHeaderItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dpt_header_item, parent, false)
                DepartmentViewHolder(binding)
            }
            DepartmentListItemType.USER.ordinal -> {
                val binding: DptUserItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dpt_user_item, parent, false)
                UserViewHolder(binding)
            }

            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DepartmentListItemType.DEPARTMENT.ordinal ->
                (holder as DepartmentViewHolder).bind(position)
            DepartmentListItemType.USER.ordinal ->
                (holder as UserViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int = currentList[position].type.ordinal
    override fun getItem(position: Int): DepartmentListItem = currentList[position]
    override fun getItemCount(): Int = currentList.size

    private fun getDepartmentListIncludeChildren(code: Int?): List<DepartmentListItem> {
        if (code == null) return listOf()
        if (!dptList.contains(code)) return listOf()
        val list = mutableListOf<DepartmentListItem>()


        dptRelation[code]!!.childList.forEach { sub ->
            // 현재 부서의 하위 부서 추가
            list.add(DepartmentListItem(dptList[sub]!!))

            // 하위의 하위 부서가 확장된 상태라면 추가
            if (dptList[sub]!!.isExpanded) {
                dptRelation[sub]!!.childList.forEach { subSub ->
                    list.addAll(getDepartmentListIncludeChildren(subSub))
                }
                userRelation[sub]!!.forEach { subUserId ->
                    list.add(DepartmentListItem(this.userList[subUserId]!!))
                }
            }
        }

        // 현재 부서의 유저 목록 추가
        userRelation[code]!!.forEach { userId ->
            list.add(DepartmentListItem(this.userList[userId]!!))
        }

        return list.toList()
    }

    inner class DepartmentViewHolder(private val binding: DptHeaderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val current = getItem(position)
            val relation = dptRelation[current.department!!.code]

            var dptName = ""
            repeat(relation!!.parentList.size - 1) {
                dptName += "    "
            }

            dptName += current.department.name
            binding.dptHeaderItemName.text = dptName
            binding.dptHeaderItemCount.text = current.department.count.toString()

            binding.root.setOnClickListener {
                val itemPosition = currentList.indexOf(current)
                val currentList = this@ExpandableDepartmentNodeListAdapter.currentList.toMutableList()
                val subList = getDepartmentListIncludeChildren(current.department.code)
                current.isExpanded = !current.isExpanded
                currentList[itemPosition] = current

                if (current.isExpanded) {
                    // 확장함
                    if (itemPosition >= itemCount)
                        currentList.addAll(itemPosition-1, subList)
                    else
                        currentList.addAll(itemPosition+1, subList) // 부서 바로 아래에 추가
                } else {
                    // 축소함
                    currentList.removeAll(subList)
                }

                dptList[current.department.code]!!.isExpanded = current.isExpanded
                submitList(currentList)
                binding.dptHeaderItemArrow.animate().setDuration(300L).rotationBy(180f).withEndAction {
                    if (binding.dptHeaderItemArrow.rotation % 180f != 0f)
                        binding.dptHeaderItemArrow.rotation = if (current.isExpanded) 180f else 0f
                }.start()
            }
        }
    }

    inner class UserViewHolder(private val binding: DptUserItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val current = getItem(position)
            binding.dptNameTv.text = current.user!!.name.replace('*', 'Ｏ')
            binding.dptNamePos.text = current.user.position_name

            if (current.user.department_name.isEmpty())
                binding.dptSubTv.text = "성도"
            else
                binding.dptSubTv.text = current.user.department_name

            binding.dptAvatarIv.load(current.user!!.avatar.ifBlank { "http://hyuny840501.cafe24.com:8080/api/user/avatar?id=-1" }) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_avatar)
                transformations(CircleCropTransformation())
            }

            binding.root.setOnClickListener {
                userClickListener.onClick(current.user)
            }
        }
    }

    fun submitNodeList(list: List<DepartmentNode>?) {
        if (list.isNullOrEmpty()) {
            submitList(mutableListOf<DepartmentListItem>())
            return
        }

        val newList = mutableListOf<DepartmentListItem>()

        list.forEach {
            newList.add(DepartmentListItem(it))
            if (it.child.isNotEmpty()) {
                val subList = subListFlatten(it.child)
                newList.addAll(subList)
            }
            newList.addAll(it.users.map { user -> DepartmentListItem(user) })
        }
        dptList = getExpandableDepartmentMapFromNodeList(list.toMutableList())
        userList = getSimpleUserMapFromNodeList(list.toMutableList())
        dptRelation = getDepartmentRelationFromItemList(newList.toMutableList())
        userRelation = getUserRelationFromNodeList(list.toMutableList())
        submitList(newList.toMutableList())
    }

    private fun subListFlatten(childList: List<DepartmentNode>): List<DepartmentListItem> {
        val list = mutableListOf<DepartmentListItem>()

        childList.forEach {
            list.add(DepartmentListItem(it))
            if (it.child.isNotEmpty())
                list.addAll(subListFlatten(it.child))
            list.addAll(it.users.mapNotNull { user -> DepartmentListItem(user) })
        }

        return list
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<DepartmentListItem>() {
            override fun areItemsTheSame(oldItem: DepartmentListItem, newItem: DepartmentListItem): Boolean {
                if (oldItem.type != newItem.type)
                    return false
                return when(newItem.type) {
                    DepartmentListItemType.DEPARTMENT ->
                        oldItem.department?.code == newItem.department?.code
                    DepartmentListItemType.USER ->
                        oldItem.user?.id == newItem.user?.id
                }
            }

            override fun areContentsTheSame(oldItem: DepartmentListItem, newItem: DepartmentListItem): Boolean {
                return when(newItem.type) {
                    DepartmentListItemType.DEPARTMENT ->
                        oldItem.department?.code == newItem.department?.code
                                && oldItem.department?.parent == newItem.department?.parent
                    DepartmentListItemType.USER ->
                        oldItem.user?.id == newItem.user?.id
                                && oldItem.user?.position == newItem.user?.position
                                && oldItem.user?.department == newItem.user?.department

                }
            }
        }
    }

    private fun getExpandableDepartmentMapFromNodeList(nodeList: MutableList<DepartmentNode>): HashMap<Int, ExpandableDepartment> {
        val map = hashMapOf<Int, ExpandableDepartment>()
        nodeList.forEach {
            if (!map.contains(it.code))
                map[it.code] = ExpandableDepartment(it)

            if (it.child.isNotEmpty()) {
                val subMap = getExpandableDepartmentMapFromNodeList(it.child)
                subMap.keys.forEach { subKey ->
                    if (!map.contains(subKey))
                        map[subKey] = subMap[subKey]!!
                }
            }
        }

        return map
    }

    private fun getSimpleUserMapFromNodeList(nodeList: MutableList<DepartmentNode>): HashMap<Int, SimpleUser> {
        val map = hashMapOf<Int, SimpleUser>()
        nodeList.forEach {
            it.users.forEach { user -> map[user.id] = user }

            if (it.child.isNotEmpty()) {
                val subMap = getSimpleUserMapFromNodeList(it.child)
                subMap.keys.forEach { subKey ->
                    if (!map.contains(subKey))
                        map[subKey] = subMap[subKey]!!
                }
            }
        }

        return map
    }

    private fun getDepartmentRelationFromItemList(list: List<DepartmentListItem>): HashMap<Int, DepartmentRelation> {
        val map = hashMapOf<Int, DepartmentRelation>()

        list.forEach { item ->
            if (item.type != DepartmentListItemType.DEPARTMENT)
                return@forEach
            if (!map.contains(item.department!!.code))
                map[item.department.code] = DepartmentRelation(mutableSetOf(), mutableSetOf())
            if (!map.contains(item.department.parent))
                map[item.department.parent] = DepartmentRelation(mutableSetOf(), mutableSetOf())

            map[item.department.parent]!!.childList.add(item.department.code)
            map[item.department.code]!!.parentList.add(item.department.parent)
        }

        list.reversed().forEach { item ->
            if (item.type != DepartmentListItemType.DEPARTMENT)
                return@forEach
            if (!map.contains(item.department!!.code))
                map[item.department.code] = DepartmentRelation(mutableSetOf(), mutableSetOf())
            if (!map.contains(item.department.parent))
                map[item.department.parent] = DepartmentRelation(mutableSetOf(), mutableSetOf())

            map[item.department.code]!!.parentList.addAll(map[item.department.parent]!!.parentList)
        }

        return map
    }

    private fun getUserRelationFromNodeList(nodeList: MutableList<DepartmentNode>): HashMap<Int, MutableSet<Int>> {
        val map = hashMapOf<Int, MutableSet<Int>>()
        nodeList.forEach {
            if (!map.contains(it.code))
                map[it.code] = mutableSetOf()
            map[it.code]!!.addAll(it.users.map { user -> user.id })

            if (it.child.isNotEmpty()) {
                val subMap = getUserRelationFromNodeList(it.child)
                subMap.keys.forEach { subKey ->
                    if (map.contains(subKey))
                        map[subKey]!!.addAll(subMap[subKey]!!)
                    else
                        map[subKey] = subMap[subKey]!!
                }
            }
        }

        return map
    }
}