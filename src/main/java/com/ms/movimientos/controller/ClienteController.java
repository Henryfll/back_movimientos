package com.ms.movimientos.controller;

import com.ms.movimientos.dto.cliente.ClienteRequestDTO;
import com.ms.movimientos.dto.cliente.ClienteResponseDTO;
import com.ms.movimientos.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operaciones CRUD para clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente en el sistema")
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO respuesta = clienteService.crearCliente(dto);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes con paginación")
    public ResponseEntity<Page<ClienteResponseDTO>> obtenerTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<ClienteResponseDTO> clientes = clienteService.obtenerTodosLosClientes(pageable);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Busca un cliente específico por su ID")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza todos los datos de un cliente")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO respuesta = clienteService.actualizarCliente(id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente cliente", description = "Actualiza parcialmente los datos de un cliente")
    public ResponseEntity<ClienteResponseDTO> actualizarParcial(
            @PathVariable Integer id,
            @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO respuesta = clienteService.actualizarCliente(id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Realiza una eliminación lógica del cliente (cambia estado a inactivo)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
