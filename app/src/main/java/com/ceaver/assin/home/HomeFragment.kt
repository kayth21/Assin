package com.ceaver.assin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ceaver.assin.R
import com.ceaver.assin.assets.AssetFragment
import com.ceaver.assin.intentions.list.IntentionListFragment
import com.ceaver.assin.markets.MarketFragment
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.main_activity.*


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<HomeViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.assinWorkerRunning.observe(viewLifecycleOwner) { if (it == false) Toast.makeText(activity, "Data refreshed", Toast.LENGTH_SHORT).show() }
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val marketFragment = MarketFragment()
        val portfolioFragment = AssetFragment()
        val intentionListFragment = IntentionListFragment()

        setCurrentFragment(marketFragment)

        homeFragmentBottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottomNavigationMenuMarkets -> setCurrentFragment(marketFragment)
                R.id.bottomNavigationMenuPortfolio -> setCurrentFragment(portfolioFragment)
                R.id.bottomNavigationMenuIntention -> setCurrentFragment(intentionListFragment)
                else -> throw IllegalStateException()
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.homeFragmentFrameLayout, fragment)
                commit()
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