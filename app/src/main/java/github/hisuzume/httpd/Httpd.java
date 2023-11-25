package github.hisuzume.httpd;

import android.app.appsearch.ReportSystemUsageRequest;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;

public class Httpd extends NanoHTTPD {
	private String rootDir;
	private boolean enableFileList = false;

	/* 例如你访问 http://localhost:8080/ ，那么你可能实际访问 http://localhost:8080/index.html */
	public static final String[] DICTIONARY_DEFAULT_FILES = new String[] { "index.html", "index.htm", };

	public Httpd(int p, String rootDir) {
		super(p);
		this.rootDir = rootDir;
	}

	// 允许文件列表
	public void setFileListEnable(boolean shit) {
		enableFileList = shit;
	}

	public static String getMimeForFile(File f) {
		return getMimeForFile(f + "");
	}

	public static String getMimeForFile(String f) {
		// 默认：输出
		String mime = "application/octet-stream";
		try {
			// 获取后辍名
			switch (f.substring(f.lastIndexOf(".") + 1)) {
			case "css":
				mime = "text/css";
				break;
			case "htm":
			case "html":
				mime = "text/html";
				break;
			case "xml":
				mime = "text/xml";
				break;
			case "md":
			case "txt":
				mime = "text/plain";
				break;
			case "gif":
				mime = "image/gif";
				break;
			case "jpg":
			case "jpeg":
				mime = "image/jpeg";
				break;
			case "png":
				mime = "image/png";
				break;
			case "svg":
				mime = "image/svg+xml";
				break;
			case "mp3":
				mime = "audio/mpeg";
				break;
			case "m3u":
				mime = "audio/mpeg-url";
				break;
			case "mp4":
				mime = "video/mp4";
				break;
			case "flv":
				mime = "video/x-flv";
				break;
			case "js":
				mime = "application/javascript";
				break;
			case "ogg":
				mime = "application/x-ogg";
				break;
			case "m3u8":
				mime = "application/vnd.apple.mpegurl";
				break;
			case "ts":
				mime = "video/mp2t";
				break;
			}
		} catch (Exception e) {
			// 忽略...
		}
		return mime;
	}

	@Override
	public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession req) {
		try {
			File reqFile = new File(rootDir + req.getUri());

			// 检测文件是否存在
			if (!reqFile.exists())
				return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 - 你所请求的资源不存在！");

			if (reqFile.isDirectory()) {
				// 检测诸如 index.html 的默认访问文件
				File _reqFile;
				for (String defFile : DICTIONARY_DEFAULT_FILES) {
					_reqFile = new File(reqFile + "/" + defFile);
					if (_reqFile.exists())
						return newChunkedResponse(Response.Status.OK, null, new FileInputStream(_reqFile));
				}

				// 访问文件夹处理	
				if (enableFileList) {
					String inside = "<a href='../'>../</a><br/>";
					try {
						for (File subFile : reqFile.listFiles()) {
							if (subFile.isFile())
								inside += "<a href='" + subFile.getName() + "'>" + subFile.getName() + "</a><br/>";
							else
								inside += "<a href='" + subFile.getName() + "/'>" + subFile.getName() + "/</a><br/>";
						}
					} catch (Exception e) {
						// 忽略...
					}
					// 返回文件列表
					return newFixedLengthResponse(Response.Status.OK, "text/html",
							"<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>文件列表 - "
									+ reqFile + "</title></head><body><h>文件列表：</h><br/>" + inside + "</body></html>");
				} else
					//报告错误信息
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "400 - 无效访问！");
			}

			// 默认文件处理
			return newChunkedResponse(Response.Status.OK, getMimeForFile(reqFile), new FileInputStream(reqFile));
		} catch (Exception e) {
			// 异常处理
			return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
					"500 - 服务器出错！" + e.getMessage());
		}
	}

}