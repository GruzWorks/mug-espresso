package test.mug.espresso.network

import com.squareup.moshi.JsonClass
import test.mug.espresso.database.DbPowerMug
import test.mug.espresso.domain.PowerMug

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
		it.asDatabaseModel()
	}.toTypedArray()
}

fun NetworkPowerMug.asDatabaseModel(): DbPowerMug {
	return DbPowerMug(
		id,
		name,
		lat,
		lon,
		address,
		num_mugs
	)
}

fun PowerMug.asNetworkModel(): NetworkPowerMug {
	return NetworkPowerMug(
		id,
		name,
		point.latitude,
		point.longitude,
		address,
		numberOfMugs
	)
}

@JsonClass(generateAdapter = true)
data class EphemeralNetworkPowerMug(
	var name: String,
	var lat: Double,
	var lon: Double,
	var address: String,
	var num_mugs: Int
)

fun PowerMug.asEphemeralNetworkModel(): EphemeralNetworkPowerMug {
	return EphemeralNetworkPowerMug(
		name,
		point.latitude,
		point.longitude,
		address,
		numberOfMugs
	)
}
