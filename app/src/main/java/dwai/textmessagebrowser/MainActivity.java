package dwai.textmessagebrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import java.util.HashMap;

import java.util.zip.GZIPInputStream;


public class MainActivity extends Activity {
    private static final String PHONE_NUMBER = "8443xxxxx";
    private HashMap<Integer, String> htmlCode = new HashMap<Integer, String>();
    private final String ROOT_HTML_FILE_NAME = "root.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sets the font for the whole layout.
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/proxima.ttf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        MainActivity.setAppFont(mContainer, mFont, true);

        //the following is an example web page stored locally.
//        ((WebView) findViewById(R.id.theWebView)).loadUrl("file:///android_asset/stuff/filename.html");

//        File file = new File("android_asset/newtab.html");
//        ((WebView) findViewById(R.id.theWebView)).loadUrl("file:///" + file);
        //Need to text twilio

        final EditText urlEditText =  (EditText) findViewById(R.id.urlEditText);
        urlEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        textToTwilio(urlEditText.getText().toString());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });




       //Need to render the data


    }
    private void textToTwilio(String whatToSend) throws Exception{
        String phone_Num = PHONE_NUMBER;
        String send_msg = whatToSend;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone_Num, null, send_msg, null, null);

    }




    //The data is compressed using the GZIP compression algorithm
    //This method decompresses the data for use in the web browser.
    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    private void saveFile(String name, String content) {
        String filename = name;
        String string = content;
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Overrides the usual function of back pressed so that it doesn't close the app but rather goes back a page with the web view.
    @Override
    public void onBackPressed() {
        WebView rootWebView = ((WebView) findViewById(R.id.theWebView));
        if (rootWebView.canGoBack()) {
            rootWebView.goBack();
        }
    }

    //Recursively sets the appFont because Android is weird like that.
    public static final void setAppFont(ViewGroup mContainer, Typeface mFont, boolean reflect) {

        if (mContainer == null || mFont == null) return;

        final int mCount = mContainer.getChildCount();

        // Loop through all of the children.
        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView) {
                ((TextView) mChild).setTypeface(mFont);
            } else if (mChild instanceof ViewGroup) {
                setAppFont((ViewGroup) mChild, mFont, true);
            } else if (reflect) {
                try {
                    Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
                    mSetTypeface.invoke(mChild, mFont);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
