package dev.javiersolis.honeywell.pda

fun interface PDAListener {
    fun onBarcodeRead(barcode:String)
}