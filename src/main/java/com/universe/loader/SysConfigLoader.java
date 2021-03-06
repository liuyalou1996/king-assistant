package com.universe.loader;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universe.util.IOUtils;
import com.universe.util.JsonUtils;

public class SysConfigLoader {

  private static final Logger logger = LoggerFactory.getLogger(SysConfigLoader.class);
  /**
   * 系统配置文件路径
   */
  private static final String CONFIG_PATH = IOUtils.getClassPath("settings", "config.properties");
  /**
   * 占位符解析模式
   */
  private static final Pattern PATTERN = Pattern.compile("(\\$\\{((?:\\w|\\.)*)\\})");
  /**
   * 系统配置
   */
  private static final Map<String, String> SYS_CONFIG_MAP = new LinkedHashMap<>();

  static {
    // 类加载时加载系统配置
    loadSysConfig();
    // 这里开启一个线程注册文件监听器，否则会一直等待
    Thread thread = new Thread(() -> {
      try {
        registerWithWatchService();
      } catch (Exception e) {
        logger.error("注册文件监听器失败.", e);
      }
    });

    // 注意设置为精灵线程，否则程序关闭后该线程将一直阻塞
    thread.setDaemon(true);
    thread.start();
  }

  public static String getProperty(String key) {
    return SYS_CONFIG_MAP.get(key);
  }

  private static void loadSysConfig() {
    try {
      Properties props = IOUtils.loadProperties(CONFIG_PATH);
      Set<String> keys = props.stringPropertyNames();
      keys.forEach(key -> {
        SYS_CONFIG_MAP.put(key, props.getProperty(key));
      });

      SYS_CONFIG_MAP.forEach((propName, propValue) -> {
        // 解析其中的占位符
        parsePlaceholder(propName, propValue);
      });
    } catch (Exception e) {
      logger.error("加载系统配置失败!", e);
    }

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
    Path path = Paths.get(IOUtils.getClassPath("settings", null));
    logger.info("被监控的文件路径为: {}", path);

    // 在WatchService上注册
    path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
    while (true) {
      WatchKey key = watcher.take();
      key.pollEvents().forEach(event -> {
        String fileName = String.valueOf(event.context());
        if ("config.properties".equals(fileName)) {
          logger.info("系统配置文件config.properties变动，开始重新加载配置文件...");
          loadSysConfig();
          logger.info("加载完毕，最新配置信息为: {}", JsonUtils.toPrettyJsonString(SYS_CONFIG_MAP));
        }
      });

      // 使WatchKey能重新入队列，等待事件通知
      boolean isValid = key.reset();
      if (!isValid) {
        break;
      }
    }
  }

}
