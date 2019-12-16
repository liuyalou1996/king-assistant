package com.universe.pojo;

import java.io.Serializable;

/**
 * 保存字体和颜色的一个类
 *
 * @author Administrator
 */
public class FontAndColor implements Serializable {

    private static final long serialVersionUID = -8414977398043885296L;
    private int fontHeight;
    private int fontStyle;
    private String fontName;
    private int red;
    private int green;
    private int blue;

    public int getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "FontAndColor [fontHeight=" + fontHeight + ", fontStyle=" + fontStyle + ", fontName=" + fontName + ", red="
                + red + ", green=" + green + ", blue=" + blue + "]";
    }

}
