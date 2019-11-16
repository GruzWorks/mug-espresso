package test.mug.espresso.addEditView

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.model.MarkerOptions
import test.mug.espresso.R
import test.mug.espresso.databinding.FragmentAddViewBinding
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.repository.getRepository
import timber.log.Timber

class AddViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var viewModel: AddViewModel

	private lateinit var fusedLocationClient: FusedLocationProviderClient

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding: FragmentAddViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_add_view, container, false
		)

		val repository = getRepository(requireNotNull(activity).application)

		val powerMug = repository.returnPlace(AddViewFragmentArgs.fromBundle(arguments!!).selectedPlace)

		viewModel = ViewModelProviders.of(this, AddViewModel.Factory(powerMug))
			.get(AddViewModel::class.java)

		binding.viewModel = viewModel

		binding.setLifecycleOwner(viewLifecycleOwner)

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		fusedLocationClient =
			LocationServices.getFusedLocationProviderClient(this.activity as Activity)

		return binding.root
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

		if (viewModel.selectedPlace.value == null) {
			if (mMap.isMyLocationEnabled) {
				fusedLocationClient.lastLocation.addOnSuccessListener(this.activity as Activity) { location ->
					if (location != null) {
						viewModel.lastLocation.value = LatLng(location.latitude, location.longitude)
					} else {
						viewModel.lastLocation.value = LatLng(51.1079, 17.0385) // Wroclaw
					}
				}
			}

			viewModel.selectedPlace.value = PowerMug(-1, "", viewModel.lastLocation.value!!, "", 0)
		}

		viewModel.currentMarker = mMap.addMarker(MarkerOptions().position(viewModel.selectedPlace.value!!.point))

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.selectedPlace.value!!.point, 13f))
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
					mMap.isMyLocationEnabled = true
					mMap.uiSettings.isMyLocationButtonEnabled = true
				}
			}
		}
	}

	private fun checkLocationPermission() {
		val permission = ContextCompat.checkSelfPermission(
			this.requireContext(),
			Manifest.permission.ACCESS_FINE_LOCATION
		)

		if (permission == PackageManager.PERMISSION_GRANTED) {
			mMap.isMyLocationEnabled = true
			mMap.uiSettings.isMyLocationButtonEnabled = true
		} else {
			requestPermissions(
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				LOCATION_REQUEST_CODE
			)
		}
	}
}
