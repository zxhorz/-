package com.hengtiansoft.bluemorpho.workbench.util;
 
import java.io.File;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
 
public class WordUtil {
    private static final Logger LOGGER = Logger.getLogger(WordUtil.class);
    public static final int WORD_HTML = 8;  
    public static final int WORD_TXT = 7;  
    public static final int EXCEL_HTML = 44;  
    public static final int EXCEL_XML = 46;
    public static final int WORD_PDF = 17;

    public static void updateIndex(File docfile) {
        ActiveXComponent app = new ActiveXComponent("Word.Application");
        Dispatch doc = null;
        try {
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();

            // 打开word文档
            doc = Dispatch.call(docs, "Open", docfile.getAbsolutePath()).toDispatch();
            Dispatch activeDocument = app.getProperty("ActiveDocument").toDispatch();
            // 获取目录
            Dispatch tablesOfContents = Dispatch.get(activeDocument, "TablesOfContents").toDispatch();
            // 获取第一个目录。若有多个目录，则传递对应的参数
            Variant tablesOfContent = Dispatch.call(tablesOfContents, "Item", new Variant(1));

            // 更新目录(参数:Update　更新域，UpdatePageNumbers　只更新页码)
            Dispatch toc = tablesOfContent.toDispatch();
            Dispatch.call(toc, "Update");
            Dispatch.call(doc, "Save");

            // /**另存为*/
            // Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {
            // docfile, new Variant(type) }, new int[1]);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit", new Variant[] {});
        }

        LOGGER.info("Finish updating index");
    }
    
    public static void wordToHtml(File docfile, String htmlfilePath) {
        ActiveXComponent app = new ActiveXComponent("Word.Application");
        Dispatch doc = null;
        try {
            File file = new File(htmlfilePath);
            if(file.exists())
                file.delete();
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", docfile.getAbsolutePath()).toDispatch();

            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { htmlfilePath, new Variant(10) },
                    new int[1]);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit", new Variant[] {});
        }
        LOGGER.info("Finish converting to html");
    }
    
    public static void wordToPdf(File docFile, String pdfPath) {
        ActiveXComponent app = new ActiveXComponent("Word.Application");
        Dispatch doc = null;
        try {
            File file = new File(pdfPath);
            if(file.exists())
                file.delete();
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", docFile.getAbsolutePath()).toDispatch();

            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { pdfPath, new Variant(WORD_PDF) },
                    new int[1]);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit", new Variant[] {});
        }
        LOGGER.info("Finish converting to pdf");
    }
    
}