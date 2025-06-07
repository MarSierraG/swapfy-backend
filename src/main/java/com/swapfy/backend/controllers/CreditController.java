package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.CreditDTO;
import com.swapfy.backend.dto.CreditResponseDTO;
import com.swapfy.backend.models.Credit;
import com.swapfy.backend.security.JwtUtil;
import com.swapfy.backend.services.CreditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.swapfy.backend.repositories.UserRepository userRepository;


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


    @GetMapping("/extract")
    public void downloadExtract(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = Long.valueOf( // obtienes el ID directamente del usuario autenticado
                userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                        .getUserId()
        );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !usuarioAutenticadoCoincide(userId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("No tienes permisos para descargar este PDF.");
            return;
        }

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


    private boolean usuarioAutenticadoCoincide(Long userIdFromRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // el email ya está en el contexto

        return userRepository.findByEmail(email)
                .map(user -> user.getUserId().equals(userIdFromRequest))
                .orElse(false);
    }



}
