package com.sjk.yoram.model.adapter

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sjk.yoram.model.DptButton
import com.sjk.yoram.model.DptButtonType

class HomeDptRecycleAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = mutableListOf<DptButton>()
    var itemListener: CardAdapter.OnDptItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //val inflater = LayoutInflater.from(parent.context)
        val button = MaterialButton(parent.context)
        return DptmentButtonHolder(button, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DptmentButtonHolder).bind(data[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = data.size

    inner class DptmentButtonHolder(val button: MaterialButton, val parent: ViewGroup): RecyclerView.ViewHolder(button) {
        fun bind(data: DptButton) {
            var txt: String
            when(data.type) {
                DptButtonType.DEPARTMENT -> {
                    txt = "부서별\n"
                    button.setBackgroundColor(Color.parseColor("#00696B"))
                }
                DptButtonType.POSITION -> {
                    txt = "직급별\n"
                    button.setBackgroundColor(Color.parseColor("#0096B6"))
                }
                DptButtonType.NAME -> {
                    txt = "이름별"
                    button.setBackgroundColor(Color.parseColor("#7343BA"))
                }
            }
            button.text = txt + data.name
            button.cornerRadius = 64
            button.isClickable = true

            button.setOnClickListener {
                // department button click event
                if (data.code == null)
                    itemListener!!.onItemClick(data.type, 0)
                else
                    itemListener!!.onItemClick(data.type, data.code!!)
            }
        }
    }


    fun fetchData(data: MutableList<DptButton>) {
        this.data = data
        notifyDataSetChanged()
    }

}