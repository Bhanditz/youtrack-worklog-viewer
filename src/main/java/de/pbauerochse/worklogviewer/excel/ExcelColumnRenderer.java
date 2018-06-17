package de.pbauerochse.worklogviewer.excel;

import de.pbauerochse.worklogviewer.fx.components.treetable.TreeTableRowModel;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public abstract class ExcelColumnRenderer {

    private static final int ADDITIONAL_ROW_HEIGHT = 5;

    private Font boldFont;
    private Font biggerBoldFont;
    private CellStyle boldCellStyle;
    private CellStyle boldRightAlignedCellStyle;
    private CellStyle regularRightAlignedCellStyle;
    private CellStyle groupHeadlineCellStyle;
    private CellStyle groupHeadlineWorklogCellStyle;

    protected void adjustRowHeight(Cell cellWithStyles) {
        Font font = cellWithStyles.getSheet().getWorkbook().getFontAt(cellWithStyles.getCellStyle().getFontIndex());
        short fontHeightInPoints = font.getFontHeightInPoints();
        Row row = cellWithStyles.getRow();
        row.setHeightInPoints(Math.max(fontHeightInPoints + ADDITIONAL_ROW_HEIGHT, row.getHeightInPoints()));
    }

    protected Row getOrCreateRow(int rowIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    protected CellStyle getHeadlineCellStyle(Sheet sheet) {
        if (boldCellStyle == null) {
            boldCellStyle = sheet.getWorkbook().createCellStyle();
            boldCellStyle.setFont(getBoldFont(sheet));
            boldCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        }
        return boldCellStyle;
    }

    protected CellStyle getWorklogCellStyle(Sheet sheet) {
        if (regularRightAlignedCellStyle == null) {
            regularRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            regularRightAlignedCellStyle.setAlignment(HorizontalAlignment.CENTER);
            regularRightAlignedCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        }
        return regularRightAlignedCellStyle;
    }

    protected CellStyle getWorklogSummaryCellStyle(Sheet sheet) {
        if (boldRightAlignedCellStyle == null) {
            boldRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            boldRightAlignedCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldRightAlignedCellStyle.setFont(getBoldFont(sheet));
            boldRightAlignedCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        }
        return boldRightAlignedCellStyle;
    }

    protected CellStyle getGroupHeadlineCellStyle(Sheet sheet) {
        if (groupHeadlineCellStyle == null) {
            groupHeadlineCellStyle = sheet.getWorkbook().createCellStyle();
            groupHeadlineCellStyle.setFont(getBiggerBoldFont(sheet));
            groupHeadlineCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        }
        return groupHeadlineCellStyle;
    }

    protected CellStyle getGroupHeadlineWorklogCellStyle(Sheet sheet) {
        if (groupHeadlineWorklogCellStyle == null) {
            groupHeadlineWorklogCellStyle = sheet.getWorkbook().createCellStyle();
            groupHeadlineWorklogCellStyle.setFont(getBiggerBoldFont(sheet));
            groupHeadlineWorklogCellStyle.setAlignment(HorizontalAlignment.CENTER);
            groupHeadlineWorklogCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        }
        return groupHeadlineWorklogCellStyle;
    }

    private Font getBoldFont(Sheet sheet) {
        if (boldFont == null) {
            boldFont = sheet.getWorkbook().createFont();
            boldFont.setFontHeightInPoints((short) 12);
            boldFont.setBold(true);
        }
        return boldFont;
    }

    private Font getBiggerBoldFont(Sheet sheet) {
        if (biggerBoldFont == null) {
            biggerBoldFont = sheet.getWorkbook().createFont();
            biggerBoldFont.setBold(true);
            biggerBoldFont.setFontHeightInPoints((short) 14);
        }
        return biggerBoldFont;
    }

    public void write(@NotNull Row row, int columnIndex, @NotNull TreeTableRowModel value) {

    }
}
