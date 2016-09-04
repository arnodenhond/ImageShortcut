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
public class AddImage extends Activity {

    private static final int REQUEST_PICK = RESULT_FIRST_USER;
    private static final int REQUEST_CROP = RESULT_FIRST_USER + 1;

    private Intent viewintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(this, "Shortcut cancelled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        switch (requestCode) {
            case REQUEST_PICK:
                Uri uri = data.getData();
                Uri fileUri = Utils.getFile(uri, this);

                viewintent = new Intent(Intent.ACTION_VIEW);
                viewintent.setDataAndType(fileUri, "image/*");

                Intent intent = Utils.makeCropIntent(uri);
                if (intent.resolveActivity(getPackageManager()) != null && Utils.isExternalStorageWritable()) {
                    startActivityForResult(intent, REQUEST_CROP);
                } else {
                    setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, BitmapFactory.decodeFile(fileUri.getEncodedPath()), this));
                    finish();
                }
                break;
            case REQUEST_CROP:
                Bitmap bitmap;
                if (data.getData() == null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else {
                    bitmap = BitmapFactory.decodeFile(data.getData().getEncodedPath());
                }
                setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, bitmap, this));
                if (data.getData() != null) {
                    new File(data.getData().getEncodedPath()).delete();
                }
                finish();
                break;
        }
    }

}


