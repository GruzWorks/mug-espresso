package test.mug.espresso.detailView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import test.mug.espresso.R
import test.mug.espresso.ThreeState
import test.mug.espresso.databinding.FragmentDetailViewBinding
import test.mug.espresso.repository.PowerMugRepository
import test.mug.espresso.repository.getRepository
import timber.log.Timber

class DetailViewFragment : Fragment(), OnMapReadyCallback {
	private val LOCATION_REQUEST_CODE: Int = 420

	private lateinit var mMap: GoogleMap

	private lateinit var binding: FragmentDetailViewBinding

	private lateinit var viewModel: DetailViewModel

	private lateinit var currentMarker: Marker

	private lateinit var repository: PowerMugRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_detail_view, container, false
		)

		repository = getRepository(requireNotNull(activity).application)

		val powerMug =
			repository.returnPlace(DetailViewFragmentArgs.fromBundle(arguments!!).selectedPlace)

		viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(repository, powerMug!!))
			.get(DetailViewModel::class.java)

		binding.viewModel = viewModel

		binding.lifecycleOwner = viewLifecycleOwner

		viewModel.deletedPlace.observe(viewLifecycleOwner, Observer {
			binding.progressBar.visibility = View.GONE

			when (it) {
				ThreeState.TRUE -> {
					viewModel.deletionHandled()
					this.findNavController()
						.navigate(DetailViewFragmentDirections.actionDetailViewFragmentToMapViewFragment())
				}
				ThreeState.FALSE -> {
					viewModel.deletionHandled()
					Snackbar.make(
						getActivity()!!.findViewById(android.R.id.content), getString(
							R.string.delete_error
						), Snackbar.LENGTH_LONG
					).show()
				}
				else -> {
				}
			}
		})

		viewModel.navigateToAddView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				currentMarker.remove()
				this.findNavController().navigate(
					DetailViewFragmentDirections.actionDetailViewFragmentToAddViewFragment(powerMug.id)
				)
				viewModel.wentToAddView()
			}
		})

		val mapFragment = childFragmentManager
			.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		setHasOptionsMenu(true)

		Timber.v("Selected item id: %s", powerMug.id.toString())

		return binding.root
	}

	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap

		checkLocationPermission()
		mMap.uiSettings.isZoomControlsEnabled = true

		currentMarker = mMap.addMarker(
			MarkerOptions().position(viewModel.selectedPlace.value!!.point).title(viewModel.selectedPlace.value!!.name)
		)

		mMap.moveCamera(
			CameraUpdateFactory.newLatLngZoom(
				viewModel.selectedPlace.value!!.point,
				15f
			)
		)
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

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_detail_view, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.delete_menu_button -> {
			binding.progressBar.visibility = View.VISIBLE
			viewModel.deletePlaceFromDb()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}
}
