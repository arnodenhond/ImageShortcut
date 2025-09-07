package arnodenhond.imageshortcut


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : ComponentActivity() {


    companion object {
        const val EXTRA_SELECTED_URI = "selected_uri"
    }


    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val titleIntent = Intent(this, TitleActivity::class.java)
                .putExtra(EXTRA_SELECTED_URI, uri.toString())
            startActivity(titleIntent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// Clean orphaned images at app start.
        CleanupUtils.cleanupOrphanImages(this)


        val btn: Button = findViewById(R.id.btn_pick)
        btn.setOnClickListener { _: View ->
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    override fun onResume() {
        super.onResume()
// Clean each time the app gains focus.
        CleanupUtils.cleanupOrphanImages(this)
    }
}