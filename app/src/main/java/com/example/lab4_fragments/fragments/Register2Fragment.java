package com.example.lab4_fragments.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.example.lab4_fragments.R;
import com.example.lab4_fragments.database.AppDatabase;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.User;
import com.example.lab4_fragments.view_models.SharedViewModel;

public class Register2Fragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_register2, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        db = AppDatabaseSingleton.getInstance(getContext());

        emailEditText = rootView.findViewById(R.id.emailEditText);
        passwordEditText = rootView.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = rootView.findViewById(R.id.confirmPasswordEditText);

        rootView.findViewById(R.id.btnBack).setOnClickListener(v -> goBackToRegister1());

        rootView.findViewById(R.id.btnFinish).setOnClickListener(v -> {
            if (validatePasswords()) {
                sharedViewModel.setEmail(emailEditText.getText().toString());
                sharedViewModel.setPassword(passwordEditText.getText().toString());

                registerUser();
                Toast.makeText(getActivity(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                goBackToStart();
            }
        });

        return rootView;
    }

    private void goBackToRegister1() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void goBackToStart() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void registerUser() {
        User user = new User();
        user.firstName = sharedViewModel.getFirstName().getValue();
        user.lastName = sharedViewModel.getLastName().getValue();
        user.dni = sharedViewModel.getDni().getValue();
        user.phone = sharedViewModel.getPhone().getValue();
        user.email = sharedViewModel.getEmail().getValue();
        user.password = sharedViewModel.getPassword().getValue();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.userDao().insertUser(user);
            Log.d("RegisterData", "Datos guardados en la base de datos");
        });
    }

    private boolean validatePasswords() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Las contraseñas no pueden estar vacías", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
