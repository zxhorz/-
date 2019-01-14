package com.hengtiansoft.bluemorpho.workbench.util;

import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Sep 19, 2018 1:18:11 PM
 */
public class PdfConverterUtil {
	
//	public static void main(String[] args) throws Exception {  
//        PdfConverterUtil converter = new PdfConverterUtil();  
//        converter.generatePDF_2(new File("E:/my.pdf"), "E:/SystemDocumentation.html"); 
//        System.out.println("success");
//    }  
	
    // 手动构造HTML代码  
    public static void generatePDF_1(File outputPDFFile, StringReader strReader) throws Exception {  
        FileOutputStream fos = new FileOutputStream(outputPDFFile);  
        PD4ML pd4ml = new PD4ML();  
        pd4ml.setPageInsets(new Insets(20, 10, 10, 10));  
        pd4ml.setHtmlWidth(950);  
        pd4ml.setPageSize(pd4ml.changePageOrientation(PD4Constants.A4));  
        pd4ml.useTTF("java:fonts", true);  
        pd4ml.setDefaultTTFs("KaiTi_GB2312", "KaiTi_GB2312", "KaiTi_GB2312");  
        pd4ml.enableDebugInfo();  
        pd4ml.render(strReader, fos);  
    }  
  
    // HTML代码来自于HTML文件  
    public static void generatePDF_2(File outputPDFFile, String inputHTMLFileName) throws Exception {  
        FileOutputStream fos = new FileOutputStream(outputPDFFile);  
        PD4ML pd4ml = new PD4ML();  
        pd4ml.setPageInsets(new Insets(20, 10, 10, 10));  
        pd4ml.setHtmlWidth(950);  
        pd4ml.setPageSize(pd4ml.changePageOrientation(PD4Constants.A4));  
        pd4ml.useTTF("java:fonts", true);  
        pd4ml.setDefaultTTFs("KaiTi_GB2312", "KaiTi_GB2312", "KaiTi_GB2312");  
        pd4ml.enableDebugInfo();  
        pd4ml.render("file:" + inputHTMLFileName, fos);  
    }  

}
