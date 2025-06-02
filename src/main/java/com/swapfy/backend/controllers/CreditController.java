package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.CreditDTO;
import com.swapfy.backend.dto.CreditResponseDTO;
import com.swapfy.backend.models.Credit;
import com.swapfy.backend.services.CreditService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @GetMapping("")
    public List<CreditResponseDTO> getAllCredits() {
        return creditService.getAllCreditsAsDTOs();
    }


    @GetMapping("/user/{userId}")
    public List<Credit> getCreditsByUser(@PathVariable Long userId) {
        return creditService.getCreditsByUserId(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCredit(@RequestBody CreditDTO creditDTO) {
        Credit saved = creditService.addCredit(creditDTO.getUserId(), creditDTO.getAmount(), creditDTO.getType());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Crédito registrado correctamente");
        response.put("creditId", saved.getCreditId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/spent-total/{userId}")
    public ResponseEntity<Integer> getTotalCreditsSpent(@PathVariable Long userId) {
        int totalSpent = creditService.getTotalSpentCredits(userId);
        return ResponseEntity.ok(totalSpent);
    }


    @GetMapping("/extract/{userId}")
    public void downloadExtract(@PathVariable Long userId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=extracto_creditos_swapfy.pdf");
        creditService.exportCreditExtract(userId, response);
    }

    @PutMapping("/{creditId}")
    public ResponseEntity<?> updateCredit(@PathVariable Long creditId, @RequestBody CreditDTO dto) {
        creditService.updateCredit(creditId, dto.getAmount(), dto.getType());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{creditId}")
    public ResponseEntity<?> deleteCredit(@PathVariable Long creditId) {
        creditService.deleteCredit(creditId);
        return ResponseEntity.ok().build();
    }
}
