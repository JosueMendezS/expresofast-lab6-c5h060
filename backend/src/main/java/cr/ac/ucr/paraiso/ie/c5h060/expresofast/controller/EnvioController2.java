package cr.ac.ucr.paraiso.ie.c5h060.expresofast.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.CambioEstadoDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.CrearEnvioDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioMapper;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioResponseDTO;
import jakarta.validation.Valid;

// Laboratorio 10 
@RestController
@RequestMapping("/api/v1/envios")
@CrossOrigin(origins = "http://localhost:4200")
public class EnvioController2 {

    private final EnvioService envioService;

    public EnvioController2(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> obtenerTodos() {
        List<Envio> envios = envioService.listarTodos();
        return ResponseEntity.ok(EnvioMapper.toResponseDTOList(envios));
    }

    @GetMapping("/rastreo/{codigo}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorRastreo(@PathVariable String codigo) {
        Envio envio = envioService.buscarPorCodigoRastreo(codigo);
        return ResponseEntity.ok(EnvioMapper.toResponseDTO(envio));
    }

    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crear(@Valid @RequestBody CrearEnvioDTO dto) {
        Envio envioCreado = envioService.registrarEnvioDesdeAngular(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvioMapper.toResponseDTO(envioCreado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(@PathVariable Integer id,
            @Valid @RequestBody CambioEstadoDTO dto) {
        Envio envioActualizado = envioService.actualizarEstado(id, dto.nuevoEstado(), dto.observaciones());
        return ResponseEntity.ok(EnvioMapper.toResponseDTO(envioActualizado));
    }
}