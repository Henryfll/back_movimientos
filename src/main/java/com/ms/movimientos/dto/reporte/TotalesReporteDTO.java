package com.ms.movimientos.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalesReporteDTO {
    private BigDecimal totalCreditosGeneral;
    private BigDecimal totalDebitosGeneral;
    private int totalMovimientos;
}
