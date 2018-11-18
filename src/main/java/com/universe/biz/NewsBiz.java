package com.universe.biz;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.universe.entity.News;


public interface NewsBiz {
	// 从页面提取新闻款项,以新闻类型为键，新闻对象的列表集合作值
	public Map<String, List<News>> extractNewsItemsFromPage() throws IOException;

	// 从新闻款项list整理TableItem所需的内容
	public List<List<News>> getTableItemContent() throws IOException;

	// 从主页获取背景图片流
	public InputStream getBackgroudImageStream() throws IOException;
}
