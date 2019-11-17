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

	private var _navigateToAddView = MutableLiveData<Boolean>()
	val navigateToAddView: LiveData<Boolean>
		get() = _navigateToAddView

	init {
		_selectedPlace.value = powerMug
	}

	fun goToAddView() {
		_navigateToAddView.value = true
	}

	fun wentToAddView() {
		_navigateToAddView.value = false
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
