package test.mug.espresso.mainView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import test.mug.espresso.database.getDatabase
import test.mug.espresso.repository.PowerMugRepository

class DataViewModel(application: Application) : AndroidViewModel(application) {

	private val viewModelJob = SupervisorJob()

	private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

	private val database = getDatabase(application)
	private val repository = PowerMugRepository(database)

	/**
	 * init{} is called immediately when this ViewModel is created.
	 */
	init {
		viewModelScope.launch {
			repository.refreshCache()
		}
	}

	val powerMugs = repository.powerMugs

	/**
	 * Cancel all coroutines when the ViewModel is cleared
	 */
	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
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
