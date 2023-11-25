package github.hisuzume.httpd;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedDataManager {
	private Context c;
	private SharedPreferences sp;

	public SharedDataManager(Context c) {
		this.c = c;
		sp = c.getSharedPreferences("data", Context.MODE_PRIVATE);
	}

	public SharedDataManager set(String key, String value) {
		sp.edit().putString(key, value).apply();
		return this;
	}

	public String get(String key, String def) {
		String s = sp.getString(key, def);
		if ("".equals(s))
			return def;
		return s;
	}

}