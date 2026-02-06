package com.ms.movimientos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ms.movimientos.dto.movimiento.MovimientoRequestDTO;
import com.ms.movimientos.dto.movimiento.MovimientoResponseDTO;

public interface MovimientoService {
    MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO dto);
    Page<MovimientoResponseDTO> obtenerMovimientos(Pageable pageable);
    MovimientoResponseDTO obtenerMovimientoPorId(Long id);
    List<MovimientoResponseDTO> obtenerMovimientosPorNumeroCuenta(String numeroCuenta);
    List<MovimientoResponseDTO> obtenerMovimientosPorNumeroCuentaYFecha(
            String numeroCuenta, LocalDateTime fechaInicial, LocalDateTime fechaFinal);
            
}
