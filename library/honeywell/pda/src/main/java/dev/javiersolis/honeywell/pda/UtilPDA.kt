package dev.javiersolis.honeywell.pda

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
//import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.honeywell.aidc.*
import java.util.*
import com.honeywell.aidc.AidcManager.CreatedCallback

class UtilPDA : BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener  {

    //var t1: TextToSpeech? = null
    var barcodeReader: BarcodeReader?= null

    lateinit var manager: AidcManager
    lateinit var listener: PDAListener
    lateinit var context: Context
    lateinit var activity: Activity

    fun onResume(){
        try {
            barcodeReader?.claim()
        } catch (e: ScannerUnavailableException) {
            e.printStackTrace()
            Toast.makeText(context, "PDA Laser no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    fun configBarcodeLaser(activity:Activity,context:Context, listener: PDAListener){
        this.listener = listener
        this.context = context
        this.activity = activity

        /*
        t1 = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                val locSpanish = Locale("spa", "MEX")
                t1?.language = locSpanish

            }
        }
        */

        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(context, CreatedCallback { aidcManager ->
            manager = aidcManager
            barcodeReader = manager.createBarcodeReader()

            //
            // register bar code event listener
            barcodeReader?.addBarcodeListener(this)

            // set the trigger mode to client control
            try {
                barcodeReader?.setProperty(
                    BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL
                )
            } catch (e: UnsupportedPropertyException) {
                //Toast.makeText(context, "Fallo al aplicar propiedades del PDA", Toast.LENGTH_SHORT).show()
            }
            // register trigger state change listener
            barcodeReader?.addTriggerListener(this)
            val properties: MutableMap<String, Any> = HashMap()
            // Set Symbologies On/Off
            properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_GS1_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = true
            properties[BarcodeReader.PROPERTY_DATAMATRIX_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = true
            properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = false
            properties[BarcodeReader.PROPERTY_AZTEC_ENABLED] = false
            properties[BarcodeReader.PROPERTY_CODABAR_ENABLED] = false
            properties[BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED] = false
            properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = false
            // Set Max Code 39 barcode length
            properties[BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH] = 10
            // Turn on center decoding
            properties[BarcodeReader.PROPERTY_CENTER_DECODE] = true
            // Enable bad read response
            properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] = true
            // Apply the settings
            barcodeReader?.setProperties(properties)

            /*
            Toast.makeText(
                context,
                "Propiedades aplicadas correctamente del PDA",
                Toast.LENGTH_SHORT
            ).show()
            */

            //
            try {
                barcodeReader?.claim()
                //Toast.makeText(context, "PDA Laser disponible", Toast.LENGTH_SHORT).show()
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
                Toast.makeText(context, "PDA Laser NO disponible", Toast.LENGTH_SHORT).show()
            }

        })

        //ActivitySetting()

    }


    override fun onBarcodeEvent(event: BarcodeReadEvent) {
        Log.e("PDA_SCAN", "onBarcodeEvent:")

        //t1?.speak("al fin funcionas basuuuuura", TextToSpeech.QUEUE_FLUSH, null)
        val list: MutableList<String> = ArrayList()
        list.add("Barcode data: " + event.barcodeData)
        list.add("Character Set: " + event.charset)
        list.add("Code ID: " + event.codeId)
        list.add("AIM ID: " + event.aimId)
        list.add("Timestamp: " + event.timestamp)

        list.forEach { it -> Log.e("PDA_SCAN", "Capture:" + it) }
        //Toast.makeText(context, "Escaneo  " + event.barcodeData, Toast.LENGTH_LONG).show()



        activity?.runOnUiThread(Runnable { // update UI to reflect the data
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.scan_click)
            mp.setOnCompletionListener { it ->
                it.reset()
                it.release()
                //mp = null
            }
            mp.start()

            listener.onBarcodeRead(event.barcodeData)
            //presenter.reviewPackageScanned(event.barcodeData)
        })
        /*
        runOnlyNoWorking {
            activity?.runOnUiThread(Runnable { // update UI to reflect the data
                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.scan_click)
                mp.setOnCompletionListener { it ->
                    it.reset()
                    it.release()
                    //mp = null
                }
                mp.start()



                presenter.reviewPackageScanned(event.barcodeData)
            })
        }
        */


    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        Log.e("PDA_SCAN", "onFailureEvent:"+p0)
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        Log.e("PDA_SCAN", "onTriggerEvent:"+p0)
    }
}