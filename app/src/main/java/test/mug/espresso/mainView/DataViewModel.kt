package test.mug.espresso.mainView

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.calculateDistance
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.domain.PowerMugWithDistance
import test.mug.espresso.repository.getRepository
import timber.log.Timber

class DataViewModel(application: Application) : AndroidViewModel(application) {

	private val viewModelJob = SupervisorJob()

	private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

	private val repository = getRepository(application)

	private var _navigateToSecondView = MutableLiveData<Boolean>()
	val navigateToSecondView: LiveData<Boolean>
		get() = _navigateToSecondView

	private var _navigateToAddView = MutableLiveData<Boolean>()
	val navigateToAddView: LiveData<Boolean>
		get() = _navigateToAddView

	init {
		viewModelScope.launch {
			repository.refreshCache()
		}
		Timber.v("DataViewModel Created")
	}

	var powerMugs = repository.powerMugs
	var searchResults: LiveData<List<PowerMug>>? = null

	var lastLocation = MutableLiveData<LatLng>(LatLng(0.0, 0.0))

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

	fun goToAddView() {
		_navigateToAddView.value = true
	}

	fun wentToAddView() {
		_navigateToAddView.value = false
	}

	fun refreshDistance() {
		powerMugsWithDistance.value?.forEach { item ->
			item.distance = calculateDistance(lastLocation.value ?: LatLng(0.0, 0.0), item.point)
		}
		powerMugsWithDistance.value?.sortedBy { it.distance }
	}

	fun search(query: String) {
		searchResults = repository.search(query)
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
