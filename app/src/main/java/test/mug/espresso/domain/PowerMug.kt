package test.mug.espresso.domain

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PowerMug (
	var id: Long = 0L,
	var name: String,
	var point: LatLng,
	var address: String,
	var numberOfMugs: Int
) : Parcelable

fun List<PowerMug>.asMarkerOptions(): List<MarkerOptions> {
	return map {
		MarkerOptions().position(it.point).title(it.id.toString())
	}
}
