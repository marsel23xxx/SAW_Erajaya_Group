package com.erajaya.datamining.util;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;

/**
 * Utility class untuk font styling dalam PDF
 */
public class FontUtil {
    
    // Base fonts
    public static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    public static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    public static final Font SUBHEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    public static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    public static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    
    // Table fonts
    public static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    public static final Font TABLE_CELL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    
    // Colors
    public static final BaseColor HEADER_COLOR = new BaseColor(70, 130, 180);
    public static final BaseColor ALTERNATE_ROW_COLOR = new BaseColor(245, 245, 245);
    public static final BaseColor PRIMARY_COLOR = new BaseColor(25, 25, 112);
    
    /**
     * Create custom font with specific size and style
     */
    public static Font createFont(int size, int style) {
        return new Font(Font.FontFamily.HELVETICA, size, style);
    }
    
    /**
     * Create bold font with specific size
     */
    public static Font createBoldFont(int size) {
        return new Font(Font.FontFamily.HELVETICA, size, Font.BOLD);
    }
    
    /**
     * Create normal font with specific size
     */
    public static Font createNormalFont(int size) {
        return new Font(Font.FontFamily.HELVETICA, size, Font.NORMAL);
    }
    
    /**
     * Create italic font with specific size
     */
    public static Font createItalicFont(int size) {
        return new Font(Font.FontFamily.HELVETICA, size, Font.ITALIC);
    }
    
    /**
     * Create font with color
     */
    public static Font createColoredFont(int size, int style, BaseColor color) {
        Font font = new Font(Font.FontFamily.HELVETICA, size, style);
        font.setColor(color);
        return font;
    }
}