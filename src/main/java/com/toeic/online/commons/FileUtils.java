/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.toeic.online.commons;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author thangdd8@viettel.com.vn
 * @version 1.0
 * @since Apr 12, 2010
 */
@Service
public class FileUtils {

    private final HttpServletRequest request;

    public FileUtils(HttpServletRequest request) {
        this.request = request;
    }

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static int validateAttachFile(MultipartFile attachFile, String fileName) {
        String fileExt = "7z,rar,zip,txt,ppt,pptx,doc,docx,xls,xlsx,pdf,jpg,jpeg,png,bmp,gif";
        List<String> lstValidFileExt = Arrays.asList(fileExt.split(","));

        String fileType = fileName.toLowerCase().substring(fileName.lastIndexOf(".") + 1);
        if (!lstValidFileExt.contains(fileType)) {
            return 24; //24-wrongFileType
        }

        double bytes = attachFile.getSize();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        if (megabytes > 20) {
            return 25; //25-wrongFileSize20MB
        }
        return 0; //0-validate ok
    }

    public static Long validateAttachFile(MultipartFile attachFile, String fileExt, Double fileSize) {
        List<String> lstValidFileExt = Arrays.asList(fileExt.split(","));
        String fileName = attachFile.getOriginalFilename();
        String fileType = fileName.toLowerCase().substring(fileName.lastIndexOf(".") + 1);
        if (!lstValidFileExt.contains(fileType)) {
            return 24L; //24-wrongFileType
        }

        double bytes = attachFile.getSize();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        if (megabytes > fileSize) {
            return 25L; //25-wrongFileSize20MB
        }
        return 0L; //0-validate ok
    }

    public static int validateAttachFileExcel(MultipartFile attachFile, String fileName) {
        String fileExt = "xlsx,xls";
        List<String> lstValidFileExt = Arrays.asList(fileExt.split(","));

        String fileType = fileName.toLowerCase().substring(fileName.lastIndexOf(".") + 1);
        if (!lstValidFileExt.contains(fileType)) {
            return 24; //24-wrongFileType
        }

        double bytes = attachFile.getSize();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        if (megabytes > 20) {
            return 25; //25-wrongFileSize20MB
        }
        return 0; //0-validate ok
    }

    public static Boolean copyAttachFile(MultipartFile file, String attachFileDir, String fileName) {
        try {
            File dir = new File(attachFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Path targetLocation = Paths.get(attachFileDir + File.separator + fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File copied successful: " + attachFileDir + File.separator + fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Boolean deleteAttachFile(String attachFileDir, String fileName) {
        try {
            File fileDelete = new File(attachFileDir + File.separator + fileName);
            fileDelete.delete();
            log.info("Delete file successful: " + attachFileDir + File.separator + fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static void saveFile(String filePath, MultipartFile multipartFile) throws IOException {
        File file = new File(filePath);
        File folder = file.getParentFile();
        if (!folder.exists()) folder.mkdirs();
        FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(new File(filePath)));
    }

    public static String copyFile(String filePath, String newFilePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName();
        File newFile = new File(newFilePath);
        File folder = newFile.getParentFile();
        if (!folder.exists()) folder.mkdirs();
        org.apache.commons.io.FileUtils.copyFileToDirectory(file, newFile);
        return fileName;
    }

    public static Object downloadFile(String path, String fileName) {
        File file;
        file = new File(path + fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return IOUtils.toByteArray(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String saveFile(MultipartFile file) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        String encryptFileName = simpleDateFormat.format(new Date()) + "_" + file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        String rootPath = request.getServletContext().getRealPath("/uploads/files/" + encryptFileName);
        Path path = Paths.get(rootPath);
        Files.write(path, bytes);
        return encryptFileName;
    }

    public static String writeFileToServer(InputStream inputStream, String fileName, String subFolder, String folder)
        throws Exception {

        String safeFileName = DataUtil.safeToString(fileName);//+ File.pathSeparator + UString.extractFileExt(fileName);
        Calendar cal = Calendar.getInstance();
        String uploadPath = folder + File.separator + getSafeFileName(subFolder) + File.separator + cal.get(Calendar.YEAR)
            + File.separator + (cal.get(Calendar.MONTH) + 1) + File.separator + cal.get(Calendar.DATE)
            + File.separator + cal.get(Calendar.MILLISECOND);
        File udir = new File(uploadPath);
        if (!udir.exists()) {
            udir.mkdirs();
        }
        try (OutputStream out = new FileOutputStream(udir.getAbsolutePath() + File.separator + safeFileName)) {
            int bytesRead = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((bytesRead = inputStream.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return uploadPath + File.separator + safeFileName;

    }

    public static String getSafeFileName(String input) {
        StringBuilder sb = new StringBuilder();
        if (input != null) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c != '/' && c != '\\' && c != 0) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    // Save Multi File
    public static String convertAllPathToString(List<MultipartFile> files, String subFolder, String folder) throws Exception {
        StringBuilder filePathToSave = new StringBuilder();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String filePath = FileUtils.writeFileToServer(file.getInputStream(), fileName, subFolder, folder);
            filePathToSave.append(filePath).append("|");
        }
        String filePathReplacedLastCharacter = null;
        if (filePathToSave.length() > 0 && filePathToSave.charAt(filePathToSave.length() - 1) == '|') {
            filePathReplacedLastCharacter = filePathToSave.substring(0, filePathToSave.length() - 1);
        }
        return filePathReplacedLastCharacter;
    }

    public static String formatPathImg(String path) {
        return path.substring(path.indexOf("/assets"));
    }

    public static ByteArrayInputStream formatExcelFile(MultipartFile file) throws IOException {
        try{
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle style = workbook.createCellStyle();
//            style.setFillForegroundColor(IndexedColors.RED.getIndex());
            Font font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);
            style.setWrapText(true);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            for (Row row : sheet) {
                if(row.getRowNum() == 0)
                    continue;
                for (Cell cell : row) {
                    cell.setCellStyle(style);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            workbook.write(baos);
            workbook.close();
            return new ByteArrayInputStream(baos.toByteArray());
        }catch (Exception e) {
            return new ByteArrayInputStream(file.getBytes());
        }
    }
}
