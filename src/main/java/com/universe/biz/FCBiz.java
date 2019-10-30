package com.universe.biz;

import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

import com.universe.entity.FontAndColor;

public interface FCBiz {

	// 初始化字体颜色对话框
	public FontDialog showInitedDialog(Shell shell, FontAndColor fc);

	// 选择对话框
	public FontDialog selectDialog(Shell shell, String path);
}
