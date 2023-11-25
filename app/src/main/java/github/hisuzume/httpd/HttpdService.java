package github.hisuzume.httpd;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.content.Intent;
import android.widget.Toast;

public class HttpdService extends Service {

	private boolean isstarted = false;
	private Httpd httpd;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		isstarted = true;

		Notification.Builder nb;

		if (Build.VERSION.SDK_INT < 26)
			nb = new Notification.Builder(this);
		else {
			// 初始化通知渠道
			NotificationChannel nc = new NotificationChannel("service_running", "Httpd is running",
					NotificationManager.IMPORTANCE_LOW);
			nc.setDescription("使 Httpd 能够在后台运行，若关闭，不能保证在所有系统上维持运行。");
			// 注册渠道
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(nc);
			nb = new Notification.Builder(this, "service_running");
		}

		SharedDataManager sdm = new SharedDataManager(this);

		int port = Integer.parseInt(sdm.get("port", "8080"));

		String path = sdm.get("rootDir", "/sdcard");
		
		boolean enableFileList = "true".equals(sdm.get("enableFileList","false"));

		nb.setContentTitle("Httpd 正在运行");

		nb.setContentText("正在托管 " + path + " 到端口 " + port + "，点击以打开设置～");

		nb.setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,SettingsActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
	
		nb.setSmallIcon(R.mipmap.ic_launcher_round);	
	
		// 转换成前台服务
		startForeground(31, nb.build());

		httpd = new Httpd(port, path);
		
		httpd.setFileListEnable(enableFileList);

		try {
			httpd.start();
		} catch (Exception e) {
			Toast.makeText(this, "启动服务失败！" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDestroy() {
		isstarted = false;

		// 停止服务
		httpd.closeAllConnections();
		httpd.stop();
		
		// 回收
		stopForeground(true);
		
		super.onDestroy();
	}

}