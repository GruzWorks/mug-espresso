package test.mug.espresso.detailView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.repository.PowerMugRepository

class DetailViewModel(private val repository: PowerMugRepository, powerMug: PowerMug) : ViewModel() {
	private val viewModelJob = SupervisorJob()

	private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

	fun deletePlaceFromDb() {
		viewModelScope.launch {
			repository.deletePlace(selectedPlace.value!!)
		}
	}

	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
	}

	class Factory(val repository: PowerMugRepository, val powerMug: PowerMug) : ViewModelProvider.Factory {
		@Suppress("unchecked_cast")
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
				return DetailViewModel(repository, powerMug) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
