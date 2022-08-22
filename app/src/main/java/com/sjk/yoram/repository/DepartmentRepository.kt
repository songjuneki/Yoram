package com.sjk.yoram.repository

import android.app.Application
import android.util.Log
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.addList
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.dto.User
import com.sjk.yoram.model.findUser
import io.github.bangjunyoung.KoreanChar
import io.github.bangjunyoung.KoreanTextMatch
import io.github.bangjunyoung.KoreanTextMatcher


class DepartmentRepository(private val application: Application) {
    suspend fun searchUserByName(name: String): MutableList<SimpleUser> {
        val all = getAllDepartmentsByName()
        return all.findUser(name)
    }

    suspend fun searchUserByNumber(number: String): MutableList<SimpleUser> {
        val list = mutableListOf<SimpleUser>()
        val all = getAllDepartmentsByName()

        return list
    }

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

    suspend fun getAllDepartmentsByDepartment(): MutableList<Department> {
        val list = mutableListOf<Department>()
        val root = MyRetrofit.dptmentApi.getAllTopDepartments()
        list.addList(root)

        list.forEach {
            it.childDepartment = getChildDepartments(it.code)
            it.users = getDepartmentsUsers(it.code)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getChildDepartments(code: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val child = MyRetrofit.dptmentApi.getChildDepartments(code)
        list.addList(child)

        list.forEach {
            it.childDepartment = getChildDepartments(it.code)
            it.users = getDepartmentsUsers(it.code)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getDepartmentsUsers(code: Int): MutableList<SimpleUser> {
        val users = MyRetrofit.userApi.getSimpleUsersDepartment(code)
        return if (users.isNullOrEmpty())
            mutableListOf()
        else
            users
    }

    suspend fun getDepartmentsByPosition(): MutableList<Department> {
        val list = mutableListOf<Department>()
        val root = MyRetrofit.dptmentApi.getAllTopPositions()
        list.addList(root)

        list.forEach {
            it.childDepartment = getChildPositions(it.code)
            it.users = getPositionsUsers(it.code)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getChildPositions(code: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val child = MyRetrofit.dptmentApi.getChildPosition(code)
        list.addList(child)

        list.forEach {
            it.childDepartment = getChildPositions(it.code)
            it.users = getPositionsUsers(it.code)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getPositionsUsers(code: Int): MutableList<SimpleUser> {
        val users = MyRetrofit.userApi.getSimpleUsersPosition(code)
        return if (users.isNullOrEmpty())
            mutableListOf()
        else
            users
    }

    companion object {
        private var instance: DepartmentRepository? = null
        fun getInstance(application: Application): DepartmentRepository? {
            if (instance == null) instance = DepartmentRepository(application)
            return instance
        }
    }
}