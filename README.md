## Для решения задачи необходимо инициализировать сразу переменную.



# 1 Шаг.
Опрделяем переменную с возможным нулевым значением. Создаем сразу, а не оставляем создание на потом.

___
```
class MainActivity : AppCompatActivity() {

    val PICK_IMAGE_REQUEST = 1 // константа для кода запроса изображения
    lateinit var select_btn: Button
    lateinit var predict_btn: Button
    lateinit var pred_img: ImageView
    lateinit var pred_text: TextView
    var bitmap: Bitmap? = null // Сразу определяем переменную !
```

# 2 Шаг.
 * Сначала проверяется, что переменная bitmap, которая хранит битовую карту выбранного изображения, не равна null. Это означает, что пользователь выбрал изображение из галереи и оно загружено в память.
* Если bitmap равна null, то это означает, что пользователь не выбрал изображение. В этом случае выводится всплывающее сообщение (toast) с текстом “Изображение не выбрано!” и длительностью Toast.LENGTH_SHORT (короткая). Затем происходит выход из обработчика события с помощью оператора return@setOnClickListener, который указывает, что возврат происходит из лямбда-выражения, переданного в метод setOnClickListener. Это необходимо, чтобы не выполнять дальнейший код обработчика события, который предназначен для работы с изображением.
___
```kotlin
        predict_btn.setOnClickListener {

            if (bitmap == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Изображение не выбрано!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
```
