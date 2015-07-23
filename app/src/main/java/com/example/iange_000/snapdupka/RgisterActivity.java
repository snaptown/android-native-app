package com.example.iange_000.snapdupka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class RgisterActivity extends Activity {

    EditText usernameText ;
    EditText passwordText ;
    String username;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgister);
        ImageView iv = (ImageView)findViewById(R.id.logoView); iv.setImageResource(R.drawable.log);

        Button logInButton = (Button) findViewById(R.id.registerButton);
        usernameText = (EditText) findViewById(R.id.editText);
        passwordText = (EditText) findViewById(R.id.editText2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    public void register(View view){

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if(!username.isEmpty()&&!password.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {


                    Log.i("From Login", "Thread started");
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("https://b20db94c.ngrok.io/snaptown/register");
                    RgisterActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(RgisterActivity.this, "Registering....", Toast.LENGTH_SHORT).show();
                        }
                    });

                    try {

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("username", username));
                        nameValuePairs.add(new BasicNameValuePair("password", password));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        Log.i("From Login", "request sent");

                        int statuscode = response.getStatusLine().getStatusCode();
                        if (statuscode == 200) {
                            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.putString("username", usernameText.getText().toString());
                            editor.putString("password", passwordText.getText().toString());
                            editor.apply();
                            Log.i("From Login", "Accepted");
                            try {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RgisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Log.i("From Login", "Unauthorised");
                            RgisterActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(RgisterActivity.this, "Username has been taken! Try with another one", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        httpclient.getConnectionManager().shutdown();
                    }
                }
            }).start();

        }
        else{
            Toast.makeText(RgisterActivity.this, "Username or password is empty!", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



  /*  Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {


            Log.i("From Login", "Thread started");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://b20db94c.ngrok.io/snaptown/login");


            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password",password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                Log.i("From Login", "request sent");

                int statuscode =response.getStatusLine().getStatusCode();
                if(statuscode == 202){
                    Log.i("From Login", "Accepted");


                }
                else{
                    Log.i("From Login", "Unauthorised");

                }





            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        }
    });
    */
}

