package com.ms.movimientos.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoClienteDTO {
    private Integer clienteId;
    private String nombre;
    private String identificacion;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;
}
