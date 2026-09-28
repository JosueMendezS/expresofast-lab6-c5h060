package cr.ac.ucr.paraiso.ie.c5h060.expresofast.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.ie.c5h060.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.EnvioDTO;
import cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto.PaginaEnvioDTO;

@RestController
@RequestMapping("/api/v1/envios/paginado")
public class EnvioPaginadoController {

    private final EnvioService envioService;

    public EnvioPaginadoController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<PaginaEnvioDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {

        Page<EnvioDTO> pagina = envioService.listarPaginado(page, size, sortBy, direction, busqueda, estado);
        return ResponseEntity.ok(PaginaEnvioDTO.desde(pagina));
    }

    @GetMapping("/procedimiento/{estado}")
    public ResponseEntity<List<EnvioDTO>> listarPorProcedimiento(@PathVariable String estado) {
        return ResponseEntity.ok(envioService.listarViaStoredProcedure(estado));
    }
}