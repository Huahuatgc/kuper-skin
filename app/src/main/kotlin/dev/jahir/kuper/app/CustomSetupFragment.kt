package dev.jahir.kuper.app

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.frames.extensions.context.openLink
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment
import dev.jahir.kuper.data.models.RequiredApp
import dev.jahir.kuper.ui.adapters.RequiredAppsAdapter

/**
 * Custom SetupFragment that overrides the download link for KLWP
 * to use a custom download URL instead of Play Store
 */
class CustomSetupFragment : BaseFramesFragment<RequiredApp>() {

    companion object {
        const val TAG = "required_apps_fragment"
        
        // Custom download link for KLWP
        private const val KLWP_DOWNLOAD_URL = "https://www.123pan.com/s/uYPLVv-FzE7d.html"
        
        // KLWP package names
        private const val KLWP_PACKAGE = "org.kustom.wallpaper"
        private const val KLWP_PRO_PACKAGE = "org.kustom.wallpaper.pro"

        @JvmStatic
        fun create(requiredApps: ArrayList<RequiredApp> = ArrayList()): CustomSetupFragment =
            CustomSetupFragment().apply { updateItemsInAdapter(requiredApps) }
    }

    private val dividerDecoration by lazy {
        object : DividerItemDecoration(context, DividerItemDecoration.VERTICAL) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position == state.itemCount - 1) {
                    outRect.setEmpty()
                    outRect.set(0, 0, 0, 0)
                } else super.getItemOffsets(outRect, view, parent, state)
            }
        }
    }

    private val requiredAppsAdapter: RequiredAppsAdapter by lazy {
        RequiredAppsAdapter(::onClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.setFastScrollEnabled(false)
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(dividerDecoration)
        recyclerView?.adapter = requiredAppsAdapter
    }

    override fun onResume() {
        super.onResume()
        cleanRecyclerViewState()
    }

    override fun loadData() {
        // Data is loaded via MainActivity's requiredAppsViewModel
        // We don't need to manually trigger loading here
    }

    private fun onClick(requiredApp: RequiredApp) {
        if (requiredApp.packageName.hasContent()) {
            // Use custom download URL for KLWP packages
            val url = when (requiredApp.packageName) {
                KLWP_PACKAGE, KLWP_PRO_PACKAGE -> KLWP_DOWNLOAD_URL
                else -> "https://play.google.com/store/apps/details?id=${requiredApp.packageName}"
            }
            context?.openLink(url)
        }
    }

    override fun getFilteredItems(
        originalItems: ArrayList<RequiredApp>,
        filter: String
    ): ArrayList<RequiredApp> = originalItems

    override fun updateItemsInAdapter(items: List<RequiredApp>) {
        requiredAppsAdapter.apps = items
        cleanRecyclerViewState()
    }

    internal fun cleanRecyclerViewState() {
        recyclerView?.apply {
            allowFirstRunCheck = false
            searching = false
            loading = false
        }
    }
}
