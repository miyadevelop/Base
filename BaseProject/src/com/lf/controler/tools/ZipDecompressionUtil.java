package com.lf.controler.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.content.Context;

/**
 * 文件的复制管理
 */
public class ZipDecompressionUtil {

	/**
	 * 解压SD卡上的文件
	 * 
	 * @param fromRoute
	 *            复制的源头，这里是一个压缩包的路径
	 * @param toRoute
	 *            复制到的位置
	 * @param saveFile
	 *            要保留文件的位置，null表示替换所有
	 * @throws IOException
	 */
	public static final void uncompressesResourceFromSD(String fromRoute, String toRoute,
			String saveFile) throws IOException {
		if (!toRoute.endsWith("/"))
			toRoute += "/";

		new File(toRoute).mkdirs();
		final FileInputStream fileIs = new FileInputStream(fromRoute);
		final ZipInputStream zipIs = new ZipInputStream(fileIs);
		final ZipFile zipFile = new ZipFile(fromRoute);
		ZipEntry zipEntry;
		while ((zipEntry = zipIs.getNextEntry()) != null) {
			String insideName = zipEntry.getName(); // 获取到在压缩包中名称
			if ((insideName == null) || (!insideName.contains(".")))
				continue; // 不包含“.”表示当前的位置指向一个目录
			InputStream is = zipFile.getInputStream(zipEntry);
			if (saveFile == null || (!insideName.endsWith(saveFile)))
				copyInsideResource(is, toRoute, insideName);
		}

	}

	/**
	 * 复制压缩包里面的文件到指定的位置
	 * 
	 * @param is
	 *            复制文件源的位置
	 * @param toRoute
	 *            复制的地址
	 * @param insideRoute
	 *            在压缩包内部的位置
	 */
	private static void copyInsideResource(InputStream is, String toRoute, String insideRoute) {
		BufferedInputStream bis = new BufferedInputStream(is);
		int indexFirstFolder = insideRoute.indexOf("/"); // 第一次出现“/”的位置
		int indexFolder = insideRoute.lastIndexOf("/"); // 记录最后出现“/”的位置
		if (indexFolder != -1 && indexFirstFolder != indexFolder) {
			final String copyFolder = toRoute
					+ insideRoute.subSequence(indexFirstFolder + 1, indexFolder);
			if (!new File(copyFolder).exists())
				new File(copyFolder).mkdirs();
		}
		final String insideStr = toRoute + insideRoute.substring(indexFirstFolder + 1);
		// 此处不需要进行文件是不是存在的判断。在因为在每次复制之前都会将之前的删除
		File insideFile = new File(insideStr);
		try {
			insideFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(insideFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int len = -1;
			while ((len = bis.read()) != -1) {
				bos.write(len);
			}
			bos.flush();
			bos.close();
			fos.close();
			bis.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 实现文件的复制功能,在内部调用
	 * 
	 * @param folder
	 *            文件存储的路径，不包含文件
	 * @param name
	 *            文件的文件名,就是存储时候的绝对路径
	 * @param buis
	 *            要复制文件的流
	 * @param key
	 */
	public static boolean WriteToSD(String saveFolder, String name, BufferedInputStream buis) {
		boolean doneCopy = false;
		if (!saveFolder.endsWith("/"))
			saveFolder += "/";
		File folder = new File(saveFolder);
		if (!folder.exists())
			folder.mkdirs();
		File file = new File(name);
		if (file.exists())
			file.delete();
		if (!file.exists() || file.length() == 0) {
			try {
				file.createNewFile();
				BufferedOutputStream buos = new BufferedOutputStream(new FileOutputStream(file));
				int len = -1;
				while ((len = buis.read()) != -1) {
					buos.write(len);
				}
				buos.flush();
				if (buos != null)
					buos.close();
				doneCopy = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				file.delete(); // 下载失败的时候删除文件
				e.printStackTrace();
			}
		}
		return doneCopy;
	}

	/**
	 * 将asset目录下面的压缩包资源解压到指定的地方，这个方法仅仅针对工程
	 * 
	 * @param folder
	 *            文件解压之后的输出路径，这里是一个绝对的路径，以"/"结尾
	 * @param fileRoute
	 *            压缩资源的路径，如果在assets下面就一assets开头，不然就以sdcard开头
	 */
	public static void uncompressesResourceFromAssets(Context context, String folder,
			String orginalCabinetRoute) {
		File saveFolderFile = new File(folder);
		if (!saveFolderFile.exists())
			saveFolderFile.mkdirs();
		ZipInputStream zipIs = null;
		try {
			InputStream readIs = context.getAssets().open(
					orginalCabinetRoute.substring(orginalCabinetRoute.indexOf("/") + 1));
			zipIs = new ZipInputStream(readIs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (zipIs == null)
			return;
		ZipEntry entry;
		try {
			BufferedInputStream buis = new BufferedInputStream(zipIs);
			while ((entry = zipIs.getNextEntry()) != null) {// 这里的文件是在assets下面的，只有一张图片
				String saveFileName;
				if (!entry.getName().contains("."))
					continue;
				if (entry.getName().endsWith(".xml"))
					saveFileName = "description.xml";// 获取到xml文件名
				else if (entry.getName().endsWith(".zip"))
					saveFileName = "live.zip";// 获取到zip的文件名
				else
					saveFileName = "big"
							+ entry.getName().substring(entry.getName().lastIndexOf("."));// 获取到图片
				WriteToSD(folder, folder + saveFileName, buis);// 按照原来的文件名称存储
			}
			zipIs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
