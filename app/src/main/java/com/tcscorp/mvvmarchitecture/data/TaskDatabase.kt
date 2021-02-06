package com.tcscorp.mvvmarchitecture.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tcscorp.mvvmarchitecture.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created on 31/01/2021 09:21
 * @author tcscorp
 */
@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            applicationScope.launch {
                taskDao.insert(Task("Wash the dishes"))
                taskDao.insert(Task("Do the laundry"))
                taskDao.insert(Task("Buy groceries", important = true))
                taskDao.insert(Task("Prepare food", completed = true))
                taskDao.insert(Task("Call Mum"))
                taskDao.insert(Task("Visit grandma", completed = true))
                taskDao.insert(Task("Repair my bike"))
                taskDao.insert(Task("Call Elon Musk"))
            }
        }
    }
}