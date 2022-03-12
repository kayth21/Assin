package com.ceaver.assin.intentions.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.databinding.IntentionListFragmentBinding
import com.ceaver.assin.intentions.Intention
import com.ceaver.assin.intentions.IntentionRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class IntentionListFragment : Fragment() {

    private val intentionListAdapter = IntentionListAdapter(OnListItemClickListener())
    private lateinit var viewModel: IntentionListViewModel
    private lateinit var binding: IntentionListFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<IntentionListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = IntentionListFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.intentionListFragmentIntentionList.adapter = intentionListAdapter
        binding.intentionListFragmentIntentionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))
        // binding.intentionListFragmentCreateIntentionButton.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment()) }

        viewModel.intentions.observe(viewLifecycleOwner) { intentionListAdapter.submitList(it.sortedByDescending { it.factorToReferencePrice }) }

        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(item: Intention)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Intention) {
            // findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToIntentionInputFragment(item))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == IntentionListAdapter.MENU_ITEM_DELETE) {
            onDeleteItemClick()
        }
        return super.onContextItemSelected(item)
    }

    private fun onDeleteItemClick() {
        lifecycleScope.launch {
            val selectedIntention = intentionListAdapter.currentLongClickIntention!!
            IntentionRepository.delete(selectedIntention)
            Snackbar.make(binding.intentionListFragmentCoordinatorLayout, "Intention removed", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { lifecycleScope.launch { IntentionRepository.insert(selectedIntention) } }
                    .show()
        }
    }
}
