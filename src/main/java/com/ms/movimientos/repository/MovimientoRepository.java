package com.ms.movimientos.repository;

import com.ms.movimientos.entity.Movimiento;
import com.ms.movimientos.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuenta_NumeroCuentaOrderByFechaDesc(String numeroCuenta);

    Page<Movimiento> findByCuenta_NumeroCuenta(String numeroCuenta, Pageable pageable);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta " +
           "AND m.fecha BETWEEN :fechaInicial AND :fechaFinal " +
           "ORDER BY m.fecha DESC")
    List<Movimiento> findByNumeroCuentaAndFechaBetween(
            @Param("numeroCuenta") String numeroCuenta,
            @Param("fechaInicial") LocalDateTime fechaInicial,
            @Param("fechaFinal") LocalDateTime fechaFinal);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cliente.clienteId = :clienteId " +
           "AND m.fecha BETWEEN :fechaInicial AND :fechaFinal " +
           "ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(
            @Param("clienteId") Integer clienteId,
            @Param("fechaInicial") LocalDateTime fechaInicial,
            @Param("fechaFinal") LocalDateTime fechaFinal);

    @Query("SELECT SUM(m.valor) FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta " +
           "AND m.tipoMovimiento = :tipoMovimiento " +
           "AND m.fecha BETWEEN :fechaInicial AND :fechaFinal")
    BigDecimal sumByTipoMovimientoAndFechaBetween(
            @Param("numeroCuenta") String numeroCuenta,
            @Param("tipoMovimiento") TipoMovimiento tipoMovimiento,
            @Param("fechaInicial") LocalDateTime fechaInicial,
            @Param("fechaFinal") LocalDateTime fechaFinal);

    @Query("SELECT COUNT(m) FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta")
    long countByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);
}
