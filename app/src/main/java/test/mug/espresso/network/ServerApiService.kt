package test.mug.espresso.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "http://192.168.1.11:3000/" // server doesnt have a hosting yet so its connecting with it running in LAN

interface ServerApiService {
	@GET("1/mugs")
	fun getMugs(): Deferred<List<NetworkPowerMug>>

	@PUT("1/mugs")
	fun insertMug(@Body mug: EphemeralNetworkPowerMug): Deferred<NetworkPowerMug>

	@PATCH("1/mugs")
	fun updateMug(@Body mug: NetworkPowerMug): Deferred<NetworkPowerMug>

	//@DELETE("1/mugs") // cannot be used as delete doesnt support body in retrofit implementation
	@HTTP(method = "DELETE", path = "1/mugs", hasBody = true)
	fun deleteMug(@Body mug: NetworkPowerMug): Deferred<ResponseBody>
}

private val moshi = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

object Network {
	private val retrofit = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.addCallAdapterFactory(CoroutineCallAdapterFactory())
		.build()

	val server = retrofit.create(ServerApiService::class.java)
}
