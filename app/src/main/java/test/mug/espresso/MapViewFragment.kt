package test.mug.espresso

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import test.mug.espresso.databinding.FragmentMapViewBinding

class MapViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var viewModel: DataViewModel

	override fun onCreateView(inflater: LayoutInflater,	container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val binding: FragmentMapViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_map_view, container, false)

		viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		setupLocationPermission()

		return binding.root
	}

	private fun setupLocationPermission() {
		val permission = ContextCompat.checkSelfPermission(this.requireContext(),
			Manifest.permission.ACCESS_FINE_LOCATION)

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this.requireActivity(),
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				LOCATION_REQUEST_CODE)
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

				} else {
					mMap.isMyLocationEnabled = true
					mMap.uiSettings.isMyLocationButtonEnabled = true
				}
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

		// Add a marker in Wroclaw and move the camera
		val wroclaw = LatLng(51.1079, 17.0385)
		mMap.addMarker(MarkerOptions().position(wroclaw).title("Marker in Wroclaw"))
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 13f), 500, null)
	}
}
