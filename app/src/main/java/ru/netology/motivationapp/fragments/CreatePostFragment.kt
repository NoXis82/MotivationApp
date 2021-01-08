package ru.netology.motivationapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.motivationapp.R
import ru.netology.motivationapp.databinding.FragmentCreatePostBinding
import ru.netology.motivationapp.utils.AndroidUtils
import ru.netology.motivationapp.utils.StringArg
import ru.netology.motivationapp.viewmodel.PostViewModel
import java.io.*

class CreatePostFragment : Fragment() {

    private val REQUEST_CODE = 100
    private lateinit var binding: FragmentCreatePostBinding
    private var filename: String = ""

    companion object {
        var Bundle.author: String? by StringArg
        var Bundle.content: String? by StringArg
        var Bundle.pictureName: String? by StringArg
    }
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        arguments?.author.let(binding.editQuery::setText)
        arguments?.content.let(binding.editContent::setText)
        if (arguments?.pictureName?.isNotEmpty() == true) {
            filename = arguments?.pictureName.toString()
        }
        if (arguments?.pictureName?.isNotEmpty() == true) {
                binding.frameImage.visibility = View.VISIBLE
                try {
                    val fileDir = File(binding.root.context?.filesDir, "Images")
                    fileDir.mkdir()
                    val file = File(fileDir, arguments?.pictureName.toString())
                    val bitmap = BitmapFactory.decodeFile(file.toString())
                    binding.viewLoadImage.setImageBitmap(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                binding.frameImage.visibility = View.GONE
            }

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
        binding.btnDeleteImage.setOnClickListener {
                binding.viewLoadImage.setImageDrawable(null)
                binding.frameImage.visibility = View.GONE
            //удаление файла из Images
                filename = ""
        }

        binding.btnSave.setOnClickListener {
            val drawable = binding.viewLoadImage.drawable
            if (drawable != null && filename.isNotEmpty()) {
                val bitmap = drawable.toBitmap()
                saveImageToExternal(bitmap)
            }

            viewModel.changeContent(
                binding.editQuery.text.toString(),
                binding.editContent.text.toString(),
                filename
            )
            viewModel.savePost()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()

        }
        return binding.root
    }

    private fun saveImageToExternal(bitmap: Bitmap) {
        val fileDir = File(context?.filesDir, "Images")
        fileDir.mkdir()
        val file = File(fileDir, filename)
        if (file.exists()) {
            file.delete()
        }
        try {
            val streamOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamOut)
            streamOut.flush()
            streamOut.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error save image!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            binding.viewLoadImage.setImageURI(data?.data)
            binding.frameImage.visibility = View.VISIBLE
            filename = File(data?.data?.path.toString()).name + ".jpg"
       }
    }


}