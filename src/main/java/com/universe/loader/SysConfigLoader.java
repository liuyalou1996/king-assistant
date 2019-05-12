package com.universe.loader;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysConfigLoader {

  private static final Logger logger = LoggerFactory.getLogger(SysConfigLoader.class);
  /**
   * 占位符解析模式
   */
  private static final Pattern PATTERN = Pattern.compile("(\\$\\{((?:\\w|\\.)*)\\})");

  /**
   * 系统配置
   */
  private static final Map<String, String> SYS_CONFIG_MAP = new HashMap<>();

  static {
    // 类加载时加载系统配置
    loadSysConfig();
    // 这里开启一个线程注册文件监听器，否则会一直等待
    new Thread(() -> {
      try {
        registerWithWatchService();
      } catch (Exception e) {
        logger.error("注册文件监听器失败:==> {}", e.getMessage(), e);
      }
    }).start();
  }

  public static String getProperty(String key) {
    return SYS_CONFIG_MAP.get(key);
  }

  private static void loadSysConfig() {
    // ResourceBundle有缓存，重新读取配置前要先清除缓存
    ResourceBundle.clearCache();
    ResourceBundle bundle = ResourceBundle.getBundle("config");
    Enumeration<String> keys = bundle.getKeys();
    String key = null;
    while (keys.hasMoreElements()) {
      key = keys.nextElement();
      SYS_CONFIG_MAP.put(key, bundle.getString(key));
    }

    SYS_CONFIG_MAP.forEach((propName, propValue) -> {
      // 解析其中的占位符
      parsePlaceholder(propName, propValue);
    });

  }

  private static void parsePlaceholder(String propName, String propValue) {
    Matcher matcher = PATTERN.matcher(propValue);
    while (matcher.find()) {
      String target = matcher.group(1);
      // 分组中的内容为引用的变量
      String refKey = matcher.group(2);
      String replaced = propValue.replace(target, SYS_CONFIG_MAP.get(refKey));
      // 如果还有占位符，递归再次替换
      if (replaced.contains("$")) {
        parsePlaceholder(propName, replaced);
      } else {
        SYS_CONFIG_MAP.put(propName, replaced);
      }
    }
  }

  private static void registerWithWatchService() throws Exception {
    WatchService watcher = FileSystems.getDefault().newWatchService();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URI uri = loader.getResource("").toURI();
    Path path = Paths.get(uri);

    // 在WatchService上注册
    path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
    while (true) {
      WatchKey key = watcher.take();
      key.pollEvents().forEach(event -> {
        logger.info("系统配置文件{}变动，开始重新加载配置文件...", event.context());
        loadSysConfig();
        logger.info("加载完毕，最新配置信息为: {}", SYS_CONFIG_MAP);
      });

      // 使WatchKey能重新入队列，等待事件通知
      boolean isValid = key.reset();
      if (!isValid) {
        break;
      }
    }
  }

}
