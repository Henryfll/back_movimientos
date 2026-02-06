package com.ms.movimientos.repository;

import com.ms.movimientos.entity.Cuenta;
import com.ms.movimientos.enums.TipoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {

    Optional<Cuenta> findByNumeroCuentaAndEstadoTrue(String numeroCuenta);

    List<Cuenta> findByCliente_ClienteIdAndEstadoTrue(Integer clienteId);

    @Query("SELECT c FROM Cuenta c WHERE c.cliente.clienteId = :clienteId")
    List<Cuenta> findByClienteId(@Param("clienteId") Integer clienteId);

    @Query("SELECT c FROM Cuenta c WHERE c.tipoCuenta = :tipoCuenta AND c.estado = true")
    Page<Cuenta> findByTipoCuenta(@Param("tipoCuenta") TipoCuenta tipoCuenta, Pageable pageable);

    @Query("SELECT c FROM Cuenta c WHERE c.estado = true")
    Page<Cuenta> findAllActivas(Pageable pageable);

    @Query("SELECT COUNT(m) FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta")
    long countMovimientosByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
