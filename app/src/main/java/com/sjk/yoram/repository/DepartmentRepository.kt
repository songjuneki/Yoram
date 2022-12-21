package com.sjk.yoram.repository

import android.app.Application
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Position
import com.sjk.yoram.model.dto.SimpleUser
import io.github.bangjunyoung.KoreanChar


class DepartmentRepository(private val application: Application) {
    suspend fun searchUserByName(name: String, request: Int): MutableList<SimpleUser> {
        val all = getAllDepartmentsByName(request)
        return all.findUser(name)
    }

    suspend fun searchUserByNumber(number: String, request: Int): MutableList<SimpleUser> {
        val list = mutableListOf<SimpleUser>()
        val all = getAllDepartmentsByName(request)

        return list
    }

    suspend fun getAllDepartmentsByName(request: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val simpleList = MyRetrofit.userApi.getAllSimpleUsersByName(request)
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

    suspend fun getAllDepartmentsByDepartment(request: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val root = MyRetrofit.dptmentApi.getAllTopDepartments()
        list.addList(root)

        list.forEach {
            it.childDepartment = getChildDepartments(it.code, request)
            it.users = getDepartmentsUsers(it.code, request)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getChildDepartments(code: Int, request: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val child = MyRetrofit.dptmentApi.getChildDepartments(code)
        list.addList(child)

        list.forEach {
            it.childDepartment = getChildDepartments(it.code, request)
            it.users = getDepartmentsUsers(it.code, request)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getDepartmentsUsers(code: Int, request: Int): MutableList<SimpleUser> {
        val users = MyRetrofit.userApi.getSimpleUsersDepartment(code, request)
        return if (users.isNullOrEmpty())
            mutableListOf()
        else
            users
    }

    suspend fun getDepartmentsByPosition(request: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val root = MyRetrofit.dptmentApi.getAllTopPositions()
        list.addList(root)

        list.forEach {
            it.childDepartment = getChildPositions(it.code, request)
            it.users = getPositionsUsers(it.code, request)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getChildPositions(code: Int, request: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val child = MyRetrofit.dptmentApi.getChildPosition(code)
        list.addList(child)

        list.forEach {
            it.childDepartment = getChildPositions(it.code, request)
            it.users = getPositionsUsers(it.code, request)
            it.getAllUserSize()
        }
        return list
    }

    private suspend fun getPositionsUsers(code: Int, request: Int): MutableList<SimpleUser> {
        val users = MyRetrofit.userApi.getSimpleUsersPosition(code, request)
        return if (users.isNullOrEmpty())
            mutableListOf()
        else
            users
    }

    suspend fun getDepartmentList(): MutableList<Department> {
        val list = mutableListOf<Department>()
        val top = MyRetrofit.dptmentApi.getAllTopDepartments()

        list.addList(top)

        list.forEach {
            it.childDepartment = getChildDepartmentList(it.code)
            it.isExpanded = false
        }
        return list
    }

    private suspend fun getChildDepartmentList(code: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val top = MyRetrofit.dptmentApi.getChildDepartments(code)

        list.addList(top)

        list.forEach {
            it.childDepartment = getChildDepartmentList(it.code)
            it.isExpanded = false
        }
        return list
    }

    suspend fun getPositionByCode(code: Int): Position {
        return MyRetrofit.dptmentApi.getPosition(code)
    }

    suspend fun getPositionList(): MutableList<Department> {
        val list = mutableListOf<Department>()
        val top = MyRetrofit.dptmentApi.getAllTopPositions()

        list.addList(top)

        list.forEach {
            it.childDepartment = getChildPositionList(it.code)
            it.isExpanded = false
        }
        return list
    }

    private suspend fun getChildPositionList(parent: Int): MutableList<Department> {
        val list = mutableListOf<Department>()
        val childs = MyRetrofit.dptmentApi.getChildPosition(parent)

        list.addList(childs)
        list.forEach {
            it.childDepartment = getChildPositionList(it.code)
            it.isExpanded = false
        }
        return list
    }

    suspend fun getDtoDepartmentList(): MutableList<com.sjk.yoram.model.dto.Department> {
        val list = MyRetrofit.dptmentApi.getAllDepartmentList()
        if (!list.isSuccessful) {
            return mutableListOf()
        }
        return list.body() ?: mutableListOf()
    }

    suspend fun getCheckDepartmentIsUsing(code: Int): Boolean {
        val result = MyRetrofit.dptmentApi.getCheckDepartmentIsUsing(code)
        if (!result.isSuccessful) {
            return false
        }
        return result.body() ?: false
    }

    suspend fun uploadDepartmentList(dptList: List<com.sjk.yoram.model.dto.Department>): Boolean {
        val result = MyRetrofit.dptmentApi.uploadDepartmentList(dptList)
        if (!result.isSuccessful) return false
        return result.body() ?: false
    }

    companion object {
        private var instance: DepartmentRepository? = null
        fun getInstance(application: Application): DepartmentRepository? {
            if (instance == null) instance = DepartmentRepository(application)
            return instance
        }
    }
}