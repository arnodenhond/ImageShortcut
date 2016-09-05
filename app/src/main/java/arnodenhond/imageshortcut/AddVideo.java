package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class AddVideo extends Activity {

    private Intent viewintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(this, "Shortcut cancelled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Uri uri = data.getData();
        Uri fileUri = Utils.getVideoFile(uri, this);
        if (fileUri.equals("file://null")) {
            Toast.makeText(this, R.string.notlocal, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewintent = Utils.makeVideoViewIntent(fileUri);
        final Bitmap bitmap = Utils.iconify(ThumbnailUtils.createVideoThumbnail(fileUri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND), this);
        final EditText title = new EditText(this);
        Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utils.checkBegAndIncrement(AddVideo.this);
                setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString()));
                Toast.makeText(AddVideo.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, this).show();
    }

}


