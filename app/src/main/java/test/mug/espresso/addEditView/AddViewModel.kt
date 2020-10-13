package test.mug.espresso.addEditView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.ThreeState
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.repository.PowerMugRepository

class AddViewModel(private val repository: PowerMugRepository, powerMug: PowerMug?) : ViewModel() {
	private val viewModelJob = SupervisorJob()

	private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

	var selectedPlace = MutableLiveData<PowerMug?>()

	var lastLocation = MutableLiveData<LatLng>(LatLng(0.0, 0.0))

	private var _saveData = MutableLiveData<Boolean>()
	val saveData: LiveData<Boolean>
		get() = _saveData

	private var _addedPlace = MutableLiveData<ThreeState>(ThreeState.UNSET)
	val addedPlace: LiveData<ThreeState>
		get() = _addedPlace

	lateinit var currentMarker: Marker

	init {
		selectedPlace.value = powerMug
	}

	fun updateDb() {
		viewModelScope.launch {
			if (repository.updatePlace(selectedPlace.value!!)) {
				_addedPlace.value = ThreeState.TRUE
			} else {
				_addedPlace.value = ThreeState.FALSE
			}
		}
	}

	fun insertToDb() {
		viewModelScope.launch {
			if (repository.insertPlace(selectedPlace.value!!)) {
				_addedPlace.value = ThreeState.TRUE
			} else {
				_addedPlace.value = ThreeState.FALSE
			}
		}
	}

	fun addingHandled() {
		_addedPlace.value = ThreeState.UNSET
	}

	fun saveData() {
		_saveData.value = true
	}

	fun savedData() {
		_saveData.value = false
	}

	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
	}

	class Factory(val repository: PowerMugRepository, val powerMug: PowerMug?) :
		ViewModelProvider.Factory {
		@Suppress("unchecked_cast")
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
				return AddViewModel(repository, powerMug) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
