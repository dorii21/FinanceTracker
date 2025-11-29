    package com.example.FinanceTracker.controllers;

    import com.example.FinanceTracker.dtos.TransactionDTO;
    import com.example.FinanceTracker.entities.Category;
    import com.example.FinanceTracker.services.TransactionService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDate;
    import java.util.List;

    @RestController
    @RequestMapping(path = "/api/financetracker")
    @RequiredArgsConstructor
    public class TransactionController {

        private final TransactionService transactionService;

        @GetMapping
        public ResponseEntity<List<TransactionDTO>> listTransactions(){
            return ResponseEntity.ok(transactionService.listTransactions());
        }

        @PostMapping
        public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO){
            return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
        }

        @PutMapping("/{id}")
        public ResponseEntity<TransactionDTO> modifyTransaction(@PathVariable Long id,@RequestBody TransactionDTO transactionDTO){
            TransactionDTO t=transactionService.modifyTransaction(id,transactionDTO);
            return ResponseEntity.ok(t);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteTransaction(@PathVariable Long id){
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/filter/category")
        public ResponseEntity<List<TransactionDTO>> filterByCategory(@RequestParam Category category){
            return ResponseEntity.ok(transactionService.filterByCategory(category));
        }

        @GetMapping("/filter/amount")
        public ResponseEntity<List<TransactionDTO>> filterByAmountBetween(@RequestParam int min, int max){
            return ResponseEntity.ok(transactionService.filterByAmountBetween(min,max));
        }

        @GetMapping("/filter/date")
        public ResponseEntity<List<TransactionDTO>> filterByDateBetween(@RequestParam LocalDate min, LocalDate max){
            return ResponseEntity.ok(transactionService.filterByDateBetween(min,max));
        }

        @GetMapping("/filter/expenses")
        public ResponseEntity<List<TransactionDTO>> getExpenses(){
            return ResponseEntity.ok(transactionService.getExpenses());
        }

        @GetMapping("/filter/income")
        public ResponseEntity<List<TransactionDTO>> getIncome(){
            return ResponseEntity.ok(transactionService.getIncome());
        }
    }
