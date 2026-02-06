package com.ms.movimientos.service;

import java.time.LocalDate;

import com.ms.movimientos.dto.reporte.PdfResponseDTO;
import com.ms.movimientos.dto.reporte.ReporteResponseDTO;

public interface ReporteService {
    ReporteResponseDTO generarReporte(Integer clienteId, LocalDate fechaInicio, LocalDate fechaFin) ;
    PdfResponseDTO generarPdf(Integer clienteId, LocalDate fechaInicio, LocalDate fechaFin) ;
}
