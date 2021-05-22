package com.example.bmicalculator;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends DbActivity {
    private BottomNavigationView bottomNavigationView;
    protected ImageView btn_history, btn_refresh;
    protected EditText inputWeight, inputHeight;
    protected Button btnCalc;
    protected TextView textResult;
    protected ListView list_bmi_results;

    public void SelectAndFillListView(){
        try {
            final ArrayList<String> history = new ArrayList<>();
            SelectSQL("SELECT * FROM BMIRESULTS ORDER BY ID",
                    null,
                    new OnSelectSuccess() {
                        @Override
                        public void OnElementSelected(String ID, String Weight, String Height, String Bmi) {
                            history.add(ID+"\t"+"  Weight: "+Weight+"\t"+"  Height: "+Height+"\t"+Bmi+"\n");
                        }
                    }
            );
            list_bmi_results.clearChoices();
            ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.activity_bmi_result,
                    R.id.textView,
                    history
                    );
            list_bmi_results.setAdapter(arrayAdapter);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    @CallSuper
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        SelectAndFillListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottomNav);
        inputWeight=findViewById(R.id.inputWeight);
        inputHeight=findViewById(R.id.inputHeight);
        btnCalc=findViewById(R.id.btnCalc);
        btn_history=findViewById(R.id.btn_history);
        btn_refresh=findViewById(R.id.btn_refresh);
        textResult=findViewById(R.id.textResult);
        list_bmi_results=findViewById(R.id.list_bmi_results);
        list_bmi_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selected = ((TextView)(view.findViewById(R.id.textView))).getText().toString();
                String[] elements = selected.split("\t");
                String ID = elements[0];
                String Weight = elements[1];
                String Height = elements[2];
                String Bmi = elements[3].trim();
                Intent intent = new Intent(MainActivity.this,
                        DeleteActivity.class
                );
                Bundle b = new Bundle();
                b.putString("ID", ID);
                b.putString("Weight", Weight);
                b.putString("Height", Height);
                b.putString("Bmi", Bmi);
                intent.putExtras(b);
                startActivityForResult(intent, 200, b);
            }
        });


        initDB();
        SelectAndFillListView();

        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double weight = Double.parseDouble(inputWeight.getText().toString());
                    double height = Double.parseDouble(inputHeight.getText().toString());

                    if ((height<100 || height>220)&&(weight<40||weight>160)){
                        textResult.setText("Невалидни стойности за тегло и височина");
                    }

                    else if (weight<40||weight>160){
                        textResult.setText("Невалидно тегло");
                    }
                    else if (height<100 || height>220) {
                        textResult.setText("Невалидна височина");
                    }
                    else{
                        double h = height/100;
                        double calculation = weight/(h*h);
                        DecimalFormat df = new DecimalFormat("####0.0");
                        textResult.setText("BMI: "+ df.format(calculation));

                        try {
                            execSQL("INSERT INTO BMIRESULTS(Weight, Height, Bmi)" +
                                            "VALUES(?, ?, ?)",
                                    new Object[]{
                                            inputWeight.getText().toString(),
                                            inputHeight.getText().toString(),
                                            textResult.getText().toString()
                                    },
                                    new OnQuerySuccessOrError() {
                                        @Override
                                        public void OnSuccess() {
                                            Toast.makeText(MainActivity.this,
                                                    "Added to history",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void OnError(String error) {
                                            Toast.makeText(MainActivity.this,
                                                    error,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                            );
                            SelectAndFillListView();
                        }
                        catch (SQLException e){
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_bmi_results.getVisibility()==View.VISIBLE){
                    list_bmi_results.setVisibility(View.GONE);
                }
                else {
                    list_bmi_results.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputWeight.setText("");
                inputHeight.setText("");
                textResult.setText("");
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.message:
                        startActivity(new Intent(getApplicationContext()
                                ,SMSActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}