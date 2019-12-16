package com.universe.pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author liuyalou
 */
public class News {

  // 新闻标题
  private String newsText;
  // 新闻发布时间
  private String newsTime;
  // 新闻地址
  private String newsUri;

  public String getNewsText() {
    return newsText;
  }

  public void setNewsText(String newsText) {
    this.newsText = newsText;
  }

  public String getNewsTime() {
    return newsTime;
  }

  public void setNewsTime(String newsTime) {
    this.newsTime = newsTime;
  }

  public String getNewsUri() {
    return newsUri;
  }

  public void setNewsUri(String newsUri) {
    this.newsUri = newsUri;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
