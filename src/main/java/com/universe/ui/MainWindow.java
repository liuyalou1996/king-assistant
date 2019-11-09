package com.universe.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universe.common.constant.AddressConst;
import com.universe.common.constant.SystemConfigConst;
import com.universe.common.pojo.FontAndColor;
import com.universe.common.pojo.News;
import com.universe.loader.SysConfigLoader;
import com.universe.service.FCService;
import com.universe.service.NewsService;
import com.universe.service.impl.FCServiceImpl;
import com.universe.service.impl.NewsServiceImpl;
import com.universe.swt.SWTResourceManager;
import com.universe.util.DialogUtils;
import com.universe.util.IOUtils;
import com.universe.util.UiUtils;

public class MainWindow {

  private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

  private NewsService biz = new NewsServiceImpl();
  protected Shell shell;
  private Table table;
  private Composite comp_top;
  private Composite comp_bottom;
  private TableCursor cursor;
  private MenuItem mi_page;
  private MenuItem mi_hero;
  private MenuItem mi_skin;
  private MenuItem mi_rebuild;
  private MenuItem mi_adjust;
  private MenuItem mi_newmode;
  private MenuItem mi_newsys;
  private MenuItem mi_systemOp;
  private MenuItem mi_drawOp;
  private MenuItem mi_font;
  private MenuItem mi_default;
  private MenuItem mi_exit;

  private String fcPath = IOUtils.getClassPath("settings", "fc.txt");

