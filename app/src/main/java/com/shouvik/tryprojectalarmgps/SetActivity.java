package com.shouvik.tryprojectalarmgps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shouvik on 06-Aug-16.
 */
public class SetActivity extends AppCompatActivity{
    private TextView text1;
    private EditText edit1;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_number);

        text1= (TextView) findViewById(R.id.textView);
        edit1= (EditText) findViewById(R.id.editText);
        button1= (Button) findViewById(R.id.button);

    }

    public void save(View view)
    {
        SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String phone=edit1.getText().toString();
        editor.putString("setPhoneNumber",edit1.getText().toString());

        editor.commit();

        if(phone.length()==0)
            Toast.makeText(getApplicationContext(),"No Phone Number was Set This Time",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Phone Number was Set to "+phone,Toast.LENGTH_LONG).show();


        /*Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);*/


        //To clear the stack of activities
        /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        startActivity(mainIntent);*/



        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
