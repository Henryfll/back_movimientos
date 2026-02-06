package com.ms.movimientos.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {
    private InfoClienteDTO cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CuentaReporteDTO> cuentas;
    private TotalesReporteDTO totales;
}
