package com.universe.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.universe.common.constant.AddressConst;
import com.universe.common.constant.SelectorConst;
import com.universe.common.constant.SystemConfigConst;
import com.universe.loader.SysConfigLoader;
import com.universe.pojo.News;
import com.universe.pojo.ResponseDto;
import com.universe.pojo.ResponseDto.Msg;
import com.universe.service.NewsService;
import com.universe.util.JsonUtils;
import com.universe.util.OkHttpUtils;
import com.universe.util.OkHttpUtils.OkHttpResp;

public class NewsServiceImpl implements NewsService {

  private static final String DEFAULT_USEA_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0";

  @Override
  public List<List<News>> getTableItemContent() throws IOException {
    List<List<News>> rows = new ArrayList<List<News>>();
    Map<String, List<News>> rowMap = extractNewsItemsFromPage();
    int rowCount = 0;
    // 计算出表格的行数，因为列数可能各不相同
    for (Map.Entry<String, List<News>> entry : rowMap.entrySet()) {
      List<News> newsList = entry.getValue();
      if (newsList.size() > rowCount) {
        rowCount = newsList.size();
      }
    }

    // 每次取出以前list中的一个，放入新的list
    for (int count = 0; count < rowCount; count++) {
      List<News> cols = new ArrayList<News>();
      // 每次只取出一列信息
      for (Map.Entry<String, List<News>> entry : rowMap.entrySet()) {
        List<News> newsList = entry.getValue();
        // 新闻列数可能小于行数
        News news = count < newsList.size() ? newsList.get(count) : null;
        cols.add(news);
      }
      rows.add(cols);
    }

    return rows;
  }

  @Override
  // 从页面提取新闻款项,以新闻类型为键，新闻对象的列表集合作值
  public Map<String, List<News>> extractNewsItemsFromPage() throws IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put("User-Agent", DEFAULT_USEA_AGENT);
    headers.put("Accept", "*/*");
    Document doc = Jsoup.connect(SysConfigLoader.getProperty(AddressConst.HOST)).headers(headers).timeout(3 * 1000).get();

    String attrName = "data-channelid";
    String hotInfoChannelId = doc.select(SysConfigLoader.getProperty(SelectorConst.HOTINFO_ID)).attr(attrName);
    String newsChannelId = doc.select(SysConfigLoader.getProperty(SelectorConst.NEWS_ID)).attr(attrName);
    String bulletChannelId = doc.select(SysConfigLoader.getProperty(SelectorConst.BULLET_ID)).attr(attrName);
    String activitiesChannelId = doc.select(SysConfigLoader.getProperty(SelectorConst.ACTIVITIES_ID)).attr(attrName);
    String competitionChannelId = doc.select(SysConfigLoader.getProperty(SelectorConst.COMPETITION_ID)).attr(attrName);

    Map<String, List<News>> map = new LinkedHashMap<String, List<News>>();
    map.put("热门", getNewsByChannelId(hotInfoChannelId));
    map.put("新闻", getNewsByChannelId(newsChannelId));
    map.put("公告", getNewsByChannelId(bulletChannelId));
    map.put("活动", getNewsByChannelId(activitiesChannelId));
    map.put("赛事", getNewsByChannelId(competitionChannelId));
    return map;
  }

  private List<News> getNewsByChannelId(String hotInfoChannelId) throws IOException {
    String url = SysConfigLoader.getProperty(AddressConst.NEWS_LIST);
    Map<String, Object> headers = new HashMap<>();
    headers.put("Accept", OkHttpUtils.JSON.toString());
    headers.put("User-Agent", DEFAULT_USEA_AGENT);
    Map<String, Object> params = new HashMap<>();
    constructRequestParams(params, hotInfoChannelId);

    OkHttpResp resp = OkHttpUtils.sendGet(url, headers, params);
    if (!resp.isSuccessful()) {
      throw new IOException();
    }

    // 对请求内容进行转换
    ResponseDto responseDto = JsonUtils.toJavaBean(resp.getRespStr(), ResponseDto.class);
    Msg msg = responseDto.getMsg();
    List<Map<String, Object>> result = msg.getResult();

    List<News> newsList = new ArrayList<>();
    String newsDetailBaseUrl = SysConfigLoader.getProperty(AddressConst.NEW_DETAIL);
    result.forEach(newsInfo -> {
      News news = new News();
      news.setNewsText(String.valueOf(newsInfo.get("sTitle")));
      news.setNewsUri(newsDetailBaseUrl + "?tid=" + newsInfo.get("iNewsId"));
      news.setNewsTime(String.valueOf(newsInfo.get("sIdxTime")));
      newsList.add(news);
    });

    return newsList;
  }

  /**
   * 构造获取新闻列表的请求参数
   * @param params
   * @param hotInfoChannelId
   */
  private void constructRequestParams(Map<String, Object> params, String hotInfoChannelId) {
    params.put("p0", 18);
    params.put("p1", "searchNewsKeywordsList");
    params.put("order", "sIdxTime");
    params.put("r0", "cors");
    params.put("type", "iTarget");
    params.put("source", "app_news_search");
    params.put("pagesize", Integer.parseInt(SysConfigLoader.getProperty(SystemConfigConst.TABLE_NEWS_ROWSNO)));
    params.put("id", Integer.parseInt(hotInfoChannelId));
  }

  @Override
  // 获得背景图片
  public InputStream getBackgroudImageStream() throws IOException {
    Document doc = Jsoup.connect(SysConfigLoader.getProperty(AddressConst.HOST)).get();
    Element ele = doc.select(SysConfigLoader.getProperty(SelectorConst.BACKGROUNDIMAGE_CLASS)).first();
    String attr = ele.attr("style");
    // 截取地址
    String bgUrl = "https:" + attr.substring(attr.indexOf("(") + 1, attr.indexOf(")"));

    URL url = new URL(bgUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestProperty("User-Agent", DEFAULT_USEA_AGENT);
    conn.setRequestProperty("Accept", "image/jpg,image/jpeg");
    conn.connect();
    return conn.getInputStream();
  }

  public static void main(String[] args) throws IOException {
    System.err.println(JsonUtils.toPrettyJsonString(new NewsServiceImpl().getTableItemContent()));
  }

}
