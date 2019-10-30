package com.universe.biz.impl;

import com.universe.biz.NewsBiz;
import com.universe.entity.News;
import com.universe.loader.SysConfigLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NewsBizImpl implements NewsBiz {

    private int count = 0;

    @Override
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
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0");
        headers.put("Accept", "*/*");
        Document doc = Jsoup.connect(SysConfigLoader.getProperty("addr.host")).headers(headers).timeout(3 * 1000).get();
        Map<String, List<News>> map = new LinkedHashMap<String, List<News>>();
        Elements all = doc.select(SysConfigLoader.getProperty("selector.allInfoId"));
        Elements news = doc.select(SysConfigLoader.getProperty("selector.newsId"));
        Elements bullet = doc.select(SysConfigLoader.getProperty("selector.bulletId"));
        Elements activities = doc.select(SysConfigLoader.getProperty("selector.activitiesId"));
        Elements competiton = doc.select(SysConfigLoader.getProperty("selector.competitionId"));
        System.err.println(doc.select("#newsList1").first().children());
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
            String newsType = li.select(SysConfigLoader.getProperty("selector.newsTypeClass")).first().html();
            String newsText = li.select(SysConfigLoader.getProperty("selector.newsTextClass")).first().html();
            String newsTime = li.select(SysConfigLoader.getProperty("selector.newsTimeClass")).first().html();
            String newsUri = li.select(SysConfigLoader.getProperty("selector.newsTextClass")).first().attr("href");

            // 存入对象中
            News news = new News();
            news.setNewsType(newsType);
            news.setNewsText(newsText);
            news.setNewsTime(newsTime);
            // 与主机地址拼接
            news.setNewsUri(SysConfigLoader.getProperty("addr.host") + newsUri);
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
            key = lis.first().select(SysConfigLoader.getProperty("selector.newsTypeClass")).first().html();
        }
        map.put(key, newsList);

    }

    @Override
    // 获得背景图片
    public InputStream getBackgroudImageStream() throws IOException {
        Document doc = Jsoup.connect(SysConfigLoader.getProperty("addr.host")).get();
        Element ele = doc.select(SysConfigLoader.getProperty("selector.backgroundImageClass")).first();
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
