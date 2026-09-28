/*
 * =====================================================================
 *  EJERCICIO 1 — Introducción a las corrutinas en el Playground de Kotlin
 * =====================================================================
 *  Dónde se ejecuta : Playground de Kotlin (https://play.kotlinlang.org
 *                     o https://developer.android.com/training/kotlinplayground)
 *  Proyecto Android : NO requiere (es un programa de consola)
 *  Package / SDK    : no aplica
 *  Recursos previos : ninguno (sin imágenes ni strings)
 *
 *  CÓMO USARLO
 *  -----------
 *  1. Copia TODO este archivo y pégalo en el Playground (reemplaza lo que haya).
 *  2. Cambia el valor de ETAPA (abajo) para elegir qué parte del codelab correr:
 *        0  = corre todas las etapas seguras (1–7 y 9–12), una tras otra
 *        1..12 = corre solo esa etapa (ideal para grabar el video)
 *  3. Presiona "Run".
 *
 *  Cada etapa vive dentro de su propio `object EtapaN { ... }` para que las
 *  funciones con el mismo nombre (printForecast, getWeatherReport, etc.) no
 *  choquen entre sí. El cuerpo de cada `ejecutar()` es EXACTAMENTE el `main()`
 *  del codelab en ese paso; si quieres mostrar el código "tal cual" en el
 *  video, copia el contenido de un object y renombra ejecutar() a main().
 *
 *  La etapa 8 NO se corre con ETAPA = 0 porque termina el programa con una
 *  excepción a propósito (así lo pide el codelab para ver el fallo).
 * =====================================================================
 */

import kotlin.system.*
import kotlinx.coroutines.*

const val ETAPA = 0

fun main() {
    val etapas: Map<Int, Pair<String, () -> Unit>> = linkedMapOf(
        1 to ("Código síncrono" to Etapa1::ejecutar),
        2 to ("delay() dentro de runBlocking()" to Etapa2::ejecutar),
        3 to ("Funciones de suspensión (suspend) + measureTimeMillis" to Etapa3::ejecutar),
        4 to ("launch(): dos corrutinas en simultáneo + tiempo" to Etapa4::ejecutar),
        5 to ("launch() es 'activar y olvidar'" to Etapa5::ejecutar),
        6 to ("async() y await()" to Etapa6::ejecutar),
        7 to ("Descomposición paralela con coroutineScope()" to Etapa7::ejecutar),
        8 to ("Excepción SIN manejar (el programa falla a propósito)" to Etapa8::ejecutar),
        9 to ("try-catch alrededor de getWeatherReport()" to Etapa9::ejecutar),
        10 to ("try-catch DENTRO de async() (productor)" to Etapa10::ejecutar),
        11 to ("Cancelación de una corrutina" to Etapa11::ejecutar),
        12 to ("Despachadores y withContext()" to Etapa12::ejecutar),
    )

    val aCorrer = if (ETAPA == 0) etapas.filterKeys { it != 8 } else etapas.filterKeys { it == ETAPA }
    for ((numero, etapa) in aCorrer) {
        println("\n========== ETAPA $numero: ${etapa.first} ==========")
        etapa.second()
    }
}

// ---------- 1. Código síncrono: programa sencillo ----------
// Cada println() termina antes de pasar a la siguiente línea → todo main() es síncrono.
object Etapa1 {
    fun ejecutar() {
        println("Weather forecast")
        println("Sunny")
    }
}

// ---------- 2. Cómo agregar un retraso ----------
// delay() es una función de SUSPENSIÓN. Si la llamas directo en main() el compilador dice:
//   "Suspend function 'delay' should be called only from a coroutine or another suspend function"
//
//   fun main() {
//       println("Weather forecast")
//       delay(1000)            // <- ERROR DE COMPILACIÓN
//       println("Sunny")
//   }
//
// Solución del codelab: envolver el código en runBlocking {}, que crea una corrutina
// y un bucle de eventos. runBlocking es síncrono: no retorna hasta que termine su bloque.
object Etapa2 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            delay(1000)
            println("Sunny")
        }
    }
}

