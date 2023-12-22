package github.hisuzume.httpd;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends Activity {

	private SharedDataManager sdm;
	private EditText e_port;
	private EditText e_rootDir;
	private Switch s_enableFileList;
	private Switch s_enableService;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setTitle("设置");

		setContentView(R.layout.settings_acitivity);

		sdm = new SharedDataManager(this);

		e_port = findViewById(R.id.port);

		e_port.setText(sdm.get("port", ""));

		e_rootDir = findViewById(R.id.rootDir);

		e_rootDir.setText(sdm.get("rootDir", ""));
		
		s_enableFileList = findViewById(R.id.enableFileList);
		
		s_enableFileList.setChecked("true".equals(sdm.get("enableFileList","false")));
		
		s_enableService = findViewById(R.id.enableService);
		
		s_enableService.setChecked(HttpdService.isRunning());
		
		// 申请读取文件权限
		if (Build.VERSION.SDK_INT >= 23)
			requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, }, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		sdm.set("port", e_port.getText().toString());
		sdm.set("rootDir", e_rootDir.getText().toString());
		sdm.set("enableFileList",s_enableFileList.isChecked() == true ? "true" : "false");
		
		// 并不会引发错误，安心使用
		stopService(new Intent(this,HttpdService.class));
		
		if(s_enableService.isChecked())
			startForegroundService(new Intent(this,HttpdService.class));
	}

}