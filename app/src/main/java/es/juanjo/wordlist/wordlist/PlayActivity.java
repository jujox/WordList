package es.juanjo.wordlist.wordlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlayActivity extends Activity {

    private List<String> words;
    private List<String> meanings;
    private Random randomGenerator;
    CountDownTimer countDownTimer;

    private DataBase db = new DataBase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        words = new ArrayList<String>();
        meanings = new ArrayList<String>();
        db.open();
        randomGenerator = new Random();
        RefrescarListaDiccionarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            return inflater.inflate(R.layout.fragment_play, container, false);
        }
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
        Spinner spinnerDiccionarios = (Spinner) findViewById(R.id.spinnerDiccionarios);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dictionaries);
        spinnerDiccionarios.setAdapter(adapter);
        //TODO Añadir una opción "todos"

    }

    public void backToMain(View view) {
        startActivity(new Intent("es.juanjo.wordlist.wordlist.MainActivity"));
    }

    public void getWords(String dictionary) {
        words.clear();
        meanings.clear();
        Cursor c = db.getWordFromDictionary(dictionary);
        if (c.moveToFirst())
        {
            do {
                words.add(c.getString(0));
                meanings.add(c.getString(1));
            } while (c.moveToNext());
        }
    }

    public void play(View view) {
        Spinner dict = (Spinner) findViewById(R.id.spinnerDiccionarios);
        getWords(dict.getSelectedItem().toString());
        PlayDialog();
    }

    public void Solve(String word, String meaning) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.solutionTitle));
        builder.setMessage(word + " : " + meaning);
        builder.setPositiveButton(R.string.playMore, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PlayDialog();
            }
        });
        builder.setNegativeButton(R.string.stopPlaying, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void PlayDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Integer pos = randomGenerator.nextInt(words.size());
        final String word = words.get(pos);
        final String meaning = meanings.get(pos);
        builder.setMessage(word);
        builder.setPositiveButton(R.string.playMore, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                PlayDialog();
            }
        });
        builder.setNeutralButton(R.string.solution, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                Solve(word, meaning);
            }
        });
        builder.setNegativeButton(R.string.stopPlaying, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                countDownTimer.cancel();
                // User cancelled the dialog
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage(word + "... " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                alertDialog.cancel();
                Solve(word, meaning);
            }
        }.start();

    }

}
