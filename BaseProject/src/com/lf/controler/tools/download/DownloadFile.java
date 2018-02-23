package com.lf.controler.tools.download;

import java.io.File;
import java.io.IOException;

public class DownloadFile {
	/**
	 * 在sd卡中创建文件
	 * 
	 * @param path
	 *            文件的路径
	 * @return file
	 * @throws IOException
	 */
	public static File create(String path) throws IOException {
		File file = new File(path);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}
}
