package com.example.jonii.pika;

import java.util.EnumMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarkodiProfil extends AppCompatActivity {

private ImageView iv;
private TextView tv;
private TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barkodi);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // barcode data
        String barkodi_tdhanat = getIntent().getExtras().getString("kodi");
        String formati = getIntent().getExtras().getString("format");
        String emri = getIntent().getExtras().getString("emri");

        // barcode image
        Bitmap bitmapi = null;
        iv= (ImageView) findViewById(R.id.barcodi);

        try {
            if(formati.equals("EAN_13")) {
                bitmapi = gjeneroBitmap(barkodi_tdhanat, BarcodeFormat.EAN_13, 700, 400);
                iv.setImageBitmap(bitmapi);
            }else if (formati.equals("CODE_128"))
            {
                bitmapi = gjeneroBitmap(barkodi_tdhanat, BarcodeFormat.CODE_128, 700, 400);
                iv.setImageBitmap(bitmapi);
            }else if (formati.equals("UPC_E"))
            {
                bitmapi = gjeneroBitmap(barkodi_tdhanat, BarcodeFormat.UPC_E, 700, 400);
                iv.setImageBitmap(bitmapi);
            }
            else if (formati.equals("UPC_A"))
            {
                bitmapi = gjeneroBitmap(barkodi_tdhanat, BarcodeFormat.UPC_A, 600, 300);
                iv.setImageBitmap(bitmapi);
            }
            else if (formati.equals("EAN_8"))
            {
                bitmapi = gjeneroBitmap(barkodi_tdhanat, BarcodeFormat.EAN_8, 600, 300);
                iv.setImageBitmap(bitmapi);
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }


        //barcode text
        tv =  (TextView) findViewById(R.id.teksti);
        tv.setText(barkodi_tdhanat);

        tv1 =  (TextView) findViewById(R.id.emri);
        tv1.setText(emri);


    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(BarkodiProfil.this,Lista.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(setIntent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent setIntent = new Intent(BarkodiProfil.this,Lista.class);
                startActivity(setIntent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap gjeneroBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
