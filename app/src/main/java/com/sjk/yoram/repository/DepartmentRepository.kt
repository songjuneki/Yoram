package com.sjk.yoram.repository

import android.app.Application
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.MyRetrofit
import io.github.bangjunyoung.KoreanChar
import io.github.bangjunyoung.KoreanTextMatch
import io.github.bangjunyoung.KoreanTextMatcher


class DepartmentRepository(private val application: Application) {
    suspend fun getAllDepartmentsByName(): MutableList<Department> {
        val list = mutableListOf<Department>()
        val simpleList = MyRetrofit.userApi.getAllSimpleUsersByName()
        var title = KoreanChar.getCompatChoseong(simpleList[0].name[0])
        var i = 0
        list.add(Department(title.toString(), title.code))

        simpleList.forEach { user ->
            val _title = KoreanChar.getCompatChoseong(user.name[0])
            if (title.code == _title.code) {
                list[i].users.add(user)
                list[i].isExpanded = true
                list[i].count = list[i].users.size
            } else {
                title = _title
                list[i].count = list[i].users.size
                i++
                list.add(Department(_title.toString(), _title.code))
                list[i].users.add(user)
                list[i].isExpanded = true
            }
        }
        return list
    }

    suspend fun getAllParentDepartments(): MutableList<Department> {
        val dtoList = MyRetrofit.dptmentApi.getAllTopDepartments()
        val list = mutableListOf<Department>()
        dtoList.forEach {
            list.add(Department(it))
        }
        return list
    }

    companion object {
        private var instance: DepartmentRepository? = null
        fun getInstance(application: Application): DepartmentRepository? {
            if (instance == null) instance = DepartmentRepository(application)
            return instance
        }
    }
}