package com.universe.biz;

/**
 * 新闻信息
 * 
 * @author Administrator
 *
 */
public interface NewsInfo {
	// 首页主机地址
	public String HOST = "http://pvp.qq.com";
	// 首页背景图片主机地址
	public String BGHOST = "http://ossweb-img.qq.com";
	// 全部信息id
	public String ALL_ID = "#newsList1";
	// 新闻id
	public String NEWS_ID = "#newsList2";
	// 公告id
	public String BULLET_ID = "#newsList3";
	// 活动id
	public String ACTIVITIES_ID = "#newsList4";
	// 赛事id
	public String COMPETITION_ID = "#newsList5";
	// 新闻类型类选择器
	public String NEWSTYPE_CLASS = "a.fl.news-type";
	// 新闻标题类选择器
	public String NEWSTXT_CLASS = "a.fl.news-txt";
	// 新闻发布时间类选择器
	public String NEWSTIME_CLASS = "em.fr.news-time";
	// 首页背景图片的类选择器
	public String BG_ClASS = ".wrapper>.kv-bg";
	// 爆料首页地址
	public String PAGE_ADDR = "http://pvp.qq.com/coming";
	// 新英雄
	public String HERO_ADDR = "http://pvp.qq.com/coming/hero-list.html";
	// 新皮肤
	public String SKIN_ADDR = "http://pvp.qq.com/coming/skin-list.html";
	// 英雄重塑
	public String REDESIGN_ADDR = "http://pvp.qq.com/coming/update-list.html";
	// 系统优化
	public String SYSTEMOP_ADDR = "http://pvp.qq.com/coming/system-list.html";
	// 美术优化
	public String DRAWOP_ADDR = "http://pvp.qq.com/coming/arts-list.html";

}
