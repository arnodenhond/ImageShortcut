package arnodenhond.imageshortcut

import android.content.Context
import android.content.pm.ShortcutManager
import java.io.File

object CleanupUtils {
    fun cleanupOrphanImages(context: Context) {
        val sm = context.getSystemService(ShortcutManager::class.java)
        val pinnedIds: Set<String> = sm.pinnedShortcuts.map { it.id }.toSet()

        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) return

        val files = imagesDir.listFiles() ?: return
        for (f in files) {
            val id = f.name
            if (!pinnedIds.contains(id)) {
                try { f.delete() } catch (_: Throwable) { /* ignore */ }
            }
        }
    }
}