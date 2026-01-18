package dev.jahir.kuper.app.ui.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import dev.jahir.kuper.app.R
import android.view.View
import android.widget.CompoundButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.chip.ChipGroup
import dev.jahir.kuper.app.ui.theme.ThemeEngine
import java.io.OutputStreamWriter

/**
 * KLWP 路径配置界面
 * 允许用户选择 KLWP 目录并写入动态内容文件
 */
class KlwpConfigActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "klwp_config"
        private const val KEY_KLWP_URI = "klwp_directory_uri"
        private const val KEY_LAST_CONTENT = "last_content"
        private const val CONFIG_FILE_NAME = "kuper_config.txt"
    }

    private lateinit var pathTextView: TextView
    private lateinit var formulaTextView: TextView
    private lateinit var contentEditText: EditText
    private lateinit var selectPathButton: Button
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button
    private lateinit var copyFormulaButton: Button

    // Theme Settings
    private lateinit var themeSettingsCard: MaterialCardView
    private lateinit var monetSwitch: MaterialSwitch
    private lateinit var colorStyleContainer: View
    private lateinit var colorStyleGroup: ChipGroup

    private var selectedDirectoryUri: Uri? = null

    // SAF 目录选择器
    private val directoryPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // 持久化权限
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            selectedDirectoryUri = it
            saveDirectoryUri(it)
            updatePathDisplay()
            Toast.makeText(this, "目录已选择", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before creation
        ThemeEngine.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klwp_config)

        // 设置 ActionBar
        supportActionBar?.apply {
            title = "KLWP 配置"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }

        // 初始化视图
        pathTextView = findViewById(R.id.pathTextView)
        formulaTextView = findViewById(R.id.formulaTextView)
        contentEditText = findViewById(R.id.contentEditText)
        selectPathButton = findViewById(R.id.selectPathButton)
        saveButton = findViewById(R.id.saveButton)
        clearButton = findViewById(R.id.clearButton)
        saveButton = findViewById(R.id.saveButton)
        clearButton = findViewById(R.id.clearButton)
        copyFormulaButton = findViewById(R.id.copyFormulaButton)
        
        // Theme UI
        themeSettingsCard = findViewById(R.id.themeSettingsCard)
        monetSwitch = findViewById(R.id.monetSwitch)
        colorStyleContainer = findViewById(R.id.colorStyleContainer)
        colorStyleGroup = findViewById(R.id.colorStyleGroup)
        
        setupThemeSettings()

        // 加载保存的配置
        loadSavedConfig()

        // 设置按钮点击事件
        selectPathButton.setOnClickListener {
            directoryPicker.launch(null)
        }

        saveButton.setOnClickListener {
            saveConfigFile()
        }

        clearButton.setOnClickListener {
            clearConfig()
        }

        copyFormulaButton.setOnClickListener {
            copyFormulaToClipboard()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * 加载保存的配置
     */
    private fun loadSavedConfig() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // 加载目录 URI
        val uriString = prefs.getString(KEY_KLWP_URI, null)
        if (uriString != null) {
            selectedDirectoryUri = Uri.parse(uriString)
        }
        
        // 加载上次的内容
        val lastContent = prefs.getString(KEY_LAST_CONTENT, "")
        contentEditText.setText(lastContent)
        
        updatePathDisplay()
    }

    /**
     * 更新路径显示和公式生成
     */
    private fun updatePathDisplay() {
        selectedDirectoryUri?.let { uri ->
            val docFile = DocumentFile.fromTreeUri(this, uri)
            val dirName = docFile?.name ?: uri.lastPathSegment ?: "未知目录"
            pathTextView.text = "已选目录: $dirName"
            
            // 生成公式
            val absolutePath = getAbsolutePathFromUri(uri)
            val formula = "\$wg(\"file://$absolutePath/$CONFIG_FILE_NAME\", raw)\$"
            formulaTextView.text = formula
            
            saveButton.isEnabled = true
            copyFormulaButton.isEnabled = true
            selectPathButton.text = "更改目录"
        } ?: run {
            pathTextView.text = "未选择目录"
            formulaTextView.text = "请先选择目录以生成公式"
            saveButton.isEnabled = false
            copyFormulaButton.isEnabled = false
            selectPathButton.text = "选择 KLWP 目录"
        }
    }

    /**
     * 尝试从 SAF URI 解析绝对路径
     * 这是一个简单的启发式方法，主要针对主存储
     */
    private fun getAbsolutePathFromUri(uri: Uri): String {
        val path = uri.path ?: return "/sdcard/Kustom"
        // 典型格式: /tree/primary:Kustom/document/primary:Kustom
        if (path.contains("primary:")) {
            val relativePath = path.substringAfter("primary:")
            // 处理可能的结尾斜杠
            val cleanPath = if (relativePath.endsWith("/")) relativePath.dropLast(1) else relativePath
            return "/sdcard/$cleanPath"
        }
        // 如果无法解析，回退到通用显示
        return "/sdcard/${uri.lastPathSegment?.replace("primary:", "") ?: "Kustom"}"
    }

    /**
     * 复制公式到剪贴板
     */
    private fun copyFormulaToClipboard() {
        val formula = formulaTextView.text.toString()
        if (formula.isEmpty() || formula.startsWith("请先")) return

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("KLWP Formula", formula)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "公式已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    /**
     * 保存目录 URI
     */
    private fun saveDirectoryUri(uri: Uri) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_KLWP_URI, uri.toString())
            .apply()
    }

    /**
     * 保存配置文件到 KLWP 目录
     */
    private fun saveConfigFile() {
        val uri = selectedDirectoryUri ?: run {
            Toast.makeText(this, "请先选择 KLWP 目录", Toast.LENGTH_SHORT).show()
            return
        }

        val content = contentEditText.text.toString()
        
        // 保存内容到偏好设置
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_CONTENT, content)
            .apply()

        try {
            val docFile = DocumentFile.fromTreeUri(this, uri) ?: run {
                Toast.makeText(this, "无法访问目录", Toast.LENGTH_SHORT).show()
                return
            }

            // 查找或创建配置文件
            var configFile = docFile.findFile(CONFIG_FILE_NAME)
            if (configFile == null) {
                configFile = docFile.createFile("text/plain", CONFIG_FILE_NAME)
            }

            configFile?.uri?.let { fileUri ->
                contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(content)
                    }
                }
                Toast.makeText(this, "配置已保存到 $CONFIG_FILE_NAME", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "无法创建配置文件", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    /**
     * 清除配置
     */
    private fun clearConfig() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        
        selectedDirectoryUri = null
        contentEditText.setText("")
        updatePathDisplay()
        
        Toast.makeText(this, "配置已清除", Toast.LENGTH_SHORT).show()
    }

    private fun setupThemeSettings() {
        if (!ThemeEngine.supportsMaterialYou()) {
            themeSettingsCard.visibility = View.GONE
            return
        }
        
        themeSettingsCard.visibility = View.VISIBLE
        val usageMode = ThemeEngine.getUsageMode()
        val isDynamic = usageMode == ThemeEngine.UsageMode.DYNAMIC
        
        monetSwitch.isChecked = isDynamic
        colorStyleContainer.visibility = if (isDynamic) View.VISIBLE else View.GONE
        
        val style = ThemeEngine.getColorStyle()
        if (style == ThemeEngine.ColorStyle.PASTEL) {
            colorStyleGroup.check(R.id.stylePastel)
        } else {
            colorStyleGroup.check(R.id.styleEmphasis)
        }
        
        monetSwitch.setOnCheckedChangeListener { _, isChecked ->
             val newMode = if (isChecked) ThemeEngine.UsageMode.DYNAMIC else ThemeEngine.UsageMode.STATIC
             ThemeEngine.setUsageMode(newMode)
             recreate()
        }
        
        colorStyleGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == View.NO_ID) return@setOnCheckedChangeListener
            
            val newStyle = when (checkedId) {
                R.id.stylePastel -> ThemeEngine.ColorStyle.PASTEL
                else -> ThemeEngine.ColorStyle.EMPHASIS
            }
            
            if (newStyle != ThemeEngine.getColorStyle()) {
                ThemeEngine.setColorStyle(newStyle)
                recreate()
            }
        }
    }
}
