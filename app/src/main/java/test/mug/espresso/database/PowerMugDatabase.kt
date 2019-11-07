package test.mug.espresso.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
	entities = [DbPowerMug::class],
	version = 1,
	exportSchema = false
) // TODO: check exportSchema var and db versioning
abstract class PowerMugDatabase : RoomDatabase() {
	abstract val powerMugDatabaseDao: PowerMugDatabaseDao
}

private lateinit var INSTANCE: PowerMugDatabase

fun getDatabase(context: Context): PowerMugDatabase {
	synchronized(PowerMugDatabase::class.java) {
		if (!::INSTANCE.isInitialized) {
			INSTANCE = Room.databaseBuilder(context.applicationContext,
				PowerMugDatabase::class.java,
				"PowerMugsDatabase").build()
		}
	}

	return INSTANCE
}
