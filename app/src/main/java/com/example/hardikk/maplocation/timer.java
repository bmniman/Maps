package com.example.hardikk.maplocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class timer extends AppCompatActivity {
 int count=1;
 TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        textView = (TextView) findViewById(R.id.textView);
        run();
    }
        public void run()
        {
            Thread t = new Thread() {


                @Override
                public void run() {

                    while (!isInterrupted()) {

                        try {
                            Thread.sleep(1000);  //1000ms = 1 sec

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    count++;
                                    textView.setText(String.valueOf(count));
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            t.start();
        }
    }

