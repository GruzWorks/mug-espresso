package test.mug.espresso.mainView

import android.os.Bundle
import android.view.*
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
import test.mug.espresso.domain.PowerMugWithDistance

class ListViewFragment : Fragment() {
	private lateinit var viewModel: DataViewModel

	private var viewModelAdapter: ListViewAdapter? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding: FragmentListViewBinding = DataBindingUtil.inflate(
			inflater, R.layout.fragment_list_view, container, false
		)

		binding.lifecycleOwner = viewLifecycleOwner

		val activity = requireNotNull(this.activity)
		viewModel = activity.run {
			ViewModelProviders.of(this, DataViewModel.Factory(activity.application))
				.get(DataViewModel::class.java)
		}

		binding.viewModel = viewModel

		viewModel.powerMugsWithDistance.observe(
			viewLifecycleOwner,
			Observer<List<PowerMugWithDistance>> { mugs ->
				mugs?.apply {
					viewModelAdapter?.powerMugs = mugs
				}
			})

		viewModel.navigateToSecondView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController()
					.navigate(ListViewFragmentDirections.actionListViewFragmentToMapViewFragment())
				viewModel.wentToSecondView()
			}
		})

		viewModel.navigateToAddView.observe(viewLifecycleOwner, Observer {
			if (it == true) {
				this.findNavController()
					.navigate(ListViewFragmentDirections.actionListViewFragmentToAddViewFragment(-1))
				viewModel.wentToAddView()
			}
		})

		viewModel.refreshDistance()

		viewModelAdapter = ListViewAdapter(PowerMugListener {
			this.findNavController()
				.navigate(ListViewFragmentDirections.actionListViewFragmentToDetailViewFragment(it))
		})

		binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
			layoutManager = LinearLayoutManager(context)
			adapter = viewModelAdapter
		}

		setHasOptionsMenu(true)

		return binding.root
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_main_view, menu)
	}
}

class PowerMugListener(val clickListener: (powerMugId: Long) -> Unit) {
	fun onClick(powerMug: PowerMugWithDistance) = clickListener(powerMug.id)
}

class ListViewAdapter(val callback: PowerMugListener) : RecyclerView.Adapter<ListViewViewHolder>() {
	var powerMugs: List<PowerMugWithDistance> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewViewHolder {
		val withDataBinding: ListviewItemBinding = DataBindingUtil.inflate(
			LayoutInflater.from(parent.context),
			ListViewViewHolder.LAYOUT,
			parent,
			false
		)
		return ListViewViewHolder(withDataBinding)
	}

	override fun getItemCount() = powerMugs.size

	override fun onBindViewHolder(holder: ListViewViewHolder, position: Int) {
		holder.viewDataBinding.also {
			it.point = powerMugs[position]
			it.pointCallback = callback
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
