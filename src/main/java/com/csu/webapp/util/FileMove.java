package com.csu.webapp.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileMove {
	/**
	 * 移动指定文件夹内的全部文件
	 * 
	 * @param fromDir
	 *            要移动的文件目录
	 * @param toDir
	 *            目标文件目录
	 * @throws Exception
	 */
	public static void fileMove(String from, String to) throws Exception {
		System.out.println("-----------------【移动文件夹开始】--------------------");

		File dir = new File(from);
		// 文件一览
		File[] files = dir.listFiles();
		if (files == null)
			return;
		// 目标
		File moveDir = new File(to);
		if (!moveDir.exists()) {
			moveDir.mkdirs();
		}
		// 文件移动
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fileMove(files[i].getPath(), to + "/" + files[i].getName());
				// 目前暂不能删除
				// 成功，删除原文件
				// files[i].delete();
			}
			File moveFile = new File(moveDir.getPath() + "/"
					+ files[i].getName());
			// 目标文件夹下存在的话，删除
			if (moveFile.exists()) {
				moveFile.delete();
			}
			// REPLACE_EXISTING
			// //Files.copy(files[i], moveFile.resolve(files[i].getName());
			// Files.copy(files[i], moveFile, options)
			files[i].renameTo(moveFile);
		}

		System.out.println("-----------------【移动文件夹结束】--------------------");
	}

	public static void fileCopy(String from, String to) throws Exception {
		System.out.println("-----------------【移动文件夹开始】--------------------");

		File dir = new File(from);
		// 文件一览
		File[] files = dir.listFiles();
		if (files == null)
			return;
		// 目标
		File moveDir = new File(to);
		if (!moveDir.exists()) {
			moveDir.mkdirs();
		}
		// 文件移动
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fileMove(files[i].getPath(), to + "/" + files[i].getName());
				// 目前暂不能删除
				// 成功，删除原文件
				// files[i].delete();
			}
			File moveFile = new File(moveDir.getPath() + "/"
					+ files[i].getName());
			// 目标文件夹下存在的话，删除
			if (moveFile.exists()) {
				moveFile.delete();
			}
			// REPLACE_EXISTING
			Files.copy(
					FileSystems.getDefault()
							.getPath(files[i].getAbsolutePath()), FileSystems
							.getDefault().getPath(moveFile.getAbsolutePath()),
					StandardCopyOption.REPLACE_EXISTING);

		}

		System.out.println("-----------------【移动文件夹结束】--------------------");
	}

	// public static void main(String[] args) throws Exception {
	// fileCopy("C:/Users/Administrator/Desktop/llll",
	// "C:/Users/Administrator/Desktop/ldap/lll");
	// }
}