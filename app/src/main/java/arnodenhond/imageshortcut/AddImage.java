package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class AddImage extends Activity {

    public static final int REQUEST_PICK = RESULT_FIRST_USER;
    public static final int REQUEST_CROP = RESULT_FIRST_USER + 1;

    Intent viewintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            Toast.makeText(this, "Shortcut cancelled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        switch (requestCode) {
            case REQUEST_PICK:
                Uri uri = data.getData();
                Uri fileUri = ShareImage.getFile(uri, this);

                viewintent = new Intent(Intent.ACTION_VIEW);
                viewintent.setDataAndType(fileUri, "image/*");

                Intent intent = ShareImage.makeCropIntent(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CROP);
                } else {
                    setResult(RESULT_OK, ShareImage.makeShortcutIntent(viewintent, fileUri));
                    finish();
                }
                break;
            case REQUEST_CROP:
                setResult(RESULT_OK, ShareImage.makeShortcutIntent(viewintent, data.getData()));
                new File(data.getData().getEncodedPath()).delete();
                finish();
                break;
        }
    }
}


