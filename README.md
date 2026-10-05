
---

# Laboratorio 11 - C5H060 

## Como correrlo?

1. Ejecutar database/04_schema_lab11.sql en SQL Server.
2. Backend: `cd backend` y `./mvnw spring-boot:run`
3. Frontend: `cd angular`, `npm install` y `ng serve`. El formulario esta en http://localhost:4200/envio-avanzado

## Pregunta 1: FormArray y UX

Con un FormArray el formulario crece segun lo que necesite el operador. Empieza con un paquete y con el boton de añadir se agregan mas, y con la X se quitan. Con 10 campos ocultos en el HTML siempre hay un maximo de 10, y hay que andar escondiendo y mostrando campos y despues filtrar los que quedaron vacios antes de mandarlos al backend.

En mantenimiento tambien es mejor porque el paquete se define una sola vez (el metodo crearPaquete) y el HTML solo lo repite con un @for. Si hay que cambiar una regla, por ejemplo el peso maximo, se cambia en un solo lugar y no en 10. Ademas el si intento meter un string en pesoKg el compilador no me deja, osea, está tipado.

## Pregunta 2: Event Loop y validadores

JavaScript tiene un solo hilo. El event loop va sacando tareas de las colas cuando el call stack esta vacio, y las promesas se atienden antes que los timers y las respuestas de red.

El validador de fechas es sincrono, solo compara dos valores que ya estan en memoria, se ejecuta completo y devuelve el error en el mismo momento. Por eso el mensaje aparece y el boton se deshabilita de una vez.

El validador de tracking es asincrono porque tiene que preguntarle al servidor, y la respuesta no esta lista cuando la funcion termina. Si el navegador se quedara esperando, la pagina se congelaria. 

Angular pide un Observable o Promise porque necesita algo a lo que suscribirse para saber cuando termino la validacion. Mientras tanto el campo queda en estado PENDING y el boton de enviar se bloquea. Con Observable ademas se puede cancelar, si el usuario sigue escribiendo, Angular cancela la consulta anterior y no se manda una peticion por cada tecla.