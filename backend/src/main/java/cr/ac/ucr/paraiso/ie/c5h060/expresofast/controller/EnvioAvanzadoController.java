package cr.ac.ucr.paraiso.ie.c5h060.expresofast.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioMapper;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioRegistroDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioResponseDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.TrackingCheckDTO;
import jakarta.validation.Valid;

// Laboratorio 11
@RestController
@RequestMapping("/api/envios")
public class EnvioAvanzadoController {

    private final EnvioService envioService;

    public EnvioAvanzadoController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping("/check-tracking/{trackingNumber}")
    public ResponseEntity<TrackingCheckDTO> checkTracking(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(new TrackingCheckDTO(trackingNumber, envioService.existeTracking(trackingNumber)));
    }

    @PostMapping("/avanzado")
    public ResponseEntity<EnvioResponseDTO> registrarConPaquetes(@Valid @RequestBody EnvioRegistroDTO dto) {
        Envio creado = envioService.registrarEnvioConPaquetes(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvioMapper.toResponseDTO(creado));
    }
}