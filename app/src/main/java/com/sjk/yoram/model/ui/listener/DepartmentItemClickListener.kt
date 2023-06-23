package com.sjk.yoram.model.ui.listener

import android.view.View
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.DepartmentNode

interface DepartmentItemClickListener {
    fun onClick(department: DepartmentNode, arrowView: View, foldableView: View)
}