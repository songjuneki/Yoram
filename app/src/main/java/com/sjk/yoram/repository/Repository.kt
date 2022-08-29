package com.sjk.yoram.repository

import android.app.Application

class Repository(private val application: Application) {

    companion object {
        private var instance: Repository? = null
        fun getInstance(application: Application): Repository? {
            if (instance == null) instance = Repository(application)
            return instance
        }
    }
}