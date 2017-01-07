package iosf.github.kaisubr.sciencefair.Database;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created at 1:11 PM for ScienceFair.
 */

public class DatabaseDownload extends AsyncTask<String, Void, List<String>> {
    private final static String TAG = "DatabaseDownload";
    protected List<String> doInBackground(String... strings) {
        //strings or states
        int count = strings.length;
        Document doc;
        List<String> numbersTotal = new ArrayList<String>();
        String baseURL = "https://www.usa.gov/state-government/";
        for (int i = 0; i < count; i++) {
            try {
                doc = Jsoup.connect(baseURL + strings[i]).get();
                //parse
                Elements numbers = doc.select(".spk, .tel");
                for (int j = 0; j < numbers.size(); j++) {
                    numbersTotal.add(numbers.get(j).text());
                }
                //numbersTotal.add(numbers.toString());
                Log.i(TAG, numbers.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error getting govt info for " + strings[i]);
            }
        }
        return numbersTotal;
    }

    protected int onProgressUpdate(Integer... process){
        return process[0];
    }

    protected void onPostExecute(Long result) {
        Log.d(TAG, "Downloaded " + result + " bytes");
    }

    //execute via new DatabaseDownload("string", "string", string");
}
