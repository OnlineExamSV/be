package com.toeic.online.commons;

import com.toeic.online.constant.AppConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ITSOL maint Oct 8, 2018 ITSOL maint
 * @author ThangNM-ITSOL
 * @version Oct 10, 2018 ThangNM-ITSOL Init.
 */
public class FileUploadUtil {

	public static final int HEADER_ROW_INDEX = 0;

	/**
	 * check file upload not null or not empty
	 *
	 * @param files CommonsMultipartFile[]
	 * @return return true if files upload is not null or not empty else return false
	 */
	public static boolean isNotNullOrEmpty( MultipartFile files ) {
		return files != null && StringUtils.isNotBlank(files.getOriginalFilename());
	}

	/**
	 * Read excel in sheet 0
	 *
	 * @param excelFile MultipartFile
	 * @return List data in excel file
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static List<List<String>> excelReader( MultipartFile excelFile ) throws IOException {
		List<List<String>> datas = new ArrayList<>();
		InputStream inputStream = excelFile.getInputStream();
		String extention = FilenameUtils.getExtension(excelFile.getOriginalFilename());
		Workbook workbook;
//		if( AppConstants.EXTENSION_XLSX.equals(extention.toLowerCase()) ) {
			workbook = new XSSFWorkbook(inputStream);
//		} else {
//			workbook = new HSSFWorkbook(inputStream);
//		}
		Sheet sheet = workbook.getSheetAt(0);

		DataFormatter formatter = new DataFormatter();

		Iterator<Row> iterator = sheet.iterator();
		while( iterator.hasNext() ) {
			Row currentRow = iterator.next();
			List<String> cells = new ArrayList<>();
			/*
			 * while( cellIterator.hasNext() ) { Cell cell = cellIterator.next(); String cellValue; if(
			 * cell.getCellType() == CellType.STRING ) { cellValue = cell.getStringCellValue(); } if( cell.getCellType()
			 * == CellType.NUMERIC ) { cellValue = formatter.formatCellValue(cell); cellValue =
			 * cellValue.replaceAll(AppConstants.COMMA_DELIMITER, StringUtils.EMPTY); } else { cellValue =
			 * formatter.formatCellValue(cell); } cells.add(cellValue); }
			 */
			for( int i = 0; i < currentRow.getLastCellNum(); i++ ) {
				Cell currCell = currentRow.getCell(i);
				String cellVal;
				if( currCell != null ) {
					if( currCell.getCellType() == CellType.STRING ) {
						cellVal = currCell.getStringCellValue();
					}
					if( currCell.getCellType() == CellType.NUMERIC ) {
						cellVal = formatter.formatCellValue(currCell);
						cellVal = cellVal.replaceAll(AppConstants.COMMA_DELIMITER, StringUtils.EMPTY);
					} else {
						cellVal = formatter.formatCellValue(currCell);
					}
				} else {
					cellVal = StringUtils.EMPTY;
				}

				cells.add(cellVal);
			}

			datas.add(cells);
		}

		return datas;
	}

	/**
	 *
	 * @param excelFile MultipartFile
	 * @param iSheet
	 * @param iBeginRow
	 * @param iFromCol
	 * @param iToCol
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static List<List<String>> excelReader( MultipartFile excelFile, int iSheet, int iBeginRow, int iFromCol,
			int iToCol ) throws IOException {
		List<List<String>> result = new ArrayList<>();
		InputStream inputStream = excelFile.getInputStream();
		String extention = FilenameUtils.getExtension(excelFile.getOriginalFilename());
		Workbook workbook;
		if( AppConstants.EXTENSION_XLSX.equals(extention.toLowerCase()) ) {
			workbook = new XSSFWorkbook(inputStream);
		} else {
			workbook = new HSSFWorkbook(inputStream);
		}
		Sheet worksheet = workbook.getSheetAt(iSheet);
		DataFormatter formatter = new DataFormatter();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DateFormat.YYYY_MM_DD);
		for( int i = iBeginRow; i <= worksheet.getLastRowNum(); i++ ) {
			List<String> list = new ArrayList<>();
			Row row = worksheet.getRow(i);

			if( row != null ) {
				String cellValue;
				for( int j = iFromCol; j < iToCol; j++ ) {

					Cell cell = row.getCell(j);

					if( cell != null ) {
						CellType cellType = cell.getCellType();
						if( cellType == CellType.STRING ) {
							cellValue = cell.getStringCellValue().trim();
						} else if( cellType == CellType.NUMERIC ) {
							if( HSSFDateUtil.isCellDateFormatted(cell) ) {
								Double doubleValue = (Double) cell.getNumericCellValue();
								Date date = HSSFDateUtil.getJavaDate(doubleValue);
								cellValue = simpleDateFormat.format(date);
							} else {
								cellValue = formatter.formatCellValue(cell);
								cellValue = cellValue.replaceAll(AppConstants.COMMA_DELIMITER, StringUtils.EMPTY);
							}
						} else {
							cellValue = formatter.formatCellValue(cell);
						}
						list.add(cellValue);
					} else {
						list.add(null);
					}
				}
				result.add(list);
			}
		}
		return result;
	}
}
