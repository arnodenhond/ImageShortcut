package arnodenhond.imageshortcut

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class TitleActivity : Activity() {

    private var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        val etTitle: EditText = findViewById(R.id.et_title)
        val btnCreate: Button = findViewById(R.id.btn_create)

        val uriStr = intent.getStringExtra(MainActivity.EXTRA_SELECTED_URI)
        selectedUri = if (uriStr != null) Uri.parse(uriStr) else null

        btnCreate.setOnClickListener {
            val title = etTitle.text?.toString()?.trim().orEmpty()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uri = selectedUri
            if (uri == null) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createPinnedShortcut(title, uri)
        }
    }

    private fun createPinnedShortcut(title: String, sourceUri: Uri) {
        val id = UUID.randomUUID().toString()

        // Copy the image to private images dir with the file name equal to id.
        val imagesDir = File(filesDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val destFile = File(imagesDir, id)
        copyUriToFile(sourceUri, destFile)

        // Build a content Uri exposed via our provider for that id.
        val contentUri = Uri.parse("content://arnodenhond.imageshortcut.images/$id")

        // Decode a small bitmap for the shortcut icon.
        val iconBitmap = decodeScaledBitmapFromUri(sourceUri, 256, 256) ?: run {
            Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show()
            return
        }

        val icon = Icon.createWithAdaptiveBitmap(iconBitmap)

        // Intent to open in default image viewer; grant read permission.
        val viewIntent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(contentUri, "image/*")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val shortcut = ShortcutInfo.Builder(this, id)
            .setShortLabel(title)
            .setIcon(icon)
            .setIntent(viewIntent)
            .build()

        val sm = getSystemService(ShortcutManager::class.java)

        if (sm.isRequestPinShortcutSupported) {
            // Ensure main-thread call.
            Handler(Looper.getMainLooper()).post {
                sm.requestPinShortcut(shortcut, null)
                Toast.makeText(this, "Pin request sent", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Pinned shortcuts not supported", Toast.LENGTH_LONG).show()
        }
    }

    private fun copyUriToFile(uri: Uri, dest: File) {
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dest).use { output ->
                val buffer = ByteArray(8 * 1024)
                while (true) {
                    val read = input.read(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }

    private fun decodeScaledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var inSampleSize = 1
        val (width, height) = bounds.outWidth to bounds.outHeight
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        val decode = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        contentResolver.openInputStream(uri)?.use { stream2 ->
            return BitmapFactory.decodeStream(stream2, null, decode)
        }
        return null
    }
}