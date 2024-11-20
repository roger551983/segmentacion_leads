// Importa las clases necesarias al principio del script
import java.util.ArrayList
import groovy.json.JsonSlurper
import org.camunda.bpm.engine.delegate.BpmnError

// Suponiendo que 'response' es una cadena JSON que recibimos
def system = java.lang.System

// Verifica si el código de estado no es 200
if (statusCode != 200) {
    // Crea el mensaje de error
    def mensajeErrorServico = "Error técnico: " + urlServicioObtenerLeads + " código de error " + statusCode
    // Imprime el mensaje de error en la consola
    system.out.println(mensajeErrorServico)
    // Asigna el mensaje de error a la variable de Camunda
    connector.setVariable("mensageErrorServico", mensajeErrorServico)
    // Lanza un error de BPMN
    throw new BpmnError("error-no-responde-datos")
} else {
    // Si la respuesta es exitosa, imprime el acceso exitoso
    system.out.println("Acceso exitoso: " + urlServicioObtenerLeads)

    // Parseamos la cadena JSON
    def jsonArray = new JsonSlurper().parseText(response)
    def resultadoArray = []  // Inicializamos el arreglo de resultados
    def chunkSize = 1000  // Procesamos en bloques de 1000
    def totalElements = jsonArray.size()  // Obtenemos el tamaño total del JSON

    try {
        // Iteramos sobre los elementos del JSON
        for (int i = 0; i < totalElements; i++) {
            def element = jsonArray[i]  // Accedemos al elemento en la posición 'i'

            // Creamos el objeto 'item' con las propiedades del elemento
            def item = [
                nombre: element.name,  // Accedemos a la propiedad 'name'
                correo: element.email,  // Accedemos a la propiedad 'email'
                condicionLaboral: element.fields[7]?.value,  // Accedemos al índice 7 de 'fields' para obtener 'condicionLaboral'
                descargaPDF: element.fields[8]?.value  // Accedemos al índice 8 de 'fields' para obtener 'descargaPDF'
            ]

            // Filtramos: solo agregamos el 'item' si tiene datos válidos
            if (item.nombre && item.correo && item.condicionLaboral && item.descargaPDF) {
                resultadoArray.add(item)  // Agregamos el item al arreglo de resultados
            }

            // Si alcanzamos el chunkSize o estamos en el último elemento, guardamos los resultados en Camunda
            if ((i + 1) % chunkSize == 0 || i == totalElements - 1) {
                // Asegúrate de que el arreglo no esté vacío
                if (!resultadoArray.isEmpty()) {
                    // Convertimos el arreglo a un ArrayList de Java antes de enviarlo a Camunda
                    def arrayList = new ArrayList(resultadoArray)
                    println("Se enviarán ${arrayList.size()} elementos a Camunda.")
                    connector.setVariable("contactosMailerLite", arrayList)
                } else {
                    println("El arreglo resultadoArray está vacío.")
                }
                // Reiniciamos el arreglo para el siguiente bloque
                resultadoArray.clear()
            }
        }
    } catch (Exception e) {
        // Capturamos y mostramos cualquier error
        println("Error al procesar los leads: ${e.message}")
    }
}

return statusCode
