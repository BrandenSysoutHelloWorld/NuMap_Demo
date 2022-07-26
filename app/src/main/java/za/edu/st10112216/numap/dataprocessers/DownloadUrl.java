package za.edu.st10112216.numap.dataprocessers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUrl {

    public String retrieveUrl(String strUrl) throws IOException {
        String data = "";
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

            data = sb.toString();
            Log.d("data", data);
            br.close();



        }catch(Exception e){
            Log.d("Exception on download", e.toString());

        }finally{
            Objects.requireNonNull(iStream).close();
            urlConnection.disconnect();

        }
        return data;
    }
}
