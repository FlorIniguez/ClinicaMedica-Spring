package med.voll.api.controller;

import med.voll.api.domain.consulta.AgendaConsultaService;
import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import med.voll.api.domain.consulta.DatosDetalleConsulta;
import med.voll.api.domain.medico.Especialidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsultaControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    //toma un objeto java y lo transforma en json
    private JacksonTester<DatosAgendarConsulta> jsonAgendarConsulta;
    @Autowired
    private JacksonTester<DatosDetalleConsulta> detalleConsultaJacksonTester;
    //para que simule el servicio
    @MockBean
    private AgendaConsultaService agendaConsultaService;

    //testeo solo este componente, sin testear service y repo
    @Test
    @DisplayName("Deberia retornar estado http 400 cuando los datos ingresados sean invalidos")
    @WithMockUser
    void agendarEscenario1() throws Exception {
        //given //when, cuando se realiza la peticion
        var response = mvc.perform(post("/consultas")).andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deberia retornar estado http 200 cuando los datos ingresados estan ok")
    @WithMockUser
    void agendarEscenario2() throws Exception {
        //given
        //una hora despues de "AHORA"
        LocalDateTime fecha = LocalDateTime.now().plusHours(1);
        Especialidad especialidad = Especialidad.CARDIOLOGIA;
        DatosDetalleConsulta datosConsulta = new DatosDetalleConsulta(null, 2L, 1L, fecha);
        //when
        //
        when(agendaConsultaService.agendar(any())).thenReturn(datosConsulta);

        MockHttpServletResponse response = mvc.perform(post("/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                //le indico a json tester que escriba un dato de DatosAgendarConsulta y que obtenga el json
                //que es el que se envia
                .content(jsonAgendarConsulta.write(new DatosAgendarConsulta(2L, 1L, fecha, especialidad)).getJson())
        ).andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        //verifico el cuerpo del msj que espero, lo paso de java a json
        String jsonEsperado = detalleConsultaJacksonTester.write(datosConsulta).getJson();

        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }
}