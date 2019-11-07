package test.mug.espresso.repository

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import test.mug.espresso.database.PowerMugDatabase
import test.mug.espresso.database.asDomainModel
import test.mug.espresso.domain.PowerMug
import timber.log.Timber

class PowerMugRepository(database: PowerMugDatabase) {
	val powerMugs: LiveData<List<PowerMug>> = Transformations.map(database.powerMugDatabaseDao.getAll()) {
		it.asDomainModel()
	}

	suspend fun refreshCache() {
		withContext(Dispatchers.IO) {
			Timber.i("Db refreshed")
		}
	}
}
