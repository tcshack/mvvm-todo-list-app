package com.tcscorp.mvvmarchitecture.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/**
 * Created on 31/01/2021 00:02
 * @author tcscorp
 */
@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Long ? = null
) : Parcelable {
    val createdAtFormattedDate: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}
