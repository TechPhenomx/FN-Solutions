package com.example.fnsolutions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;


public class VerifyUsernameAndPasswordFragment extends Fragment {
//    initializing firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputEditText verifyEmail, verifyPassword;
    MaterialButton verifyButton;
    String userID;


    public VerifyUsernameAndPasswordFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VerifyUsernameAndPasswordFragment newInstance(FirebaseUser user) {
        VerifyUsernameAndPasswordFragment fragment = new VerifyUsernameAndPasswordFragment();
        Bundle args = new Bundle();
        args.putString("userID", user.getUid());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_username_and_password, container, false);
        verifyEmail = view.findViewById(R.id.verifyEmail);
        verifyPassword = view.findViewById(R.id.verifyPassword);
        verifyButton = view.findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUserNameAndPassword();
            }
        });

        return view;
    }

    private void verifyUserNameAndPassword(){
        userID = getArguments().getString("userID");
        Log.d("m,dnfm,nf,.snf,.snf", "DocumentSnapshot data: " + userID);
        DocumentReference docRef = db.collection("user").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("m,dnfm,nf,.snf,.snf", "DocumentSnapshot data: " + document.getData());
                        String email = document.getString("email");
                        String password = document.getString("password");
                        if(verifyEmail.getText().toString().trim().equals(email) && verifyPassword.getText().toString().equals(password)){
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                            linkUsernameAndPassword(credential);
                        }else{
                            commonUtilities.showMessage(getActivity(), "wrong", "");
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }


        });
    }

    private void linkUsernameAndPassword(AuthCredential credential) {
        Toast.makeText(getContext(), "dfdsf", Toast.LENGTH_SHORT).show();
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
//                            updateUI(user);
                            Toast.makeText(getContext(), "afadf", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.w(TAG, "linkWithCredential:failure", task.getException());
//                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            Toast.makeText(getContext(), "cvx", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}