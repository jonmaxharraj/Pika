package com.example.jonii.pika;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.manateeworks.CameraManager.TAG;


public class InfoFragment extends Fragment {

    ImageView frontImage;
    ImageView backImage;

    private ArrayList<ListaObjektet> arrayList;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    SharedPreferences sp;
    int position;
    int frontOrBack = 0;
    Uri mUriPhotoTaken;
    File file;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        position = getActivity().getIntent().getExtras().getInt("position");
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        arrayList = new ArrayList<>();
        loadArray(getContext());
        frontImage = view.findViewById(R.id.frontImage);
        backImage = view.findViewById(R.id.backImage);
        Uri logoPathFront = Uri.parse(arrayList.get(position).getFrontImage());
        Uri logoPathBack = Uri.parse(arrayList.get(position).getBackImage());
//        if (!arrayList.get(position).getBackImage().equals("")) {
            setFrontImage(logoPathFront);
            setBackImage(logoPathBack);
//        }

        frontImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontOrBack = 1;
                showAlertDialog();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontOrBack = 2;
                showAlertDialog();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    Uri selectedImageUri;
                    if (data == null || data.getData() == null) {
                        selectedImageUri = mUriPhotoTaken;
                    } else {
                        selectedImageUri = data.getData();
                    }

                    selectedImagePath = selectedImageUri.toString();
                    loadArray(getContext());
                    if (frontOrBack == 1) {
                        arrayList.get(position).setFrontImage(selectedImagePath);
                        setFrontImage(selectedImageUri);
                        saveArray();
                    } else if (frontOrBack == 2) {
                        arrayList.get(position).setBackImage(selectedImagePath);
                        setBackImage(selectedImageUri);
                        saveArray();
                    }
                case SELECT_PICTURE:
                    Uri selectedImageUri2;
                    if (data == null || data.getData() == null) {
                        selectedImageUri2 = mUriPhotoTaken;
                    } else {
                        selectedImageUri2 = data.getData();
                    }
                    selectedImagePath = selectedImageUri2.toString();
                    loadArray(getContext());
                    if (frontOrBack == 1) {
                        arrayList.get(position).setFrontImage(selectedImagePath);
                        setFrontImage(selectedImageUri2);
                        saveArray();
                    } else if (frontOrBack == 2) {
                        arrayList.get(position).setBackImage(selectedImagePath);
                        setBackImage(selectedImageUri2);
                        saveArray();
                    }
            }
        }
    }

    // save the list in the SharedPreferences so when you close the application you dont lose the items in the list
    public boolean saveArray() {
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("Status_size", arrayList.size());


        for (int i = 0; i < arrayList.size(); i++) {
            mEdit1.remove("Status_" + i);
            try {
                JSONObject cacheJSON = new JSONObject();

                cacheJSON.put("emri", arrayList.get(i).emri);
                cacheJSON.put("kodi", arrayList.get(i).kodi);
                cacheJSON.put("lloji", arrayList.get(i).lloji);
                cacheJSON.put("frontImage", arrayList.get(i).frontImage);
                cacheJSON.put("backImage", arrayList.get(i).backImage);

                mEdit1.putString("Status_" + i, cacheJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mEdit1.commit();

    }

    // load the list with items whe you open the application
    public void loadArray(Context mContext) {
        arrayList.clear();
        int size = sp.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            try {
                JSONObject cacheJSON = new JSONObject(sp.getString("Status_" + i, null));
                ListaObjektet object = new ListaObjektet();
                object.emri = cacheJSON.getString("emri");
                object.kodi = cacheJSON.getString("kodi");
                object.lloji = cacheJSON.getString("lloji");
                object.frontImage = cacheJSON.getString("frontImage");
                object.backImage = cacheJSON.getString("backImage");

                arrayList.add(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.choose_image_dialog, null);
        Button takeButton = alertLayout.findViewById(R.id.takeButton);
        Button chooseButton = alertLayout.findViewById(R.id.chooseButton);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Choose a pic");
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getActivity().getExternalCacheDir(),
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                mUriPhotoTaken = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                dialog.dismiss();
            }

        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    private void setFrontImage(Uri logoPathFront) {
        Glide.with(getContext())
                .load(logoPathFront)
                .centerCrop()
                .fitCenter()
                .into(frontImage);
    }

    private void setBackImage(Uri logoPathBack) {
        Glide.with(getContext())
                .load(logoPathBack)
                .centerCrop()
                .fitCenter()
                .into(backImage);
    }
}
