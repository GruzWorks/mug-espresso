package test.mug.espresso.domain

import com.google.android.gms.maps.model.LatLng

data class PowerMugWithDistance (
	var id: Long = 0L,
	var name: String,
	var point: LatLng,
	var address: String,
	var distance: Double,
	var numberOfMugs: Int
)
