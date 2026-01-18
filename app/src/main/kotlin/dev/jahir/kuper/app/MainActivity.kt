package dev.jahir.kuper.app

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.jahir.kuper.app.ui.config.KlwpConfigActivity
import dev.jahir.kuper.app.ui.home.MikuHomeFragment
import dev.jahir.kuper.app.ui.theme.ThemeEngine

// New MainActivity completely overriding Kuper structure
class MainActivity : AppCompatActivity() {

    private lateinit var pillHome: FrameLayout
    private lateinit var pillIcons: FrameLayout
    private lateinit var pillWalls: FrameLayout
    
    private lateinit var textHome: TextView
    private lateinit var textIcons: TextView
    private lateinit var textWalls: TextView

    private lateinit var iconHome: ImageView
    private lateinit var iconIcons: ImageView
    private lateinit var iconWalls: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply MikuBox Theme
        ThemeEngine.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_redesign)

        // Init Views
        pillHome = findViewById(R.id.pill_home)
        pillIcons = findViewById(R.id.pill_icons)
        pillWalls = findViewById(R.id.pill_walls)
        
        textHome = findViewById(R.id.text_home)
        textIcons = findViewById(R.id.text_icons)
        textWalls = findViewById(R.id.text_walls)
        
        iconHome = findViewById(R.id.icon_home)
        iconIcons = findViewById(R.id.icon_icons)
        iconWalls = findViewById(R.id.icon_walls)

        // Set Click Listeners
        findViewById<android.view.View>(R.id.nav_home).setOnClickListener { switchTab("home") }
        findViewById<android.view.View>(R.id.nav_icons).setOnClickListener { switchTab("icons") }
        findViewById<android.view.View>(R.id.nav_walls).setOnClickListener { switchTab("walls") }

        // Initial Load
        switchTab("home")
    }

    fun switchTab(tabName: String) {
        val fragment: Fragment = when (tabName) {
            "home" -> MikuHomeFragment() // We already created this
            "icons" -> dev.jahir.kuper.app.ui.icons.MikuIconsFragment() // Need to create class
            else -> dev.jahir.kuper.app.ui.home.MikuHomeFragment() // Fallback
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()

        updateNavUI(tabName)
    }

    private fun updateNavUI(activeTab: String) {
        val activePillBg = R.drawable.bg_nav_pill_active
        val inactivePillBg = R.drawable.bg_nav_pill_inactive
        
        val activeColor = ContextCompat.getColor(this, R.color.html_onPrimary)
        val inactiveColor = ContextCompat.getColor(this, R.color.html_outline)
        val whiteColor = ContextCompat.getColor(this, android.R.color.white)

        // Home
        val isHome = activeTab == "home"
        pillHome.setBackgroundResource(if (isHome) activePillBg else inactivePillBg)
        iconHome.setColorFilter(if (isHome) activeColor else inactiveColor)
        textHome.setTextColor(if (isHome) whiteColor else inactiveColor)

        // Icons
        val isIcons = activeTab == "icons"
        pillIcons.setBackgroundResource(if (isIcons) activePillBg else inactivePillBg)
        iconIcons.setColorFilter(if (isIcons) activeColor else inactiveColor)
        textIcons.setTextColor(if (isIcons) whiteColor else inactiveColor)

        // Walls
        val isWalls = activeTab == "walls"
        pillWalls.setBackgroundResource(if (isWalls) activePillBg else inactivePillBg)
        iconWalls.setColorFilter(if (isWalls) activeColor else inactiveColor)
        textWalls.setTextColor(if (isWalls) whiteColor else inactiveColor)
    }
}
