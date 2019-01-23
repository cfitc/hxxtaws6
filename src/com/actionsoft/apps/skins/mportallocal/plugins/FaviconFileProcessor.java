package com.actionsoft.apps.skins.mportallocal.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import com.actionsoft.bpms.server.fs.AbstFileProcessor;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.server.fs.FileProcessorListener;
import com.actionsoft.bpms.server.fs.dc.DCMessage;
import com.actionsoft.bpms.util.UtilIO;

public class FaviconFileProcessor extends AbstFileProcessor implements FileProcessorListener {
	private static final String[] images = { "png", "jpg", "jpeg", "ico" };
	private static final String tempSuffix = "_big";

	/**
	 * 获得上传图形的后缀
	 * 
	 * @param fn
	 * @return
	 */
	public static String getPhotoFileSuffix(String fn) {
		String suffix = fn.substring(fn.lastIndexOf("."));
		return suffix;
	}

	public boolean uploadReady(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		File folder = new File(context.getPath());
		// 文件的后缀 如.jpg
		String postfix = context.getFileName();
		InputStream in = (InputStream) param.get("data");
		if (!folder.exists()) {
			folder.mkdirs();
		} else {
			// 将其他文件删除，避免用户上传多个不同文件类型的头像（源头像）
			/* for (File file : folder.listFiles()) { file.delete(); } */
		}
		context.setFileName("tempfav.ico");
		// 保存源图片
		DataInputStream data = (DataInputStream) param.get("data");
		DataOutputStream os = null;
		File tempImage = new File(context.getPath() + context.getFileName());
		try {
			os = new DataOutputStream(new FileOutputStream(tempImage));
			UtilIO.copy(data, os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			context.setDCMessage(DCMessage.ERROR, e.getMessage());
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
		}
		context.setDCMessage(DCMessage.OK, "");
		context.getDCMessage().addAttr("fileName", context.getFileName());
		String url = context.getDownloadURL();
		context.getDCMessage().addAttr("url", url);
		return true;
	}

	public void uploadError(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
	}

	public void uploadSuccess(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		context.setDCMessage(DCMessage.OK, "");
		context.getDCMessage().addAttr("fileName", context.getFileName());
		context.getDCMessage().addAttr("url", context.getDownloadURL());
	}

	@Override
	public InputStream downloadContent(Map<String, Object> param) throws Exception {
		DCContext context = (DCContext) param.get("DCContext");
		InputStream fav = new FileInputStream(context.getFilePath());
		return fav;

	}

	public void downloadComplete(Map<String, Object> param) {
		super.downloadComplete(param);
	}
}
