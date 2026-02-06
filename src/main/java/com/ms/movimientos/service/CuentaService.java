package com.ms.movimientos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ms.movimientos.dto.cuenta.CuentaRequestDTO;
import com.ms.movimientos.dto.cuenta.CuentaResponseDTO;
import com.ms.movimientos.entity.Cuenta;

public interface CuentaService {

   CuentaResponseDTO crearCuenta(CuentaRequestDTO dto);
   Page<CuentaResponseDTO> obtenerTodasLasCuentas(Pageable pageable);
   List<CuentaResponseDTO> obtenerTodasLasCuentasPorCliente(Integer clienteId);
   CuentaResponseDTO obtenerCuentaPorNumero(String numero);
   CuentaResponseDTO actualizarCuenta(String numero, CuentaRequestDTO dto) ;
   void eliminarCuenta(String numero) ;
}
