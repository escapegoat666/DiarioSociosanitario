package com.diariosociosanitario.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VisitaEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun visitaDao(): VisitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "visitas_db"
                ).fallbackToDestructiveMigration() // ✅ Esto borra y recrea la BD si hay cambios
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}