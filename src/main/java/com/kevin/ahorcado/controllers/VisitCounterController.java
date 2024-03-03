package com.kevin.ahorcado.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kevin.ahorcado.services.VisitCounterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin({"http://localhost:3000", "https://luiskevinescudero.github.io"})
@RequestMapping("/visitCounter")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    // Maneja las solicitudes GET a la ruta "/visit"
    @GetMapping("/visit")
    public int getVisitCount(HttpServletRequest request, HttpServletResponse response) {
        return visitCounterService.getVisitCount(request, response);
    }

    // Maneja las solicitudes GET a la ruta "/incrementVisit"
    @GetMapping("/incrementVisit")
    public int incrementVisit() {
        return visitCounterService.incrementVisit();
    }

}
