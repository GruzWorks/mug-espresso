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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import test.mug.espresso.R
import test.mug.espresso.ThreeState
import test.mug.espresso.databinding.FragmentAddViewBinding
import test.mug.espresso.domain.PowerMug
import test.mug.espresso.repository.PowerMugRepository
import test.mug.espresso.repository.getRepository
import timber.log.Timber

class AddViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var viewModel: AddViewModel

	private lateinit var fusedLocationClient: FusedLocationProviderClient

	private lateinit var binding: FragmentAddViewBinding

	private lateinit var repository: PowerMugRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_add_view, container, false
		)

		repository = getRepository(requireNotNull(activity).application)

		val powerMug =
			repository.returnPlace(AddViewFragmentArgs.fromBundle(arguments!!).selectedPlace)

		viewModel = ViewModelProviders.of(this, AddViewModel.Factory(repository, powerMug))
			.get(AddViewModel::class.java)

		binding.viewModel = viewModel

		binding.lifecycleOwner = viewLifecycleOwner

		viewModel.addedPlace.observe(viewLifecycleOwner, Observer {
			binding.progressBar.visibility = View.GONE

			when (it) {
				ThreeState.TRUE -> {
					viewModel.addingHandled()
					this.findNavController()
						.navigate(AddViewFragmentDirections.actionAddViewFragmentToMapViewFragment())
				}
				ThreeState.FALSE -> {
					viewModel.addingHandled()
					Snackbar.make(
						getActivity()!!.findViewById(android.R.id.content), getString(
							R.string.insert_update_error
						), Snackbar.LENGTH_LONG
					).show()
				}
				else -> {
				}
			}
		})

		viewModel.saveData.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				viewModel.selectedPlace.value!!.name = binding.pointNameInput.text.toString()
				viewModel.selectedPlace.value!!.address = binding.pointAddressInput.text.toString()
				viewModel.selectedPlace.value!!.numberOfMugs =
					Integer.parseInt(binding.pointNoOfMugsInput.text.toString())
				viewModel.selectedPlace.value!!.point = viewModel.currentMarker.position

				binding.progressBar.visibility = View.VISIBLE

				if (viewModel.selectedPlace.value!!.id == -1L) {
					viewModel.insertToDb()
				} else {
					viewModel.updateDb()
				}

				viewModel.savedData()
			}
		})

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		fusedLocationClient =
			LocationServices.getFusedLocationProviderClient(this.activity as Activity)

		Timber.v("Selected item id: %s", powerMug?.id.toString())

		return binding.root
	}

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

					viewModel.selectedPlace.value =
						PowerMug(-1, "", viewModel.lastLocation.value!!, "", 0)

					viewModel.currentMarker =
						mMap.addMarker(MarkerOptions().position(viewModel.selectedPlace.value!!.point))

					mMap.moveCamera(
						CameraUpdateFactory.newLatLngZoom(
							viewModel.selectedPlace.value!!.point,
							16f
						)
					)
				}
			}
		} else {
			viewModel.currentMarker =
				mMap.addMarker(MarkerOptions().position(viewModel.selectedPlace.value!!.point))

			mMap.moveCamera(
				CameraUpdateFactory.newLatLngZoom(
					viewModel.selectedPlace.value!!.point,
					16f
				)
			)
		}
		mMap.setOnMapClickListener(mapClickListener)
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

	private val mapClickListener = GoogleMap.OnMapClickListener {
		viewModel.currentMarker.remove()
		viewModel.currentMarker = mMap.addMarker(MarkerOptions().position(it))
	}
}
