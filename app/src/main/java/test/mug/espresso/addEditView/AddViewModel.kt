package test.mug.espresso.addEditView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import test.mug.espresso.domain.PowerMug

class AddViewModel(powerMug: PowerMug?) : ViewModel() {
	var selectedPlace = MutableLiveData<PowerMug?>()

	var lastLocation = MutableLiveData<LatLng>(LatLng(0.0,0.0))

	private var _saveData = MutableLiveData<Boolean>()
	val saveData: LiveData<Boolean>
		get() = _saveData

	lateinit var currentMarker: Marker

	init {
		selectedPlace.value = powerMug
	}

	fun saveData() {
		_saveData.value = true
	}

	fun savedData() {
		_saveData.value = false
	}

	class Factory(val powerMug: PowerMug?) : ViewModelProvider.Factory {
		@Suppress("unchecked_cast")
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
				return AddViewModel(powerMug) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
