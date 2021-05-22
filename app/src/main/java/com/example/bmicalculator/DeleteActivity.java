package com.example.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class DeleteActivity extends DbActivity {

    protected TextView text_Bmi, text_Weight, text_Height;
     protected Button btnDelete;
     protected String ID;
     protected void Back(){
         finishActivity(200);
         Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
         startActivity(intent);
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        text_Weight=findViewById(R.id.text_Weight);
        text_Height=findViewById(R.id.text_Height);
        text_Bmi=findViewById(R.id.text_Bmi);
        btnDelete=findViewById(R.id.btnDelete);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            ID=b.getString("ID");
            text_Weight.setText(b.getString("Weight"));
            text_Height.setText(b.getString("Height"));
            text_Bmi.setText(b.getString("Bmi"));
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    execSQL("DELETE FROM BMIRESULTS WHERE ID = ?",
                            new Object[]{ID},
                            new OnQuerySuccessOrError() {
                                @Override
                                public void OnSuccess() {
                                    Toast.makeText(DeleteActivity.this, "Delete successfully", Toast.LENGTH_LONG).show();
                                    Back();

                                }
                                @Override
                                public void OnError(String error) {
                                    Toast.makeText(DeleteActivity.this, error, Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}