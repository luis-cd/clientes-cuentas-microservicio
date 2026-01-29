package com.example.banca.infrastructure.rest.mappers;

import com.example.banca.domain.model.Cliente;
import com.example.banca.domain.model.CuentaBancaria;
import com.example.banca.infrastructure.rest.DTOs.ClienteDTO;
import com.example.banca.infrastructure.rest.DTOs.CuentaDTO;

import java.util.List;
import java.util.stream.Collectors;

public final class ClienteRestMapper {

    private ClienteRestMapper() {
        // evitar instanciación
    }

    // Convierte un Cliente del dominio a ClienteDTO para REST
    public static ClienteDTO toDTO(Cliente cliente) {
        if (cliente == null) return null;

        List<CuentaDTO> cuentasDTO = cliente.getCuentas()
                .stream()
                .map(ClienteRestMapper::mapCuenta)
                .collect(Collectors.toList());

        return new ClienteDTO(
                cliente.getDni().getValor(),
                cliente.getNombre(),
                cliente.getApellido1(),
                cliente.getApellido2(),
                cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().toString() : null,
                cuentasDTO
        );
    }

    // Convierte una CuentaBancaria del dominio a CuentaDTO para REST
    private static CuentaDTO mapCuenta(CuentaBancaria cuenta) {
        if (cuenta == null) return null;

        return new CuentaDTO(
                cuenta.getId(),
                cuenta.getTipoCuenta().name(),
                cuenta.getTotal(),
                cuenta.getDniCliente().getValor()
        );
    }
}