package com.universe.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuyalou
 * @Description:
 * @since 2019年5月6日
 * <p>
 */
public class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static final String DEDAULT_SEPERATOR = File.separator;

    /**
     * 将文件读入内存中
     *
     * @param filePath 文件的抽象路径
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] readFiletoByteArray(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[1024];
            int length = -1;
            while ((length = fis.read(b)) != -1) {
                baos.write(b, 0, length);
                baos.flush();
            }
        } catch (Exception e) {
            throw e;
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
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(content.getBytes());
            bos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 加载属性文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static Properties loadProperties(String path) throws Exception {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(path);
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (Exception e) {
            throw e;
        }

        return props;
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            obj = ois.readObject();
        } catch (Exception e) {
            throw e;
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 以不同单位表示文件大小
     *
     * @param length 文件长度
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
     * 用户目录下的文件路径
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static String getUserPath(String dir, String fileName) {
        String basePath = System.getProperty("user.home");
        if (StringUtils.isBlank(dir)) {
            return basePath + DEDAULT_SEPERATOR;
        }

        if (StringUtils.isBlank(fileName)) {
            return basePath + DEDAULT_SEPERATOR + dir + DEDAULT_SEPERATOR;
        }

        return basePath + DEDAULT_SEPERATOR + dir + DEDAULT_SEPERATOR + fileName;
    }

    /**
     * 项目路径下的文件路径
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static String getClassPath(String dir, String fileName) {
        String basePath = System.getProperty("user.dir");
        if (StringUtils.isBlank(dir)) {
            return basePath + DEDAULT_SEPERATOR;
        }

        if (StringUtils.isBlank(fileName)) {
            return basePath + DEDAULT_SEPERATOR + dir + DEDAULT_SEPERATOR;
        }

        return basePath + DEDAULT_SEPERATOR + dir + DEDAULT_SEPERATOR + fileName;
    }

}
