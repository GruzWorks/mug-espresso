package test.mug.espresso.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import test.mug.espresso.database.DbPowerMug
import test.mug.espresso.database.PowerMugDatabase
import test.mug.espresso.database.asDomainModel
import test.mug.espresso.database.getDatabase
import test.mug.espresso.domain.PowerMug
import java.text.SimpleDateFormat
import java.util.*

class PowerMugRepository(private val database: PowerMugDatabase) {
	val powerMugs: LiveData<List<PowerMug>> = Transformations.map(database.powerMugDatabaseDao.getAll()) {
		it.asDomainModel()
	}

	suspend fun refreshCache() {
		withContext(Dispatchers.IO) {
			//createRandomEntryNearWroclaw()
		}
	}

	fun returnPlace(key: Long) : PowerMug? {
		return database.powerMugDatabaseDao.get(key)?.asDomainModel()
	}

	private fun createRandomEntryNearWroclaw() {
		val x = DbPowerMug(
			0,
			"Test " + SimpleDateFormat("dd/M/yyyy hh:mm:ss").toString(),
			51 + Random().nextDouble(),
			17 + Random().nextDouble(),
			"Testtset",
			Random().nextInt()
		)
		database.powerMugDatabaseDao.insert(x)
	}
}

private lateinit var INSTANCE: PowerMugRepository

fun getRepository(context: Context): PowerMugRepository {
	synchronized(PowerMugDatabase::class.java) {
		if (!::INSTANCE.isInitialized) {
			val db = getDatabase(context)
			INSTANCE = PowerMugRepository(db)
		}
	}

	return INSTANCE
}
