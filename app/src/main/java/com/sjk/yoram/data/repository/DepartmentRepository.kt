package com.sjk.yoram.data.repository

import android.app.Application
import com.sjk.yoram.data.entity.Position
import com.sjk.yoram.data.entity.RequestUser
import com.sjk.yoram.data.entity.SimpleUser
import com.sjk.yoram.model.*
import com.sjk.yoram.presentation.main.department.Department
import com.sjk.yoram.presentation.main.department.DepartmentNode
import com.sjk.yoram.presentation.main.department.findUser
import com.sjk.yoram.data.repository.retrofit.MyRetrofit
import com.sjk.yoram.presentation.main.department.addList
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

    suspend fun getDepartmentNodeListByName(requestUser: RequestUser): MutableList<DepartmentNode> {
        val response = MyRetrofit.dptmentApi.getDepartmentNodeListByName(requestUser)
        if(!response.isSuccessful)
            return mutableListOf()
        return response.body() ?: mutableListOf()
    }

    suspend fun getDepartmentNodeListByDepartment(requestUser: RequestUser): MutableList<DepartmentNode> {
        val response = MyRetrofit.dptmentApi.getDepartmentNodeListByDepartment(requestUser)
        if(!response.isSuccessful)
            return mutableListOf()
        return response.body() ?: mutableListOf()
    }

    suspend fun getDepartmentNodeListByPosition(requestUser: RequestUser): MutableList<DepartmentNode> {
        val response = MyRetrofit.dptmentApi.getDepartmentNodeListByPosition(requestUser)
        if(!response.isSuccessful)
            return mutableListOf()
        return response.body() ?: mutableListOf()
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

    suspend fun getDtoDepartmentList(): MutableList<com.sjk.yoram.data.entity.Department> {
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

    suspend fun uploadDepartmentList(dptList: List<com.sjk.yoram.data.entity.Department>): Boolean {
        val result = MyRetrofit.dptmentApi.uploadDepartmentList(dptList)
        if (!result.isSuccessful) return false
        return result.body() ?: false
    }

    suspend fun getDtoPositionList(): MutableList<Position> {
        val result = MyRetrofit.dptmentApi.getAllPositions()
        if (!result.isSuccessful) return mutableListOf()
        return result.body() ?: mutableListOf()
    }

    suspend fun getCheckPositionIsUsing(code: Int): Boolean {
        val result = MyRetrofit.dptmentApi.getCheckPositionIsUsing(code)
        if (!result.isSuccessful) return false
        return result.body() ?: false
    }

    suspend fun uploadPositionList(posList: List<Position>): Boolean {
        val result = MyRetrofit.dptmentApi.uploadPositionList(posList)
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