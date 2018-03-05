package simapps.nettools.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import simapps.nettools.MenuActivity;
import simapps.nettools.R;

public class html_edit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        String html = intent.getStringExtra("html");
        EditText heditor = (EditText)findViewById(R.id.editHTMLText);
        heditor.setText(html);
        Button back = (Button)findViewById(R.id.backb);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(, MenuActivity.class);
                intent.putExtra("html", html);
                startActivity(intent);
            }
        });
    }

}
