package dev.jahir.kuper.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.jahir.kuper.app.R
import dev.jahir.kuper.app.ui.config.KlwpConfigActivity

class MikuHomeFragment : Fragment(R.layout.fragment_miku_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find Navigation Controller (BottomNavigationView in Parent Activity)
        // Note: ID 'navigation' is standard in Frames/Kuper templates
        val bottomNav = activity?.findViewById<BottomNavigationView>(
            resources.getIdentifier("navigation", "id", context?.packageName)
        )

        // Actions Grid
        view.findViewById<View>(R.id.cardApply)?.setOnClickListener {
            // Placeholder for Apply
        }

        view.findViewById<View>(R.id.cardIcons)?.setOnClickListener {
            (requireActivity() as? dev.jahir.kuper.app.MainActivity)?.switchTab("icons")
        }

        view.findViewById<View>(R.id.cardWallpapers)?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, dev.jahir.kuper.app.ui.browser.KustomBrowserFragment())
                .addToBackStack(null)
                .commit()
        }

        // Secondary Actions (Empty for now)
        view.findViewById<View>(R.id.cardRequest)?.setOnClickListener { }
        view.findViewById<View>(R.id.cardFaqs)?.setOnClickListener { }
        view.findViewById<View>(R.id.cardSettings)?.setOnClickListener { }
        
        // Header Icon click -> Open KLWP Config (Secret/Shortcut)
        view.findViewById<View>(R.id.iconContainer)?.setOnClickListener {
             startActivity(Intent(requireContext(), KlwpConfigActivity::class.java))
        }
    }
}
