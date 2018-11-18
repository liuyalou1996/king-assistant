package com.universe.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.universe.util.LogUtil;

public class MyProperties extends Properties {

  private static final long serialVersionUID = -6129351698634150628L;
  private static MyProperties instance;

  private MyProperties() {
    try {
      InputStream is = MyProperties.class.getClassLoader().getResourceAsStream("settings.properties");
      super.load(new InputStreamReader(is, "UTF-8"));
    } catch (IOException e) {
      LogUtil.error(e);
    }
  }

  public static MyProperties getInstance() {
    if (instance == null) {
      instance = new MyProperties();
    }
    return instance;
  }
}
