package arnodenhond.imageshortcut;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

/**
 * Created by arnodenhond on 08/10/16.
 */
public class AddShortcut extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent pickintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickintent.addCategory(Intent.CATEGORY_OPENABLE);
        pickintent.setType("*/*");
        if (pickintent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(this, "pick a file to create shortcut to", Toast.LENGTH_SHORT).show();
            startActivityForResult(pickintent, 0);
        } else {
            Toast.makeText(this, R.string.couldnotpick, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(this, "Shortcut cancelled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
       //String filepath = Utils.getFileUrl(data.getData(),this);
        Intent viewintent = new Intent(Intent.ACTION_VIEW);
        viewintent.setDataAndType(data.getData(),getContentResolver().getType(data.getData()));
        if (viewintent.resolveActivity(getPackageManager())!=null) {
            setResult(RESULT_OK, Utils.makeShortcutIntent(viewintent, null, "shortcut title"));
        } else {
            Toast.makeText(this, "No activity for "+viewintent.toString(), Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
