package com.ms.movimientos.service.impl;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.ms.movimientos.dto.reporte.CuentaReporteDTO;
import com.ms.movimientos.dto.reporte.InfoClienteDTO;
import com.ms.movimientos.dto.reporte.MovimientoReporteDTO;
import com.ms.movimientos.dto.reporte.PdfResponseDTO;
import com.ms.movimientos.dto.reporte.ReporteResponseDTO;
import com.ms.movimientos.dto.reporte.TotalesReporteDTO;
import com.ms.movimientos.entity.Cliente;
import com.ms.movimientos.entity.Cuenta;
import com.ms.movimientos.entity.Movimiento;
import com.ms.movimientos.enums.TipoMovimiento;
import com.ms.movimientos.exception.RecursoNoEncontradoException;
import com.ms.movimientos.repository.ClienteRepository;
import com.ms.movimientos.repository.CuentaRepository;
import com.ms.movimientos.repository.MovimientoRepository;
import com.ms.movimientos.service.ReporteService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService{

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    @Transactional(readOnly = true)
    public ReporteResponseDTO generarReporte(Integer clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        // Validar fechas
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        // Buscar cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", "id", clienteId));

        // Obtener cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByCliente_ClienteIdAndEstadoTrue(clienteId);
        if (cuentas.isEmpty()) {
            throw new RecursoNoEncontradoException("Cuentas", "clienteId", clienteId);
        }

        // Convertir fechas a LocalDateTime para consulta
        LocalDateTime fechaInicioDT = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinDT = fechaFin.atTime(LocalTime.MAX);

        // Calcular totales generales
        BigDecimal totalCreditosGeneral = BigDecimal.ZERO;
        BigDecimal totalDebitosGeneral = BigDecimal.ZERO;
        int totalMovimientos = 0;

        // Construir respuesta
        List<CuentaReporteDTO> cuentasReporte = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            // Obtener movimientos por cuenta y fecha
            List<Movimiento> movimientos = movimientoRepository
                    .findByNumeroCuentaAndFechaBetween(
                            cuenta.getNumeroCuenta(), fechaInicioDT, fechaFinDT);

            // Calcular totales por cuenta
            BigDecimal totalCreditos = BigDecimal.ZERO;
            BigDecimal totalDebitos = BigDecimal.ZERO;

            List<MovimientoReporteDTO> movimientosDTO = new ArrayList<>();
            for (Movimiento mov : movimientos) {
                MovimientoReporteDTO movDTO = MovimientoReporteDTO.builder()
                        .id(mov.getId())
                        .fecha(mov.getFecha())
                        .tipoMovimiento(mov.getTipoMovimiento().name())
                        .valor(mov.getValor())
                        .saldo(mov.getSaldo())
                        .build();

                movimientosDTO.add(movDTO);

                // Calcular totales
                if (mov.getTipoMovimiento() == TipoMovimiento.CREDITO) {
                    totalCreditos = totalCreditos.add(mov.getValor().abs());
                    totalCreditosGeneral = totalCreditosGeneral.add(mov.getValor().abs());
                } else {
                    totalDebitos = totalDebitos.add(mov.getValor().abs());
                    totalDebitosGeneral = totalDebitosGeneral.add(mov.getValor().abs());
                }
                totalMovimientos++;
            }

            CuentaReporteDTO cuentaDTO = CuentaReporteDTO.builder()
                    .numeroCuenta(cuenta.getNumeroCuenta())
                    .tipoCuenta(cuenta.getTipoCuenta().name())
                    .saldoActual(cuenta.getSaldo())
                    .saldoInicial(cuenta.getSaldoInicial())
                    .movimientos(movimientosDTO)
                    .totalCreditos(totalCreditos.setScale(2, RoundingMode.HALF_UP))
                    .totalDebitos(totalDebitos.setScale(2, RoundingMode.HALF_UP))
                    .build();

            cuentasReporte.add(cuentaDTO);
        }

        TotalesReporteDTO totales = TotalesReporteDTO.builder()
                .totalCreditosGeneral(totalCreditosGeneral.setScale(2, RoundingMode.HALF_UP))
                .totalDebitosGeneral(totalDebitosGeneral.setScale(2, RoundingMode.HALF_UP))
                .totalMovimientos(totalMovimientos)
                .build();

        return ReporteResponseDTO.builder()
                .cliente(mapToInfoClienteDTO(cliente))
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .cuentas(cuentasReporte)
                .totales(totales)
                .build();
    }

    @Transactional(readOnly = true)
    public PdfResponseDTO generarPdf(Integer clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteResponseDTO reporte = generarReporte(clienteId, fechaInicio, fechaFin);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Formateador de fechas
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Encabezado
            document.add(new Paragraph("ESTADO DE CUENTA")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(""));

            // Información del cliente
            document.add(new Paragraph("Información del Cliente")
                    .setBold()
                    .setFontSize(12));
            document.add(new Paragraph("Nombre: " + reporte.getCliente().getNombre()));
            document.add(new Paragraph("Identificación: " + reporte.getCliente().getIdentificacion()));
            document.add(new Paragraph("Dirección: " + reporte.getCliente().getDireccion()));
            document.add(new Paragraph("Teléfono: " + reporte.getCliente().getTelefono()));
            document.add(new Paragraph("Período: " + fechaInicio.format(dateFormatter) + " - " + fechaFin.format(dateFormatter)));
            document.add(new Paragraph(""));

            // Por cada cuenta
            for (CuentaReporteDTO cuenta : reporte.getCuentas()) {
                document.add(new Paragraph("----------------------------------------"));
                document.add(new Paragraph("CUENTA: " + cuenta.getNumeroCuenta())
                        .setBold()
                        .setFontSize(11));
                document.add(new Paragraph("Tipo: " + cuenta.getTipoCuenta()));
                document.add(new Paragraph("Saldo Actual: $" + cuenta.getSaldoActual()));
                document.add(new Paragraph(""));

                if (!cuenta.getMovimientos().isEmpty()) {
                    // Tabla de movimientos con 4 columnas
                    Table table = new Table(UnitValue.createPercentArray(new float[]{25, 20, 20, 35}))
                            .setWidth(UnitValue.createPercentValue(100));

                    // Fila de encabezado
                    Cell fechaHeader = new Cell().add(new Paragraph("Fecha").setBold())
                            .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    Cell tipoHeader = new Cell().add(new Paragraph("Tipo").setBold())
                            .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    Cell valorHeader = new Cell().add(new Paragraph("Valor").setBold())
                            .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    Cell saldoHeader = new Cell().add(new Paragraph("Saldo").setBold())
                            .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    table.addHeaderCell(fechaHeader);
                    table.addHeaderCell(tipoHeader);
                    table.addHeaderCell(valorHeader);
                    table.addHeaderCell(saldoHeader);

                    // Filas de movimientos
                    for (MovimientoReporteDTO mov : cuenta.getMovimientos()) {
                        table.addCell(new Cell().add(new Paragraph(mov.getFecha().format(dateTimeFormatter))));
                        table.addCell(new Cell().add(new Paragraph(mov.getTipoMovimiento())));
                        String valorStr = mov.getTipoMovimiento().equals("CREDITO") 
                                ? "+$" + mov.getValor().abs().toString() 
                                : "-$" + mov.getValor().abs().toString();
                        table.addCell(new Cell().add(new Paragraph(valorStr)));
                        table.addCell(new Cell().add(new Paragraph("$" + mov.getSaldo().toString())));
                    }

                    document.add(table);
                    document.add(new Paragraph(""));
                } else {
                    document.add(new Paragraph("No hay movimientos en este período")
                            .setItalic());
                }

                document.add(new Paragraph(""));
            }

            // Pie de página
            document.add(new Paragraph(""))
                    .add(new Paragraph("Reporte generado el: " + LocalDateTime.now().format(dateTimeFormatter))
                            .setFontSize(8)
                            .setTextAlignment(TextAlignment.CENTER));

            document.close();

            String pdfBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            return PdfResponseDTO.builder()
                    .clienteNombre(reporte.getCliente().getNombre())
                    .clienteIdentificacion(reporte.getCliente().getIdentificacion())
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .pdfBase64(pdfBase64)
                    .nombreArchivo("estado_cuenta_" + reporte.getCliente().getIdentificacion() + ".pdf")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }

    private InfoClienteDTO mapToInfoClienteDTO(Cliente cliente) {
        return InfoClienteDTO.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .identificacion(cliente.getIdentificacion())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .build();
    }
}