  public static void main(String[] args) {
    try {
      MainWindow window = new MainWindow();
      window.open();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

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

  protected void createContents() {
    shell = new Shell();
    shell.setImage(SWTResourceManager.getImage(MainWindow.class, "/images/logo.png"));
    shell.setSize(769, 454);
    shell.setText("王者荣耀小助手");
    shell.setLayout(new FillLayout(SWT.HORIZONTAL));
    // 窗口全屏
    UiUtils.showFullScreen(shell);
    SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

    comp_top = new Composite(sashForm, SWT.NONE);
    comp_top.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

    comp_bottom = new Composite(sashForm, SWT.NONE);
    comp_bottom.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
    comp_bottom.setLayout(new FillLayout(SWT.HORIZONTAL));

    // 初始化表格
    initTable(sashForm);

    Menu menu = new Menu(shell, SWT.BAR);
    shell.setMenuBar(menu);

    // 初始化爆料菜单
    initComingMenu(menu);
    // 初始化设置菜单
    initSettingMenu(menu);
    // 给爆料的item加选择事件
    addSelectionListenerForMenuItemOfNews();
    // 给设置下的item加选择事件
    addSelectionListenerForMenuItemOfSettins();
    // 给表格加相关事件
    addListenerForTable();
    // 设置背景图片
    setBackgroundImage();

    // 为表格设置内容，由于网络请求耗时比较长，故采用异步刷新
    new Thread(() -> {
      Display.getDefault().asyncExec(() -> {
        setContentForTable();
      });
    }).start();

    // 设置字体和颜色
    setFontAndColorForTable();
  }

  private void initTable(SashForm sashForm) {
    table = new Table(comp_bottom, SWT.BORDER | SWT.FULL_SELECTION);
    table.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
    table.setHeaderVisible(true);
    // 为表格设置光标
    Cursor cursor_hand = new Cursor(shell.getDisplay(), SWT.CURSOR_HAND);
    table.setCursor(cursor_hand);

    int columnWidth = shell.getBounds().width / 5;
    TableColumn tc_all = new TableColumn(table, SWT.NONE);
    tc_all.setWidth(columnWidth);
    tc_all.setText("热门");

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
  }

  private void initComingMenu(Menu menu) {
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

    mi_rebuild = new MenuItem(menu_new, SWT.NONE);
    mi_rebuild.setText("英雄重塑");

    mi_adjust = new MenuItem(menu_new, SWT.NONE);
    mi_adjust.setText("英雄调整");

    mi_newmode = new MenuItem(menu_new, SWT.NONE);
    mi_newmode.setText("新模式");

    mi_newsys = new MenuItem(menu_new, SWT.NONE);
    mi_newsys.setText("新系统");

    mi_systemOp = new MenuItem(menu_new, SWT.NONE);
    mi_systemOp.setText("系统优化");

    mi_drawOp = new MenuItem(menu_new, SWT.NONE);
    mi_drawOp.setText("美术优化");
  }

  private void initSettingMenu(Menu menu) {
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
  }

  // 设置背景图片，网上读取需要时间，可以加线程
  private void setBackgroundImage() {
    try {
      InputStream is = biz.getBackgroudImageStream();
      Image image = new Image(shell.getDisplay(), is);
      comp_top.setBackgroundImage(image);
    } catch (IOException e) {
      logger.error("读取图片响应流失败：{}", e.getMessage(), e);
    }
  }

  // 为表格设置内容
  private void setContentForTable() {
    try {
      List<List<News>> rows = biz.getTableItemContent();
      for (int rowCount = 0; rowCount < rows.size(); rowCount++) {
        TableItem ti = new TableItem(table, SWT.NONE);
        ti.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        ti.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        List<News> cols = rows.get(rowCount);
        for (int colCount = 0; colCount < cols.size(); colCount++) {
          News news = cols.get(colCount);
          if (news != null) {
            ti.setText(colCount, news.getNewsText());
            ti.setData("" + colCount, news.getNewsUri());
          } else {
            ti.setText(colCount, "");
          }
        }
      }
    } catch (IOException e) {
      DialogUtils.showErrorDialog(shell, "错误", "网络连接有问题，请检查网络连接!");
    }
  }

  // 显示新闻内容
  private void showContentsOfNews() {
    TableItem ti = cursor.getRow();
    int column = cursor.getColumn();
    // 获得这条新闻的地址,使用浏览器加载
    String newsUrl = (String) ti.getData("" + column);
    if (StringUtils.isNotBlank(newsUrl)) {
      BrowserWindow window = new BrowserWindow(newsUrl, ti.getText());
      window.open();
    }
  }

  // 设置字体和颜色
  private void setFontAndColorForTable() {
    FontAndColor fc = null;
    try {
      fc = (FontAndColor) IOUtils.readObject(new File(fcPath));
    } catch (Exception e) {
      logger.info("获取字体颜色信息失败，采用系统默认字体和颜色!");
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

      @Override
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
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.COMMING_HOME), mi_page.getText());
        container.open();
      }
    });
    mi_hero.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.NEW_HREO), mi_hero.getText());
        container.open();
      }
    });
    mi_skin.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.NEW_SKIN), mi_skin.getText());
        container.open();
      }
    });
    mi_rebuild.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.HERO_REBUILD), mi_rebuild.getText());
        container.open();
      }
    });
    mi_newsys.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.NEW_SYS), mi_rebuild.getText());
        container.open();
      }
    });
    mi_newmode.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.NEW_MODE), mi_rebuild.getText());
        container.open();
      }
    });
    mi_systemOp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.OPTIMIZATION_SYSTEM), mi_systemOp.getText());
        container.open();
      }
    });
    mi_drawOp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        BrowserWindow container = new BrowserWindow(SysConfigLoader.getProperty(AddressConst.OPTIMIZATION_ART), mi_systemOp.getText());
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
                FCService biz = new FCServiceImpl();
                FontDialog dialog = biz.selectDialog(shell, fcPath);
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
                  IOUtils.writeObject(fcPath, fc);
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
        String tableBackground = SysConfigLoader.getProperty(SystemConfigConst.TABLE_BACKGROUND);
        String cursorBackground = SysConfigLoader.getProperty(SystemConfigConst.CURSOR_BACKGROUND);
        String tableItemBackground = SysConfigLoader.getProperty(SystemConfigConst.TABLE_ITEM_BACKGROUND);
        String tableItemForeground = SysConfigLoader.getProperty(SystemConfigConst.TABLE_ITEM_FOREGROUND);

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
        IOUtils.write(fcPath, "");
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
