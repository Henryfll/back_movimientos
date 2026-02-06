package com.ms.movimientos.controller;

import com.ms.movimientos.dto.cuenta.CuentaRequestDTO;
import com.ms.movimientos.dto.cuenta.CuentaResponseDTO;
import com.ms.movimientos.entity.Cuenta;
import com.ms.movimientos.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Operaciones CRUD para cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    @Autowired
    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Registra una nueva cuenta bancaria asociada a un cliente")
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO dto) {
        CuentaResponseDTO respuesta = cuentaService.crearCuenta(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar cuentas", description = "Obtiene todas las cuentas con paginación")
    public ResponseEntity<Page<CuentaResponseDTO>> obtenerTodas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<CuentaResponseDTO> cuentas = cuentaService.obtenerTodasLasCuentas(pageable);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Obtener cuenta por número", description = "Busca una cuenta específica por su número")
    public ResponseEntity<CuentaResponseDTO> obtenerPorNumero(@PathVariable String numero) {
        CuentaResponseDTO cuenta = cuentaService.obtenerCuentaPorNumero(numero);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{numero}")
    @Operation(summary = "Actualizar cuenta", description = "Actualiza los datos de una cuenta")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable String numero,
            @Valid @RequestBody CuentaRequestDTO dto) {
        CuentaResponseDTO respuesta = cuentaService.actualizarCuenta(numero, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{numero}")
    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta (valida que no tenga movimientos)")
    public ResponseEntity<Void> eliminar(@PathVariable String numero) {
        cuentaService.eliminarCuenta(numero);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("findbyClientId/{numero}")
    @Operation(summary = "Obtener cuentas de un cliente", description = "Busca una las cuentas de un cliente")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerPorClienteId(@PathVariable Integer numero) {
        List<CuentaResponseDTO> cuentas = cuentaService.obtenerTodasLasCuentasPorCliente(numero);
        return ResponseEntity.ok(cuentas);
    }
}
