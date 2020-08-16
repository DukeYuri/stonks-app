package com.dukeyuri.stonks.main.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dukeyuri.stonks.base.ui.fragment.BaseFragment
import com.dukeyuri.stonks.feed.ui.fragment.FeedFragment
import com.dukeyuri.stonks.main.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var tabs = hashMapOf<String, BaseFragment>()
    private val tabKeys = arrayListOf<String>()

    private val mapOfTab = hashMapOf(
        TAB_PORTFOLIO to TabModel(
            R.id.menu_portfolio,
            R.string.menu_portfolio,
            R.drawable.ic_portfolio,
            FeedFragment()
        ),
        TAB_FEED to TabModel(
            R.id.menu_feed,
            R.string.menu_feed,
            R.drawable.ic_stonks,
            FeedFragment()
        ),
        TAB_RATING to TabModel(
            R.id.menu_rating,
            R.string.menu_rating,
            R.drawable.ic_rating,
            FeedFragment()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateNavigation()

        if (savedInstanceState == null) {
            showNavigation(navigation)
        } else {
            tabs = findFragments()
        }

        navigation.setOnNavigationItemSelectedListener { item ->
            showTab(getPositionByItem(item), getSelectedPosition())
            true
        }
    }

    private fun updateNavigation() {
        // Можно завязать на RemoteConfig для динамической перестановки вкладок
        mapOfTab.forEach { item ->
            tabKeys.add(item.key)
            tabs[item.key] = item.value.fragment

            navigation.menu
                .add(Menu.NONE, item.value.id, Menu.NONE, item.value.name)
                .setIcon(item.value.icon)
        }
    }

    private fun showNavigation(navigation: BottomNavigationView) {
        val portfolio = getFragmentByPosition(0)
        val feed = getFragmentByPosition(1)
        val rating = getFragmentByPosition(2)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, portfolio, tabKeys[0])
            .add(R.id.container, feed, tabKeys[1])
            .add(R.id.container, rating, tabKeys[2])
            .hide(portfolio)
            .hide(rating)
            .commit()

        navigation.menu.getItem(1).isChecked = true
    }

    private fun showTab(newItem: Int, oldItem: Int) {
        if (newItem == oldItem) {
            return
        }

        val oldFragment = getFragmentByPosition(oldItem)
        val newFragment = getFragmentByPosition(newItem)

        if (oldFragment.isAdded) {
            oldFragment.onHide()
        }

        supportFragmentManager.beginTransaction()
            .hide(oldFragment)
            .show(newFragment)
            .commitAllowingStateLoss()

        if (newFragment.isAdded) {
            newFragment.onShow()
        }
    }

    private fun getFragmentByPosition(position: Int) = tabs[tabKeys[position]] as BaseFragment

    private fun findFragments(): HashMap<String, BaseFragment> = hashMapOf(
        tabKeys[0] to supportFragmentManager.findFragmentByTag(tabKeys[0]) as BaseFragment,
        tabKeys[1] to supportFragmentManager.findFragmentByTag(tabKeys[1]) as BaseFragment,
        tabKeys[2] to supportFragmentManager.findFragmentByTag(tabKeys[2]) as BaseFragment
    )

    private fun getPositionByItem(item: MenuItem): Int {
        val menu = navigation.menu
        for (i in 0 until menu.size()) {
            if (menu.getItem(i) == item) {
                return i
            }
        }
        return 0
    }

    private fun getSelectedPosition(): Int {
        val menu = navigation.menu
        for (i in 0 until menu.size()) {
            if (menu.getItem(i).isChecked) {
                return i
            }
        }
        return 0
    }

    private data class TabModel(
        val id: Int,
        val name: Int,
        val icon: Int,
        val fragment: BaseFragment
    )

    companion object {
        const val TAB_PORTFOLIO = "tab_portfolio"
        const val TAB_FEED = "tab_feed"
        const val TAB_RATING = "tab_rating"
    }
}