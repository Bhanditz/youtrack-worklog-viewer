package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.ProgressCallback;
import de.pbauerochse.worklogviewer.excel.ExcelExporter;
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableViewData;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Task the triggers the Excel Report generation
 * of the given data
 */
public class ExportToExcelTask extends WorklogViewerTask<File> {

    private static final Logger LOG = LoggerFactory.getLogger(ExportToExcelTask.class);

    private final String text;
    private final WorklogsTreeTableViewData data;
    private final File targetFile;

    public ExportToExcelTask(String text, WorklogsTreeTableViewData data, File targetFile) {
        super();
        this.text = text;
        this.data = data;
        this.targetFile = targetFile;
    }

    @NotNull
    @Override
    public String getLabel() {
        return getFormatted("task.excelexport", text);
    }

    @Override
    public File start(@NotNull ProgressCallback progressCallback) {
        Workbook workbook = ExcelExporter.createWorkbook(text, data);

        progressCallback.setProgress(getFormatted("worker.excel.exporting"), 50);

        try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            LOG.warn("Could not write Excel to " + targetFile.getAbsolutePath(), e);
        } finally {
            progressCallback.setProgress(getFormatted("exceptions.excel.success", targetFile.getAbsolutePath()), 100);
        }

        return targetFile;
    }

}
