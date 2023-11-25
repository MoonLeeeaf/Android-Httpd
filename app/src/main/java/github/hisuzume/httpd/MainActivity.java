package github.hisuzume.httpd;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startForegroundService(new Intent(this, HttpdService.class));

		// 动态权限申请
		if (Build.VERSION.SDK_INT >= 23)
			requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, }, 0);

		finish();
	}
}