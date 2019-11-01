package test.mug.espresso

import android.app.Application
import timber.log.Timber

class EspressoApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())
	}
}
