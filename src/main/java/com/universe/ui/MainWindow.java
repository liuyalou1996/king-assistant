package com.universe.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.layout.FillLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.universe.biz.FCBiz;
import com.universe.biz.NewsBiz;
import com.universe.biz.NewsInfo;
import com.universe.biz.impl.FCBizImpl;
import com.universe.biz.impl.NewsBizImpl;
import com.universe.entity.FontAndColor;
import com.universe.entity.MyProperties;
import com.universe.entity.News;
import com.universe.swt.SWTResourceManager;
import com.universe.util.DialogUtil;
import com.universe.util.IOUtil;
import com.universe.util.LogUtil;
import com.universe.util.UiUtil;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MainWindow {

  private NewsBiz biz = new NewsBizImpl();
  protected Shell shell;
  private Table table;
  private Composite comp_top;
  private Composite comp_bottom;
  private TableCursor cursor;
  private MenuItem mi_page;
  private MenuItem mi_hero;
  private MenuItem mi_skin;
  private MenuItem mi_redesign;
  private MenuItem mi_systemOp;
  private MenuItem mi_drawOp;
  private MenuItem mi_font;
  private MenuItem mi_default;

  private MenuItem mi_exit;
  private String path = IOUtil.getClassPath("settings" + File.separator + "fc.txt");

  /**
   * Launch the application.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
      MainWindow window = new MainWindow();
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
    shell.setImage(SWTResourceManager.getImage(MainWindow.class, "/images/logo.png"));
    shell.setSize(769, 454);
    shell.setText("王者荣耀小助手");
    shell.setLayout(new FillLayout(SWT.HORIZONTAL));
    // 窗口全屏
    UiUtil.showFullScreen(shell);
    SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

    comp_top = new Composite(sashForm, SWT.NONE);
    comp_top.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

    comp_bottom = new Composite(sashForm, SWT.NONE);
    comp_bottom.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
    comp_bottom.setLayout(new FillLayout(SWT.HORIZONTAL));

    table = new Table(comp_bottom, SWT.BORDER | SWT.FULL_SELECTION);
    table.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
    table.setHeaderVisible(true);
    // 为表格设置光标
    Cursor cursor_hand = new Cursor(shell.getDisplay(), SWT.CURSOR_HAND);
    table.setCursor(cursor_hand);

    int columnWidth = shell.getBounds().width / 5;
    TableColumn tc_all = new TableColumn(table, SWT.NONE);
    tc_all.setWidth(columnWidth);
    tc_all.setText("全部");

    TableColumn tc_news = new TableColumn(table, SWT.NONE);
    tc_news.setWidth(columnWidth);
    tc_news.setText("新闻");

    TableColumn tc_bullet = new TableColumn(table, SWT.NONE);
    tc_bullet.setWidth(columnWidth);
    tc_bullet.setText("公告");

    TableColumn tc_activities = new TableColumn(table, SWT.NONE);
    tc_activities.setWidth(columnWidth);
    tc_activities.setText("活动");

    TableColumn tc_competition = new TableColumn(table, SWT.NONE);
    tc_competition.setWidth(columnWidth);
    tc_competition.setText("赛事");

    cursor = new TableCursor(table, SWT.NONE);
    cursor.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
    sashForm.setWeights(new int[] { 223, 169 });

    Menu menu = new Menu(shell, SWT.BAR);
    shell.setMenuBar(menu);

    MenuItem submenu_new = new MenuItem(menu, SWT.CASCADE);
    submenu_new.setText("爆料站");

    Menu menu_new = new Menu(submenu_new);
    submenu_new.setMenu(menu_new);

    mi_page = new MenuItem(menu_new, SWT.NONE);
    mi_page.setText("首页");

    mi_hero = new MenuItem(menu_new, SWT.NONE);
    mi_hero.setText("新英雄");

    mi_skin = new MenuItem(menu_new, SWT.NONE);
    mi_skin.setText("新皮肤");

    mi_redesign = new MenuItem(menu_new, SWT.NONE);
    mi_redesign.setText("英雄重塑");

    mi_systemOp = new MenuItem(menu_new, SWT.NONE);
    mi_systemOp.setText("系统优化");

    mi_drawOp = new MenuItem(menu_new, SWT.NONE);
    mi_drawOp.setText("美术优化");

    MenuItem submenu_settings = new MenuItem(menu, SWT.CASCADE);
    submenu_settings.setText("设置");

    Menu menu_settings = new Menu(submenu_settings);
    submenu_settings.setMenu(menu_settings);

    mi_font = new MenuItem(menu_settings, SWT.NONE);
    mi_font.setText("表格字体设置");

    mi_default = new MenuItem(menu_settings, SWT.NONE);
    mi_default.setText("恢复默认设置");

    mi_exit = new MenuItem(menu_settings, SWT.NONE);
    mi_exit.setText("退出  Ctrl+q");
    mi_exit.setAccelerator(SWT.CTRL | 'q');

    // 给爆料的item加选择事件
    addSelectionListenerForMenuItemOfNews();
    // 给设置下的item加选择事件
    addSelectionListenerForMenuItemOfSettins();
    // 给表格加相关事件
    addListenerForTable();
    // 设置背景图片
    setBackgroundImage();
    // 为表格设置内容
    setContentsForTable();
    // 设置字体和颜色
    setFontAndColorForTable();
  }

  // 设置背景图片，网上读取需要时间，可以加线程
  private void setBackgroundImage() {
    try {
      InputStream is = biz.getBackgroudImageStream();
      Image image = new Image(shell.getDisplay(), is);
      comp_top.setBackgroundImage(image);
    } catch (IOException e) {
      LogUtil.error(e);
    }
  }

  // 为表格设置内容
  private void setContentsForTable() {
    try {
      List<List<News>> list = biz.getTableItemContent();
      for (int i = 0; i < list.size(); i++) {
        TableItem ti = new TableItem(table, SWT.NONE);
        ti.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        ti.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        List<News> newsList = list.get(i);
        for (int j = 0; j < newsList.size(); j++) {
          News news = newsList.get(j);
          if (news != null) {
            ti.setText(j, news.getNewsText());
            ti.setData("" + j, news.getNewsUri());
          } else {
            ti.setText(j, "");
          }
        }
      }
    } catch (IOException e) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
              DialogUtil.showErrorDialog(shell, "错误", "网络连接有问题，请检查网络连接!");
            }
          });
        }
      }).start();
    }
  }

  // 显示新闻内容
  private void showContentsOfNews() {
    TableItem ti = cursor.getRow();
    int column = cursor.getColumn();
    // 获得这条新闻的地址,使用浏览器加载
    String newsUrl = (String) ti.getData("" + column);
    if (newsUrl != null && !newsUrl.equals("")) {
      BrowserWindow window = new BrowserWindow(newsUrl, ti.getText());
      window.open();
    }
  }

  // 设置字体和颜色
  private void setFontAndColorForTable() {
    FontAndColor fc = null;
    try {
      fc = (FontAndColor) IOUtil.readObject(new File(path));
    } catch (Exception e) {

    }
    if (fc != null) {
      for (TableItem ti : table.getItems()) {
        Font font = new Font(shell.getDisplay(), fc.getFontName(), fc.getFontHeight(), fc.getFontStyle());
        Color color = new Color(shell.getDisplay(), fc.getRed(), fc.getGreen(), fc.getBlue());
        ti.setFont(font);
        ti.setForeground(color);
      }
    }
  }

  // 给表格加相关事件
  private void addListenerForTable() {
    // 改变表格行高
    table.addListener(SWT.MeasureItem, new Listener() {

      public void handleEvent(Event event) {
        event.height = 25;
      }
    });
    // 双击两次便会触发
    cursor.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(MouseEvent e) {
        if (e.button == 1) {
          showContentsOfNews();
        }
      }
    });
    // 给光标加选择事件,选择后按Enter键才会触发
    cursor.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        showContentsOfNews();
      }
    });
  }

  // 给爆料下的菜单项加选择事件
  private void addSelectionListenerForMenuItemOfNews() {
    mi_page.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.PAGE_ADDR, mi_page.getText());
        container.open();
      }
    });
    mi_hero.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.HERO_ADDR, mi_hero.getText());
        container.open();
      }
    });
    mi_skin.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.SKIN_ADDR, mi_skin.getText());
        container.open();
      }
    });
    mi_redesign.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.REDESIGN_ADDR, mi_redesign.getText());
        container.open();
      }
    });
    mi_systemOp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.SYSTEMOP_ADDR, mi_systemOp.getText());
        container.open();
      }
    });
    mi_drawOp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(NewsInfo.DRAWOP_ADDR, mi_systemOp.getText());
        container.open();
      }
    });
  }

  // 给设置下的菜单项加选择事件
  private void addSelectionListenerForMenuItemOfSettins() {
    // 给表格行设置字体
    mi_font.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        new Thread(new Runnable() {

          @Override
          public void run() {
            Display.getDefault().asyncExec(new Runnable() {

              @Override
              public void run() {
                FCBiz biz = new FCBizImpl();
                FontDialog dialog = biz.selectDialog(shell, path);
                FontData fontData = dialog.open();
                if (fontData != null) {
                  Font font = new Font(shell.getDisplay(), fontData);
                  RGB rgb = dialog.getRGB();
                  int red = rgb.red;
                  int green = rgb.green;
                  int blue = rgb.blue;
                  Color color = new Color(shell.getDisplay(), red, green, blue);
                  for (TableItem ti : table.getItems()) {
                    ti.setFont(font);
                    ti.setForeground(color);
                  }
                  // 将配置对象写入文件
                  FontAndColor fc = new FontAndColor();
                  fc.setRed(red);
                  fc.setGreen(green);
                  fc.setBlue(blue);
                  fc.setFontName(fontData.getName());
                  fc.setFontHeight(fontData.getHeight());
                  fc.setFontStyle(fontData.getStyle());
                  IOUtil.writeObject(path, fc);
                }
              }
            });
          }
        }).start();
      }
    });
    // 恢复默认设置
    mi_default.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        MyProperties properties = MyProperties.getInstance();
        String tableBackground = properties.getProperty("tableBackground");
        String cursorBackground = properties.getProperty("cursorBackground");
        String tableItemBackground = properties.getProperty("tableItemBackground");
        String tableItemForeground = properties.getProperty("tableItemForeground");

        Display display = shell.getDisplay();
        FontData systemFd = display.getSystemFont().getFontData()[0];
        String fontName = systemFd.getName();
        int fontHeight = systemFd.getHeight();
        int fontStyle = systemFd.getStyle();
        table.setBackground(display.getSystemColor(Integer.parseInt(tableBackground)));
        cursor.setBackground(display.getSystemColor(Integer.parseInt(cursorBackground)));

        TableItem[] tis = table.getItems();
        for (TableItem ti : tis) {
          FontData fd = new FontData();
          fd.setName(fontName);
          fd.setHeight(fontHeight);
          fd.setStyle(fontStyle);
          Font font = new Font(display, fd);
          ti.setBackground(display.getSystemColor(Integer.parseInt(tableItemBackground)));
          ti.setForeground(display.getSystemColor(Integer.parseInt(tableItemForeground)));
          ti.setFont(font);
        }
        IOUtil.write(path, "");
      }
    });
    // 退出系统
    mi_exit.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        System.exit(0);
      }
    });
  }
}
