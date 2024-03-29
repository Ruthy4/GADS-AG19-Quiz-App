package com.example.gadsag19educationquiz.ui.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.gadsag19educationquiz.R
import com.example.gadsag19educationquiz.ui.DashboardActivity
import com.example.gadsag19educationquiz.databinding.FragmentLoginBinding
import com.example.gadsag19educationquiz.util.Constants
import com.example.gadsag19educationquiz.util.Constants.TOKEN
import com.example.gadsag19educationquiz.util.SessionManager
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    private lateinit var sessionManager: SessionManager
    lateinit var pref: SharedPreferences
    lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = this.requireActivity().getSharedPreferences(Constants.TOKEN, Context.MODE_PRIVATE)
        sessionManager = SessionManager(pref)

        email = binding.loginFragmentEmailAddressEditText
        password = binding.loginFragmentPasswordEditText

        auth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                login()
            }else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
        binding.loginFragmentSignUpForFreeTextView.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun login() {
        val email = binding.loginFragmentEmailAddressEditText.text.toString()
        val password = binding.loginFragmentPasswordEditText.text.toString()

        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                sessionManager.saveToSharedPref(TOKEN, email)
                val intent = Intent(requireContext(), DashboardActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }.addOnFailureListener { exception ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}