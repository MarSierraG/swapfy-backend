package com.swapfy.backend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.swapfy.backend.models.Credit;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.CreditRepository;
import com.swapfy.backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import java.awt.*;
import java.io.IOException;
import java.util.List;


@Service
public class CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Credit> getCreditsByUserId(Long userId) {
        return creditRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    public Credit addCredit(Long userId, int amount, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (amount < 0 && (user.getCredits() == null || user.getCredits() < Math.abs(amount))) {
            throw new RuntimeException("No hay créditos suficientes");
        }

        // Crear y guardar el crédito
        Credit credit = new Credit();
        credit.setUser(user);
        credit.setAmount(amount); // negativo si es gasto
        credit.setType(type);
        Credit saved = creditRepository.save(credit);

        // Actualizar saldo del usuario
        int newCredits = user.getCredits() != null ? user.getCredits() + amount : amount;
        user.setCredits(newCredits);
        userRepository.save(user);

        return saved;
    }

    public int getTotalSpentCredits(Long userId) {
        List<Credit> gastos = creditRepository.findByUser_UserIdAndAmountLessThan(userId, 0);
        return gastos.stream().mapToInt(c -> Math.abs(c.getAmount())).sum();
    }

    public void exportCreditExtract(Long userId, HttpServletResponse response) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();

        List<Credit> credits = creditRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        titleFont.setSize(18);
        titleFont.setColor(Color.DARK_GRAY);

        Paragraph title = new Paragraph("Extracto de Créditos - Swapfy", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(title);
        document.add(new Paragraph("Usuario: " + user.getName()));
        document.add(new Paragraph("Email: " + user.getEmail()));
        document.add(new Paragraph("Fecha: " + java.time.LocalDate.now()));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{2f, 1f, 2f});
        table.setSpacingBefore(10);

        table.addCell("Tipo");
        table.addCell("Cantidad");
        table.addCell("Fecha");

        for (Credit credit : credits) {
            table.addCell(credit.getType());
            table.addCell(String.valueOf(credit.getAmount()));
            table.addCell(String.valueOf(credit.getCreatedAt()));
        }

        document.add(table);
        document.close();
    }




}


