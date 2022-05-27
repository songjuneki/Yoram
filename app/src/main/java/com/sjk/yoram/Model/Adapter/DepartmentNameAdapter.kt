package com.sjk.yoram.Model.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.Model.dto.SimpleUser
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DptNameItemBinding
import com.sjk.yoram.databinding.DptNameItemHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class nameData(val user: SimpleUser?, val header: String?)

class DepartmentNameAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isLogin = false
    var users = mutableListOf<SimpleUser>()
    var data = mutableListOf<nameData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: DptNameItemBinding = DataBindingUtil.inflate(inflater, R.layout.dpt_name_item, parent, false)
        val headerBinding: DptNameItemHeaderBinding = DataBindingUtil.inflate(inflater, R.layout.dpt_name_item_header, parent, false)
        return when(viewType) {
            0 -> DepartmentNameHeaderHolder(headerBinding)
            1 -> DepartmentNameHolder(itemBinding)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (this.data[position].header.isNullOrBlank())
            (holder as DepartmentNameHolder).bind(position)
        else
            (holder as DepartmentNameHeaderHolder).bind(position)
//        holder.setIsRecyclable(false)
    }

    override fun getItemViewType(position: Int): Int {
        if (data[position].header.isNullOrBlank())
            return 1
        return 0
    }

    override fun getItemCount(): Int = data.size

    fun fetchUsers(users: MutableList<SimpleUser>) {
        this.users = users
        makeData()
//        notifyDataSetChanged()
    }

    private fun makeData() {
        this.data.clear()
        if (this.users.isEmpty()) {
            notifyDataSetChanged()
            return
        }
        var x: Char = makeInitialKor(this.users[0].fname[0])
        var y: Char
        this.users.forEach {
            y = makeInitialKor(it.fname[0])
            if (data.isEmpty())
                this.data.add(nameData(null, x.toString()))
            if (x != y) {
                x = y
                this.data.add(nameData(null, x.toString()))
            }
            this.data.add(nameData(it, null))
        }
        notifyDataSetChanged()
    }

    private fun makeInitialKor(kor: Char): Char {
        val initialList = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
        val i: Int = (((kor.code - 0xAC00) / 28) / 21) % 19
        return initialList[i]
    }

    inner class DepartmentNameHolder(binding: DptNameItemBinding): RecyclerView.ViewHolder(binding.root) {
        val nameTv: TextView = binding.dptNameTv
        fun bind(position: Int) {
            if (isLogin)
                nameTv.text = this@DepartmentNameAdapter.data[position].user!!.fname + this@DepartmentNameAdapter.data[position].user!!.lname
            else {
                val olname = data[position].user!!.lname
                var lname =""
                for (i in olname)
                    lname += "Ｏ"
                nameTv.text = data[position].user!!.fname + lname
            }
        }
    }

    inner class DepartmentNameHeaderHolder(binding: DptNameItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        val headerTv: TextView = binding.dptNameHeaderTv
        fun bind(position: Int) {
            headerTv.text = this@DepartmentNameAdapter.data[position].header ?: "?"
        }
    }

    fun loginDataChanged(isLogin: Boolean) {
        this.isLogin = isLogin
        notifyDataSetChanged()
    }

}