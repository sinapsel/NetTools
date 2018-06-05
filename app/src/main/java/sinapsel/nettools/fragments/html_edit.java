

package sinapsel.nettools.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import sinapsel.nettools.R;

public class html_edit extends AppCompatActivity {
    String html;
    EditText heditor;
    private static final int RES = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_edit);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        html = intent.getStringExtra("html");
        heditor = (EditText)findViewById(R.id.editHTMLText);
        heditor.setText(html);
        Button back = (Button)findViewById(R.id.backb);
        Button fpick = (Button)findViewById(R.id.filepicker);

        fpick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, RES);
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.putExtra("html", heditor.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
    private String readFile(String FILENAME) {
        StringBuilder sb = new StringBuilder();
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d("FILEREADER", "SD-карта не доступна: " + Environment.getExternalStorageState());
            return "";
        }
        File sdPath = Environment.getExternalStorageDirectory();
        File sdFile = new File(sdPath, FILENAME.replace(sdPath.getAbsolutePath(), ""));

        try {
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RES:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    heditor.setText(readFile(FilePath));
                }
                break;
        }
    }

}
