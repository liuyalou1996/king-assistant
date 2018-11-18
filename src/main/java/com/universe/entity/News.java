package com.universe.entity;

/**
 * 新闻
 * 
 * @author liuyalou
 *
 */
public class News {

  // 新闻类型
  private String newsType;
  // 新闻标题
  private String newsText;
  // 新闻发布时间
  private String newsTime;
  // 新闻地址
  private String newsUri;

  public String getNewsType() {
    return newsType;
  }

  public void setNewsType(String newsType) {
    this.newsType = newsType;
  }

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
    return "News [newsType=" + newsType + ", newsText=" + newsText + ", newsTime=" + newsTime + ", newsUri=" + newsUri
        + "]";
  }

}
