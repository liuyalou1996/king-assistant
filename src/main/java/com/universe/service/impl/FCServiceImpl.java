package com.universe.service.impl;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

import com.universe.common.pojo.FontAndColor;
import com.universe.service.FCService;
import com.universe.util.IOUtils;

public class FCServiceImpl implements FCService {

  @SuppressWarnings("deprecation")
  @Override
  // 初始化对话框
  public FontDialog showInitedDialog(Shell shell, FontAndColor fc) {
    FontData fd = new FontData();
    fd.setName(fc.getFontName());
    fd.setHeight(fc.getFontHeight());
    fd.setStyle(fc.getFontStyle());
    FontDialog dialog = new FontDialog(shell, SWT.NONE);
    dialog.setFontData(fd);
    RGB rgb = new RGB(fc.getRed(), fc.getGreen(), fc.getBlue());
    dialog.setRGB(rgb);
    return dialog;
  }

  @Override
  // 选择对话框
  public FontDialog selectDialog(Shell shell, String path) {
    FontDialog dialog = null;
    try {
      FontAndColor fc = (FontAndColor) IOUtils.readObject(new File(path));
      dialog = showInitedDialog(shell, fc);
    } catch (Exception e) {
    }
    // 如果不能读取则采取系统默认配置
    if (dialog == null) {
      dialog = new FontDialog(shell, SWT.NONE);
    }
    return dialog;
  }

}
