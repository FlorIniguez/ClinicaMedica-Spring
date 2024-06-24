package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicoConTurno implements ValidadorDeConsultas {
    @Autowired
    private ConsultaRepository consultaRepository;

    public void validar(DatosAgendarConsulta datos) {
        if (datos.idMedico() == null) {
            return;
        }

        var medicoConTurno = consultaRepository.existsByMedicoIdAndFecha(datos.idMedico(), datos.fecha());

        if (medicoConTurno) {
            throw new ValidationException("Este Dr/a Ya tiene una consulta en ese horario");

        }
    }
}
