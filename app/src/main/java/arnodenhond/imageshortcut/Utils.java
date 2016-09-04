package arnodenhond.imageshortcut;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class Utils {

    private static Bitmap iconify(Bitmap bitmap, Context context) {
        if (bitmap == null)
            return null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int iconsize = am.getLauncherLargeIconSize();

        bitmap = Bitmap.createScaledBitmap(bitmap, iconsize, iconsize, true);
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


    public static Intent makeShortcutIntent(Intent viewintent, Bitmap bitmap, Context context) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, viewintent);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconify(bitmap, context));
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "");
        return shortcutintent;
    }


    public static Intent makeCropIntent(Uri uri) {
        Intent cropintent = new Intent("com.android.camera.action.CROP");
        cropintent.setDataAndType(uri, "image/*");
        cropintent.putExtra("crop", "true");
        cropintent.putExtra("aspectX", 1);
        cropintent.putExtra("aspectY", 1);
        cropintent.putExtra("outputX", 256);
        cropintent.putExtra("outputY", 256);
        cropintent.putExtra("scale", true);
        cropintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/imageshortcut.jpg"));
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

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
