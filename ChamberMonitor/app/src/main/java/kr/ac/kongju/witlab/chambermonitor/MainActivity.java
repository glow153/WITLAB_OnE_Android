package kr.ac.kongju.witlab.chambermonitor;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private BackgroundTask task = null;
    private JakePushNotification jpn = null;
    private String result; // result of getting from db

    public static int TEMP_UPPER_LIMIT = 30;
    public static int TEMP_LOWER_LIMIT = 10;

    private TextView tvDatetime;
    private TextView tvTemp;
    private TextView tvHumi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bind();

        task = new BackgroundTask(MainActivity.this);
        this.jpn = new JakePushNotification(MainActivity.this);
        task.execute();
    }

    private void bind() {
        tvDatetime = findViewById(R.id.tvDatetime);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumi = findViewById(R.id.tvHumi);
    }

    @Override
    protected void onDestroy() {
        task.cancel(true);
        task = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause()");
//        task.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity", "onResume()");
        super.onResume();
        if(task == null)
            task = new BackgroundTask(MainActivity.this);
        task.resume();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private Context mainContext;
        private RequestQueue queue;

        private boolean terminated = false;
        private boolean paused = false;

        private static final String url = "http://210.102.142.14/select.php";


        BackgroundTask(Context mainContext) {
            this.mainContext = mainContext;
        }

        @Override
        protected void onPreExecute() {
            queue = Volley.newRequestQueue(mainContext);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            String[] s = result.split(",");
            Log.d("onProgressUpdate",
                 "datetime:" + s[0] + ", temp:" + s[1] + ", humi:" + s[2]);

            double temp = Double.parseDouble(s[1].trim());
            double humi = Double.parseDouble(s[2].trim());

            if(temp < TEMP_LOWER_LIMIT || temp > TEMP_UPPER_LIMIT) {
                jpn.sendNotification(JakePushNotification.CHAMBER_TEMPHUMI);
            }

            tvDatetime.setText(s[0].trim());
            tvTemp.setText(temp + " ℃");
            tvHumi.setText(humi + " %");
        }

        @Override
        protected void onCancelled() {
            terminated = true;
            super.onCancelled();
        }

        void pause() {
            paused = true;
        }

        void resume() {
            paused = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(!terminated) {
                if (!paused) {
                    sendGetMethod(); // select and store at var 'String result'
                    try {
                        Thread.sleep(2900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


    }
}
