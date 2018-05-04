package com.maheshchandran.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button optionOne, optionTwo, optionThree, optionFour;
    ArrayList<String> celebrityNames = new ArrayList<String>();
    ArrayList<String> celebrityImagesURL = new ArrayList<String>();

    int answerPlacement;

    public class DownloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String htmlContent = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) {

                    char current = (char) data;

                    htmlContent +=current;

                    data = reader.read();
                }
                Log.i("Info", htmlContent);
                return htmlContent;

            } catch (Exception e) {
                e.printStackTrace();

                return "Failed";
            }

        }
    }


    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Bitmap celebrityImage = BitmapFactory.decodeStream(in);

                return celebrityImage;


            } catch (Exception e){

                e.printStackTrace();

                return null;
            }

        }
    }

    public void checkAnswer(View view) {
        String result = "";
        if (Integer.parseInt(view.getTag().toString()) == answerPlacement) {
            result = "Correct!";
        } else {
            result = "Wrong!";
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        getNextCelebrity();
    }

    public void getNextCelebrity() {

        int chance = new Random().nextInt(74);

        fetchCelebrityImage(celebrityImagesURL.get(chance));

        String correctCelebrityName = celebrityNames.get(chance);

        Log.i("Correct name is: ", correctCelebrityName);

        setUserOptions(correctCelebrityName);

    }

    public String returnRandomCelebrityName() {
        int chance = new Random().nextInt(74);

        return celebrityNames.get(chance);
    }

    public void setUserOptions(String correctCelebrityName) {

        answerPlacement = new Random().nextInt(4);

        switch (answerPlacement) {

            case 0:
                    optionOne.setText(correctCelebrityName);
                    optionTwo.setText(returnRandomCelebrityName());
                    optionThree.setText(returnRandomCelebrityName());
                    optionFour.setText(returnRandomCelebrityName());

                    break;

            case 1:
                    optionOne.setText(returnRandomCelebrityName());
                    optionTwo.setText(correctCelebrityName);
                    optionThree.setText(returnRandomCelebrityName());
                    optionFour.setText(returnRandomCelebrityName());

                    break;

            case 2:
                    optionOne.setText(returnRandomCelebrityName());
                    optionTwo.setText(returnRandomCelebrityName());
                    optionThree.setText(correctCelebrityName);
                    optionFour.setText(returnRandomCelebrityName());

                    break;

            case 3:
                    optionOne.setText(returnRandomCelebrityName());
                    optionTwo.setText(returnRandomCelebrityName());
                    optionThree.setText(returnRandomCelebrityName());
                    optionFour.setText(correctCelebrityName);

                    break;

        }

    }

    public void extractCelebrityImages(String rawHTMLContent) {

        Pattern p = Pattern.compile("<img src=\"(.*?)\"");
        Matcher m = p.matcher(rawHTMLContent);

        while(m.find()) {
            celebrityImagesURL.add(m.group(1));

        }

        Log.i("Images URL :", celebrityImagesURL.toString());

    }

    public void extractCelebrityNames(String rawHTMLContent) {

        Pattern p = Pattern.compile("alt=\"(.*?)\"/>");
        Matcher m = p.matcher(rawHTMLContent);

        while(m.find()){
            celebrityNames.add(m.group(1));
        }
        Log.i("Names :", celebrityNames.toString());
    }

    public void fetchCelebrityImage(String imageURL) {

        DownloadImage task = new DownloadImage();
        Bitmap celebrityImage = null;

        try {

           celebrityImage = task.execute(imageURL).get();
           imageView.setImageBitmap(celebrityImage);
        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        optionOne = findViewById(R.id.optionOne);
        optionTwo = findViewById(R.id.optionTwo);
        optionThree = findViewById(R.id.optionThree);
        optionFour = findViewById(R.id.optionFour);

        DownloadContent task = new DownloadContent();
        String htmlContent = "";
        try {
            htmlContent = task.execute("http://www.posh24.se/kandisar").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        extractCelebrityNames(htmlContent);
        extractCelebrityImages(htmlContent);
        getNextCelebrity();

    }
}
