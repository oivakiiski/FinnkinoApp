package com.oliot5.finnkino;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Fragment_Main extends Fragment {

    View view;

    String[] theatre;
    String[] id;
    EditText editTextDate;
    EditText editTextTime;
    EditText editTextTime2;
    EditText editTextMovie;
    MovieManager mClass = new MovieManager();
    ListView listView;
    Spinner spinner;
    String date, username = "", fileName = "", movieName;
    String time1 = "00:00";
    String time2 = "23:59";
    String movie = "";
    TextView textView, printStars;
    Button hae;
    int idSelecter, howManyStars;
    ArrayList<String> tmp = new ArrayList<>();
    SeekBar seekBar;


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            date = editTextDate.getText().toString();
            textView.setText(date);
            time1 = editTextTime.getText().toString();
            time2 = editTextTime2.getText().toString();
            movie = editTextMovie.getText().toString();

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fr_headwindow, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            username = getArguments().getString("key");
            System.out.println(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        theatre = mClass.readXML();
        id = mClass.getIDList();
        textView = (TextView) this.view.findViewById(R.id.textView);

        editTextDate = (EditText) this.view.findViewById(R.id.editTextDate);
        editTextDate.addTextChangedListener(textWatcher);

        editTextTime = (EditText) this.view.findViewById(R.id.editTextTime);
        editTextTime.addTextChangedListener(textWatcher);

        editTextTime2 = (EditText) this.view.findViewById(R.id.editTextTime2);
        editTextTime2.addTextChangedListener(textWatcher);

        editTextMovie = (EditText) this.view.findViewById(R.id.editTextMovie);
        editTextMovie.addTextChangedListener(textWatcher);

        spinner = (Spinner) this.view.findViewById(R.id.spinner);


        hae = (Button) this.view.findViewById(R.id.button);

        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item, theatre);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idSelecter = i;

                ArrayList<String> arrayList = mClass.readXML2(id[i], date, time1, time2, movie);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Movie (Listview) selection functionality
        listView = (ListView) this.view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                int index = position;
                movieName = tmp.get(index);
                Document document = null;
                fileName = username + ".xml";

                //this row will be deleted when username functionality is working
                //fileName = "user1.xml";
                System.out.println(fileName);

                String string = "";
                InputStream inputStream = null;
                try {
                    //inputStream = getContext().getAssets().open(fileName);
                    inputStream = getContext().openFileInput(fileName);
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    string = new String(buffer);
                    inputStream.close();
                    System.out.println("Tiedoston avaaminen onnistui!");
                    System.out.println(string);
                } catch (IOException e) {
                    e.printStackTrace();
                    string = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<MovieInformation>\n" +
                            "</MovieInformation>";
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;

                //Convert String to document
                try {
                    builder = factory.newDocumentBuilder();
                    document = builder.parse(new InputSource(new StringReader(string)));
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }

                //root element
                Element root = document.getDocumentElement();

//                Attr attr = document.createAttribute("Id");
//                attr.setValue("1");
//                root.setAttributeNode(attr);

                //movie element
                Element film = document.createElement("movie");
//                name.appendChild(document.createTextNode(movieName));
                root.appendChild(film);

                //child: movie name
                Element filmName = document.createElement("MovieName");
                filmName.appendChild(document.createTextNode(movieName));
                film.appendChild(filmName);

                //child: stars
                Element amountOfStars = document.createElement("stars");
                amountOfStars.appendChild(document.createTextNode(Integer.toString(howManyStars)));
                film.appendChild(amountOfStars);

                //Convert document to string
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer;

                String output = null;
                try {
                    transformer = tf.newTransformer();
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    output = writer.getBuffer().toString();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

                System.out.println(output);

                try {
                    FileOutputStream writer = getContext().openFileOutput(fileName,0);
                    writer.write(output.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                pLuokka.saveEntries(movieName, howManyStars);
            }
        });

        //seekbar functionality
        printStars = (TextView) this.view.findViewById(R.id.textViewStars);
        seekBar = (SeekBar) this.view.findViewById(R.id.seekBar);
        seekBar.setMax(5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                howManyStars = progress;
                printStars.setText(""+howManyStars+" stars");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button searchButton = (Button) this.view.findViewById(R.id.button);
        View.OnClickListener listener2 = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                System.out.println(username);
                ArrayList<String> lista2 = mClass.readXML2(id[idSelecter], date, time1, time2, movie);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,lista2);
                listView.setAdapter(arrayAdapter);
                tmp = lista2;
            }
        };
        searchButton.setOnClickListener(listener2);
    }

}
