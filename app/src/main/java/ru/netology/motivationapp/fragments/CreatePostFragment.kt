package ru.netology.motivationapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import ru.netology.motivationapp.R
import ru.netology.motivationapp.databinding.FragmentCreatePostBinding
import ru.netology.motivationapp.viewmodel.PostViewModel

class CreatePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCreatePostBinding.inflate(layoutInflater)


        binding.btnSave.setOnClickListener {
            Toast.makeText(
                context,
                binding.editContent.text,
                Toast.LENGTH_LONG
            ).show()
        }

        return binding.root
    }


}