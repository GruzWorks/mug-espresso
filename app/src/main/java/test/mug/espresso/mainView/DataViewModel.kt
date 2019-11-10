package test.mug.espresso.mainView

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.database.getDatabase
import test.mug.espresso.domain.PowerMugWithDistance
import test.mug.espresso.domain.asMarkerOptions
import test.mug.espresso.repository.PowerMugRepository
import timber.log.Timber
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DataViewModel(application: Application) : AndroidViewModel(application) {

	private val viewModelJob = SupervisorJob()

	private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

	private val database = getDatabase(application)
	private val repository = PowerMugRepository(database)

	private var _navigateToSecondView = MutableLiveData<Boolean>()
	val navigateToSecondView: LiveData<Boolean>
		get() = _navigateToSecondView

	/**
	 * init{} is called immediately when this ViewModel is created.
	 */
	init {
		viewModelScope.launch {
			repository.refreshCache()
		}
		Timber.i("ViewModelCreated")
	}

	val powerMugs = repository.powerMugs

	val markers = Transformations.map(powerMugs) {
		it.asMarkerOptions()
	}

	val lastLocation = MutableLiveData<LatLng>(LatLng(0.0,0.0))

	val powerMugsWithDistance = Transformations.map(powerMugs) { mugs ->
		mugs.map { item ->
			PowerMugWithDistance(
				item.id,
				item.name,
				item.point,
				item.address,
				calculateDistance(item.point),
				item.numberOfMugs
			)
		}.sortedBy { it.distance }
	}

	private fun calculateDistance(point: LatLng) : Double {
		val earthRadiusKm = 6371

		val dLat = degreesToRadians((lastLocation.value?.latitude ?: 0.0) - point.latitude)
		val dLon = degreesToRadians((lastLocation.value?.longitude ?: 0.0) - point.longitude)

		val lat1 = degreesToRadians((lastLocation.value?.latitude ?: 0.0))
		val lat2 = degreesToRadians(point.latitude)

		val a = sin(dLat/2) * sin(dLat/2) + sin(dLon/2) * sin(dLon/2) * cos(lat1) * cos(lat2)
		val c = 2 * atan2(sqrt(a), sqrt(1-a))
		return earthRadiusKm * c
	}

	private fun degreesToRadians(degrees: Double) : Double {
		return degrees * Math.PI / 180
	}

	/**
	 * Cancel all coroutines when the ViewModel is cleared
	 */
	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
	}

	fun goToSecondView() {
		_navigateToSecondView.value = true
	}

	fun wentToSecondView() {
		_navigateToSecondView.value = false
	}

	class Factory(val app: Application) : ViewModelProvider.Factory {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
				@Suppress("UNCHECKED_CAST")
				return DataViewModel(app) as T
			}
			throw IllegalArgumentException("Unable to construct viewmodel")
		}
	}
}
