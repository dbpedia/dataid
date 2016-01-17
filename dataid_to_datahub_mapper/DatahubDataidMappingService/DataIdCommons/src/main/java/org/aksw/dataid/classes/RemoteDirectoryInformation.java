package org.aksw.dataid.classes;

import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.InnerFutureException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Chile on 10/28/2015.
 */
public class RemoteDirectoryInformation {
    CompletionService compService = new ExecutorCompletionService(Executors.newFixedThreadPool(100));
    private List<Map<String, List<String>>> files = new ArrayList<>();
    private PrefixTree<Map.Entry<String, String>> magicNumbers;
    private DirectoryReader dReader;
    private URL url;

    public RemoteDirectoryInformation(URL url, PrefixTree<Map.Entry<String, String>> tree) {
        this.url = url;
        this.dReader = new DirectoryReader(url);
        this.magicNumbers = tree;
    }

    public void runInformationGathering() throws DataIdInputException, InterruptedException {
        try {
            List<String> ff = dReader.getFiles();

            while(ff.size() > 0) {
                for(String u : ff)
                {
                    compService.submit(new RemoteFileInformation(u, magicNumbers));
                }
                Future<Map<String, List<String>>> f = compService.take();
                do {
                    try {
                        Map<String, List<String>> zw = f.get();
                        String test = url.toString() + zw.get("Filename").get(0);
                        if(ff.contains(test))
                        {
                            ff.remove(test);
                            files.add(zw);
                        }
                    } catch (ExecutionException ex) {
                        //if (InnerFutureException.class.isAssignableFrom(ex.getCause().getClass()) || ConnectException.class.isAssignableFrom(ex.getCause().getClass()))
                            //compService.submit(new RemoteFileInformation(new URL(ex.getCause().getMessage()), magicNumbers));
                    }
                    f = compService.take();
                } while (files.size() < this.dReader.getFiles().size() && f != null);
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public URL getUrl() {
        return url;
    }

    public List<Map<String, List<String>>> getFiles() {
        return files;
    }
}
