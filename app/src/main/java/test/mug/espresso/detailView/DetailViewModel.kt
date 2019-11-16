package test.mug.espresso.detailView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import test.mug.espresso.domain.PowerMug

class DetailViewModel(powerMug: PowerMug) : ViewModel() {
	private val _selectedPlace = MutableLiveData<PowerMug>()
	val selectedPlace: LiveData<PowerMug>
		get() = _selectedPlace

	init {
		_selectedPlace.value = powerMug
	}

	class Factory(val powerMug: PowerMug) : ViewModelProvider.Factory {
		@Suppress("unchecked_cast")
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
				return DetailViewModel(powerMug) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
