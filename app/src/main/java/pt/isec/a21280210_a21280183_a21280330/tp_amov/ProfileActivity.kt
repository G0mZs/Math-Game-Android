package pt.isec.a21280210_a21280183_a21280330.tp_amov

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.EditText
import android.widget.Toast
import pt.isec.a21280210_a21280183_a21280330.tp_amov.database.LocalReader
import pt.isec.a21280210_a21280183_a21280330.tp_amov.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val colorDrawable = ColorDrawable(Color.parseColor("#FF018786"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }

    override fun onRestart() {
        super.onRestart()
        binding.name.text = LocalReader.getName()

        if (LocalReader.hasAvatar()) {
            val bitmap = BitmapFactory.decodeFile(LocalReader.getAvatarPath())
            binding.imageViewAvatar.setImageBitmap(bitmap)
        }
    }

    fun onClickChangeName(view: View) {
        val textBox = EditText(this).apply {
            maxLines = 1
            filters = arrayOf(object : InputFilter {
                override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                    if (source?.none { it.isLetterOrDigit()} == true)
                        return ""
                    return null
                }
            })
        }

        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.change_name))
            .setMessage(resources.getString(R.string.change_name_description))
            .setView(textBox)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                val newName = textBox.text

                if (newName.isBlank()) {
                    Toast.makeText(this, R.string.change_name_error, Toast.LENGTH_LONG).show()
                } else {
                    LocalReader.setName(newName.toString())
                    onRestart()
                }
            }
            .show()
    }
    fun onClickNewAvatar(view: View) {
        val intent = Intent(this, ImageCaptureActivity::class.java)
        startActivity(intent)
    }

}