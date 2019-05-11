package com.universe.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.universe.swt.SWTResourceManager;
import com.universe.util.UiUtils;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

import org.eclipse.swt.browser.Browser;

/**
 * 
 * @author liuyalou
 *
 */
public class BrowserWindow {

  protected Shell shell;

  private String newsUrl;
  private String item;
  private Browser browser;

  public BrowserWindow() {

  }

  /**
   * 
   * @param newsUrl
   *            新闻的url地址
   * @param item
   *            款项名
   */
  public BrowserWindow(String newsUrl, String item) {
    this.newsUrl = newsUrl;
    this.item = item;
  }

  /**
   * Launch the application.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      BrowserWindow window = new BrowserWindow();
      window.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Open the window.
   */
  public void open() {
    Display display = Display.getDefault();
    createContents();
    shell.open();
    shell.layout();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }

  /**
   * Create contents of the window.
   */
  protected void createContents() {
    shell = new Shell();
    shell.setImage(SWTResourceManager.getImage(BrowserWindow.class, "/images/logo.png"));
    shell.setSize(450, 300);
    shell.setText(item);
    shell.setLayout(new FillLayout(SWT.HORIZONTAL));

    // 设置全屏
    UiUtils.showFullScreen(shell);

    browser = new Browser(shell, SWT.NONE);
    browser.setUrl(newsUrl);

  }
}
