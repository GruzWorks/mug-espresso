package test.mug.espresso.mainView

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import test.mug.espresso.R
import test.mug.espresso.databinding.FragmentMapViewBinding
import timber.log.Timber

class MapViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var fusedLocationClient: FusedLocationProviderClient

	private val viewModel: DataViewModel by lazy {
		val activity = requireNotNull(this.activity) {
			"You can only access the viewModel after onActivityCreated()"
		}
		activity.run {
			ViewModelProviders.of(this, DataViewModel.Factory(activity.application))
				.get(DataViewModel::class.java)
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		viewModel.navigateToSecondView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController().navigate(R.id.action_mapViewFragment_to_listViewFragment)
				viewModel.wentToSecondView()
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding: FragmentMapViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_map_view, container, false
		)

		binding.setLifecycleOwner(viewLifecycleOwner)

		binding.viewModel = viewModel

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

		if (mMap.isMyLocationEnabled) {
			moveToUserLocation()
		} else {
			moveToDefaultLocation()
		}

		viewModel.markers.observe(viewLifecycleOwner, Observer<List<MarkerOptions>> { mugs ->
			val it = mugs.listIterator()
			for (item in it) {
				mMap.addMarker(item)
			}
			mMap.setOnMarkerClickListener(markerClickListener)
		})
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
					moveToUserLocation()
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

	private fun moveToUserLocation() {
		fusedLocationClient.lastLocation.addOnSuccessListener(this.activity as Activity) { location ->
			if (location != null) {
				viewModel.lastLocation.value = LatLng(location.latitude, location.longitude)
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.lastLocation.value, 15f), 1000, null)
			} else {
				moveToDefaultLocation()
			}
		}
	}

	private fun moveToDefaultLocation() {
		viewModel.lastLocation.value = LatLng(51.1079, 17.0385) // Wroclaw
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.lastLocation.value, 13f), 1000, null)
	}

	private val markerClickListener =
		GoogleMap.OnMarkerClickListener { marker ->
			this.findNavController().navigate(MapViewFragmentDirections.actionMapViewFragmentToDetailViewFragment(
				marker!!.title.toLong()))
			true
		}
}
