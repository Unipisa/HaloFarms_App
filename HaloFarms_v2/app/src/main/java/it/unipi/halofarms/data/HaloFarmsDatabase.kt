package it.unipi.halofarms.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.map.MapDao
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointDao
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.point.PointValueDao
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.data.sample.SampleDao


@Database(entities = [Map::class, PointValue::class, PerimeterPoint::class, Sample::class], version = 6, exportSchema = false)
abstract class HaloFarmsDatabase : RoomDatabase() {

    abstract fun mapDao(): MapDao
    abstract fun pointValueDao(): PointValueDao
    abstract fun perimeterPointDao(): PerimeterPointDao
    abstract fun sampleDao(): SampleDao

    companion object {
        @Volatile
        private var INSTANCE: HaloFarmsDatabase? = null

        fun getInstance(context: Context): HaloFarmsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HaloFarmsDatabase::class.java,
                        "halofarms_database")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}