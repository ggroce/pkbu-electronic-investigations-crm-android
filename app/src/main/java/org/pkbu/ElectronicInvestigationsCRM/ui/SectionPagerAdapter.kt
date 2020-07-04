package org.pkbu.ElectronicInvestigationsCRM.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionPagerAdapter(manager: FragmentManager, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: ArrayList<Fragment> = arrayListOf()
    public val fragmentTitleList: ArrayList<String> = arrayListOf()
    private val pageIds = fragmentList.map { it.hashCode().toLong() }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }

    fun addScreen(fragment: Fragment, title: String, position: Int) {
        fragmentList.add(position, fragment)
        fragmentTitleList.add(position, title)
        notifyDataSetChanged()
    }

    fun removeScreen(position: Int) {
        fragmentList.removeAt(position)
        fragmentTitleList.removeAt(position)
        notifyItemRangeChanged(position, fragmentList.size)
        notifyDataSetChanged()
    }

    fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    fun getPagePosition(title: String): Int? {
        return fragmentTitleList.indexOf(title)
    }
}