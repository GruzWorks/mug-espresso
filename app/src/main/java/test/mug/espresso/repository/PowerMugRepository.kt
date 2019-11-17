package test.mug.espresso.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import test.mug.espresso.database.*
import test.mug.espresso.domain.PowerMug
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PowerMugRepository(private val database: PowerMugDatabase) {
	val powerMugs: LiveData<List<PowerMug>> = Transformations.map(database.powerMugDatabaseDao.getAll()) {
		it.asDomainModel()
	}

	suspend fun refreshCache() {
		Timber.i("Run refresh cache")
		withContext(Dispatchers.IO) {
//			for (i in 0..39) {
//				createRandomEntryNearWroclaw()
//				createRandomEntryNearWarsaw()
//			}
		}
	}

	suspend fun updatePlace(powerMug: PowerMug) {
		Timber.i("Run update place")
		withContext(Dispatchers.IO) {
			database.powerMugDatabaseDao.update(powerMug.asDbModel())
		}
	}

	fun returnPlace(key: Long) : PowerMug? {
		for (item in powerMugs.value!!) {
			if (item.id == key) {
				return item//.copy()
			}
		}
		return null
	}

	private val names = arrayOf("CH Unicornia", "Burgers DP.", "SFT", "NdGonlas", "CH Wroclaw", "Dworzec", "Przystanek")
	private val addresses = arrayOf("ul. Zielonogorska 42, 51-555 Wrocław", "ul. Zielińskiego 26, 53-534 Wrocław", "ul. Jugosłowiańska 112, 51-112 Wrocław", "ul. Seweryna Goszczyńskiego 295, 51-138 Wrocław", "ul. Marii Konopnickiej 50-40, 51-141 Wrocław", "ul. Kornela Makuszyńskiego 8, 51-142 Wrocław", "ul. Mariana Falskiego 1-21, 51-173 Wrocław", "ul. Torfowa 15, 51-170 Wrocław", "ul. Przejazdowa 7, 51-167 Wrocław")
	private val addressesWarsaw = arrayOf("ul. Zielonogorska 42, 00-555 Warszawa", "ul. Zielińskiego 26, 00-534 Warszawa", "ul. Jugosłowiańska 112, 00-112 Warszawa", "ul. Seweryna Goszczyńskiego 295, 00-138 Warszawa", "ul. Marii Konopnickiej 50-40, 00-141 Warszawa", "ul. Kornela Makuszyńskiego 8, 00-142 Warszawa", "ul. Mariana Falskiego 1-21, 00-173 Warszawa", "ul. Torfowa 15, 00-170 Warszawa", "ul. Przejazdowa 7, 00-167 Warszawa")

	private fun createRandomEntryNearWroclaw() {
		val x = DbPowerMug(
			0,
			names[Random().nextInt(names.size)],
			51.05 + Random().nextDouble()*15/100,
			16.81 + Random().nextDouble()*36/100,
			addresses[Random().nextInt(addresses.size)] + "; " + SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US).format(Date()),
			0 + Random().nextInt(15)
		)
		database.powerMugDatabaseDao.insert(x)
	}

	private fun createRandomEntryNearWarsaw() {
		val x = DbPowerMug(
			0,
			names[Random().nextInt(names.size)] + " Warsaw",
			52.10 + Random().nextDouble()*27/100,
			20.85 + Random().nextDouble()*40/100,
			addressesWarsaw[Random().nextInt(addressesWarsaw.size)] + "; " + SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US).format(Date()),
			0 + Random().nextInt(15)
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
