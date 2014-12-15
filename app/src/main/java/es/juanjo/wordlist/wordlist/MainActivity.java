package es.juanjo.wordlist.wordlist;

import android.app.Activity;
//import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    DataBase db = new DataBase(this);
    private List<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        words = new ArrayList<String>();
        db.open();
        RefrescarListaDiccionarios();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_play) {
            startActivity(new Intent("es.juanjo.wordlist.wordlist.PlayActivity"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return  inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    public void showPlayActivity(View view) {
        startActivity(new Intent("es.juanjo.wordlist.wordlist.PlayActivity"));
    }

    public void saveWord(View view) {
        final EditText word = (EditText) findViewById(R.id.editTextAddWord);
        final EditText meaning = (EditText) findViewById(R.id.editTextMeaning);
        final EditText dictionary = (EditText) findViewById(R.id.editTextDictionary);
        System.out.println("Saving word: " + word.getText().toString());
        db.insertWord(word.getText().toString(), meaning.getText().toString(),
                dictionary.getText().toString());

        // Mostramos mensaje y borramos los textos anteriores
        // @TODO Los mensajes deben ir a la configuración por idiomas
        String msg = getString(R.string.Saved);
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        word.setText("");
        meaning.setText("");
        RefrescarListaDiccionarios();
    }

    protected void showPlay(Menu menu) {
    }

    protected void RefrescarListaDiccionarios() {
        Cursor c = db.getDictionaries();
        List<String> dictionaries = new ArrayList<String>();
        if (c.moveToFirst())
        {
            do {
                dictionaries.add(c.getString(0));
            } while (c.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, dictionaries);
        AutoCompleteTextView editTextDictionaryView = (AutoCompleteTextView)
                findViewById(R.id.editTextDictionary);
        editTextDictionaryView.setAdapter(adapter);
    }

    public void getMeanings(String word) {
        words.clear();
        Cursor c = db.getWordMeaning(word);
        if (c.moveToFirst())
        {
            do {
                String value = c.getString(0) + " ; " + c.getString(1) + " ; " + c.getString(2);
                words.add(value);
                System.out.println(value);
            } while (c.moveToNext());
        }
    }

    public void searchWord(View view) {
        EditText searchText = (EditText) findViewById(R.id.editTextSearch);
        getMeanings(searchText.getText().toString());
        WordsDialog(searchText.getText().toString());
    }

    public void Delete(List<String> list) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            String deleteString = iterator.next().toString();
            System.out.println(deleteString);
            String[] delete = deleteString.split(" ; ");
            if (db.deleteWord(delete[0], delete[2])) {
                String msg = getString(R.string.deleted) + delete[0] + " on " + delete[2];
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void WordsDialog(String word) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.searchedWords) + " " + word);
        final CharSequence[] cs = words.toArray(new CharSequence[words.size()]);
        final List<String> deleteList = new ArrayList<String>();
        builder.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int id, boolean checked) {
                if (checked)
                    deleteList.add(cs[id].toString());
                else
                    deleteList.remove(cs[id].toString());
            }
        });
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Delete(deleteList);
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
