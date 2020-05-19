package com.example.hp.androidtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileFragment extends Fragment {

    String username;
    String profilPicUrl;
    TextView usernameTv;
    CircleImageView profilPicImageView;
    Button logoutButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FacebookSdk.sdkInitialize(getApplicationContext());
        usernameTv = (TextView) view.findViewById(R.id.nameTv);
        profilPicImageView = (CircleImageView) view.findViewById(R.id.profilPicImageView);
        logoutButton=view.findViewById(R.id.buttonLogout);

        MainActivity mainActivity = (MainActivity) getActivity();

        username = mainActivity.getUsername();
        profilPicUrl = mainActivity.getProfilPicUrl();

        Picasso.get().load(profilPicUrl).into(profilPicImageView);
        usernameTv.setText(mainActivity.getUsername());
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        Intent login = new Intent(getActivity(), LoginActivity.class);
                        startActivity(login);
            }
        });

        return view;
    }


}