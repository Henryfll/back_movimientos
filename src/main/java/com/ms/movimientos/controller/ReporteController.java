package com.ms.movimientos.controller;

import com.ms.movimientos.dto.reporte.PdfResponseDTO;
import com.ms.movimientos.dto.reporte.ReporteResponseDTO;
import com.ms.movimientos.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para generación de reportes de estado de cuenta")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    @Operation(summary = "Generar reporte en formato JSON", description = "Retorna el estado de cuenta del cliente en formato JSON con todos los movimientos del período")
    public ResponseEntity<ReporteResponseDTO> generarReporteJson(
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam Integer clienteId,
            @Parameter(description = "Fecha de inicio del período (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin del período (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        ReporteResponseDTO reporte = reporteService.generarReporte(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/pdf")
    @Operation(summary = "Generar reporte en formato PDF", description = "Genera un estado de cuenta en formato PDF y lo retorna codificado en Base64")
    public ResponseEntity<PdfResponseDTO> generarReportePdf(
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam Integer clienteId,
            @Parameter(description = "Fecha de inicio del período (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin del período (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        PdfResponseDTO pdf = reporteService.generarPdf(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(pdf);
    }
}
