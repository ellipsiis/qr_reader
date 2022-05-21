package com.tallercmovil.qrreader

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.google.zxing.client.result.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

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
        try{
//            for email intent
            val listEmail = scanResult.split(":")
            var emailIntent = Intent(Intent.ACTION_VIEW)
            emailIntent.setData(Uri.parse("mailto:"))
            emailIntent.putExtra(Intent.EXTRA_EMAIL,listEmail[2])
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,listEmail[4])
            emailIntent.putExtra(Intent.EXTRA_TEXT,listEmail[6])
            startActivity(emailIntent)
            finish()


        }catch(e: Exception){
            AlertDialog.Builder(this@QR)
                .setTitle(getString(R.string.error_title_alertdialog))
                .setMessage(getString(R.string.error_qr_lecture))
                .setPositiveButton(getString(R.string.positive_button), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }

        try{
//            For reading a sms
            val list = scanResult.split(":")
            var smsIntent = Intent(Intent.ACTION_VIEW)
            smsIntent.setData(Uri.parse("smsto:"+list[1]))
            smsIntent.putExtra("sms_body",list[2])
            startActivity(smsIntent)
            finish()

        }catch(e:Exception){
            AlertDialog.Builder(this@QR)
                .setTitle(getString(R.string.error_title_alertdialog))
                .setMessage(getString(R.string.error_qr_lecture))
                .setPositiveButton(getString(R.string.positive_button), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }

        try{
//            for url
            var url = URL(scanResult)
            var i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(scanResult))
            startActivity(i)
            finish()

        }catch(e: Exception){
            AlertDialog.Builder(this@QR)
                .setTitle(getString(R.string.error_title_alertdialog))
                .setMessage(getString(R.string.error_qr_lecture))
                .setPositiveButton(getString(R.string.positive_button), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }

        try{
            var parsedResult = ResultParser.parseResult(p0) as AddressBookParsedResult
            var nValue = parsedResult.names
            var nkValue = parsedResult.nicknames
            var eValue = parsedResult.emails
            var etValue = parsedResult.emailTypes
            var phValue = parsedResult.phoneNumbers
            var phtValue = parsedResult.phoneTypes



            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.PHONETIC_NAME, nkValue)
                putExtra(ContactsContract.Intents.Insert.NAME, nValue)
                putExtra(ContactsContract.Intents.Insert.EMAIL,eValue)
                putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,etValue)
                putExtra(ContactsContract.Intents.Insert.PHONE,phValue)
                putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,phtValue)
            }
            startActivity(intent)

        }catch(e:Exception){
            AlertDialog.Builder(this@QR)
//                .setTitle(getString(R.string.error_title_alertdialog))
                .setTitle(getString(R.string.error_title_alertdialog))
                .setMessage(getString(R.string.error_qr_lecture))
                .setPositiveButton(getString(R.string.positive_button), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }

    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermissions()){
                if(scannerView == null){
                    scannerView = ZXingScannerView(this@QR)
                    setContentView(scannerView)
                }else{
                    scannerView?.setResultHandler(this@QR)
                    scannerView?.startCamera()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView?.stopCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            camera_permission ->{
                if(grantResults.isNotEmpty()){
                    if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                AlertDialog.Builder(this@QR)
                                    .setTitle(getString(R.string.required_permission))
                                    .setMessage(getString(R.string.camera_permission_message))
                                    .setPositiveButton(getString(R.string.positive_button),DialogInterface.OnClickListener { dialogInterface, i ->
                                        requestPermissions(arrayOf(Manifest.permission.CAMERA), camera_permission)
                                    })
                                    .setNegativeButton(getString(R.string.negative_button),DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        finish()
                                    })
                                    .create()
                                    .show()
                            }else{
                                Toast.makeText(this@QR,getString(R.string.camera_permission_negative_message),Toast.LENGTH_LONG)
                                    .show()
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

}