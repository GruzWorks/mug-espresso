package test.mug.espresso.domain

import com.google.android.gms.maps.model.LatLng

data class PowerMugWithDistance (
	var id: Long = 0L,
	var name: String,
	var point: LatLng,
	var address: String,
	var distance: Double,
	var numberOfMugs: Int
) {
	fun formatDistance(distance: Double) : String {
		@Suppress("NAME_SHADOWING") // kotlin made all function parameters as val and they cannot be reassigned
		var distance = distance

		if (distance < 0) {
			distance *= -1
		}

		if (distance < 1) {
			return String.format("%.0f", distance*1000) + "m"
		} else {
			return String.format("%.2f", distance) + "km"
		}
	}
}
