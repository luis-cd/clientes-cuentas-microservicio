package com.example.banca.infrastructure.rest.Controladores;

import com.example.banca.application.services.CuentaService;
import com.example.banca.domain.model.ValueObjects.Dni;
import com.example.banca.domain.model.ValueObjects.TipoCuenta;
import com.example.banca.infrastructure.rest.DTOs.CuentaDTO;
import com.example.banca.infrastructure.rest.DTOs.CrearCuentaRequest;
import com.example.banca.infrastructure.rest.DTOs.ActualizarCuentaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaDTO crearCuenta(@RequestBody CrearCuentaRequest request) {
        var cuenta = cuentaService.crearCuenta(
            Dni.of(request.getDniCliente()),
            TipoCuenta.fromString(request.getTipoCuenta()),
            request.getTotal()
        );

        return new CuentaDTO(
            cuenta.getId(),
            cuenta.getTipoCuenta().name(),
            cuenta.getTotal(),
            cuenta.getDniCliente().getValor()
        );
    }

    @PutMapping("/{idCuenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarCuenta(@PathVariable Long idCuenta, @RequestBody ActualizarCuentaRequest request) {
        cuentaService.actualizarSaldo(idCuenta, request.getTotal());
    }
}
