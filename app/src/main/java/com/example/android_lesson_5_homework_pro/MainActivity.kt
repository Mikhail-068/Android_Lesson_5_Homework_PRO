package com.example.android_lesson_5_homework_pro

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_lesson_5_homework_pro.ml.Model
import kotlinx.coroutines.selects.select
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class MainActivity : AppCompatActivity() {

    val PICK_IMAGE_REQUEST = 1 // константа для кода запроса изображения
    lateinit var select_btn: Button
    lateinit var predict_btn: Button
    lateinit var pred_img: ImageView
    lateinit var pred_text: TextView
    var bitmap: Bitmap? = null



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_btn = findViewById(R.id.select_1)
        predict_btn = findViewById(R.id.predict_1)
        pred_img = findViewById(R.id.image_1)
        pred_text = findViewById(R.id.text_1)


        val imgProc = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 1.0f))
            .build()


        select_btn.setOnClickListener {
            // создаем новое намерение для выбора изображения из галереи
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            // запускаем активность для получения результата
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        predict_btn.setOnClickListener {
            if (bitmap == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Изображение не выбрано!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            var my_pred_img = TensorImage(DataType.FLOAT32)
            my_pred_img.load(bitmap)

            my_pred_img = imgProc.process(my_pred_img)



            val model = Model.newInstance(this)

            // Создает входные данные для справки.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(my_pred_img.buffer) // просто передать изобр нельзя!!!

            // Запускает вывод модели и получает результат.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.intArray

            var maxIdx = 0
            outputFeature0.forEachIndexed { index, fl ->
                if(outputFeature0[maxIdx] > fl){
                    maxIdx = index
                }
            }

            val label_1 = application.assets.open("Label.txt").bufferedReader().readLines()
            pred_text.setText(label_1[maxIdx])
//             Преобразуем массив в список значений
            val values = outputFeature0.map { it.toString() }

            // Находим максимальное значение
            val maxValue = values.maxOrNull()

            // Получаем его индекс
            val maxIndex = values.indexOf(maxValue)

//             Выводим индекс
            pred_text.setText(label_1[maxIndex].toString())



            model.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        // проверяем, что результат соответствует нашему коду запроса и не пустой
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // получаем URI выбранного изображения
            val imageUri = data.data;
            // устанавливаем его в наш ImageView
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            pred_img.setImageBitmap(bitmap)
        }
    }
}


