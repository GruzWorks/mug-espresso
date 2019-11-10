package test.mug.espresso.mainView

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.database.getDatabase
import test.mug.espresso.domain.asMarkerOptions
import test.mug.espresso.repository.PowerMugRepository

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
	}

	val powerMugs = repository.powerMugs

	val markers = Transformations.map(powerMugs) {
		it.asMarkerOptions()
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
