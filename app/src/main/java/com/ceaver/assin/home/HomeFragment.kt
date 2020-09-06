package com.ceaver.assin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetFragment
import com.ceaver.assin.intentions.list.IntentionListFragment
import com.ceaver.assin.markets.MarketFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.main_activity.*


class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stateAdapter = HomeFragmentStateAdapter(this)
        viewPager = home_fragment_view_pager
        viewPager.adapter = stateAdapter

        TabLayoutMediator(home_fragment_tab_layout, viewPager) { currentTab, currentPosition ->
            currentTab.text = when (currentPosition) {
                0 -> "Markets"
                1 -> "Assets"
                2 -> "Intentions"
                else -> throw IllegalStateException()
            }
        }.attach()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().main_activity_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    override fun onStop() {
        super.onStop()
        requireActivity().main_activity_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}

class HomeFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MarketFragment()
            1 -> AssetFragment()
            2 -> IntentionListFragment()
            else -> throw IllegalStateException()
        }
    }
}
