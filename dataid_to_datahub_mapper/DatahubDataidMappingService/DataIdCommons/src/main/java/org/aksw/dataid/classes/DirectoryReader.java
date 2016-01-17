package org.aksw.dataid.classes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryReader {

    private URL directory;
    private List<String> fileList;

    public DirectoryReader(URL directoryUrl) {
        this.directory = directoryUrl;
    }

    private void gatherFiles()
    {
        StringBuilder sb = new StringBuilder();
        try {
            URLConnection conn = directory.openConnection();
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\r\n");
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("(href\\=\")([^\"]+)", Pattern.MULTILINE);
        Matcher matcher = p.matcher(sb.toString());
        StringBuilder ss = new StringBuilder();
        while (matcher.find())
            ss.append(String.valueOf(matcher.group(2)) + "\r\n");

        p = Pattern.compile("^[\\d,\\w,\\s-]+(\\.[A-Za-z0-9]{1,6})+$", Pattern.MULTILINE);
        matcher = p.matcher(ss.toString());
        while (matcher.find())
            fileList.add((directory.toString().endsWith("/") ? directory.toString() : directory.toString() + "/") + matcher.group());
    }

    public List<String> getFiles()
    {
        if(fileList == null) {
            fileList = new ArrayList<>();
            gatherFiles();
        }
        return fileList;
    }
}