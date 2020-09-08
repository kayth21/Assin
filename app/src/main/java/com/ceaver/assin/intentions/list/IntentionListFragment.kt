package com.ceaver.assin.intentions.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.databinding.IntentionListFragmentBinding
import com.ceaver.assin.home.HomeFragmentDirections
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class IntentionListFragment : Fragment() {

    private val intentionListAdapter = IntentionListAdapter(OnListItemClickListener())
    private lateinit var viewModel: IntentionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<IntentionListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: IntentionListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.intention_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.intentionListFragmentIntentionList.adapter = intentionListAdapter
        binding.intentionListFragmentIntentionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))
        binding.intentionListFragmentCreateIntentionButton.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment()) }

        viewModel.intentions.observe(viewLifecycleOwner, Observer { intentionListAdapter.intentions = it })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        Toast.makeText(activity, "Markets refreshed", Toast.LENGTH_SHORT).show();
        viewModel.loading.set(false)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        Toast.makeText(activity, "Observed refreshed", Toast.LENGTH_SHORT).show();
        viewModel.loading.set(false)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Intention)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Intention) {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment(item))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.groupId == 2 && item.itemId == 0) {
            lifecycleScope.launch {
                val selectedIntention = intentionListAdapter.currentLongClickIntention!!
                IntentionRepository.deleteIntention(selectedIntention)
            }
        }
        return super.onContextItemSelected(item)
    }
}
