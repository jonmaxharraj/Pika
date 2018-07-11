package com.example.jonii.pika;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.jonii.pika.R.id.scun;

public class Lista extends AppCompatActivity {

    private EditText editTxt;
    private ListView list;
    private CustomAdapter adapter;
    private ArrayList<ListaObjektet> arrayList;
    SharedPreferences sp;
    private String resultType;
    private String codi;
    private String resultContent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_SCANNER = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Fresco.initialize(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editTxt = (EditText) findViewById(R.id.edittext);
        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<ListaObjektet>();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setIcon(R.mipmap.icona_round);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        adapter = new CustomAdapter(this, arrayList, new CustomAdapter.AdapterListener() {
            @Override
            public void onDeleteClicked(final int position) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Lista.this);
                adb.setMessage("Jeni të sigurt që të fshihni objektin " + arrayList.get(position).emri);
                adb.setNegativeButton("Anulo", null);
                adb.setPositiveButton("Vazhdo", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        arrayList.remove(position);
                        adapter.notifyDataSetChanged();
                        saveArray();
                    }
                });
                adb.show();
            }

            @Override
            public void onImgCodeSelected(int position) {
                SharedPreferences.Editor mEdit1 = sp.edit();
                mEdit1.putInt("barcodePosition", position);
                mEdit1.apply();
                new IntentIntegrator(Lista.this).initiateScan();
            }

            @Override
            public void onEditClicked(int position) {
                onEditClick(position);
            }
        });

        list.setAdapter(adapter);


        loadArray(this);
        adapter.notifyDataSetChanged();


        // eduttxt is the place from where you add the emri for the items and is add to the list
        editTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        Lista.this.editTxt.getWindowToken(), 0);
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    ListaObjektet object = new ListaObjektet();
                    object.emri = editTxt.getText().toString();

                    arrayList.add(object);
                    saveArray();
                    adapter.notifyDataSetChanged();
                    editTxt.setText("");
                    editTxt.clearFocus();


                }
                return false;
            }

        });

        // here is the click on the list row and show you the information for the item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Lista.this, BarkodiProfil.class);

                intent.putExtra("kodi", arrayList.get(position).kodi);
                intent.putExtra("format", arrayList.get(position).lloji);
                intent.putExtra("emri", arrayList.get(position).emri);
                intent.putExtra("position", position);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.barkodmeny, menu);

        return super.onCreateOptionsMenu(menu);
    }


    // googltalk icon in the acction bar and you can use your voice to add item in the list
    // barkodmeny icon is open the scanner and you can add items in the list by scanning the barcode

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case scun:
//                Intent intent = new Intent();
//                intent.setAction("com.google.zxing.client.android.SCAN");
//                startActivityForResult(intent, REQ_CODE_SCANNER);
                SharedPreferences.Editor mEdit1 = sp.edit();
                mEdit1.putInt("barcodePosition", 0);
                new IntentIntegrator(this).initiateScan();

                break;
            case R.id.google_talk:

                promptSpeechInput();

                break;
            default:
                return false;
        }
        return true;
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


    // this is the adapter
    private Context context;


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    // kodi for google talk
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // google talk kodi
        if(requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK && null != data) {
                    super.onActivityResult(requestCode, resultCode, data);

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTxt.setText(result.get(0));

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


                }


        } else {
            int barcodePosition = sp.getInt("barcodePosition", 0);
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (barcodePosition != 0) {
                    Log.d("positionExisting", barcodePosition + "");
                    String type = scanningResult.getFormatName();
                    String code = scanningResult.getContents();

                    ListaObjektet object = arrayList.get(barcodePosition);
                    object.lloji = type;
                    object.kodi = code;

                    saveArray();
                    adapter.notifyDataSetChanged();

                    //  callback from scanner and add new item in the list with code in the item
                } else {
                    if (scanningResult != null) {
                        Log.d("positionNew", barcodePosition + "");
                        String type = scanningResult.getFormatName();
                        String code = scanningResult.getContents();
                        ListaObjektet object = new ListaObjektet();
                        object.emri = editTxt.getText().toString();
                        object.kodi = code;
                        object.lloji = type;

                        arrayList.add(object);
                        saveArray();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Nuk ka te dhena te skanuara!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        }

    }

    private void onEditClick(final int position) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(Lista.this);
        adb.setMessage("Deshironi të ndërroni emrin e këtij objekti " + arrayList.get(position).emri);
        final EditText input = new EditText(Lista.this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        input.requestFocus();

        adb.setView(input, 100, 50, 100, 0);
        adb.setNegativeButton("Anulo", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });

        InputMethodManager immm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        adb.setPositiveButton("Vazhdo", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                ListaObjektet object = arrayList.get(position);
                object.emri = input.getText().toString();
                saveArray();
                adapter.notifyDataSetChanged();
                input.setText("");


                Toast.makeText(getApplicationContext(), "Emri u ndryshua", Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);


            }
        });
        adb.show();
    }

}
