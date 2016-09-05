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
                //TODO add flags
                viewintent.setDataAndType(fileUri, "image/*");

                Intent intent = Utils.makeCropIntent(uri);
                if (intent.resolveActivity(getPackageManager()) != null && Utils.isExternalStorageWritable()) {
                    startActivityForResult(intent, REQUEST_CROP);
                } else {
                    final Bitmap bitmap = Utils.iconify(BitmapFactory.decodeFile(fileUri.getEncodedPath()), this);
                    final EditText title = new EditText(this);
                    Utils.buildTitleDialog(bitmap, title, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString(), AddImage.this));
                            Toast.makeText(AddImage.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, this).show();
                }
                break;
            case REQUEST_CROP:
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
                        setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, bitmap, title.getText().toString(), AddImage.this));
                        Toast.makeText(AddImage.this, R.string.shortcutadded, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, this).show();
                break;
        }
    }

}


