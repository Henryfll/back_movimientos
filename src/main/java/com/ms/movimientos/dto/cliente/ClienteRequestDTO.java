package com.ms.movimientos.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 20, message = "El género no puede exceder 20 caracteres")
    private String genero;

    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 10, message = "La identificación no puede exceder 10 caracteres")
    private String identificacion;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 10, message = "El teléfono no puede exceder 10 caracteres")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String contrasena;
}
