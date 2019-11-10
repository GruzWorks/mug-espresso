package test.mug.espresso.mainView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import test.mug.espresso.R
import test.mug.espresso.databinding.FragmentListViewBinding
import test.mug.espresso.domain.PowerMug

class ListViewFragment : Fragment() {
	private val viewModel: DataViewModel by lazy {
		val activity = requireNotNull(this.activity) {
			"You can only access the viewModel after onActivityCreated()"
		}
		ViewModelProviders.of(this, DataViewModel.Factory(activity.application))
			.get(DataViewModel::class.java)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

//		viewModel.powerMugs.observe(viewLifecycleOwner, Observer<List<PowerMug>> { mugs ->
//
//		})

		viewModel.navigateToSecondView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController().navigate(R.id.action_listViewFragment_to_mapViewFragment)
				viewModel.wentToSecondView()
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding: FragmentListViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_list_view, container, false
		)

		binding.setLifecycleOwner(viewLifecycleOwner)

		binding.viewModel = viewModel

		return binding.root
	}
}