// ---------- 3. Cómo suspender funciones ----------
// Extraemos la "solicitud de red" a su propia función. Como llama a delay(),
// DEBE llevar el modificador `suspend`. Luego agregamos printTemperature()
// (práctica del codelab) y medimos el tiempo con measureTimeMillis (paso opcional).
// Resultado esperado: ~2.1 s, porque las dos funciones corren una DESPUÉS de la otra.
object Etapa3 {
    fun ejecutar() {
        val time = measureTimeMillis {
            runBlocking {
                println("Weather forecast")
                printForecast()
                printTemperature()
            }
        }
        println("Execution time: ${time / 1000.0} seconds")
    }

    suspend fun printForecast() {
        delay(1000)
        println("Sunny")
    }

    suspend fun printTemperature() {
        delay(1000)
        println("30°C")
    }
}

// ---------- 4. Código asíncrono: launch() ----------
// Cada llamada va en su propia corrutina con launch → corren en simultáneo.
// Resultado esperado: ~1.1 s (antes ~2.1 s).
object Etapa4 {
    fun ejecutar() {
        val time = measureTimeMillis {
            runBlocking {
                println("Weather forecast")
                launch {
                    printForecast()
                }
                launch {
                    printTemperature()
                }
            }
        }
        println("Execution time: ${time / 1000.0} seconds")
    }

    suspend fun printForecast() {
        delay(1000)
        println("Sunny")
    }

    suspend fun printTemperature() {
        delay(1000)
        println("30°C")
    }
}

// ---------- 5. launch() es "activar y olvidar" ----------
// Pregunta del codelab: ¿dónde aparece "Have a good day!"?
// Respuesta: justo después de "Weather forecast", porque launch retorna de inmediato
// sin esperar a que termine la corrutina que inició.
// Salida: Weather forecast / Have a good day! / Sunny / 30°C
object Etapa5 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            launch {
                printForecast()
            }
            launch {
                printTemperature()
            }
            println("Have a good day!")
        }
    }

    suspend fun printForecast() {
        delay(1000)
        println("Sunny")
    }

    suspend fun printTemperature() {
        delay(1000)
        println("30°C")
    }
}

// ---------- 6. async() y await() ----------
// Las funciones ahora DEVUELVEN un String (get* en vez de print*).
// async devuelve un Deferred<String> (una "promesa"); await() espera y entrega el valor.
// Salida: Weather forecast / Sunny 30°C / Have a good day!
object Etapa6 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            val forecast: Deferred<String> = async {
                getForecast()
            }
            val temperature: Deferred<String> = async {
                getTemperature()
            }
            println("${forecast.await()} ${temperature.await()}")
            println("Have a good day!")
        }
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(1000)
        return "30°C"
    }
}

// ---------- 7. Descomposición paralela con coroutineScope() ----------
// getWeatherReport() hace dos tareas en paralelo POR DENTRO, pero hacia afuera se ve
// síncrona: coroutineScope no retorna hasta que sus corrutinas hijas terminan.
// Eso es la simultaneidad estructurada.
object Etapa7 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            println(getWeatherReport())
            println("Have a good day!")
        }
    }

    suspend fun getWeatherReport() = coroutineScope {
        val forecast = async { getForecast() }
        val temperature = async { getTemperature() }
        "${forecast.await()} ${temperature.await()}"
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(1000)
        return "30°C"
    }
}

// ---------- 8. Excepciones con corrutinas (SIN manejar) ----------
// Ejemplo previo del codelab (no se ejecuta aquí): dividir entre cero
//   val numberOfPeople = 0; val numberOfPizzas = 20
//   println("Slices per person: ${numberOfPizzas / numberOfPeople}")  // ArithmeticException
//
// getTemperature() lanza AssertionError a los 500 ms. La corrutina hija falla,
// el error se propaga HACIA ARRIBA, se cancela la corrutina padre y también la
// hermana (getForecast). El programa termina con AssertionError.
// (El compilador avisa "Unreachable code" en el return: es esperado.)
object Etapa8 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            println(getWeatherReport())
            println("Have a good day!")
        }
    }

    suspend fun getWeatherReport() = coroutineScope {
        val forecast = async { getForecast() }
        val temperature = async { getTemperature() }
        "${forecast.await()} ${temperature.await()}"
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(500)
        throw AssertionError("Temperature is invalid")
        return "30°C"
    }
}

// ---------- 9. Excepciones de try-catch (en el llamador) ----------
// Capturamos el error alrededor de getWeatherReport(). El programa ya no falla,
// pero se pierde TODO el informe (aunque el pronóstico sí se había obtenido).
object Etapa9 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            try {
                println(getWeatherReport())
            } catch (e: AssertionError) {
                println("Caught exception in runBlocking(): $e")
                println("Report unavailable at this time")
            }
            println("Have a good day!")
        }
    }

    suspend fun getWeatherReport() = coroutineScope {
        val forecast = async { getForecast() }
        val temperature = async { getTemperature() }
        "${forecast.await()} ${temperature.await()}"
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(500)
        throw AssertionError("Temperature is invalid")
        return "30°C"
    }
}

// ---------- 10. try-catch DENTRO de async (el productor maneja el error) ----------
// async = productor, await = consumidor. Si el productor captura la excepción y
// devuelve un valor válido, el consumidor nunca se entera del error.
// Salida: ... / Sunny { No temperature found } / Have a good day!
object Etapa10 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            println(getWeatherReport())
            println("Have a good day!")
        }
    }

    suspend fun getWeatherReport() = coroutineScope {
        val forecast = async { getForecast() }
        val temperature = async {
            try {
                getTemperature()
            } catch (e: AssertionError) {
                println("Caught exception $e")
                "{ No temperature found }"
            }
        }

        "${forecast.await()} ${temperature.await()}"
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(500)
        throw AssertionError("Temperature is invalid")
        return "30°C"
    }
}

// ---------- 11. Cancelación ----------
// A los 200 ms cancelamos solo la corrutina de la temperatura. Cancelar una hija
// NO cancela a su padre ni a sus hermanas: el pronóstico se imprime igual.
// Salida: Weather forecast / Sunny / Have a good day!
object Etapa11 {
    fun ejecutar() {
        runBlocking {
            println("Weather forecast")
            println(getWeatherReport())
            println("Have a good day!")
        }
    }

    suspend fun getWeatherReport() = coroutineScope {
        val forecast = async { getForecast() }
        val temperature = async { getTemperature() }

        delay(200)
        temperature.cancel()

        "${forecast.await()}"
    }

    suspend fun getForecast(): String {
        delay(1000)
        return "Sunny"
    }

    suspend fun getTemperature(): String {
        delay(1000)
        return "30°C"
    }
}

// ---------- 12. Despachadores y withContext() ----------
// withContext(Dispatchers.Default) cambia el hilo SOLO para ese bloque; al salir,
// la corrutina vuelve al hilo principal. Thread.currentThread().name lo demuestra.
// Salida esperada (similar):
//   main @coroutine#1 - runBlocking function
//   Loading...
//   main @coroutine#2 - launch function
//   DefaultDispatcher-worker-1 @coroutine#2 - withContext function
//   10 results found.
//   main @coroutine#2 - end of launch function
// (El sufijo "@coroutine#N" aparece si el modo depuración de corrutinas está activo,
//  como en el Playground; en otros entornos puede mostrarse solo el nombre del hilo.)
object Etapa12 {
    fun ejecutar() {
        runBlocking {
            println("${Thread.currentThread().name} - runBlocking function")
            launch {
                println("${Thread.currentThread().name} - launch function")
                withContext(Dispatchers.Default) {
                    println("${Thread.currentThread().name} - withContext function")
                    delay(1000)
                    println("10 results found.")
                }
                println("${Thread.currentThread().name} - end of launch function")
            }
            println("Loading...")
        }
    }
}
