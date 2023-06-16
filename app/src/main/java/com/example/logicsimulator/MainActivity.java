package com.example.logicsimulator;

import static com.example.logicsimulator.Propositions.LogicProposition.convertToCNF;
import static com.example.logicsimulator.Propositions.LogicProposition.removeRedundantParens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logicsimulator.Gates.ANDGate;
import com.example.logicsimulator.Gates.Gate;
import com.example.logicsimulator.Gates.GateView;
import com.example.logicsimulator.Gates.InputGate;
import com.example.logicsimulator.Gates.NOTGate;
import com.example.logicsimulator.Gates.ORGate;
import com.example.logicsimulator.Gates.OutputGate;
import com.example.logicsimulator.Propositions.LogicProposition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final String PROPOSITION_FILE_NAME = "SavedPropositions.txt";
    private static final String CIRCUIT_FILE_NAME = "SavedCircuits.txt";
    private static final String PROPOSITION_FILE_PATH = "/data/data/com.example.logicsimulator/files/SavedPropositions.txt";
    private static final String CIRCUIT_FILE_PATH = "/data/data/com.example.logicsimulator/files/SavedCircuits.txt";

    Button validate, save, fnc, fnd;
    Button newButton;
    Button ANDGate;
    EditText FormulaText;
    TextView Error;
    public String Formula;
    LogicProposition lp;
    private static Context context;
    private GateView gateView;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        gateView = new GateView(context);

        //setContentView(R.layout.main_page);
        //setContentView(gateView);
        setContentView(R.layout.circuit_designer_main);


    }

    private final TextWatcher propositionTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String propositionInput = FormulaText.getText().toString().trim();

            validate.setEnabled(!propositionInput.isEmpty());
            save.setEnabled(!propositionInput.isEmpty());
            fnc.setEnabled(!propositionInput.isEmpty());
            fnd.setEnabled(!propositionInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void showPopup(View view) {
        final String[] options = {"Save", "Load", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Option 1 selected
                                Toast.makeText(MainActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
                                saveCircuit();
                                break;
                            case 1:
                                // Option 2 selected
                                Toast.makeText(MainActivity.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                // Cancel selected
                                Toast.makeText(MainActivity.this, "Cancel selected", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void logicProposition(View view) {
        setContentView(R.layout.proposition_main);
        FormulaText = findViewById(R.id.FormulaText);
        validate = findViewById(R.id.Read);
        save = findViewById(R.id.saveProposition);
        fnc = findViewById(R.id.cnf);
        fnd = findViewById(R.id.dnf);

        validate.setEnabled(false);
        save.setEnabled(false);
        fnc.setEnabled(false);
        fnd.setEnabled(false);

        FormulaText.addTextChangedListener(propositionTextWatcher);
    }

    public void circuitDesigner(View view){
        setContentView(R.layout.circuit_designer_main);
    }

    public void backToMainPage(View view) {
        setContentView(R.layout.main_page);
    }

    public void backToPropositionPage(View view) {
        setContentView(R.layout.proposition_main);
        FormulaText = findViewById(R.id.FormulaText);
        validate = findViewById(R.id.Read);
        save = findViewById(R.id.saveProposition);
        fnc = findViewById(R.id.cnf);
        fnd = findViewById(R.id.dnf);

        validate.setEnabled(false);
        save.setEnabled(false);
        fnc.setEnabled(false);
        fnd.setEnabled(false);

        FormulaText.addTextChangedListener(propositionTextWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.proposition_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "item 1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "item 2", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item31:
                Toast.makeText(this, "item 31", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item32:
                Toast.makeText(this, "item 32", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item33:
                Toast.makeText(this, "item 33", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item4:
                Toast.makeText(this, "item 4", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Read(View view) {
        FormulaText = findViewById(R.id.FormulaText);
        FormulaText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        Error = findViewById(R.id.ErrorText);
        Formula = String.valueOf(FormulaText.getText());
        lp = new LogicProposition(Formula, Error);
        if (lp.errorCheck()) {
            Error.setVisibility(View.INVISIBLE);
        }
    }

    public void convertToCNFText(View view) {
        String CNF;
        CNF = convertToCNF(Formula);
        CNF = removeRedundantParens(CNF);
        System.out.println(CNF);
        FormulaText = findViewById(R.id.FormulaText);
        FormulaText.setText(CNF);
    }

    public void revert(View view) {
        FormulaText = findViewById(R.id.FormulaText);
        FormulaText.setText(Formula);
    }

    public void saveCircuit(){
        String circuit = gateView.returnCircuitRaw();
            gateView.buildLoadedCircuit(circuit);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(CIRCUIT_FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            fos.write(circuit.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }


    public void save(View view) {

        AlertDialog.Builder myAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        myAlertDialogBuilder.setTitle("Title");
        myAlertDialogBuilder.setMessage("Are you sure you want to save?");
        myAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String proposition = FormulaText.getText().toString();
                proposition += '\n';
                FileOutputStream fos = null;

                try {
                    fos = openFileOutput(PROPOSITION_FILE_NAME, MODE_PRIVATE | MODE_APPEND);
                    fos.write(proposition.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
        myAlertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        myAlertDialogBuilder.show();
    }

    public void load(View view) {
        setContentView(R.layout.proposition_load_list);
        try (FileReader fileReader = new FileReader(PROPOSITION_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                addButton(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addButton(String text) {

        LinearLayout linearLayout = findViewById(R.id.rootlayout);
        newButton = new Button(this);
        newButton.setText(text);
        newButton.setOnClickListener(view -> {

            String buttonText = ((Button) view).getText().toString();
            setContentView(R.layout.proposition_main);
            FormulaText = findViewById(R.id.FormulaText);
            FormulaText.setText(buttonText);

        });
        linearLayout.addView(newButton);
    }

    public void delete(View view) {
        setContentView(R.layout.proposition_load_list);
        try (FileReader fileReader = new FileReader(PROPOSITION_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            int count = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                addButtonForDelete(line, count);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addButtonForDelete(String text, int count) {

        LinearLayout linearLayout = findViewById(R.id.rootlayout);
        newButton = new Button(this);
        newButton.setText(text);
        newButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLine(count, PROPOSITION_FILE_PATH);
                Toast.makeText(MainActivity.this, "Proposition Deleted", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.proposition_main);
            }
        });
        linearLayout.addView(newButton);
    }

    private void deleteLine(int lineNumber, String filePath) {
        try {
            File inputFile = new File(filePath);
            File tempFile = File.createTempFile("temp", ".txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            int lineCounter = 0;

            while ((currentLine = reader.readLine()) != null) {
                lineCounter++;
                if (lineCounter == 1 && lineNumber == 0) {
                    // skip the first line if it is the line to be deleted
                    continue;
                }
                if (lineCounter != lineNumber + 1) {
                    writer.write(currentLine);
                    writer.newLine();
                }
            }

            writer.close();
            reader.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeGate(View view){
        gateView.deleteSelectedGate();
    }

    public void addANDGate(View view){
        System.out.println("added");
        Gate gate = new ANDGate(gateView.getContext());
        gateView.addGate(gate);
    }

    public void addORGate(View view){
        System.out.println("added");
        Gate gate = new ORGate(gateView.getContext());
        gateView.addGate(gate);
    }

    public void addNOTGate(View view){
        System.out.println("added");
        Gate gate = new NOTGate(gateView.getContext());
        gateView.addGate(gate);
    }

    public void addInputGate(View view){
        InputGate inputgate = new InputGate(gateView.getContext());
        gateView.addInputGate(inputgate);
    }

    public void addOutputGate(View view){
        OutputGate outputGate = new OutputGate(gateView.getContext());
        gateView.addOutputGate(outputGate);
    }

}