package test.mug.espresso

import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun calculateDistance(pointA: LatLng, pointB: LatLng): Double {
	val earthRadiusKm = 6371

	val deltaLat = degreesToRadians(pointA.latitude - pointB.latitude)
	val deltaLon = degreesToRadians(pointA.longitude - pointB.longitude)

	val latA = degreesToRadians(pointA.latitude)
	val latB = degreesToRadians(pointB.latitude)

	return 2 * earthRadiusKm * asin(
		sqrt(
			sin(deltaLat / 2) * sin(deltaLat / 2)
					+ sin(deltaLon / 2) * sin(deltaLon / 2) * cos(latA) * cos(latB)
		)
	)
}

fun degreesToRadians(degrees: Double): Double {
	return degrees * Math.PI / 180
}

enum class ThreeState {
	TRUE,
	FALSE,
	UNSET
}
