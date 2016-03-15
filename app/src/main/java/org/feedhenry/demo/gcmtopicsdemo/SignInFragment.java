package org.feedhenry.demo.gcmtopicsdemo;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;


public class SignInFragment extends Fragment {

    @Inject
    GoogleApiClient googleApiClient;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((GCMTopicDemoApplication) context.getApplicationContext()).getObjectGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleApiClient apiClient = googleApiClient;
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                getActivity().startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN);
            }
        });
        return view;
    }

}
