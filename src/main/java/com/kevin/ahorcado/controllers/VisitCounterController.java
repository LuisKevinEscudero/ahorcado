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
@CrossOrigin({"http://localhost:3000", "https://luiskevinescudero.github.io"})
public class VisitCounterController {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String VISIT_COOKIE_NAME = "visitCookie";
    private static final String VISIT_IP_ATTRIBUTE_NAME = "visitIp";
    private int visitCount = 0;

    // Constructor que recibe SimpMessagingTemplate como parámetro
    public VisitCounterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Maneja las solicitudes GET a la ruta "/visit"
    @GetMapping("/visit")
    public int getVisitCount(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la dirección IP del usuario
        String userIp = getClientIp(request);
        System.err.println("Solicitud recibida en /visit. IP del usuario: " + userIp);

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

        return visitCount;
    }

    // Maneja las solicitudes GET a la ruta "/incrementVisit"
    @GetMapping("/incrementVisit")
    public int incrementVisit() {
        System.err.println("Solicitud recibida en /incrementVisit");
        visitCount++;
        // Envía el nuevo contador a todos los clientes suscritos al destino "/topic/visitCount"
        messagingTemplate.convertAndSend("/topic/visitCount", visitCount);
        System.err.println("Contador incrementado a: " + visitCount);
        return visitCount;
    }

    // Verifica si el usuario ha visitado antes
    private boolean hasVisited(HttpServletRequest request, String userIp) {
        String storedIp = (String) request.getSession().getAttribute(VISIT_IP_ATTRIBUTE_NAME);

        if (storedIp != null && storedIp.equals(userIp)) {
            System.err.println("El usuario ya ha visitado antes. IP: " + userIp);
            return true;
        }

        System.err.println("El usuario está visitando por primera vez. IP: " + userIp);
        return false;
    }

    // Establece que el usuario ha visitado
    private void setVisited(HttpServletRequest request, HttpServletResponse response, String userIp) {
        request.getSession().setAttribute(VISIT_IP_ATTRIBUTE_NAME, userIp);
        System.err.println("Estableciendo atributo de IP de visita para la IP: " + userIp);

        // Crea y configura una cookie de visita
        Cookie cookie = new Cookie(VISIT_COOKIE_NAME, userIp);
        cookie.setMaxAge(3600 * 24 * 365); // Expiración de 1 año
        response.addCookie(cookie);
        System.err.println("Estableciendo cookie de visita para la IP: " + userIp);
    }

    // Obtiene la dirección IP del cliente
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
