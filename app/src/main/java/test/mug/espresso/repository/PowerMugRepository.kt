package test.mug.espresso.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import test.mug.espresso.database.*
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.network.Network
import test.mug.espresso.network.asDatabaseModel
import test.mug.espresso.network.asEphemeralNetworkModel
import test.mug.espresso.network.asNetworkModel
import timber.log.Timber

class PowerMugRepository(private val database: PowerMugDatabase) {
	private var status = true

	val powerMugs: LiveData<List<PowerMug>> =
		Transformations.map(database.powerMugDatabaseDao.getAll()) {
			it.asDomainModel()
		}

	suspend fun refreshCache() {
		Timber.v("Run refresh cache")
		withContext(Dispatchers.IO) {
			try {
				val mugs = Network.server.getMugs().await()
				database.powerMugDatabaseDao.clear()
				database.powerMugDatabaseDao.insertAll(*mugs.asDatabaseModel())
			} catch (e: Throwable) {
				Timber.w(e)
				Timber.w("No connection to server!")
			}
		}
	}

	suspend fun updatePlace(powerMug: PowerMug): Boolean {
		Timber.v("Run update place")
		withContext(Dispatchers.IO) {
			try {
				val updatedMug = Network.server.updateMug(powerMug.asNetworkModel()).await()
				database.powerMugDatabaseDao.update(updatedMug.asDatabaseModel())
				status = true
			} catch (e: Throwable) {
				Timber.w(e)
				Timber.w("No connection to server!")
				status = false
			}
		}
		return status
	}

	suspend fun insertPlace(powerMug: PowerMug): Boolean {
		Timber.v("Run insert place")
		withContext(Dispatchers.IO) {
			try {
				val insertedMug = Network.server.insertMug(powerMug.asEphemeralNetworkModel()).await()
				database.powerMugDatabaseDao.insert(insertedMug.asDatabaseModel())
				status = true
			} catch (e: Throwable) {
				Timber.w(e)
				Timber.w("No connection to server!")
				status = false
			}
		}
		return status
	}

	suspend fun deletePlace(powerMug: PowerMug): Boolean {
		Timber.v("Run delete place")
		withContext(Dispatchers.IO) {
			try {
				val result = Network.server.deleteMug(powerMug.asNetworkModel()).await()
				database.powerMugDatabaseDao.delete(powerMug.asDbModel())
				status = true
			} catch (e: Throwable) {
				Timber.w(e)
				Timber.w("No connection to server!")
				status = false
			}
		}
		return status
	}

	fun search(query: String): LiveData<List<PowerMug>> {
		@Suppress("NAME_SHADOWING")
		val query = "%$query%"
		Timber.v("Run search place: $query")
		return Transformations.map(database.powerMugDatabaseDao.searchFor(query)) {
			it.asDomainModel()
		}
	}

	fun returnPlace(key: Long): PowerMug? {
		for (item in powerMugs.value!!) {
			if (item.id == key) {
				return item
			}
		}
		return null
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
