package com.kevin.ahorcado.services.impl;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kevin.ahorcado.models.VisitCounter;
import com.kevin.ahorcado.repositories.VisitCounterRepository;
import com.kevin.ahorcado.services.VisitCounterService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class VisitCounterServiceImpl implements VisitCounterService{

	private final SimpMessagingTemplate messagingTemplate;
    //private static final String VISIT_COOKIE_NAME = "visitCookie";
    private static final String VISIT_IP_ATTRIBUTE_NAME = "visitIp";
    //private int visitCount = 0;
    private VisitCounterRepository visitCounterRepository;
    
	public VisitCounterServiceImpl(SimpMessagingTemplate messagingTemplate, VisitCounterRepository visitCounterRepository) {
		this.messagingTemplate = messagingTemplate;
		this.visitCounterRepository = visitCounterRepository;
	}

	@Override
	public String getClientIp(HttpServletRequest request) {
		String userIp = request.getHeader("X-Forwarded-For");

        if (userIp == null || userIp.isEmpty() || "unknown".equalsIgnoreCase(userIp)) {
            userIp = request.getHeader("Proxy-Client-IP");
        }

        if (userIp == null || userIp.isEmpty() || "unknown".equalsIgnoreCase(userIp)) {
            userIp = request.getHeader("WL-Proxy-Client-IP");
        }

        if (userIp == null || userIp.isEmpty() || "unknown".equalsIgnoreCase(userIp)) {
            userIp = request.getRemoteAddr();
        }

        return userIp;
	}

	@Override
	public void setVisited(HttpServletRequest request, HttpServletResponse response, String userIp) {
		request.getSession().setAttribute(VISIT_IP_ATTRIBUTE_NAME, userIp);
        System.err.println("Estableciendo atributo de IP de visita para la IP: " + userIp);

        // Crea y configura una cookie de visita
        Cookie cookie = new Cookie(generateRandomCookie(), userIp);
        cookie.setMaxAge(24 * 60 * 60); // Expiración de 1 dia
        response.addCookie(cookie);
        System.err.println("Estableciendo cookie de visita para la IP: " + userIp);
		
	}

	@Override
	public boolean hasVisited(HttpServletRequest request, String userIp) {
		String storedIp = (String) request.getSession().getAttribute(VISIT_IP_ATTRIBUTE_NAME);

        if (storedIp != null && storedIp.equals(userIp)) {
            System.err.println("El usuario ya ha visitado antes. IP: " + userIp);
            return true;
        }

        System.err.println("El usuario está visitando por primera vez. IP: " + userIp);
        return false;
	}

	@Override
	public int incrementVisit() {
		System.err.println("Solicitud recibida en /incrementVisit");
		int visitCount = getVisitCount();
        visitCount++;
        // Envía el nuevo contador a todos los clientes suscritos al destino "/topic/visitCount"
        messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
        System.err.println("Contador incrementado a: " + visitCount);
        return visitCount;
	}

	@Override
	public int getVisitCount(HttpServletRequest request, HttpServletResponse response) {
		// Obtiene la dirección IP del usuario
		
		VisitCounter visitCounter = new VisitCounter();
		
        String userIp = getClientIp(request);
        System.err.println("Solicitud recibida en /visit. IP del usuario: " + userIp);
        
        int visitCount = getVisitCount();
        
        // Verifica si el usuario ha visitado antes
        if (!hasVisited(request, userIp)) {
        	
            visitCount++;
            setVisited(request, response, userIp);
            // Envía el nuevo contador a todos los clientes suscritos al destino "/topic/visitCount"
            messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
            System.err.println("Nueva visita detectada. Contador incrementado a: " + visitCount);
        } else {
            System.err.println("El usuario ya ha visitado antes. El contador permanece en: " + visitCount);
        }
        
        visitCounter.setCount(visitCount);
        visitCounter.setIp(userIp);
        visitCounter.setCookie(generateRandomCookie());
        
        visitCounterRepository.save(visitCounter);
        
        return visitCount;
	}
	
	private int getVisitCount() {
	    Integer n = visitCounterRepository.getMaxCount();
	    System.err.println("contador actual: "+n);
	    return (n != null) ? n : 0;
	}
	
	private String generateRandomCookie() {
	    SecureRandom secureRandom = new SecureRandom();
	    byte[] randomBytes = new byte[20];
	    secureRandom.nextBytes(randomBytes);
	    BigInteger bigInteger = new BigInteger(1, randomBytes);
	    String randomCookie = bigInteger.toString(16);

	    // Asegurarse de que el cookie tenga la longitud deseada
	    while (randomCookie.length() < 20) {
	        randomCookie = "0" + randomCookie;
	    }

	    return randomCookie;
	}
	
}
