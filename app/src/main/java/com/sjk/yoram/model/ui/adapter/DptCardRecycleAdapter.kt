package com.sjk.yoram.model.ui.adapter

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.R
import java.lang.IllegalArgumentException

data class dptSubData (val child: Department?, val users: SimpleUser?, val type: dptSubDataType)
enum class dptSubDataType { CHILD, USER }

class DptCardRecycleAdapter(val departments: MutableList<Department>, val dpt: Department, val parentPos: Int, val isLogin: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isUser = false
    var cardListener: DepartmentCardAdapter.onDptSubClickListener? = null
    val items: MutableList<dptSubData> = mutableListOf()

    init {
        val childs = departments.filter { it.parentCode == dpt.code }
        childs.forEach { items.add(dptSubData(it, null, dptSubDataType.CHILD)) }
        dpt.users.forEach { items.add(dptSubData(null, it, dptSubDataType.USER)) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = LinearLayout(parent.context)
        var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val dp_8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, parent.context.resources.displayMetrics)
        param.setMargins(dp_8.toInt())
        layout.layoutParams = param
        layout.orientation = LinearLayout.VERTICAL

        return when (viewType) {
            dptSubDataType.CHILD.ordinal -> DptSubHolder(layout)
            dptSubDataType.USER.ordinal -> DptUserHolder(layout)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (items[position].type) {
                dptSubDataType.CHILD -> (holder as DptSubHolder).bind(position)
                dptSubDataType.USER -> (holder as DptUserHolder).bind(position)
            }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when(items[position].type) {
            dptSubDataType.CHILD -> dptSubDataType.CHILD.ordinal
            dptSubDataType.USER -> dptSubDataType.USER.ordinal
        }
    }


    inner class DptUserHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout) {
        fun bind(position: Int) {
            val rowLayout = LinearLayout(layout.context)
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val dp_8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, rowLayout.context.resources.displayMetrics)
            val dp_4 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, rowLayout.context.resources.displayMetrics)

            param.setMargins(dp_8.toInt())
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = param

            layout.addView(rowLayout)

            val img = ImageView(rowLayout.context)
            val innerParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val dp_30 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, rowLayout.context.resources.displayMetrics)
            innerParam.setMargins(dp_4.toInt())
            innerParam.gravity = Gravity.CENTER
            innerParam.width = dp_30.toInt()
            innerParam.height = dp_30.toInt()

            img.layoutParams = innerParam
            img.setImageResource(R.drawable.ic_baseline_face_24)

            val name = TextView(rowLayout.context)
            innerParam.width = LinearLayout.LayoutParams.WRAP_CONTENT
            innerParam.height = LinearLayout.LayoutParams.WRAP_CONTENT

            name.layoutParams = innerParam
            name.setTextColor(Color.BLACK)
            name.setTextSize(androidx.annotation.Dimension.SP, 18F)
            if (isLogin)
                name.text = items[position].users!!.name
            else {
                val olname = items[position].users!!.name
                var lname = ""
                for (i in olname)
                    lname += "Ｏ"
                name.text = items[position].users!!.name
            }

            rowLayout.addView(img)
            rowLayout.addView(name)
        }

    }

    inner class DptSubHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout) {
        fun bind(position: Int) {
            val sub = items[position].child!!

            val rowLayout = LinearLayout(layout.context)
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val dp_8 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8F,
                rowLayout.context.resources.displayMetrics
            )
            val dp_4 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4F,
                rowLayout.context.resources.displayMetrics
            )
            val dp_30 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                30F,
                rowLayout.context.resources.displayMetrics
            )

            param.setMargins(dp_8.toInt())
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = param

            val img30Param = LinearLayout.LayoutParams(
                dp_30.toInt(),
                dp_30.toInt()
            )
            img30Param.setMargins(dp_4.toInt())
            img30Param.gravity = Gravity.CENTER

            val wrapParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            wrapParam.setMargins(dp_4.toInt())
            wrapParam.gravity = Gravity.CENTER

            val img = ImageView(rowLayout.context)

            img.layoutParams = img30Param
            img.setImageResource(R.drawable.ic_baseline_perm_contact_calendar_24)


            val title = TextView(rowLayout.context)
            title.text = sub.name
            title.setTextColor(Color.BLACK)
            title.setTextSize(androidx.annotation.Dimension.SP, 20F)
            title.layoutParams = wrapParam

            val arrow = ImageView(rowLayout.context)
            arrow.layoutParams = img30Param
            arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)


            val recycler = RecyclerView(layout.context)
            val manager = LinearLayoutManager(layout.context)
            val adapter = DptCardRecycleAdapter(departments, items[position].child!!, position, isLogin)
            manager.orientation = LinearLayoutManager.VERTICAL
            recycler.layoutManager = manager
            recycler.adapter = adapter

            rowLayout.addView(img)
            rowLayout.addView(title)
            rowLayout.addView(arrow)

            rowLayout.setOnClickListener { view ->
                val show = toggleLayout(sub.isExpanded, arrow, recycler)
                cardListener!!.onDptSubClick(sub.code)
                notifyItemChanged(adapterPosition)
//                cardListener!!.onDptParentNotify(parentPos)
            }

            layout.addView(rowLayout)
            layout.addView(recycler)
            if (sub.isExpanded) {
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

    fun userChange(isUser: Boolean) {
        this.isUser = isUser
        notifyDataSetChanged()
    }

}