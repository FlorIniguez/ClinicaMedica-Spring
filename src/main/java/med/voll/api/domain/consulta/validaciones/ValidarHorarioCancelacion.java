package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosCancelamientoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidarHorarioCancelacion implements ValidadorCancelarConsulta {
    @Autowired
    private ConsultaRepository consultaRepository;

    @Override
    public void validar(DatosCancelamientoConsulta datos) {
        var consulta = consultaRepository.getReferenceById(datos.idConsulta());
        LocalDateTime horarioActual = LocalDateTime.now();
        var diferenciaEnHoras = Duration.between(horarioActual,consulta.getFecha()).toHours();

        if (diferenciaEnHoras < 24){
            throw new ValidationException("La consulta solo puede ser cancelada con 24hs de anticipacion");
        }

    }
}
