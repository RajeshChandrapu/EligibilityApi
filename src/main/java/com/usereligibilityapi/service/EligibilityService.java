package com.usereligibilityapi.service;


import java.io.IOException;
import java.util.List;

import com.usereligibilityapi.model.EligibilityModel;
import com.usereligibilityapi.request.SearchRequest;
import com.usereligibilityapi.response.SearchResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface EligibilityService {

    EligibilityModel createUser(EligibilityModel model);

    List<String> getUniquePlanNames();

    List<String> getUniquePlanStatus();

    List<SearchResponse> search(SearchRequest request);

    public void generateExcel(HttpServletResponse response) throws IOException;

    public void generatePdf(HttpServletResponse response) throws IOException;
}
