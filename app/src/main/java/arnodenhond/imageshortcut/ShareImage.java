package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
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
        Uri fileUri = Utils.getFile(uri, this);

        viewintent = Utils.makeViewIntent(fileUri);

        Intent cropintent = Utils.makeCropIntent(uri);
        if (cropintent.resolveActivity(getPackageManager()) != null && Utils.isExternalStorageWritable()) {
            startActivityForResult(cropintent, 0);
        } else {
            final Bitmap bitmap = Utils.iconify(BitmapFactory.decodeFile(fileUri.getEncodedPath()), this);
            final EditText title = new EditText(this);
            Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Utils.checkBegAndIncrement(ShareImage.this);
                    sendBroadcast(Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString()));
                    Toast.makeText(ShareImage.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, this).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            finish();
            return;
        }
        final Bitmap bitmap;
        if (data.getData() == null) {
            bitmap = Utils.iconify((Bitmap) data.getExtras().get("data"), this);
        } else {
            String file = data.getData().getEncodedPath();
            bitmap = Utils.iconify(BitmapFactory.decodeFile(file), this);
            new File(file).delete();
        }
        final EditText title = new EditText(this);
        Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utils.checkBegAndIncrement(ShareImage.this);
                sendBroadcast(Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString()));
                Toast.makeText(ShareImage.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, this).show();
    }


}
