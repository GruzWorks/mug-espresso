package test.mug.espresso.mainView

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
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
import com.google.android.material.snackbar.Snackbar
import test.mug.espresso.R
import test.mug.espresso.databinding.FragmentMapViewBinding
import test.mug.espresso.domain.PowerMug
import timber.log.Timber

class MapViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var fusedLocationClient: FusedLocationProviderClient

	private lateinit var viewModel: DataViewModel

	private lateinit var binding: FragmentMapViewBinding

	private var markers = mutableListOf<Marker>()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_map_view, container, false
		)

		binding.lifecycleOwner = viewLifecycleOwner

		val activity = requireNotNull(this.activity)
		viewModel = activity.run {
			ViewModelProviders.of(this, DataViewModel.Factory(activity.application))
				.get(DataViewModel::class.java)
		}

		binding.viewModel = viewModel

		viewModel.navigateToSecondView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController().navigate(R.id.action_mapViewFragment_to_listViewFragment)
				viewModel.wentToSecondView()
			}
		})

		viewModel.navigateToAddView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController()
					.navigate(MapViewFragmentDirections.actionMapViewFragmentToAddViewFragment(-1))
				viewModel.wentToAddView()
			}
		})

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		fusedLocationClient =
			LocationServices.getFusedLocationProviderClient(this.activity as Activity)

		setHasOptionsMenu(true)

		return binding.root
	}

	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap

		checkLocationPermission()

		if (mMap.isMyLocationEnabled) {
			moveToUserLocation()
		} else {
			moveToDefaultLocation()
		}

		if (viewModel.searchResults != null) {
			viewModel.searchResults = null
		}
		viewModel.powerMugs.observe(viewLifecycleOwner, powerMugsObserver)
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
				mMap.animateCamera(
					CameraUpdateFactory.newLatLngZoom(
						viewModel.lastLocation.value,
						15f
					), 1000, null
				)
			} else {
				moveToDefaultLocation()
			}
		}
	}

	private fun moveToDefaultLocation() {
		viewModel.lastLocation.value = LatLng(51.1079, 17.0385) // Wroclaw
		mMap.animateCamera(
			CameraUpdateFactory.newLatLngZoom(viewModel.lastLocation.value, 15f),
			1000,
			null
		)
	}

	private val markerClickListener =
		GoogleMap.OnMarkerClickListener { marker ->
			this.findNavController().navigate(
				MapViewFragmentDirections.actionMapViewFragmentToDetailViewFragment(
					marker!!.title.toLong()
				)
			)
			true
		}

	private val queryTextListener =
		object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String): Boolean {
				Timber.i("onQueryTextSubmit: $query")

				viewModel.powerMugs.removeObservers(viewLifecycleOwner)

				viewModel.search(query)

				viewModel.searchResults!!.observe(
					viewLifecycleOwner,
					Observer<List<PowerMug>> { mugs ->
						markers.forEach { item ->
							item.remove()
						}
						markers.clear()
						val it = mugs.listIterator()
						for (item in it) {
							markers.add(
								mMap.addMarker(
									MarkerOptions().position(item.point).title(
										item.id.toString()
									)
								)
							)
						}
						mMap.setOnMarkerClickListener(markerClickListener)

						if (markers.size > 0) {
							mMap.animateCamera(
								CameraUpdateFactory.newLatLngZoom(
									markers[0].position,
									15f
								), 1000, null
							)
						} else {
							Snackbar.make(
								getActivity()!!.findViewById(android.R.id.content), getString(
									R.string.no_results_error
								), Snackbar.LENGTH_LONG
							).show()
						}
					})

				return false
			}

			override fun onQueryTextChange(newText: String): Boolean {
				return true
			}
		}

	private val closeListener = SearchView.OnCloseListener {
		if (viewModel.searchResults?.hasObservers() == true) {
			viewModel.searchResults!!.removeObservers(viewLifecycleOwner)
		}
		viewModel.searchResults = null

		if (viewModel.powerMugs.hasObservers()) {
			viewModel.powerMugs.removeObservers(viewLifecycleOwner)
		}
		viewModel.powerMugs.observe(viewLifecycleOwner, powerMugsObserver)

		false
	}

	private val powerMugsObserver = Observer<List<PowerMug>> { mugs ->
		markers.forEach { item ->
			item.remove()
		}
		markers.clear()
		val it = mugs.listIterator()
		for (item in it) {
			markers.add(mMap.addMarker(MarkerOptions().position(item.point).title(item.id.toString())))
		}
		mMap.setOnMarkerClickListener(markerClickListener)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_main_view, menu)

		val searchView = menu.findItem(R.id.search_menu_button).actionView as SearchView

		searchView.setOnQueryTextListener(queryTextListener)
		searchView.setOnCloseListener(closeListener)
	}
}
