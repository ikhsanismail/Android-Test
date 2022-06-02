package com.raiz.scanmecalculator.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.raiz.scanmecalculator.BuildConfig
import com.raiz.scanmecalculator.databinding.ActivityMainBinding
import com.raiz.scanmecalculator.utils.Helpers.getBitmapFromStorage
import com.raiz.scanmecalculator.utils.Helpers.getResizedBitmap
import com.raiz.scanmecalculator.utils.Helpers.isNumeric
import java.io.FileDescriptor
import java.lang.IllegalArgumentException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var cameraResult = ""
    var removeSpace = ""
    var take = ""
    var selectedImage: Uri? = null
    var selectedBitmap: Bitmap? = null
    var firstOperand: Char = '\u0000'
    var secondOperand: Char = '\u0000'
    private lateinit var textRecognition: TextRecognizer
    var s: List<String> = listOf()

    companion object {
        val IMAGE_REQUEST_CODE = 100
        val CAMERA_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddInput.setOnClickListener {
            if (BuildConfig.APP_TYPE == "builtincamera") {
                getTextFromCamera()
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun getTextFromCamera() {
        val i = Intent(this@MainActivity, CameraActivity::class.java)
        startActivityForResult(i, CAMERA_REQUEST_CODE)
    }

    private fun pickImageFromGallery() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, IMAGE_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            cameraResult = data!!.getStringExtra("result")!!
            removeSpace = cameraResult.filterNot { it.isWhitespace() }

            take = removeSpace.take(3)
            binding.tvInput.text = take

            if (take.count() == 3) {
                firstOperand = take[0]
                secondOperand = take[2]
            }

            if (isNumeric(firstOperand.toString()) && isNumeric(secondOperand.toString())) {
                if (take.contains("-")) {
                    s = take.split("-")
                    val result = Integer.parseInt(s[0]) - Integer.parseInt(s[1])
                    binding.tvResult.text = result.toString()
                } else if (take.contains("+")) {
                    s = take.split("+")
                    val result = Integer.parseInt(s[0]) + Integer.parseInt(s[1])
                    binding.tvResult.text = result.toString()
                } else if (take.contains("/")) {
                    s = take.split("/")
                    try {
                        val result = Integer.parseInt(s[0]) / Integer.parseInt(s[1])
                        binding.tvResult.text = result.toString()
                    } catch (e: ArithmeticException) {
                        Toast.makeText(
                            this,
                            "Cannot Divide By Zero",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (take.contains("*") || take.contains("x")) {
                    s = take.split("*")
                    s = take.split("x")
                    val result = Integer.parseInt(s[0]) * Integer.parseInt(s[1])
                    binding.tvResult.text = result.toString()
                }
            } else if (take.count() < 3) {
                binding.tvResult.text = ""
            } else {
                binding.tvResult.text = ""
            }

        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedImage = data?.data

            selectedBitmap = getBitmapFromStorage(selectedImage!!,this)
            val compressedBitmap = getResizedBitmap(selectedBitmap!!, 1080)
            getTextFromImage(compressedBitmap!!)

            binding.ivFromGallery.setImageBitmap(compressedBitmap)
        }
    }

    private fun getTextFromImage(bitmap: Bitmap) {
        textRecognition = TextRecognizer.Builder(this).build()
        if (!textRecognition.isOperational) {
            Toast.makeText(this, "Error Occurred!!!", Toast.LENGTH_SHORT).show()
        }
        val frame = Frame.Builder().setBitmap(bitmap).build()

        val text: SparseArray<TextBlock> = textRecognition.detect(frame)
        val stringBuilder = StringBuilder()
        for (i in 0 until text.size()) {
            val textBlock = text.valueAt(i)
            if (textBlock != null && textBlock.value != null) {
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
        }

        removeSpace = stringBuilder.toString().filterNot { it.isWhitespace() }

        take = removeSpace.take(3)
        binding.tvInput.setText(take)

        if (take.count() == 3) {
            firstOperand = take[0]
            secondOperand = take[2]
        }

        if (isNumeric(firstOperand.toString()) && isNumeric(secondOperand.toString())) {
            if (take.contains("-")) {
                s = take.split("-")
                val result = Integer.parseInt(s[0]) - Integer.parseInt(s[1])
                binding.tvResult.text = result.toString()
            } else if (take.contains("+")) {
                s = take.split("+")
                val result = Integer.parseInt(s[0]) + Integer.parseInt(s[1])
                binding.tvResult.text = result.toString()
            } else if (take.contains("/")) {
                s = take.split("/")
                try {
                    val result = Integer.parseInt(s[0]) / Integer.parseInt(s[1])
                    binding.tvResult.text = result.toString()
                } catch (e: ArithmeticException) {
                    Toast.makeText(
                        this,
                        "Cannot Divide By Zero",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (take.contains("*") || take.contains("x")) {
                s = take.split("*")
                s = take.split("x")
                val result = Integer.parseInt(s[0]) * Integer.parseInt(s[1])
                binding.tvResult.text = result.toString()
            }
        } else if (take.count() < 3) {
            binding.tvResult.text = ""
        } else {
            binding.tvResult.text = ""
        }
        textRecognition.release();
    }
}