package com.ms.movimientos.service;

import com.ms.movimientos.dto.cliente.ClienteRequestDTO;
import com.ms.movimientos.dto.cliente.ClienteResponseDTO;
import com.ms.movimientos.entity.Cliente;
import com.ms.movimientos.exception.RecursoDuplicadoException;
import com.ms.movimientos.exception.RecursoNoEncontradoException;
import com.ms.movimientos.repository.ClienteRepository;
import com.ms.movimientos.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteServiceImpl(clienteRepository, passwordEncoder);
    }

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;

    @BeforeEach
    void setUpTest() {
        clienteRequestDTO = ClienteRequestDTO.builder()
                .nombre("Juan Pérez")
                .genero("M")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Calle 123")
                .telefono("0987654321")
                .contrasena("password123")
                .build();

        cliente = Cliente.builder()
                .clienteId(1)
                .nombre("Juan Pérez")
                .genero("M")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Calle 123")
                .telefono("0987654321")
                .contrasena("encodedPassword")
                .estado(true)
                .build();
    }

    @Test
    void crearCliente_ShouldReturnClienteResponseDTO() {
        when(clienteRepository.existsByIdentificacion(clienteRequestDTO.getIdentificacion())).thenReturn(false);
        when(passwordEncoder.encode(clienteRequestDTO.getContrasena())).thenReturn("encodedPassword");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO resultado = clienteService.crearCliente(clienteRequestDTO);

        assertNotNull(resultado);
        assertEquals(clienteRequestDTO.getNombre(), resultado.getNombre());
        assertEquals(clienteRequestDTO.getIdentificacion(), resultado.getIdentificacion());
        assertTrue(resultado.getEstado());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crearCliente_WhenIdentificacionExists_ShouldThrowException() {
        when(clienteRepository.existsByIdentificacion(clienteRequestDTO.getIdentificacion())).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> clienteService.crearCliente(clienteRequestDTO));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void obtenerClientePorId_ShouldReturnClienteResponseDTO() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO resultado = clienteService.obtenerClientePorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getClienteId());
        assertEquals("Juan Pérez", resultado.getNombre());
    }

    @Test
    void obtenerClientePorId_WhenNotFound_ShouldThrowException() {
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> clienteService.obtenerClientePorId(999));
    }

    @Test
    void eliminarCliente_ShouldSetEstadoToFalse() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.eliminarCliente(1);

        assertFalse(cliente.getEstado());
        verify(clienteRepository).save(cliente);
    }
}
