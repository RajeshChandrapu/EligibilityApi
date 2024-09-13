package com.usereligibilityapi.service;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.usereligibilityapi.entity.EligibilityEntity;
import com.usereligibilityapi.model.EligibilityModel;
import com.usereligibilityapi.repository.EligibilityRepository;
import com.usereligibilityapi.request.SearchRequest;
import com.usereligibilityapi.response.SearchResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class EligibilityServiceImpl implements EligibilityService {

	private final EligibilityRepository eligibilityRepository;

	public EligibilityServiceImpl(EligibilityRepository eligibilityRepository) {
		this.eligibilityRepository = eligibilityRepository;
	}

	@Override
	public EligibilityModel createUser(EligibilityModel model) {
		EligibilityEntity user = new EligibilityEntity();
		BeanUtils.copyProperties(model, user);
		EligibilityEntity savedEntity = eligibilityRepository.save(user);
		EligibilityModel responseModel = new EligibilityModel();
		BeanUtils.copyProperties(savedEntity, responseModel);
		return responseModel;
	}

	@Override
	public List<String> getUniquePlanNames() {
		return eligibilityRepository.findPlanNames();
	}

	@Override
	public List<String> getUniquePlanStatus() {
		return eligibilityRepository.findPlanStatus();
	}

	@Override
	public List<SearchResponse> search(SearchRequest request) {
		List<SearchResponse> response = new ArrayList<>();
		EligibilityEntity queryBuilder = new EligibilityEntity();

		if (request.getPlanName() != null && !request.getPlanName().trim().isEmpty()) {
			queryBuilder.setPlanName(request.getPlanName());
		}
		if (request.getPlanStatus() != null && !request.getPlanStatus().trim().isEmpty()) {
			queryBuilder.setPlanStatus(request.getPlanStatus());
		}
		if (request.getPlanStartDate() != null) {
			queryBuilder.setPlanStartDate(request.getPlanStartDate());
		}
		if (request.getPlanEndDate() != null) {
			queryBuilder.setPlanEndDate(request.getPlanEndDate());
		}

		Example<EligibilityEntity> example = Example.of(queryBuilder);
		List<EligibilityEntity> entities = eligibilityRepository.findAll(example);
		for (EligibilityEntity entity : entities) {
			SearchResponse searchResponse = new SearchResponse();
			BeanUtils.copyProperties(entity, searchResponse);
			response.add(searchResponse);
		}

		return response;
	}

	@Override
	public void generateExcel(HttpServletResponse response) throws IOException {
		List<EligibilityEntity> entities = eligibilityRepository.findAll();

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow headerRow = sheet.createRow(0);

		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Email");
		headerRow.createCell(2).setCellValue("Gender");
		headerRow.createCell(3).setCellValue("MobileNo");
		headerRow.createCell(4).setCellValue("SSN");

		int i = 1;
		for (EligibilityEntity entity : entities) {
			HSSFRow dataRow = sheet.createRow(i);
			dataRow.createCell(0).setCellValue(entity.getName());
			dataRow.createCell(1).setCellValue(entity.getEmail());
			dataRow.createCell(2).setCellValue(String.valueOf(entity.getGender()));
			dataRow.createCell(3).setCellValue(entity.getMobile());
			dataRow.createCell(4).setCellValue(entity.getSsn());
			i++;
		}
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	@Override
	public void generatePdf(HttpServletResponse response) throws IOException {
		List<EligibilityEntity> entities = eligibilityRepository.findAll();

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);

		Paragraph p = new Paragraph("SEARCH REPORT", font);
		p.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(p);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 1.5f, 3.5f, 3.0f, 1.5f, 3.0f });
		table.setSpacingBefore(10);

		PdfPCell cell = new PdfPCell();

		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);

		cell.setPhrase(new Phrase("Name", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Email", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Mobile No", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Gender", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("SSN", font));
		table.addCell(cell);

		for (EligibilityEntity entity : entities) {
			table.addCell(entity.getName());
			table.addCell(entity.getEmail());
			table.addCell(String.valueOf(entity.getMobile()));
			table.addCell(String.valueOf(entity.getGender()));
			table.addCell(String.valueOf(entity.getSsn()));
		}
		document.add(table);
		document.close();
	}

}
