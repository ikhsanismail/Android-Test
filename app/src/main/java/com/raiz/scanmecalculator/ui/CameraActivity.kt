package com.raiz.scanmecalculator.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.raiz.scanmecalculator.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var textRecog: TextRecognizer
    private lateinit var mCameraSource: CameraSource
    private val PERMISSIONS_REQUEST_CAMERA: Int = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textRecog = TextRecognizer.Builder(this).build()
        if(!textRecog.isOperational) {
            Toast.makeText(this,"Error Occurred!!!",Toast.LENGTH_SHORT).show()
        }
        mCameraSource = CameraSource.Builder(applicationContext, textRecog)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(2.0f)
            .build()

        binding.svCamera.holder.addCallback(object : SurfaceHolder.Callback {


            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder) {
                if (ContextCompat.checkSelfPermission(this@CameraActivity, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CameraActivity,
                        arrayOf(android.Manifest.permission.CAMERA),
                        123
                    )
                } else {
                   mCameraSource.start(binding.svCamera.holder)
                }

            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }


            override fun surfaceDestroyed(p0: SurfaceHolder) {
                mCameraSource.stop()
            }
        })

        textRecog.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                binding.tvCameraResult.post {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    binding.tvCameraResult.text = stringBuilder.toString()
                }
            }
        })

        //button click
        binding.btnCapture.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", binding.tvCameraResult.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                mCameraSource.start(binding.svCamera.holder)
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}