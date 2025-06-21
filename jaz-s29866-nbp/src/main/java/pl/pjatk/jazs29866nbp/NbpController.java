package pl.pjatk.jazs29866nbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
public class NbpController {

    private final NbpService nbpService;

    @Autowired
    public NbpController(NbpService nbpService) {
        this.nbpService = nbpService;
    }

    // Strona główna
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Formularz
    @PostMapping("/check-rate")
    public String checkRate(
            @RequestParam String currency,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            ExchangeRateResponse response = nbpService.getAverageExchangeRate(currency, start, end);

            model.addAttribute("result", response);
            model.addAttribute("currency", currency);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "index";

        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowy format daty. Użyj YYYY-MM-DD");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    // API
    @GetMapping("/api/average-rate")
    @ResponseBody
    public ResponseEntity<ExchangeRateResponse> getAverageExchangeRateApi(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ExchangeRateResponse response = nbpService.getAverageExchangeRate(currency, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}