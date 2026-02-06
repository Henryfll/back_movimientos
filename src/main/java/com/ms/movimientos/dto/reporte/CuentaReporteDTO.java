package com.ms.movimientos.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaReporteDTO {
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoActual;
    private BigDecimal saldoInicial;
    private List<MovimientoReporteDTO> movimientos;
    private BigDecimal totalCreditos;
    private BigDecimal totalDebitos;
}
