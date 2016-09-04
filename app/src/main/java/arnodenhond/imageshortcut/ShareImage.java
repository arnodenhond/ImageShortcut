package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class ShareImage extends Activity {

    private Intent viewintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri == null) {
            finish();
            return;
        }
        Uri fileUri = Utils.getFile(uri, this);

        viewintent = new Intent(Intent.ACTION_VIEW);
        viewintent.setDataAndType(fileUri, "image/*");

        Intent cropintent = Utils.makeCropIntent(uri);
        if (cropintent.resolveActivity(getPackageManager()) != null && Utils.isExternalStorageWritable()) {
            startActivityForResult(cropintent, 0);
        } else {
            sendBroadcast(Utils.makeShortcutIntent(viewintent, BitmapFactory.decodeFile(fileUri.getEncodedPath()), this));
            Toast.makeText(this, "Shortcut added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            finish();
            return;
        }
        Bitmap bitmap;
        if (data.getData() == null) {
            bitmap = (Bitmap) data.getExtras().get("data");
        } else {
            bitmap = BitmapFactory.decodeFile(data.getData().getEncodedPath());
        }
        sendBroadcast(Utils.makeShortcutIntent(viewintent, bitmap, this));
        Toast.makeText(this, "Shortcut added", Toast.LENGTH_SHORT).show();
        if (data.getData() != null) {
            new File(data.getData().getEncodedPath()).delete();
        }
        finish();
    }

}
