package test.mug.espresso.network

import com.squareup.moshi.JsonClass
import test.mug.espresso.database.DbPowerMug

@JsonClass(generateAdapter = true)
data class NetworkPowerMug(
	var id: Long,
	var name: String,
	var lat: Double,
	var lon: Double,
	var address: String,
	var num_mugs: Int
)

fun List<NetworkPowerMug>.asDatabaseModel(): Array<DbPowerMug> {
	return map {
		DbPowerMug(
			id = it.id,
			name = it.name,
			lat = it.lat,
			lng = it.lon,
			address = it.address,
			numberOfMugs = it.num_mugs
		)
	}.toTypedArray()
}

@JsonClass(generateAdapter = true)
data class EphemeralNetworkPowerMug(
	var name: String,
	var lat: Double,
	var lon: Double,
	var address: String,
	var num_mugs: Int
)
