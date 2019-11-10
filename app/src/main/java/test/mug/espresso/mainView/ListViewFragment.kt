package test.mug.espresso.mainView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import test.mug.espresso.R
import test.mug.espresso.databinding.FragmentListViewBinding
import test.mug.espresso.databinding.ListviewItemBinding
import test.mug.espresso.domain.PowerMug

class ListViewFragment : Fragment() {
	private val viewModel: DataViewModel by lazy {
		val activity = requireNotNull(this.activity) {
			"You can only access the viewModel after onActivityCreated()"
		}
		ViewModelProviders.of(this, DataViewModel.Factory(activity.application))
			.get(DataViewModel::class.java)
	}

	private var viewModelAdapter: ListViewAdapter? = null

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		viewModel.powerMugs.observe(viewLifecycleOwner, Observer<List<PowerMug>> { mugs ->
			mugs?.apply {
				viewModelAdapter?.powerMugs = mugs
			}
		})

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

		viewModelAdapter = ListViewAdapter()

		binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
			layoutManager = LinearLayoutManager(context)
			adapter = viewModelAdapter
		}

		return binding.root
	}
}

class ListViewAdapter : RecyclerView.Adapter<ListViewViewHolder>() {
	var powerMugs: List<PowerMug> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewViewHolder {
		val withDataBinding: ListviewItemBinding = DataBindingUtil.inflate(
			LayoutInflater.from(parent.context),
			ListViewViewHolder.LAYOUT,
			parent,
			false)
		return ListViewViewHolder(withDataBinding)
	}

	override fun getItemCount() = powerMugs.size

	override fun onBindViewHolder(holder: ListViewViewHolder, position: Int) {
		holder.viewDataBinding.also {
			it.point = powerMugs[position]
		}
	}
}

class ListViewViewHolder(val viewDataBinding: ListviewItemBinding) :
	RecyclerView.ViewHolder(viewDataBinding.root) {
	companion object {
		@LayoutRes
		val LAYOUT = R.layout.listview_item
	}
}
