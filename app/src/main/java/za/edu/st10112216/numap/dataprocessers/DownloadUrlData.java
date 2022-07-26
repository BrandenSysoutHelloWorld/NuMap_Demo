package za.edu.st10112216.numap.dataprocessers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUrlData {

    public String retrieveUrl(String strUrl) throws IOException {
        String dataData = "";
        InputStream iStream = null;
        HttpsURLConnection urlConnection = null;

        try{

            URL url = new URL(strUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb  = new StringBuilder();
            String line;

            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            dataData = sb.toString();
            Log.d("dataData", dataData);
            br.close();



        }catch(Exception e){
            Log.d("Exception on download", e.toString());

        }finally{
            Objects.requireNonNull(iStream).close();
            urlConnection.disconnect();

        }
        return dataData;
    }
}
