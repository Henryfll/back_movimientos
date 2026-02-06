package com.ms.movimientos.repository;

import com.ms.movimientos.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByIdentificacion(String identificacion);

    Optional<Cliente> findByClienteIdAndEstadoTrue(Integer clienteId);

    @Query("SELECT c FROM Cliente c WHERE c.estado = true")
    Page<Cliente> findAllActivos(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% AND c.estado = true")
    Page<Cliente> findByNombreContaining(@Param("nombre") String nombre, Pageable pageable);

    boolean existsByIdentificacion(String identificacion);
}
