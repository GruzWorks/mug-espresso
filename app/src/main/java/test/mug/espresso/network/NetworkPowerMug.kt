package test.mug.espresso.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkPowerMug(
	var id: Long,
	var name: String,
	var lat: Double,
	var lon: Double,
	var address: String,
	var num_mugs: Int
)

@JsonClass(generateAdapter = true)
data class EphemeralNetworkPowerMug(
	var name: String,
	var lat: Double,
	var lon: Double,
	var address: String,
	var num_mugs: Int
)
