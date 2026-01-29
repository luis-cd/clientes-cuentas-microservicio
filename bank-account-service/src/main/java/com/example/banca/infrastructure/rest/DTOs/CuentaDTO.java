package com.example.banca.infrastructure.rest.DTOs;

import com.example.banca.domain.model.ValueObjects.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {
    private Long id;
    private String tipoCuenta;
    private double total;
    private String dniCliente; // obligatorio
}
