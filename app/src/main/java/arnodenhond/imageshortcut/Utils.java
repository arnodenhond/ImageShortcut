package arnodenhond.imageshortcut;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by arnodenhond on 04/09/16.
 */
public class Utils {

    public static Bitmap iconify(Bitmap bitmap, Context context) {
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

    public static Intent makeShortcutIntent(Intent viewintent, Bitmap bitmap, String title, Context context) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, viewintent);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        return shortcutintent;
    }

    public static Intent makeCropIntent(Uri uri) {
        Intent cropintent = new Intent("com.android.camera.action.CROP");
        cropintent.setDataAndType(uri, "image/*");
        cropintent.putExtra("return-data", true);
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

    public static AlertDialog.Builder buildTitleDialog(final Bitmap bitmap, EditText title, DialogInterface.OnClickListener oklistener, final Activity activity) {
        AlertDialog.Builder titleAlert = new AlertDialog.Builder(activity);
        titleAlert.setTitle(R.string.settitle);
        titleAlert.setView(title);
        titleAlert.setIcon(new BitmapDrawable(bitmap));
        titleAlert.setNeutralButton(R.string.ok, oklistener);
        titleAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(activity, R.string.shortcutcanceled, Toast.LENGTH_SHORT).show();
                activity.setResult(Activity.RESULT_CANCELED);
                activity.finish();
            }
        });
        return titleAlert;
    }

}
