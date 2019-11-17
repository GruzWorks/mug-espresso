package test.mug.espresso

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun calculateDistance(pointA: LatLng, pointB: LatLng): Double {
	val earthRadiusKm = 6371

	val deltaLat = degreesToRadians(pointA.latitude - pointB.latitude)
	val deltaLon = degreesToRadians(pointA.longitude - pointB.longitude)

	val latA = degreesToRadians(pointA.latitude)
	val latB = degreesToRadians(pointB.latitude)

	val a =	sin(deltaLat / 2) * sin(deltaLat / 2)
		+ sin(deltaLon / 2) * sin(deltaLon / 2) * cos(latA) * cos(latB)
	val c = 2 * atan2(sqrt(a), sqrt(1 - a))

	return earthRadiusKm * c
}

fun degreesToRadians(degrees: Double): Double {
	return degrees * Math.PI / 180
}
