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

	private static boolean isstarted = false;
	private Httpd httpd;
	
	public static boolean isRunning() {
		return isstarted;
	}

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
			nc.setDescription("按道理来说只要允许后台运行和自启动都无所谓，如果被杀后台了，请尝试显示此通知！");
			// 注册渠道
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(nc);
			nb = new Notification.Builder(this, "service_running");
		}

		SharedDataManager sdm = new SharedDataManager(this);

		int port = Integer.parseInt(sdm.get("port", "8080"));

		String path = sdm.get("rootDir", "/sdcard");
		
		boolean enableFileList = "true".equals(sdm.get("enableFileList","false"));

		nb.setContentTitle("Httpd 正在运行");

		nb.setContentText("正在托管文件夹 " + path + " 到端口 " + port + "～");

		nb.setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,SettingsActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
	
		nb.setSmallIcon(R.mipmap.ic_launcher_round);	
	
		// 转换成前台服务
		startForeground(31, nb.build());

		httpd = new Httpd(port, path);
		
		httpd.setFileListEnable(enableFileList);

		try {
			httpd.start();
			Toast.makeText(this, "Httpd 已启动～", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, "启动 Httpd 失败！" + e.getMessage(), Toast.LENGTH_LONG).show();
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