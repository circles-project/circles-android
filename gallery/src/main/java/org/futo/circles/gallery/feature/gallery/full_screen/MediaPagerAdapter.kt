package org.futo.circles.gallery.feature.gallery.full_screen


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.futo.circles.gallery.feature.gallery.full_screen.media_item.FullScreenMediaFragment
import org.futo.circles.gallery.model.GalleryContentListItem


class MediaPagerAdapter(fragment: Fragment, private val roomId: String) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {

    private var itemsList = listOf<GalleryContentListItem>()

    override fun getItemCount(): Int = itemsList.size

    override fun createFragment(position: Int): Fragment =
        FullScreenMediaFragment.create(roomId, itemsList[position].id)

    fun submitList(list: List<GalleryContentListItem>) {
        itemsList = list
        notifyItemRangeChanged(0, list.size)
    }
}
