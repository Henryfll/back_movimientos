package com.ms.movimientos.controller;

import com.ms.movimientos.dto.movimiento.MovimientoRequestDTO;
import com.ms.movimientos.dto.movimiento.MovimientoResponseDTO;
import com.ms.movimientos.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos", description = "Operaciones para movimientos bancarios")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @Autowired
    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento", description = "Registra un nuevo movimiento (crédito o débito) en una cuenta")
    public ResponseEntity<MovimientoResponseDTO> registrar(@Valid @RequestBody MovimientoRequestDTO dto) {
        MovimientoResponseDTO respuesta = movimientoService.registrarMovimiento(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar movimientos", description = "Obtiene todos los movimientos con paginación")
    public ResponseEntity<Page<MovimientoResponseDTO>> obtenerTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<MovimientoResponseDTO> movimientos = movimientoService.obtenerMovimientos(pageable);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID", description = "Busca un movimiento específico por su ID")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        MovimientoResponseDTO movimiento = movimientoService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    @Operation(summary = "Obtener movimientos por cuenta", description = "Lista todos los movimientos de una cuenta")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorNumeroCuenta(@PathVariable String numeroCuenta) {
        List<MovimientoResponseDTO> movimientos = movimientoService.obtenerMovimientosPorNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }
}
