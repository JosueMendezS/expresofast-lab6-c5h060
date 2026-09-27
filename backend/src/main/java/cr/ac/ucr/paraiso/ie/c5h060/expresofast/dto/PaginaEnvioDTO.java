package cr.ac.ucr.paraiso.ie.c5h060.expresofast.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaEnvioDTO(
        List<EnvioDTO> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    public static PaginaEnvioDTO desde(Page<EnvioDTO> pagina) {
        return new PaginaEnvioDTO(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast());
    }
}