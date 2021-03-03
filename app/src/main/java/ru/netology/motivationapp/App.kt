package ru.netology.motivationapp

import android.app.Application
import ru.netology.motivationapp.db.AppDb
import ru.netology.motivationapp.repository.IPostRepository
import ru.netology.motivationapp.repository.PostRepositorySQLite

class App : Application() {

    companion object {
        lateinit var repositorySQLite: IPostRepository
    }

    override fun onCreate() {
        super.onCreate()
        repositorySQLite = PostRepositorySQLite(
            AppDb.getInstance(applicationContext)
                .postDao()
        )
    }
}