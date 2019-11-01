package test.mug.espresso

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import test.mug.espresso.databinding.FragmentMapViewBinding
import timber.log.Timber

class MapViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private lateinit var lastLocation: Location

	private lateinit var viewModel: DataViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding: FragmentMapViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_map_view, container, false
		)

		viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		fusedLocationClient =
			LocationServices.getFusedLocationProviderClient(this.activity as Activity)

		return binding.root
	}

	private fun checkLocationPermission() {
		val permission = ContextCompat.checkSelfPermission(
			this.requireContext(),
			Manifest.permission.ACCESS_FINE_LOCATION
		)

		if (permission == PackageManager.PERMISSION_GRANTED) {
			Timber.i("Permission already granted")
			mMap.isMyLocationEnabled = true
			mMap.uiSettings.isMyLocationButtonEnabled = true
		} else {
			Timber.i("Permission not yet granted. Asking for permission")
			requestPermissions(
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				LOCATION_REQUEST_CODE
			)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		when (requestCode) {
			LOCATION_REQUEST_CODE -> {
				if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					Timber.w("Permission denied!")
				} else {
					Timber.i("Permission granted")
					mMap.isMyLocationEnabled = true
					mMap.uiSettings.isMyLocationButtonEnabled = true
					moveToUserLocation()
				}
			}
		}
	}

	private fun moveToUserLocation() {
		fusedLocationClient.lastLocation.addOnSuccessListener(this.activity as Activity) { location ->
			if (location != null) {
				lastLocation = location
				val currentLatLng = LatLng(location.latitude, location.longitude)
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
			}
		}
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap

		checkLocationPermission()
		mMap.uiSettings.isZoomControlsEnabled = true

		if (mMap.isMyLocationEnabled) {
			moveToUserLocation()
		} else {
			// Move the camera to Wroclaw
			val wroclaw = LatLng(51.1079, 17.0385)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 13f), 500, null)
		}
	}
}
