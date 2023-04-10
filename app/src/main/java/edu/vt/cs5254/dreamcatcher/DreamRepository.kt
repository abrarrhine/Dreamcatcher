package edu.vt.cs5254.dreamcatcher

import android.content.Context
import androidx.room.Room
import edu.vt.cs5254.dreamcatcher.database.DreamDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

private const val DATABASE_NAME = "dream-database"

class DreamRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
){

private val database: DreamDatabase = Room.databaseBuilder(
        context.applicationContext,
        DreamDatabase::class.java,
        DATABASE_NAME
    ).createFromAsset(DATABASE_NAME).build()

    companion object{
        private var INSTANCE: DreamRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = DreamRepository(context)
            }
        }

        fun get(): DreamRepository{
            return INSTANCE?:
            throw IllegalStateException("DreamRepository must be initialized")
        }
    }

    fun getDreams(): Flow<List<Dream>> {
        return database.dreamDao().getDreams().map { dreamMap ->
            dreamMap.keys.map { dream ->
                dream.apply { entries = dreamMap.getValue(dream) }
            }
        }
    }

    suspend fun getDream(id: UUID): Dream {
        return database.dreamDao().getDreamAndEntries(id)
    }


    fun updateDream(dream:Dream){
        coroutineScope.launch {
            database.dreamDao().updateDreamAndEntries(dream)
        }
    }

    suspend fun addDream(dream: Dream) {
        database.dreamDao().insertDreamAndEntries(dream)
    }

    suspend fun deleteDream(dream: Dream) {
        database.dreamDao().deleteDreamAndEntries(dream)
    }
}