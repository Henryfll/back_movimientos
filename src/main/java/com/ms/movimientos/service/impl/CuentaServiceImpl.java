package com.ms.movimientos.service.impl;

import com.ms.movimientos.dto.cuenta.CuentaRequestDTO;
import com.ms.movimientos.dto.cuenta.CuentaResponseDTO;
import com.ms.movimientos.entity.Cuenta;
import com.ms.movimientos.entity.Cliente;
import com.ms.movimientos.exception.RecursoDuplicadoException;
import com.ms.movimientos.exception.RecursoNoEncontradoException;
import com.ms.movimientos.repository.ClienteRepository;
import com.ms.movimientos.repository.CuentaRepository;
import com.ms.movimientos.service.CuentaService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService{

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    public CuentaResponseDTO crearCuenta(CuentaRequestDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con este número");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", "id", dto.getClienteId()));

        if (!cliente.getEstado()) {
            throw new RecursoDuplicadoException("El cliente no está activo");
        }

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(dto.getNumeroCuenta())
                .tipoCuenta(dto.getTipoCuenta())
                .saldoInicial(dto.getSaldoInicial())
                .saldo(dto.getSaldoInicial())
                .estado(true)
                .cliente(cliente)
                .build();

        cuenta = cuentaRepository.save(cuenta);
        return mapToResponseDTO(cuenta);
    }

    @Transactional(readOnly = true)
    public Page<CuentaResponseDTO> obtenerTodasLasCuentas(Pageable pageable) {
        return cuentaRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public CuentaResponseDTO obtenerCuentaPorNumero(String numero) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuentaAndEstadoTrue(numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", "numero", numero));
        return mapToResponseDTO(cuenta);
    }

    public CuentaResponseDTO actualizarCuenta(String numero, CuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuentaAndEstadoTrue(numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", "numero", numero));

        cuenta.setTipoCuenta(dto.getTipoCuenta());
        if (dto.getSaldoInicial() != null) {
            cuenta.setSaldoInicial(dto.getSaldoInicial());
        }

        cuenta = cuentaRepository.save(cuenta);
        return mapToResponseDTO(cuenta);
    }

    public void eliminarCuenta(String numero) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuentaAndEstadoTrue(numero)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", "numero", numero));

        long movimientosCount = cuentaRepository.countMovimientosByNumeroCuenta(numero);
        if (movimientosCount > 0) {
            throw new RecursoDuplicadoException("No se puede eliminar una cuenta con movimientos");
        }

        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
    }

    private CuentaResponseDTO mapToResponseDTO(Cuenta cuenta) {
        return CuentaResponseDTO.builder()
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente().getClienteId())
                .clienteNombre(cuenta.getCliente().getNombre())
                .build();
    }

    @Override
    public List<CuentaResponseDTO> obtenerTodasLasCuentasPorCliente(Integer clienteId) {
        // TODO Auto-generated method stub
        List<Cuenta> cuentas= cuentaRepository.findByClienteId(clienteId);
       return cuentas.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
}
