package ru.netology.motivationapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import ru.netology.motivationapp.R
import ru.netology.motivationapp.databinding.FragmentCreatePostBinding
import ru.netology.motivationapp.utils.StringArg
import ru.netology.motivationapp.viewmodel.PostViewModel
import java.io.*

class CreatePostFragment : Fragment() {

    private val REQUEST_CODE = 100
    private lateinit var binding: FragmentCreatePostBinding

    companion object {
        var Bundle.idPost: String? by StringArg
        var Bundle.author: String? by StringArg
        var Bundle.published: String? by StringArg
        var Bundle.content: String? by StringArg
    }
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(layoutInflater)

        arguments?.author.let(binding.editQuery::setText)
        arguments?.content.let(binding.editContent::setText)




        binding.bottomAppBar.apply {
            setOnMenuItemClickListener {
                item ->
                when(item.itemId) {
                    R.id.app_bar_add_image -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, REQUEST_CODE)
                        true
                    }
                    else -> false
                }
            }
        }



        binding.btnSave.setOnClickListener {
            Toast.makeText(
                context,
                binding.editContent.text,
                Toast.LENGTH_SHORT
            ).show()
        }
      return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            binding.viewLoadImage.setImageURI(data?.data)
            val filename = File(data?.data?.path.toString()).name
            val drawable = binding.viewLoadImage.drawable
            val bitmap = drawable.toBitmap()
            val fileDir = File(context?.filesDir, "Images")
            fileDir.mkdir()
            val file = File(fileDir, "$filename.jpg")
            if (file.exists()) {
                file.delete()
            }
            try {
                val streamOut = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamOut)
                streamOut.flush()
                streamOut.close()
                Toast.makeText(context, "Save image successful", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error save image!", Toast.LENGTH_SHORT).show()
            }
       }


    }

}