package dev.jahir.kuper.app.ui.browser

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.kuper.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.zip.ZipInputStream

class KustomBrowserFragment : Fragment(R.layout.fragment_kustom_browser) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.kustom_list)
        recycler.layoutManager = GridLayoutManager(context, 2)
        
        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        try {
            val wallpapers = context?.assets?.list("wallpapers") ?: emptyArray()
            val presets = wallpapers.filter { it.endsWith(".klwp") || it.endsWith(".klwp.zip") }
            
            recycler.adapter = PresetAdapter(presets) { fileName ->
                applyPreset(fileName)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading assets: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyPreset(fileName: String) {
        try {
            val context = requireContext()
            // Construct URI using kfile scheme as per Kuper source
            val uri = Uri.Builder()
                .scheme("kfile")
                .authority("${context.packageName}.kustom.provider")
                .appendPath("wallpapers")
                .appendPath(fileName)
                .build()
            
            val intent = Intent()
            intent.component = android.content.ComponentName("org.kustom.wallpaper", "org.kustom.lib.editor.WpAdvancedEditorActivity")
            intent.data = uri
            
            try {
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback: Try generic view with kfile scheme
                val fallbackIntent = Intent(Intent.ACTION_VIEW)
                fallbackIntent.data = uri
                fallbackIntent.setPackage("org.kustom.wallpaper")
                
                try {
                    startActivity(fallbackIntent)
                } catch (e2: Exception) {
                    Toast.makeText(context, "KLWP App not installed or not found!", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "启动失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    inner class PresetAdapter(
        private val items: List<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<PresetAdapter.Holder>() {

        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val image: ImageView = v.findViewById(R.id.preset_image)
            val name: TextView = v.findViewById(R.id.preset_name)
            val size: TextView = v.findViewById(R.id.preset_size)
            val root: View = v
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_kustom_preset, parent, false)
            return Holder(v)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val fileName = items[position]
            holder.name.text = fileName.removeSuffix(".klwp").removeSuffix(".zip").replace("_", " ")
            holder.size.text = "KLWP"
            holder.root.setOnClickListener { onClick(fileName) }

            // Load Thumbnail Async
            holder.image.setImageDrawable(null)
            val primaryColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.html_primary)
            holder.image.setColorFilter(primaryColor) // Default tint for ic_image

            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = loadThumbnail(fileName)
                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        holder.image.clearColorFilter() // Clear tint for actual bitmap
                        holder.image.setImageBitmap(bitmap)
                    } else {
                        holder.image.setColorFilter(primaryColor)
                        holder.image.setImageResource(R.drawable.ic_image)
                    }
                }
            }
        }

        private fun loadThumbnail(fileName: String): android.graphics.Bitmap? {
            return try {
                val assetManager = requireContext().assets
                val stream = assetManager.open("wallpapers/$fileName")
                val zipStream = ZipInputStream(stream)
                var entry = zipStream.nextEntry
                var bitmap: android.graphics.Bitmap? = null
                
                while (entry != null) {
                    val name = entry.name.lowercase()
                    if (name.endsWith("preset_thumb_portrait.jpg") || 
                        name.endsWith("preset_thumb_landscape.jpg") ||
                        name.endsWith("thumbnail.jpg") ||
                        name.endsWith("portrait.jpg")) {
                        val bytes = zipStream.readBytes()
                        if (bytes.isNotEmpty()) {
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }
                        break
                    }
                    entry = zipStream.nextEntry
                }
                zipStream.close()
                stream.close()
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun getItemCount() = items.size
    }
}
