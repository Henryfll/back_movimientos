package com.ms.movimientos.service.impl;

import com.ms.movimientos.dto.cliente.ClienteRequestDTO;
import com.ms.movimientos.dto.cliente.ClienteResponseDTO;
import com.ms.movimientos.entity.Cliente;
import com.ms.movimientos.exception.RecursoDuplicadoException;
import com.ms.movimientos.exception.RecursoNoEncontradoException;
import com.ms.movimientos.repository.ClienteRepository;
import com.ms.movimientos.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ClienteServiceImpl implements ClienteService{

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new RecursoDuplicadoException("Ya existe un cliente con esta identificación");
        }

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .genero(dto.getGenero())
                .edad(dto.getEdad())
                .identificacion(dto.getIdentificacion())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .estado(true)
                .build();

        cliente = clienteRepository.save(cliente);
        return mapToResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> obtenerTodosLosClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", "id", id));
        return mapToResponseDTO(cliente);
    }

    public ClienteResponseDTO actualizarCliente(Integer id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", "id", id));

        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            cliente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

        cliente = clienteRepository.save(cliente);
        return mapToResponseDTO(cliente);
    }

    public void eliminarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", "id", id));
        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .estado(cliente.getEstado())
                .build();
    }
}
