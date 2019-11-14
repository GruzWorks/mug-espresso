package test.mug.espresso.mainView

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.calculateDistance
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

	var powerMugs = repository.powerMugs

	var markers = Transformations.map(powerMugs) {
		it.asMarkerOptions()
	}

	var lastLocation = MutableLiveData<LatLng>(LatLng(0.0,0.0))

	var powerMugsWithDistance = Transformations.map(powerMugs) { mugs ->
		mugs.map { item ->
			PowerMugWithDistance(
				item.id,
				item.name,
				item.point,
				item.address,
				calculateDistance(lastLocation.value!!, item.point),
				item.numberOfMugs
			)
		}.sortedBy { it.distance }
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
