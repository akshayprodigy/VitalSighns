package com.augmentingtechs.vitalsigns;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class History extends AppCompatActivity {

    private Constants constants;

    private GridView gridView;
    private ImageButton backButton;
    private TextView test;
    private RelativeLayout noDocuments;

    private ArrayList<String> historyType;
    private ArrayList<String> historyTime;
    private ArrayList<String> historyResult;

    private JSONArray mainArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyType = new ArrayList<>();
        historyTime = new ArrayList<>();
        historyResult = new ArrayList<>();

        gridView = findViewById(R.id.history_grid);
        backButton = findViewById(R.id.history_back);
        test = findViewById(R.id.Test);
        noDocuments = findViewById(R.id.history_no_documents);

        constants = new Constants();

        initListeners();

        try {
            String jsonString = readData(History.this);
            if (!jsonString.equals("")) {
                mainArray = new JSONArray(jsonString);
                new GetItems().execute();
            } else {
                gridView.setVisibility(View.GONE);
                noDocuments.setVisibility(View.VISIBLE);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void initListeners() {
        backButton.setOnClickListener(v -> finish());

        test.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), Primary.class);
            startActivity(i);
            finish();
        });
    }

    private String readData(Context context) {
        String returnDATA = "";

        try {
            InputStream stream = context.openFileInput(constants.getContentNAME());
            if (stream != null) {
                InputStreamReader reader =
                        new InputStreamReader(stream);
                BufferedReader bufferedReader =
                        new BufferedReader(reader);
                String receiveString;
                StringBuilder builder =
                        new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    builder.append(receiveString);
                }

                stream.close();
                returnDATA = builder.toString();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        return returnDATA;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetItems extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < mainArray.length(); i++) {
                    JSONObject mainObject = mainArray.getJSONObject(i);
                    String type = mainObject.getString("type");
                    String time = mainObject.getString("time");
                    String result = mainObject.getString("result");

                    historyType.add(i, type);
                    historyTime.add(i, time);
                    historyResult.add(i, result);
                }
            } catch (Exception error) {
                error.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            gridView.setAdapter(new JSONAdapter(History.this));

            gridView.setVisibility(View.VISIBLE);
            noDocuments.setVisibility(View.GONE);
        }
    }

    public class JSONAdapter extends BaseAdapter {
        Context context;

        public JSONAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return historyResult.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View ConvertView, ViewGroup parent) {

            View convertView = ConvertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.content_history, parent, false);

                viewHolder.type = convertView.findViewById(R.id.history_content_type);
                viewHolder.time = convertView.findViewById(R.id.history_content_time);
                viewHolder.result = convertView.findViewById(R.id.history_content_result);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String type = historyType.get(position);
            String time = historyTime.get(position);
            String result = historyResult.get(position);

            viewHolder.type.setText(type);
            viewHolder.time.setText(time);
            viewHolder.result.setText(result);

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView type;
        TextView time;
        TextView result;
    }
}
