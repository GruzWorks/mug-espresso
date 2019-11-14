package test.mug.espresso.detailView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import test.mug.espresso.domain.PowerMug

class DetailViewModel(powerMug: PowerMug) : ViewModel() {
	private val _selectedPlace = MutableLiveData<PowerMug>()

	// The external LiveData for the SelectedProperty
	val selectedPlace: LiveData<PowerMug>
		get() = _selectedPlace

	// Initialize the _selectedProperty MutableLiveData
	init {
		_selectedPlace.value = powerMug
	}
}
