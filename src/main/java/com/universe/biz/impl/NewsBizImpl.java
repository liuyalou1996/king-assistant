package com.universe.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.universe.biz.NewsBiz;
import com.universe.biz.NewsInfo;
import com.universe.entity.News;

public class NewsBizImpl implements NewsBiz {

  private int count = 0;

  public List<List<News>> getTableItemContent() throws IOException {
    List<List<News>> list = new ArrayList<List<News>>();
    Map<String, List<News>> map = extractNewsItemsFromPage();
    int size = 0;
    // 每一行是一个list，由于各列的新闻数不同，所以list的大小应为列的最大数
    for (Map.Entry<String, List<News>> entry : map.entrySet()) {
      List<News> newsList = entry.getValue();
      if (newsList.size() > size) {
        size = newsList.size();
      }
    }

    // 每次取出以前list中的一个，放入新的list
    for (int i = 0; i < size; i++) {
      List<News> lineList = new ArrayList<News>();
      for (Map.Entry<String, List<News>> entry : map.entrySet()) {
        List<News> newsList = entry.getValue();
        if (i < newsList.size()) {
          lineList.add(newsList.get(i));
        } else {
          lineList.add(null);
        }
      }
      list.add(lineList);
    }
    return list;
  }

  @Override
  // 从页面提取新闻款项,以新闻类型为键，新闻对象的列表集合作值
  public Map<String, List<News>> extractNewsItemsFromPage() throws IOException {
    Document doc = Jsoup.connect(NewsInfo.HOST).timeout(3 * 1000).get();
    Map<String, List<News>> map = new LinkedHashMap<String, List<News>>();
    Elements all = doc.select("#newsList1");
    Elements news = doc.select("#newsList2");
    Elements bullet = doc.select("#newsList3");
    Elements activities = doc.select("#newsList4");
    Elements competiton = doc.select("#newsList5");

    extractItemsOfEachType(map, all);
    extractItemsOfEachType(map, news);
    extractItemsOfEachType(map, bullet);
    extractItemsOfEachType(map, activities);
    extractItemsOfEachType(map, competiton);

    return map;
  }

  // 提取每种类型下的新闻款项，存入list中
  private void extractItemsOfEachType(Map<String, List<News>> map, Elements uls) {
    Elements lis = uls.first().children();
    List<News> newsList = new ArrayList<News>();
    for (Element li : lis) {
      // 获得新闻的相关信息
      String newsType = li.select(NewsInfo.NEWSTYPE_CLASS).first().html();
      String newsText = li.select(NewsInfo.NEWSTXT_CLASS).first().html();
      String newsTime = li.select(NewsInfo.NEWSTIME_CLASS).first().html();
      String newsUri = li.select(NewsInfo.NEWSTXT_CLASS).first().attr("href");

      // 存入对象中
      News news = new News();
      news.setNewsType(newsType);
      news.setNewsText(newsText);
      news.setNewsTime(newsTime);
      // 与主机地址拼接
      news.setNewsUri(NewsInfo.HOST + newsUri);
      newsList.add(news);
    }
    // 新闻类型作键
    String key = null;
    // 为0则为全部信息
    if (count++ == 0) {
      key = "全部";
    } else if (count == 5) {
      key = "赛事";
    } else {
      key = lis.first().select(NewsInfo.NEWSTYPE_CLASS).first().html();
    }
    map.put(key, newsList);
  }

  @Override
  // 获得背景图片
  public InputStream getBackgroudImageStream() throws IOException {
    Document doc = Jsoup.connect(NewsInfo.HOST).get();
    Element ele = doc.select(NewsInfo.BG_ClASS).first();
    String attr = ele.attr("style");
    String bgUrl = "http:" + attr.substring(attr.indexOf("(") + 1, attr.indexOf(")"));

    // 发送get请求
    URL url = new URL(bgUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestProperty("Accept", "image/jpg,image/jpeg");
    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; W…) Gecko/20100101 Firefox/57.0");
    conn.setRequestMethod("GET");
    // 设置连接时长为4秒
    conn.setConnectTimeout(3 * 1000);
    conn.connect();

    InputStream is = conn.getInputStream();
    // 以流的形式返回响应
    return is;
  }

}
