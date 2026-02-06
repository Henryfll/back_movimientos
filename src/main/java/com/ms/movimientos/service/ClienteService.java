package com.ms.movimientos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ms.movimientos.dto.cliente.ClienteRequestDTO;
import com.ms.movimientos.dto.cliente.ClienteResponseDTO;

public interface ClienteService {
     ClienteResponseDTO crearCliente(ClienteRequestDTO dto);
     Page<ClienteResponseDTO> obtenerTodosLosClientes(Pageable pageable);
     ClienteResponseDTO obtenerClientePorId(Integer id);
     ClienteResponseDTO actualizarCliente(Integer id, ClienteRequestDTO dto);
     void eliminarCliente(Integer id);
}
