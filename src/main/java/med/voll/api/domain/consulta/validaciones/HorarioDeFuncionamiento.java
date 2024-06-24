package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class HorarioDeFuncionamiento implements  ValidadorDeConsultas{
    public void validar(DatosAgendarConsulta datos) {
        var domingo = DayOfWeek.SUNDAY.equals(datos.fecha().getDayOfWeek());
        var antesApertura = datos.fecha().getHour() < 7;
        var despuesDeCierre = datos.fecha().getHour() > 19;

        if (domingo || antesApertura || despuesDeCierre) {
            throw new ValidationException("El horario de atención es de lun a vier de 07 a 19hs");
        }
    }
}

