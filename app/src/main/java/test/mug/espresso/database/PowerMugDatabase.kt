package test.mug.espresso.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
	entities = [PowerMug::class],
	version = 1,
	exportSchema = false
) // TODO: check exportSchema var and db versioning
abstract class PowerMugDatabase : RoomDatabase() {
	abstract val powerMugDatabaseDao: PowerMugDatabaseDao

	companion object {
		@Volatile
		private var INSTANCE: PowerMugDatabase? = null

		fun getInstance(context: Context): PowerMugDatabase {
			synchronized(this) {
				var instance = INSTANCE

				if (instance == null) {
					instance = Room.databaseBuilder(
						context.applicationContext,
						PowerMugDatabase::class.java,
						"PowerMugsDatabase"
					).fallbackToDestructiveMigration().build()
					// TODO: check fallbackToDestructiveMigration and db versioning

					INSTANCE = instance
				}

				return instance
			}
		}
	}
}
