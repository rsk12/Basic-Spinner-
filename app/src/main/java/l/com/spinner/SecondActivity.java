package l.com.spinner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 2/6/2018.
 */

public class SecondActivity extends Activity {

    String name;

    InputStream is=null;
    String result=null;
    String line =null;
    int code;


    DateFormat formatDateTime = DateFormat.getDateTimeInstance();
    Calendar date = Calendar.getInstance();

    TextView myDate2;


    Button submit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner_result);
        final EditText editText = findViewById(R.id.txt_bundle);
        Bundle bundle = getIntent().getExtras();
        String data = bundle.get("data").toString();
        editText.setText(data);

        //textView
        myDate2 = findViewById(R.id.myDate2);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        //submitButton for inserting doctor value
        submit = findViewById(R.id.btnAddTime);

        button1.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                updateDate();
            }
        });


        button2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                updateTime();
            }
        });

        updateTextLabel();




        //insert button for inserting the values
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = editText.getText().toString();

                insert();
            }
        });



    }

    public void insert()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


        nameValuePairs.add(new BasicNameValuePair("name",name));

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.1.27/server_side_php/doctor-request.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }





    private void updateDate() {
        new DatePickerDialog(this, d, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime() {
        new TimePickerDialog(this, t, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            date.set(Calendar.YEAR, i);
            date.set(Calendar.MONTH, i1);
            date.set(Calendar.DAY_OF_MONTH, i2);

            myDate2.setText(formatDateTime.format(date.getTime()));

        }
    };
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            date.set(Calendar.HOUR_OF_DAY, i);
            date.set(Calendar.MINUTE, i1);
            myDate2.setText(formatDateTime.format(date.getTime()));

        }
    };

    private void updateTextLabel() {
        myDate2.setText(formatDateTime.format(date.getTime()));

    }


}
