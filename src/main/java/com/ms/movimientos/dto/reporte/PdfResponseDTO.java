package com.ms.movimientos.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfResponseDTO {
    private String clienteNombre;
    private String clienteIdentificacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String pdfBase64;
    private String nombreArchivo;
}
