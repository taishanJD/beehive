package com.quarkdata.yunpan.api.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

/**
 * Created by liuda
 * Date : 2018/3/26
 */
public class EsUtils {

    static Logger logger = LoggerFactory.getLogger(EsUtils.class);

    public static String getContent(InputStream file, String documentType) throws IOException {
        switch (documentType) {
            case "pdf":
                return readPdf(file);
            case "docx":
                return readDocx(file);
            case "doc":
                return readDoc(file);
            case "xls":
                return readXls(file);
            case "xlsx":
                return readXlsx(file);
            case "ppt":
                return readPpt(file);
            case "pptx":
                return readPptx(file);
            case "txt":
                return readTxt(file);
            default:
                return null;
        }
    }

    public static String readFile(MultipartFile file) throws IOException {
        String documentName = file.getOriginalFilename();
        String documentType = FilenameUtils.getExtension(documentName);
        switch (documentType) {
            case "pdf":
                return readPdf(file.getInputStream());
            case "docx":
                return readDocx(file.getInputStream());
            case "doc":
                return readDoc(file.getInputStream());
            case "xls":
                return readXls(file.getInputStream());
            case "xlsx":
                return readXlsx(file.getInputStream());
            case "ppt":
                return readPpt(file.getInputStream());
            case "pptx":
                return readPptx(file.getInputStream());
            case "txt":
                return readTxt(file.getInputStream());
            default:
                return null;
        }
    }

    /**
     * 读取txt文件
     *
     * @param file
     */
    private static String readTxt(InputStream file) {
        StringBuilder stringBuilder = new StringBuilder();
        String code;
        String line;
        BufferedReader reader = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            bufferedInputStream = new BufferedInputStream(file);
            bufferedInputStream.mark(3);
            code = getUnicode(bufferedInputStream);
            bufferedInputStream.reset();
            reader = new BufferedReader(new InputStreamReader(bufferedInputStream, code));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析txt文档失败");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("字符缓冲流关闭失败");
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("字符缓冲流关闭失败");
                }
            }
        }
        return null;
    }

    /**
     * 解析docx文档
     *
     * @param file
     * @return
     */
    private static String readDocx(InputStream file) {
        String content = null;
        try {
            XWPFDocument document = new XWPFDocument(file);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder contentBuilder = new StringBuilder();
            for (XWPFParagraph para : paragraphs) {
                contentBuilder.append(para.getText().trim());
            }
            content = contentBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析docx文档失败");
        }
        return content;
    }

    /**
     * 解析doc文档
     *
     * @param file
     * @return
     */
    private static String readDoc(InputStream file) {
        StringBuilder text = null;
        try {
            HWPFDocument document = new HWPFDocument(file);
            text = document.getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析doc文档失败");
        }
        return text.toString();
    }

    /**
     * 解析pdf文件
     *
     * @param file
     * @return
     */
    private static String readPdf(InputStream file) {
        String content = null;
        PDDocument document = null;
        // 加载 pdf 文档
        try {
            //创建解析器对象
            PDFParser pdfParser = new PDFParser(new RandomAccessBuffer(file));
            pdfParser.parse();
            //pdf文档
            document = pdfParser.getPDDocument();
            //pdf文本操作对象,使用该对象可以获取所读取pdf的一些信息
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            content = pdfTextStripper.getText(document);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析pdf文档失败");
        } finally {
            //document对象时使用完后必须要关闭
            if (null != document)
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("PDDocumen关闭失败");
                }
        }
        return content;
    }

    /**
     * 解析xls文件
     *
     * @param file
     * @return
     */
    private static String readXls(InputStream file) {
        HSSFWorkbook wb = null;
        String text = null;
        try {
            wb = new HSSFWorkbook(new POIFSFileSystem(file));
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
            text = extractor.getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析xls文档失败");
        }
        return text;
    }

    /**
     * 解析xlsx文件
     *
     * @param file
     * @return
     */
    private static String readXlsx(InputStream file) {
        String text = null;
        try {
            XSSFWorkbook xwb = new XSSFWorkbook(file);
            XSSFExcelExtractor xssfExcelExtractor = new XSSFExcelExtractor(xwb);
            text = xssfExcelExtractor.getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析xlsx文档失败");
        }
        return text;
    }

    /**
     * 解析ppt文件
     *
     * @param file
     * @return
     */
    private static String readPpt(InputStream file) {
        String text = null;
        try {
            PowerPointExtractor powerPointExtractor = new PowerPointExtractor(file);
            text = powerPointExtractor.getText();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析ppt文档失败");
        }
        return text;
    }

    /**
     * 解析pptx文档
     *
     * @param file
     * @return
     */
    private static String readPptx(InputStream file) {
        String text = null;
        try {
            try {
                text = new XSLFPowerPointExtractor(new XMLSlideShow(OPCPackage.open(file)))
                        .getText();
            } catch (XmlException e) {
                e.printStackTrace();
                logger.error("es解析pptx文档失败");
            } catch (OpenXML4JException e) {
                e.printStackTrace();
                logger.error("es解析pptx文档失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("es解析pptx文档失败");
        }
        return text;
    }

    private static String getUnicode(InputStream file) {
        String code1 = "";
        byte[] head = new byte[3];
        try {
            file.read(head);
            code1 = "gb2312";
            if (head[0] == -1 && head[1] == -2)
                code1 = "UTF-16";
            if (head[0] == -2 && head[1] == -1)
                code1 = "Unicode";
            if (head[0] == -17 && head[1] == -69 && head[2] == -65)
                code1 = "UTF-8";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code1;
    }
}
