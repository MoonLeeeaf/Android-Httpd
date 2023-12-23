package github.hisuzume.httpd;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class StartBrowserForLoaclHostActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:8080/")));
		
		new Handler(Looper.getMainLooper()).postDelayed(() -> finish(),10);
	}
	
}