package com.kevin.ahorcado.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface VisitCounterService {

	String getClientIp(HttpServletRequest request);
	void setVisited(HttpServletRequest request, HttpServletResponse response, String userIp);
	boolean hasVisited(HttpServletRequest request, String userIp);
	int incrementVisit();
	int getVisitCount(HttpServletRequest request, HttpServletResponse response);
}
