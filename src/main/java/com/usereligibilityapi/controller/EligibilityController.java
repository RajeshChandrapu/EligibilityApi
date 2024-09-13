package com.usereligibilityapi.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usereligibilityapi.binding.EligibilityBinding;
import com.usereligibilityapi.model.EligibilityModel;
import com.usereligibilityapi.request.SearchRequest;
import com.usereligibilityapi.response.SearchResponse;
import com.usereligibilityapi.service.EligibilityService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/Eligibility")
public class EligibilityController {

	private EligibilityService eligService;

	public EligibilityController(EligibilityService eligservice) {
		super();
		this.eligService = eligservice;
	}

	@PostMapping("/create")
	public ResponseEntity<EligibilityBinding> createUser(@RequestBody EligibilityBinding binding) {
		System.out.println("this is from controller====>" + binding.getPlanName());
		EligibilityModel em = new EligibilityModel();

		BeanUtils.copyProperties(binding, em);

		EligibilityModel user = eligService.createUser(em);

		EligibilityBinding eb = new EligibilityBinding();
		// System.out.println(eb);
		BeanUtils.copyProperties(user, eb);

		return new ResponseEntity(eb, HttpStatus.CREATED);
	}

	@GetMapping("/plan")
	public ResponseEntity<List<String>> getPlanName() {
		List<String> planNames = eligService.getUniquePlanNames();
		return new ResponseEntity<List<String>>(planNames, HttpStatus.OK);

	}

	@GetMapping("/status")
	public ResponseEntity<List<String>> getPlanStatus() {
		List<String> planStatus = eligService.getUniquePlanStatus();
		return new ResponseEntity<List<String>>(planStatus, HttpStatus.OK);

	}

	@PostMapping("/search")
	public ResponseEntity<List<SearchResponse>> search(@RequestBody SearchRequest request) {
		List<SearchResponse> search = eligService.search(request);
		return new ResponseEntity<List<SearchResponse>>(search, HttpStatus.OK);

	}

	@GetMapping("/excel")
	public void generateExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment;filename =data.xls";
		response.setHeader(headerKey, headerValue);

		eligService.generateExcel(response);

	}

	@GetMapping("/pdf")
	public void generatePdf(HttpServletResponse response) {
		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename = data.pdf";
		response.setHeader(headerKey, headerValue);

		try {
			eligService.generatePdf(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
