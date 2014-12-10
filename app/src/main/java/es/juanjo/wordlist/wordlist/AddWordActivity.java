package es.juanjo.wordlist.wordlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddWordActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefrescarListaDiccionarios();
    }

    public void saveWord(View view) {
        final EditText word = (EditText) findViewById(R.id.editTextAddWord);
        final EditText meaning = (EditText) findViewById(R.id.editTextMeaning);
        final EditText dictionary = (EditText) findViewById(R.id.editTextDictionary);
        System.out.println("Saving word: " + word.getText().toString());
        DataBase db = new DataBase(this);
        db.open();
        db.insertWord(word.getText().toString(), meaning.getText().toString(),
                dictionary.getText().toString());
        db.close();

        // Mostramos mensaje y borramos los textos anteriores
        // @TODO Los mensajes deben ir a la configuración por idiomas
        String msg = getString(R.string.Saved);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        EditText editTextAdWord = (EditText) findViewById(R.id.editTextAddWord);
        editTextAdWord.setText("");
        EditText editTextMeaning = (EditText) findViewById(R.id.editTextMeaning);
        editTextMeaning.setText("");
        RefrescarListaDiccionarios();
    }

    protected void RefrescarListaDiccionarios() {
        DataBase db = new DataBase(this);
        db.open();
        Cursor c = db.getDictionaries();
        List<String> dictionaries = new ArrayList<String>();
        if (c.moveToFirst())
        {
            do {
                dictionaries.add(c.getString(0));
            } while (c.moveToNext());
        }
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, dictionaries);
        AutoCompleteTextView editTextDictionaryView = (AutoCompleteTextView)
                findViewById(R.id.editTextDictionary);
        editTextDictionaryView.setAdapter(adapter);
    }

    public void backToMain(View view) {
        startActivity(new Intent("es.juanjo.wordlist.wordlist.MainActivity"));
    }

    public void showPlayActivity(View view) {
        startActivity(new Intent("es.juanjo.wordlist.wordlist.PlayActivity"));
    }
}
