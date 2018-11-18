package com.universe.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * IO操作的工具类
 * 
 * @author Administrator
 *
 */
public class IOUtil {
	/**
	 * 将文件读入内存中
	 * 
	 * @param filePath
	 *            文件的抽象路径
	 * @return 字节数组
	 * @throws IOException
	 */
	public static byte[] readFile(File file) throws IOException {
		// ByteArrayOutputStream是向内存写入数据的流，主要用于缓冲
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int length = -1;
			while ((length = fis.read(b)) != -1) {
				baos.write(b, 0, length);
				baos.flush();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				LogUtil.error(e);
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 向指定文件写入内容
	 * 
	 * @param filePath
	 * @param content
	 */
	public static void write(String filePath, String content) {
		File file = new File(filePath);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(content.getBytes());
			bos.flush();
		} catch (IOException e) {
			LogUtil.error(e);
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				LogUtil.error(e);
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Object readObject(File file) throws Exception {
		Object obj = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			obj = ois.readObject();
		} catch (Exception e) {
			throw e;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					LogUtil.error(e);
				}
			}
		}
		return obj;
	}

	/**
	 * 将对象写入指定文件中
	 * 
	 * @param filePath
	 * @param obj
	 */
	public static void writeObject(String filePath, Object obj) {
		File file = new File(filePath);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
			oos.flush();
		} catch (Exception e) {
			LogUtil.error(e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					LogUtil.error(e);
				}
			}
		}
	}

	/**
	 * 以不同单位表示文件大小
	 * 
	 * @param length
	 *            文件长度
	 * @return 文件长度字符串
	 */
	public static String parseFileSize(long length) {
		if (length / 1024 / 1024 / 1024 / 1024 / 1024 > 0) {
			return length / 1024 / 1024 / 1024 / 1024 / 1024 + "PB";
		} else if (length / 1024 / 1024 / 1024 / 1024 > 0) {
			return length / 1024 / 1024 / 1024 / 1024 + "TB";
		} else if (length / 1024 / 1024 / 1024 > 0) {
			return length / 1024 / 1024 / 1024 + "GB";
		} else if (length / 1024 / 1024 > 0) {
			return length / 1024 / 1024 + "MB";
		} else if (length / 1024 > 0) {
			return length / 1024 + "KB";
		} else {
			return length + "B";
		}
	}

	/**
	 * 
	 * @param file
	 *            文件名
	 * @return 用户目录下的文件路径
	 */
	public static String getUserPath(String file) {
		String path = System.getProperty("user.home");
		String filePath = null;
		if (file == null) {
			filePath = path + File.separator;
		} else {
			filePath = path + File.separator + file;
		}
		return filePath;
	}

	/**
	 * 项目路径
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getClassPath(String fileName) {
		String path = System.getProperty("user.dir");
		String filePath = null;
		if (fileName == null) {
			filePath = path + File.separator;
		} else {
			filePath = path + File.separator + fileName;
		}
		return filePath;
	}
}
