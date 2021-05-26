import React from "react";
import styled from "@emotion/styled";
import Layout from "../components/layouts/Layout";

const Head = styled.h2`
  color: #ad140c;
  text-align: center;
  font-weight: 700;
  margin-top: 5%;
`;
const Head2 = styled.h3`
  color: #000000;
  text-align: center;
  font-weight: 700;
`;
const Head3 = styled.h4`
  color: #000000;
  font-weight: 700;
`;
const COntenido = styled.div`
  color: #000000;
  text-align: justify;
  margin-left: 12%;
  margin-right: 12%;
`;

const OL = styled.ol`
  counter-reset: item;
`;
const OLetra = styled.ol`
  counter-reset: item;
  list-style-type: lower-latin;
`;
const LI = styled.li`
  display: block;
  ::before {
    content: counters(item, ".") " ";
    counter-increment: item;
  }
`;

const Politicas = () => {
  return (
    <Layout>
      <COntenido>
        <Head>
          TÉRMINOS Y CONDICIONES GENERALES PARA EL USO DE LA PLATAFORMA
          “RAPIDFAST”
        </Head>
        <Head2>
          DISPOSICIONES APLICABLES PARA EL CONTROL Y MITIGACIÓN DEL COVID-19
        </Head2>
        <p>
          {" "}
          Los Usuarios declara que durante la vigencia de la crisis por
          Covid-19:
        </p>
        <p>
          Se asegurará de que las solicitudes y el uso de los servicios
          prestados por la plataforma se utilizarán exclusivamente para la
          ejecución de aquellas actividades que estén expresamente exentas por
          las leyes dictadas por las autoridades nacionales y locales, y que
          estas actividades se ejecutarán adecuadamente.
        </p>
        <p>
          En este sentido, los usuarios que soliciten servicios serán los únicos
          responsables de demostrar a las autoridades competentes si han sido
          autorizados o cubiertos por alguna excepción a las restricciones de
          circulación.
        </p>
        <p>
          Durante la ejecución del servicio, siempre utilizarán correctamente
          los elementos de protección personal requeridos por las autoridades
          sanitarias, por lo que, en todo momento, los usuarios que soliciten el
          servicio deberán portar al menos sus respectivas mascarillas. Cuando
          descubra que tiene síntomas de alarma como fiebre, tos o dificultad
          para respirar, no solicitará ningún servicio
        </p>
        <p>
          Durante la ejecución del servicio, el usuario que solicita el servicio
          y el usuario prestador que acepta el servicio deben mantener las
          ventanas abiertas y tomar las medidas necesarias para facilitar la
          circulación del aire en el interior.
        </p>

        <p>
          Suministrará información veraz sobre su estado de salud a través de
          los canales de notificación y acompañamiento dispuestos por las
          autoridades de salud y por Rapidfast; Notificará a su EPS cuando tenga
          síntomas tales como fiebre, tos o dificultad respiratoria.
        </p>
        <p>
          La solicitud y ejecución de los Servicios, en todo caso, se efectúa
          bajo su voluntad, autonomía y responsabilidad, eximiendo a Rapidfast
          respecto de cualquier reclamación.
        </p>
        <p>
          Cuando el Usuario Solicitante del Servicio se da cuenta de que el
          Usuario Prestador del Servicio no ha adoptado las medidas y protocolos
          de bioseguridad personal adoptados por las autoridades sanitarias para
          controlar y mitigar al Covid-19, deberá evitar recibir cualquier
          servicio. Asimismo, cuando el Usuario Solicitante del Servicio no
          cumpla con las medidas y acuerdos anteriores, el Usuario Prestador del
          Servicio deberá evitar iniciar cualquier servicio, así mismo Usuario
          Solicitante del Servicio entiende y acepta que el Usuario Prestador
          del servicio se abstendrá de transportar más de dos (2) Usuarios
          Solicitantes durante la ejecución de un mismo servicio.
        </p>
        <p>
          La solicitud y ejecución de los Servicios, en todo caso, se efectúa
          bajo su voluntad, autonomía y responsabilidad, eximiendo a Rapidfast
          respecto de cualquier reclamación.
        </p>

        <OL>
          <LI>
            <b>
              POLITICAS DE USO DE LA PLATAFORMA, REQUISITOS TÉCNICOS Y
              RESTRICCIONES DE USO:
            </b>
            <OL>
              {" "}
              <br></br>
              <LI>
                Todas las partes deben declarar que aceptan los términos y
                políticas desarrollados por Rapidfast y las actualizaciones
                realizadas a este documento y plataforma. Si las partes
                involucradas en este proceso no están de acuerdo con los
                términos, políticas o actualizaciones, deben evitar el uso de la
                plataforma y las actualizaciones.
              </LI>{" "}
              <br></br>
              <LI>
                Los Usuarios de la plataforma no pueden dejar su Nickname o
                Contraseña a nadie más que a ellos mismos, porque son las únicas
                personas que tienen derecho a utilizar sus datos personales. El
                servicio no es exclusivo de una comunidad o grupo específico y
                puede ser revocado. No Puede ser cedido o encomendado a terceros
                y debe cumplir con las asignaciones definidas en este documento.
              </LI>
              <br></br>
              <LI>
                Todo beneficiario de la plataforma, ya sea como Usuario
                Solicitante del Servicio o Usuario Prestador del servicio, debe
                declarar que ha alcanzado la mayoría de edad y está en
                condiciones de cumplir con los términos y políticas anteriores,
                y confirmar su declaración, de acuerdo a lo establecido por el
                Código Civil colombiano.
              </LI>{" "}
              <br></br>
              <LI>
                Al descargar la aplicación en el dispositivo, los Usuarios
                Solicitantes del Servicio garantizan que pueden cumplir con
                todos los términos y condiciones de la plataforma y pueden pagar
                por los servicios prestados. La información proporcionada a la
                plataforma debe ser veraz y concisa.
              </LI>{" "}
              <br></br>
              <LI>
                Los Usuarios recibirán una contraseña por SMS al realizar el
                registro en la plataforma, por lo que tendrá derechos de control
                y será el único usuario que tenga acceso a la plataforma. Por lo
                tanto, es responsable de la confidencialidad de su información.{" "}
                <b>
                  (Tenga en cuenta: No se autorizarán dos cuentas con el mismo
                  número telefónico), así como también no podrá solicitar y
                  tomar dos Servicios en un mismo momento y con el mismo usuario
                  de la Plataforma.
                </b>
              </LI>
              <br></br>
              <LI>
                Todo usuario será responsable de poseer un dispositivo
                (teléfono, table o computadora con sistema operativo ANDROID)
                que cumpla con los requisitos mínimos de la plataforma de
                descarga y tenga un funcionamiento eficiente. Se ha determinado
                que cada usuario debe asumir sus costos de facturación y
                servicios telefónicos. Rapidfast se reserva el derecho de
                comprobar si el sistema operativo del dispositivo es óptimo para
                el funcionamiento de la plataforma, e informar los requisitos
                técnicos necesarios para cumplir eficazmente las condiciones de
                funcionamiento.
              </LI>
              <br></br>
              <LI>
                Rapidfast no se hace responsable por fallas de los equipos
                utilizados ya sea por exceso de funcionamiento o por mala
                operación, siempre y cuando la plataforma no sea la responsable
                de dicho daño se abstiene de responder, así como el mal
                desempeño de la aplicación, no se responderá siempre y cuando
                sea impugnable a una deficiencia en la señal de conexión a
                internet, en caso de presentarse esta situación debe
                comprobarse, que no sean imputables a la actividad de Rapidfast.
              </LI>
              <br></br>
              <LI>
                Rapidfast no asume ninguna responsabilidad por fallas del equipo
                causadas por un funcionamiento excesivo o señal deficiente de
                conexión a internet. Siempre que la plataforma no cause dichos
                daños se abstiene de la responsabilidad por los daños antes
                mencionados.
              </LI>
              <br></br>
              <LI>
                El usuario está obligado a no modificar, copiar, realizar
                ingeniería inversa, rediseñar, descompilar, adaptar, traducir,
                preparar trabajos derivados de la plataforma ni utilizar la
                plataforma para desarrollar ningún software u otros materiales
                basados en la plataforma. Asimismo, está obligado a utilizar la
                plataforma únicamente en las formas permitidas por estos
                términos. La plataforma está protegida por las leyes vigentes de
                derechos de autor y propiedad intelectual de Colombia.
              </LI>
              <br></br>
              <LI>
                El solicitante del servicio acepta y reconoce que Rapidfast
                puede iniciar las acciones legales que está autorizado a tomar,
                para exigir y asegurar el cumplimiento de todos los términos y
                condiciones establecidos, y para exigir una compensación por los
                daños causados por la violación de estos términos y condiciones.
                Obligaciones y declaraciones, incluida la indemnización por
                pérdidas indirectas y lucro cesante. Asimismo, Rapidfast no
                responderá por daños y perjuicios que pudiera sufrir los Usuario
                o un tercero ocasionados por el incumplimiento de estos términos
                y condiciones
              </LI>
              <br></br>
              <LI>
                La información que los Usuarios comparta con Rapidfast deberá
                ser precisa y transparente. Garantizará la autenticidad de todos
                aquellos datos que comunique, al diligenciar y enviar a
                Rapidfast los formularios necesarios para su registro y las
                solicitudes en la Plataforma. Así mismo, el Solicitante del
                Servicio será responsable de mantener permanentemente
                actualizada toda la información brindada a Rapidfast, a través
                de la Plataforma, de forma que responda, en cada momento, a su
                situación presentada con prontitud. De igual forma, será el
                único responsable de las manifestaciones falsas o inexactas que
                realice, y en general, cualquier información que reporte o
                brinde a la Plataforma, como también de los perjuicios que
                pudiere causar a Rapidfast o a terceros, por la información
                incorrecta que facilite.
              </LI>
              <br></br>
              <LI>
                Los Usuarios declara ser los únicos y exclusivos responsables
                del uso que le dé a la Plataforma, así como de cualquier acción
                que tenga lugar mediante dicho uso, por lo que no cabe
                imputación alguna de responsabilidad a Rapidfast, por parte de
                los mismos o terceros.
              </LI>
              <br></br>
              <LI>
                El Solicitante del Servicio reconoce el derecho de Rapidfast, en
                el caso de presentarse uso impropio de la Plataforma, ilícito,
                abusivo o viole los presentes términos y condiciones, los
                términos y condiciones complementarios, los términos específicos
                de Servicios o en general los derechos de terceros, las leyes y
                los reglamentos vigentes establecidos por la Republica de
                Colombia, a proceder inmediatamente y sin necesidad de
                notificación previa, a retirarlo de la Plataforma y/o a bloquear
                su acceso a éste. Lo anterior, sin perjuicio de todas las
                acciones y procedimientos legales que Rapidfast pueda iniciar,
                con el fin de reclamar la totalidad de los perjuicios causados
                por el Solicitante del Servicio
              </LI>
              <br></br>
              <LI>
                Los Usuarios que violen la legislación o infringe los derechos
                de terceros, Rapidfast tiene derecho a desvincularlo de la
                Plataforma, así como también facilitar, con previa solicitud de
                cualquier autoridad legítima (autoridades administrativas,
                judiciales o policiales), cualquier información que permita o
                facilite la identificación de usuario infractor.
              </LI>
            </OL>
          </LI>{" "}
          <br></br>
          {/* segundo paragrafo */}
          {/*  <LI>
            li element
            <OL>
              <LI>sub li element</LI>
              <LI>sub li element</LI>
              <LI>sub li element</LI>
            </OL>
          </LI>*/}
          <LI>
            <b>FUNCIONES DE LA PLATAFORMA</b>
            <OL>
              <br></br>
              <LI>
                Por medio de la Plataforma, el Solicitante del Servicio podrá
                solicitar los Servicios a los Prestadores del servicio
                registrados, así como: (a) Calificar a los Usuarios Prestadores
                del Servicio mediante una escala numérica de 1-5; (b)
                Identificar al Usuarios Prestador del servicio que brindará los
                estos, pudiendo conocer el nombre, ubicación y otra información
                del individuo; (c) Identificar el vehículo que brindará los
                Servicios, para lo cual la Plataforma le indicará el modelo y la
                placa del vehículo mediante el cual el Usuario Prestador del
                servicio le brindará el respectivo servicio; (d) Rastrear la
                ejecución del Servicio en tiempo y ubicación real, así como la
                ruta tomada por el Prestador del servicio; (e) Conocer la tarifa
                que a juicio del Usuario Prestador del servicio sea aplicable al
                mismo; y (f) Realizar el Pago del Servicio en las formas
                previstas en este documento. (g) El Usuario Solicitante del
                Servicio reconoce que la información que reciba de los Usuarios
                Prestadores del servicio es considerada un Dato Personal, por lo
                cual será el único responsable del manejo de la misma, y se
                compromete a mantener la confidencialidad de los datos como
                también a utilizarla exclusivamente a efectos de solicitar los
                Servicios y por la duración de los mismos.
              </LI>
              <br></br>
              <LI>
                Los Usuarios Prestadores son independientes y libres de aceptar
                o rechazar las solicitudes de Servicios que envíe el Usuario
                Solicitante del Servicio, incluso si aparecen como disponibles
                en la Plataforma. Al respecto, queda establecido en estos
                términos y condiciones que Rapidfast únicamente presta servicios
                de licenciamiento tecnológico por lo que no tiene injerencia
                alguna en la aceptación o rechazo de las solicitudes de
                Servicios.
              </LI>
              <br></br>
              <LI>
                Es importante mencionar y advertir que, el Usuario Prestador del
                Servicio registrado y validado a través de la Plataforma, será
                el único autorizado a prestar los mismos al Usuario Solicitante
                del Servicio, así mismo podrá calificar al Usuario Prestador del
                Servicio, y sus calificaciones y evaluaciones serán compartidas
                con otros Usuarios Solicitantes del servicio
              </LI>
              <br></br>
              <LI>
                Por medio de estos términos y condiciones, queda establecido que
                Rapidfast no es responsable de las calificaciones que otorgue
                los Usuarios Solicitantes del Servicio a los Usuarios
                Prestadores de servicio, relacionados a los Servicios prestados.
              </LI>
              <br></br>
              <LI>
                Rapidfast se reserva el derecho de bloquear, cuando estime
                necesario, el acceso del Usuario Solicitante del Servicio a la
                Plataforma o remover en forma parcial o totalmente, toda
                información, comunicación o material, incluyendo calificaciones
                que el Usuario Solicitante del Servicio haga del Usuario
                Prestador del Servicio y aquellos que el Usuario Solicitante del
                Servicio publique, a través de las Redes Sociales u otro medio
                de comunicación o espacio generado por Rapidfast, que cumpla con
                las siguientes condiciones:
                <OLetra>
                  <br></br>
                  <li>
                    Que esté protegido por derechos de propiedad intelectual,
                    amparados por el secreto comercial o marca comercial, o que
                    impliquen los derechos de propiedad de terceros, incluidos
                    los derechos de privacidad y publicidad, a menos que sea el
                    propietario de dichos derechos o tenga permiso o una
                    licencia de su legítimo propietario para publicar el
                    material; Que sea dañino, abusivo, ilegal, amenazante,
                    acosador, difamatorio, pornográfico, calumnioso, invasivo de
                    la privacidad, que dañe o pueda dañar a menores de cualquier
                    manera.
                  </li>
                  <br></br>
                  <li>
                    Que acose, degrade, intimide o sea odioso o discriminatorio
                    hacia un individuo o grupo de individuos, por cuestiones de
                    religión, género, orientación sexual, raza, etnia, edad o
                    discapacidad; Que incluya información personal o de
                    identificación de otra persona, sin el consentimiento
                    expreso de la misma.
                  </li>
                  <br></br>

                  <li>
                    Que imite a otra persona o entidad, incluyendo, pero sin
                    limitarse a, un empleado de Rapidfast, que constituya una
                    declaración falsa o que distorsione su afiliación con otra
                    persona o entidad.
                  </li>
                  <br></br>
                  <li>
                    Que constituya o contenga “esquemas piramidales”, “marketing
                    de afiliación”, “código de referencia a enlace”, “correo
                    basura”, “spam”, “cartas en cadena”, o anuncios
                    publicitarios no solicitados de carácter comercial.
                  </li>
                  <br></br>
                  <li>
                    Que haga referencia o incluya enlaces a servicios
                    comerciales o sitios Web de terceros
                  </li>
                  <br></br>
                  <li>
                    Que publicite servicios ilícitos o la venta de cualquier
                    artículo, cuya venta esté prohibida o restringida por la ley
                    aplicable
                  </li>
                  <br></br>

                  <li>
                    Que contenga virus de software, o cualquier otro código de
                    computadora, archivos o programas diseñados para
                    interrumpir, destruir o limitar la funcionalidad de
                    cualquier software o hardware o equipo de
                    telecomunicaciones.
                  </li>
                  <br></br>
                  <li>
                    Que interrumpa el flujo normal del diálogo con una cantidad
                    excesiva de mensajes (ataque masivo) a los Servicios, o que
                    afecte en forma negativa la capacidad de otros usuarios para
                    utilizar cualquiera de los Servicios.
                  </li>
                  <br></br>
                </OLetra>
              </LI>
              <br></br>
              <LI>
                El Usuario Solicitante del Servicio podrá también conocer la
                calificación consolidada de los Usuarios Prestadores del
                servicio, con base en las calificaciones brindadas por otros
                Usuarios Solicitantes del servicio.
              </LI>
              <br></br>
              <LI>
                El Usuario Solicitante del Servicio es libre en todo momento de
                enviar solicitudes de Servicios a través de la Plataforma y
                contactar al Prestador del servicio de su preferencia, no
                encontrándose obligado a enviar solicitudes de Servicio o a
                contactar a un Usuario Prestador del Servicio en particular. La
                relación entre el Usuario Prestador del Servicio y el Usuario
                Solicitante del Servicio es autónoma e independiente a Rapidfast
                y a los presentes términos y condiciones.
              </LI>
              <br></br>
              <LI>
                En este documento, queda establecido que Rapidfast no es
                responsable de las calificaciones que otorgue los Usuarios
                Solicitantes del servicio, a los Usuarios Prestadores del
                servicio relacionadas con los Servicios. Asimismo, el Usuario
                Solicitante del Servicio reconoce que asume el riesgo respecto
                de la honradez, solvencia y/o cumplimiento de los Usuarios
                Prestadores del servicio que acepten sus solicitudes de
                Servicio, sin que sea posible imputar a Rapidfast
                responsabilidad alguna por este concepto. Rapidfast tampoco será
                responsable por actos o hechos de los Usuarios Prestadores del
                servicio, ni garantiza su honradez, solvencia, moralidad o
                comportamiento.
              </LI>
              <br></br>
              <LI>
                Si el Usuario Solicitante del Servicio desea obtener mayor
                información acerca de un Prestador del servicio en particular,
                podrá consultarlo con Rapidfast, quien evaluará dicho
                requerimiento, de modo tal que no incumpla la legislación
                vigente para entregar dicha información.
              </LI>
              <br></br>
            </OL>
          </LI>
          <LI>
            <b>ACUERDOS DE PAGO</b>
            <OL>
              {" "}
              <br></br>
              <LI>
                De acuerdo con el alcance de estos términos y condiciones,
                Rapidfast solo puede cobrar al Usuario Prestador del Servicio
                por la licencia otorgada para descargar, usar y utilizar la
                plataforma. La tarifa de licencia se usa para proporcionar
                servicios basados en la plataforma. El pago específico calculado
                no tiene nada que ver con la tarifa que cobra el Usuario
                Prestador del Servicio al Usuario Solicita del Servicio por la
                renta de vehículo y conductor privado.
              </LI>
            </OL>
          </LI>
        </OL>
      </COntenido>
    </Layout>
  );
};

export default Politicas;
