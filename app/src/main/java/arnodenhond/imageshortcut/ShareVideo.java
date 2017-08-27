package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class ShareVideo extends Activity {

    private Intent viewintent;
    private Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        Uri fileUri = Utils.getVideoFile(uri, this);
        if (fileUri==null) {
            Toast.makeText(this, R.string.notlocal, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        viewintent = Utils.makeVideoViewIntent(fileUri);

        image = Utils.saveBitmap(ThumbnailUtils.createVideoThumbnail(fileUri.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND),this);
        Log.d("ImageShortcut","preview uri: "+image.toString());
        Intent cropintent = Utils.makeCropIntent(image);
        if (cropintent.resolveActivity(getPackageManager()) != null && Utils.isExternalStorageWritable()) {
            startActivityForResult(cropintent, 0);
        } else {
            final Bitmap bitmap = Utils.iconify(ThumbnailUtils.createVideoThumbnail(fileUri.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND), this);
            final EditText title = new EditText(this);
            Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Utils.checkBegAndIncrement(ShareVideo.this);
                    sendBroadcast(Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString()));
                    Toast.makeText(ShareVideo.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, this).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.deleteBitmap(Utils.getImageFile(image, this),this);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            finish();
            return;
        }
        final Bitmap bitmap;
        if (data.getData() == null) {
            bitmap = Utils.iconify((Bitmap) data.getExtras().get("data"), this);
        } else {
            Uri fileUri = Utils.getImageFile(data.getData(), this);
            Log.d("ImageShortcut","fileuri"+fileUri.getPath());
            bitmap = Utils.iconify(BitmapFactory.decodeFile(fileUri.getPath()), this);
            new File(fileUri.getEncodedPath()).delete();
        }
        final EditText title = new EditText(this);
        Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utils.checkBegAndIncrement(ShareVideo.this);
                sendBroadcast(Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString()));
                Toast.makeText(ShareVideo.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, this).show();
    }
}
