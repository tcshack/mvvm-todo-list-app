package com.tcscorp.mvvmarchitecture.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tcscorp.mvvmarchitecture.data.TaskDao
import com.tcscorp.mvvmarchitecture.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created on 31/01/2021 09:25
 * @author tcscorp
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callBack: TaskDatabase.Callback
    ): TaskDatabase = Room
        .databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callBack)
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope