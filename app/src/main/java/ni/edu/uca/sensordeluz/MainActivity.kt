package ni.edu.uca.sensordeluz

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import com.mikhaellopez.circularprogressbar.CircularProgressBar

//Implementación el detector de eventos del sensor (SensorEventListner)

class MainActivity : AppCompatActivity(), SensorEventListener{

    //Administrador del sensor
    private lateinit var sensorManager: SensorManager
    //Variable para el brillo del sensor y de tipo sensor
    private var brightness: Sensor? = null
    //Elementos del xml donse se verán los resultados
    private lateinit var text: TextView
    private lateinit var pb: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Configuración del modo nocturno
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Inicialización del text view de resultados
        text = findViewById(R.id.Sensor_Result)
        //Inicialización de la barra de progreso
        pb = findViewById(R.id.ProgessBar)

        //Llamado a la función de configuración del sensor
        setUpSensor()
    }

    //Funcion de la configuración del sensor
    private fun setUpSensor() {
        //Obtenemos el administrador del sensor con el servicio de sensor del sistema
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        /*Inicializamos nuestro brillo e igualamos al administrador de sensores para
          obtener el sensor predeterminado de tipo de sensor de luz
        */
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    //Funcoón para detectar los cambios en el sensor
    override fun onSensorChanged(event: SensorEvent?) {
        //Creamos el evento del sensor igualandolo a un sensor de tipo luz
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            //Creación de un valor para la luz en 0
            val light1 = event.values[0]

            /*Configurtación del texto para la vista de nuestros resultados.
             *Interpolamos las variables de la luz para obtener un ressultado numérico y de cadena
            */
            text.text = "Sensor: $light1%\nIntensidad: ${brightness(light1)}"
            //Para la barra de progresos solo llamamos a la animación y le pasamos el valor de la luz
            pb.setProgressWithAnimation(light1)
        }
    }

    /*Función que tomará el nivel de brillo captado por el sensor  y de tipo float ya que
      es el tipo de dato con el que trabaja el sensor
     */
    private fun brightness(brightness: Float): String {
        //Devolveremos estos strings según la medida del sensor
        return when (brightness.toInt()) {
            /*Cuando la medida se encuentre en el rango correspondiente a la cadena de
              texto se mostrará lo siguiente:
             */
            0 -> "Sin luz"
            in 1..10 -> "Oscuro"
            in 11..50 -> "Poco Oscuro"
            in 51..5000 -> "Normal"
            in 5001..25000 -> "Brillante"
            else -> "Excesivamente brillante"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    //Registra el llamado al sensor.
    override fun onResume() {
        super.onResume()
        /*El contexto que tomará será el sensor de brillo  y llamamos al administrador de sensores
          con retraso con el fin de captar lecturas a una frecuencia normal o lenta
        */
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    //Esta función cancela el registro del sensor para evitar ejecuciones en segundo plano
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}