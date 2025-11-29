package com.example.FinanceTracker.controllers;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.services.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/financetracker")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> listTransactions() {
        return ResponseEntity.ok(transactionService.listTransactions());
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> modifyTransaction(@PathVariable Long id, @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO t = transactionService.modifyTransaction(id, transactionDTO);
        return ResponseEntity.ok(t);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/category")
    public ResponseEntity<List<TransactionDTO>> filterByCategory(@RequestParam Category category) {
        return ResponseEntity.ok(transactionService.filterByCategory(category));
    }

    @GetMapping("/filter/amount")
    public ResponseEntity<List<TransactionDTO>> filterByAmountBetween(@RequestParam int min, int max) {
        return ResponseEntity.ok(transactionService.filterByAmountBetween(min, max));
    }

    @GetMapping("/filter/date")
    public ResponseEntity<List<TransactionDTO>> filterByDateBetween(@RequestParam LocalDate min, LocalDate max) {
        return ResponseEntity.ok(transactionService.filterByDateBetween(min, max));
    }

    @GetMapping("/filter/expenses")
    public ResponseEntity<List<TransactionDTO>> getExpenses() {
        return ResponseEntity.ok(transactionService.getExpenses());
    }

    @GetMapping("/filter/income")
    public ResponseEntity<List<TransactionDTO>> getIncome() {
        return ResponseEntity.ok(transactionService.getIncome());
    }

    @GetMapping("/csv")
    public void csvExport(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("type,amount,date,category,comment");
            for (TransactionDTO transactionDTO : transactionService.listTransactions()) {
                String comment = transactionDTO.getComment();
                if (comment == null) {
                    comment = "";
                }
                if (comment.contains(",") || comment.contains("\"") || comment.contains("\n") || comment.contains("\r")) {
                    comment = "\"" + comment.replace("\"", "\"\"") + "\"";
                }
                String type = transactionDTO.getType() != null ? transactionDTO.getType().toString() : "";
                String amount = String.valueOf(transactionDTO.getAmount());
                String date = transactionDTO.getDate() != null ? transactionDTO.getDate().toString() : "";
                String category = transactionDTO.getCategory() != null ? transactionDTO.getCategory().toString() : "";
                String line = String.join(",", type, amount, date, category, comment);
                writer.println(line);
            }
            writer.flush();
        }
    }
}
