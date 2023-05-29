package com.example.musicplayer.custom_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.musicplayer.LoginActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.SearchResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeaderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HeaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Header.
     */
    // TODO: Rename and change types and number of parameters
    public static HeaderFragment newInstance(String param1, String param2) {
        HeaderFragment fragment = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    SearchView sv;

    ImageButton user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);
        sv = view.findViewById(R.id.sv);
        user = view.findViewById(R.id.imgBtnUser);
        //homeFragment = new HomeFragment();

        user.setOnClickListener(v -> {
            View popupView = getLayoutInflater().inflate(R.layout.user_popup, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(popupView);
            // Set focusable
            popupWindow.setFocusable(true);

            // Set outside touchable
            popupWindow.setOutsideTouchable(true);

            // Show popup window
            popupWindow.showAsDropDown(v);

            ImageButton btnLogout = (ImageButton) popupView.findViewById(R.id.imgBtnLogout);
            TextView tvUser = (TextView) popupView.findViewById(R.id.tvUser);
            TextView tvEmail = (TextView) popupView.findViewById(R.id.tvEmail);


            SharedPreferences sharedPreferences = getContext().getSharedPreferences("my_preferences", getContext().MODE_PRIVATE);

            JSONObject userData;
            try {
                userData = new JSONObject(sharedPreferences.getString("data", ""));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String name = "",email = "";
            try {
                email = userData.getString("email");
                name = userData.getString("user_name");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            tvUser.setText(name);
            tvEmail.setText(email);

            btnLogout.setOnClickListener(v1 -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("isLogin");
                editor.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            });

        });

        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    sv.clearFocus();
                    Intent intent = new Intent(getContext(), SearchResultActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
            }
        });

        return view;
    }
}