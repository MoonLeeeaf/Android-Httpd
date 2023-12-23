package github.hisuzume.httpd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import java.io.File;

public class StartHttpdAtAnotherDirActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		SharedDataManager sdm = new SharedDataManager(this);
		
		String dic = new File(getIntent().getData().getPath()).getParent();
		
		sdm.set("rootDir",dic);
		
		stopService(new Intent(this,HttpdService.class));
		
		startForegroundService(new Intent(this,HttpdService.class));
		
		finish();
	}
	
}