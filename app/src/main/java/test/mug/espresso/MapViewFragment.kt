package test.mug.espresso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

		// Add a marker in Wroclaw and move the camera
		val wroclaw = LatLng(51.1079, 17.0385)
		mMap.addMarker(MarkerOptions().position(wroclaw).title("Marker in Wroclaw"))
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 13f), 500, null)
	}
}
