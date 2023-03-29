package com.patient.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@RestController
@CrossOrigin
@RequestMapping("/download")
public class DownloadController {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@GetMapping(value = "/excel", produces = "application/vnd.ms-excel")
	public ResponseEntity<byte[]> downloadExcel() throws IOException{
		
		List<Object[]> rows = entityManager.createNativeQuery("select * from Clinic").getResultList();
		System.out.println(Arrays.toString(rows.get(0)));
		
		Query q = entityManager.createNativeQuery("desc clinic");
		List<Object[]> col = q.getResultList();
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("ListOfClinics");
		
		//create header row
		Row header = sheet.createRow(0);
		int colNum = 0;
		for(Object[] column : col) {
	
			System.out.println((String)column[0]);
			Cell cell = header.createCell(colNum);
			colNum++;
			cell.setCellValue((String) column[0]);
			
		}
		
		//create Data rows
		int rowNum = 1;
		for(Object[] row:rows) {
			Row dataRow = sheet.createRow(rowNum++);
			colNum =0;
			for(Object value:row) {
				Cell cell = dataRow.createCell(colNum++);
				if(value instanceof String) {
					cell.setCellValue((String)value);
				}
				else if(value instanceof Integer) {
					cell.setCellValue((Integer)value);
				}
				else if(value instanceof Date) {
					cell.setCellValue((Date)value);
				}
			}
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clinic.xlsx").body(baos.toByteArray());
		
	}
	
}
