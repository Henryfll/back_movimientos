package com.ms.movimientos.service.impl;

import com.ms.movimientos.dto.movimiento.MovimientoRequestDTO;
import com.ms.movimientos.dto.movimiento.MovimientoResponseDTO;
import com.ms.movimientos.entity.Cuenta;
import com.ms.movimientos.entity.Movimiento;
import com.ms.movimientos.enums.TipoMovimiento;
import com.ms.movimientos.exception.RecursoNoEncontradoException;
import com.ms.movimientos.exception.SaldoInsuficienteException;
import com.ms.movimientos.repository.CuentaRepository;
import com.ms.movimientos.repository.MovimientoRepository;
import com.ms.movimientos.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    @Autowired
    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public MovimientoResponseDTO registrarMovimiento(MovimientoRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuentaAndEstadoTrue(dto.getNumeroCuenta())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta", "numero", dto.getNumeroCuenta()));

        BigDecimal valorMovimiento;
        if (dto.getTipoMovimiento() == TipoMovimiento.CREDITO) {
            valorMovimiento = dto.getValor();
        } else {
            valorMovimiento = dto.getValor().negate();
        }

        if (dto.getTipoMovimiento() == TipoMovimiento.DEBITO) {
            if (dto.getValor().compareTo(BigDecimal.ZERO) == 0) {
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
            if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) <= 0) {
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
            if (cuenta.getSaldo().compareTo(dto.getValor()) < 0) {
                throw new SaldoInsuficienteException("Saldo no disponible");
            }
        }

        BigDecimal nuevoSaldo = cuenta.getSaldo().add(valorMovimiento);
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(dto.getTipoMovimiento())
                .valor(valorMovimiento)
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        movimiento = movimientoRepository.save(movimiento);
        return mapToResponseDTO(movimiento);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponseDTO> obtenerMovimientos(Pageable pageable) {
        return movimientoRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerMovimientoPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento", "id", id));
        return mapToResponseDTO(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorNumeroCuenta(String numeroCuenta) {
        return movimientoRepository.findByCuenta_NumeroCuentaOrderByFechaDesc(numeroCuenta)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorNumeroCuentaYFecha(
            String numeroCuenta, LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        return movimientoRepository.findByNumeroCuentaAndFechaBetween(numeroCuenta, fechaInicial, fechaFinal)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private MovimientoResponseDTO mapToResponseDTO(Movimiento movimiento) {
        return MovimientoResponseDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .numeroCuenta(movimiento.getCuenta().getNumeroCuenta())
                .build();
    }
}
