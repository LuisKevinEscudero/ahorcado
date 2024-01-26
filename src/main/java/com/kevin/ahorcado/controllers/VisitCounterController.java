package com.kevin.ahorcado.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("http://localhost:3000")
public class VisitCounterController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String VISIT_COOKIE_NAME = "visitCookie";
    private static final String VISIT_IP_ATTRIBUTE_NAME = "visitIp";
    private int visitCount = 0;

    public VisitCounterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/visit")
    public int getVisitCount(HttpServletRequest request, HttpServletResponse response) {
        String userIp = getClientIp(request);
        System.err.println("Request received to /visit. User IP: " + userIp);

        if (!hasVisited(request, userIp)) {
            visitCount++;
            setVisited(request, response, userIp);
            messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
            System.err.println("New visit detected. Incremented count to: " + visitCount);
        } else {
            System.err.println("User has visited before. Count remains at: " + visitCount);
        }

        return visitCount;
    }

    @GetMapping("/incrementVisit")
    public int incrementVisit() {
        System.err.println("Request received to /incrementVisit");
        visitCount++;
        // Enviar el nuevo contador a todos los clientes suscritos al destino "/topic/visitCount"
        messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
        System.err.println("Incremented count to: " + visitCount);
        return visitCount;
    }

    private boolean hasVisited(HttpServletRequest request, String userIp) {
        String storedIp = (String) request.getSession().getAttribute(VISIT_IP_ATTRIBUTE_NAME);

        if (storedIp != null && storedIp.equals(userIp)) {
            System.err.println("User has visited before. IP: " + userIp);
            return true;
        }

        System.err.println("User is visiting for the first time. IP: " + userIp);
        return false;
    }

    private void setVisited(HttpServletRequest request, HttpServletResponse response, String userIp) {
        request.getSession().setAttribute(VISIT_IP_ATTRIBUTE_NAME, userIp);
        System.err.println("Setting visit IP attribute for IP: " + userIp);

        Cookie cookie = new Cookie(VISIT_COOKIE_NAME, userIp);
        cookie.setMaxAge(3600 * 24 * 365); // 1 year expiration
        response.addCookie(cookie);
        System.err.println("Setting visit cookie for IP: " + userIp);
    }

    private String getClientIp(HttpServletRequest request) {
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
}



/*@RestController
@CrossOrigin("http://localhost:3000")
public class VisitCounterController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String VISIT_COOKIE_NAME = "visitCookie";
    private int visitCount = 0;

    public VisitCounterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/visit")
    public int getVisitCount(@CookieValue(value = VISIT_COOKIE_NAME, defaultValue = "false") boolean visited,
                             HttpServletResponse response) {
        if (!visited) {
            visitCount++;
            response.addCookie(new Cookie(VISIT_COOKIE_NAME, "true"));
            messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
        }
        return visitCount;
    }

    @GetMapping("/incrementVisit")
    public int incrementVisit() {
        System.err.println("entrada del incrementVisit");
        visitCount++;
        // Enviar el nuevo contador a todos los clientes suscritos al destino "/topic/visitCount"
        messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
        return visitCount;
    }
}*/
