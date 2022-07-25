package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.model.Department
import com.sjk.yoram.R
import com.sjk.yoram.databinding.CardDepartmentBinding

class DepartmentCardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isLogin = false
    var departments = mutableListOf<Department>()
    private var subClickListener : onDptSubClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: CardDepartmentBinding = DataBindingUtil.inflate(inflater, R.layout.card_department, parent, false)
        return DepartmentCardHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (departments[position].parentCode == 0)
            (holder as DepartmentCardHolder).bind(position)
    }

    override fun getItemCount(): Int {
        val childCount = departments.count { it.parentCode != 0 }
        return departments.size - childCount
    }

    inner class DepartmentCardHolder(val binding: CardDepartmentBinding): RecyclerView.ViewHolder(binding.root) {
        val recycler = binding.cardDptRecycler
        val title = binding.cardDptTitle
        val arrow = binding.cardDptArrow
        val layoutManager = LinearLayoutManager(binding.root.context)
        fun bind(position: Int) {
            val adapter = DptCardRecycleAdapter(departments, departments[position], position, isLogin)
            adapter.cardListener = subClickListener
            recycler.adapter = adapter
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recycler.layoutManager = layoutManager
            title.text = departments[position].name

            binding.root.setOnClickListener {
                val show = toggleLayout(!departments[position].isExpanded, arrow, recycler)
                subClickListener!!.onDptSubClick(departments[position].code)
                notifyItemChanged(adapterPosition)
            }
            if(departments[position].isExpanded) {
                recycler.visibility = View.VISIBLE
                arrow.animate().setDuration(300).rotation(180f)
            } else {
                recycler.visibility = View.GONE
                arrow.animate().setDuration(0).rotation(180f)
                arrow.animate().setDuration(300).rotation(0f)
            }

        }

        private fun toggleLayout(isExpanded: Boolean, view: View, layout: RecyclerView): Boolean {
            ToggleAnimation.toggleArrow(view, isExpanded)
            if (isExpanded)
                layout.visibility = View.VISIBLE
            else
                layout.visibility = View.GONE

            return isExpanded
        }
    }

    fun fetchData(department: MutableList<Department>) {
        this.departments = department
//        notifyDataSetChanged()
    }


    fun parentListener(parentPos: Int) {
        notifyItemChanged(parentPos)
    }

    interface onDptSubClickListener {
        fun onDptSubClick(dptCode: Int)
        fun onDptParentNotify(parentPos: Int)
    }

    fun setOnDptSubClickListener(listener: onDptSubClickListener) {
        this.subClickListener = listener
    }

    fun loginDataChanged(isLogin: Boolean) {
        this.isLogin = isLogin
        notifyDataSetChanged()
    }

}

class ToggleAnimation {
    companion object {
        fun toggleArrow(view: View, isExpanded: Boolean): Boolean {
            if (isExpanded) {
                view.animate().setDuration(300).rotation(180f)
                return true
            } else {
                view.animate().setDuration(300).rotation(0f)
                return false
            }
        }
    }
}