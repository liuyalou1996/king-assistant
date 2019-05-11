package com.universe.loader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SysConfigLoader {

  /**
   * 占位符解析模式
   */
  private static final Pattern PATTERN = Pattern.compile(".*(\\$\\{(.+)\\}).*");

  /**
   * 系统配置
   */
  private static final Map<String, String> SYS_CONFIG_MAP = new HashMap<>();

  static {
    // 类加载时加载系统配置
    loadSysConfig();
  }

  public static String getProperty(String key) {
    return SYS_CONFIG_MAP.get(key);
  }

  private static void loadSysConfig() {
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

}
