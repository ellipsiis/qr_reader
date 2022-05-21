package com.tallercmovil.qrreader

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QR : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private val camera_permission = 1
    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this@QR)
        setContentView(scannerView)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermissions())
            {
//                the permission is enable
            }else{
                enablePermission()
            }
        }

        scannerView?.setResultHandler(this@QR)
        scannerView?.startCamera()
    }

    private fun enablePermission() {
        ActivityCompat.requestPermissions(this@QR, arrayOf(android.Manifest.permission.CAMERA), camera_permission)
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this@QR, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    override fun handleResult(p0: Result?) {
//        result for reading from QR scanner
        val scanResult = p0?.text

        Log.d("QR_LEIDO",scanResult!!)

    }
}