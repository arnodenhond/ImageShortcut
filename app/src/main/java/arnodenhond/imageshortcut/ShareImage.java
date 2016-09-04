package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class ShareImage extends Activity {

    Intent viewintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri == null) {
            finish();
            return;
        }
        Uri fileUri = getFile(uri, this);

        viewintent = new Intent(Intent.ACTION_VIEW);
        viewintent.setDataAndType(fileUri, "image/*");

        Intent cropintent = makeCropIntent(uri);
        if (cropintent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cropintent, 0);
        } else {
            sendBroadcast(makeShortcutIntent(viewintent, fileUri));
            Toast.makeText(this,"Shortcut added",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            finish();
            return;
        }
        sendBroadcast(makeShortcutIntent(viewintent, data.getData()));
        Toast.makeText(this,"Shortcut added",Toast.LENGTH_SHORT).show();
        new File(data.getData().getEncodedPath()).delete();
        finish();
    }

    public static Bitmap iconify(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 8;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Intent makeShortcutIntent(Intent viewintent, Uri uri) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, viewintent);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconify(BitmapFactory.decodeFile(uri.getEncodedPath())));
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "");
        return shortcutintent;
    }

    public static Intent makeCropIntent(Uri uri) {
        Intent cropintent = new Intent("MOC.android.camera.action.CROP");
        cropintent.setDataAndType(uri, "image/*");
        cropintent.putExtra("crop", "true");
        cropintent.putExtra("aspectX", 1);
        cropintent.putExtra("aspectY", 1);
        cropintent.putExtra("outputX", 256);
        cropintent.putExtra("outputY", 256);
        cropintent.putExtra("scale", true);
        cropintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/imageshortcut.jpg"));
        return cropintent;
    }

    public static Uri getFile(Uri uri, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        String string = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
        cursor.close();
        return Uri.parse("file://" + string);
    }
}
