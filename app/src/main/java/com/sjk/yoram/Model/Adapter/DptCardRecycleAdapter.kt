package com.sjk.yoram.Model.Adapter

import android.animation.LayoutTransition
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.util.DisplayMetrics
import android.util.Log
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
import coil.load
import coil.size.Dimension
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DepartmentV2
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.Model.dto.User
import com.sjk.yoram.R
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

data class dptSubData (val child: Department?, val users: User?, val type: dptSubDataType)
enum class dptSubDataType { CHILD, USER }

class DptCardRecycleAdapter(val departments: MutableList<Department>, val dpt: Department): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            innerParam.setMargins(dp_4.toInt())
            innerParam.gravity = Gravity.CENTER
            img.layoutParams = innerParam
            val dp_30 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, rowLayout.context.resources.displayMetrics)

            img.load(R.drawable.ic_baseline_face_24) {
                size(dp_30.toInt(), dp_30.toInt())
            }
            img.imageTintMode = PorterDuff.Mode.DARKEN

            val name = TextView(rowLayout.context)
            name.layoutParams = innerParam
            name.setTextColor(Color.BLACK)
            name.setTextSize(androidx.annotation.Dimension.SP, 18F)
            name.text = items[position].users!!.Fname + items[position].users!!.Lname

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
            param.setMargins(dp_8.toInt())
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.layoutParams = param

            val innerParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            innerParam.setMargins(dp_4.toInt())
            innerParam.gravity = Gravity.CENTER
            val img = ImageView(rowLayout.context)
            val dp_30 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                30F,
                rowLayout.context.resources.displayMetrics
            )
            img.load(R.drawable.ic_baseline_perm_contact_calendar_24) {
                size(dp_30.toInt())
            }
            img.layoutParams = innerParam

            val title = TextView(rowLayout.context)
            title.text = sub.name
            title.setTextColor(Color.BLACK)
            title.setTextSize(androidx.annotation.Dimension.SP, 20F)
            title.layoutParams = innerParam

            val arrow = ImageView(rowLayout.context)
            arrow.load(R.drawable.ic_baseline_arrow_drop_down_24) {
                size(dp_30.toInt())
            }
            arrow.layoutParams = innerParam


            val recycler = RecyclerView(layout.context)
            val manager = LinearLayoutManager(layout.context)
            val adapter = DptCardRecycleAdapter(departments, items[position].child!!)
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

}