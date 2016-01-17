package org.aksw.dataid.classes;

import org.aksw.dataid.config.DataIdConfig;
import org.aksw.dataid.errors.DataIdInputException;
import org.aksw.dataid.errors.InnerFutureException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Chile on 10/28/2015.
 */
public class RemoteFileInformation implements Callable<Map<String, List<String>>> {

    private URL url;
    private URLConnection conn;
    private InputStream stream;
    private List<String> formats = new ArrayList<>();
    private PrefixTree<Map.Entry<String, String>> magicNumbers;

    public RemoteFileInformation(String url, PrefixTree<Map.Entry<String, String>> tree ) throws DataIdInputException, IOException {
        try{
            this.url = new URL(url);
            this.magicNumbers = tree;
            this.conn = this.url.openConnection();
            this.conn.setConnectTimeout(2000);
        } catch (MalformedURLException e) {
            throw new DataIdInputException(e);
        }
    }

    public RemoteFileInformation(URL url) throws DataIdInputException, IOException {
        this(url, null);
    }

    public RemoteFileInformation(URL url, PrefixTree<Map.Entry<String, String>> tree) throws DataIdInputException, IOException {
        this.url = url;
        this.magicNumbers = tree;
        this.conn = this.url.openConnection();
        this.conn.setConnectTimeout(2000);
    }

    public void getCompressions() throws DataIdInputException, IOException {
            BufferedInputStream bs = new BufferedInputStream(conn.getInputStream());
            bs.mark(0);
            formats.add(getCompression(bs));
            bs.reset();
            InputStream comp = bs;
            if (formats.get(formats.size() - 1) != null) {
                if (formats.get(formats.size() - 1).equals("html"))
                    throw new DataIdInputException("htmlresource: Resource is a html document. Html documents are not supported.");

                InputStream zw = getStream(comp);
                if (zw != null) {
                    comp = zw;
                    formats.add(getCompression(comp));
                }
            }
            bs.close();
    }

    public String getCompression(InputStream stream) throws DataIdInputException {

        try {
            Integer ch;
            while ((ch = stream.read()) != -1 && ch == 0) {}
            byte[] signature = new byte[1024];
            signature[0] = ch.byteValue();
            stream.read(signature, 1, 1023);
            for(String sig : DataIdConfig.getCompressions().keySet())
            {
                if(matchFileSignitures(sig, signature))
                    return DataIdConfig.getCompressions().get(sig);
            }
        } catch (IOException e) {
            throw new DataIdInputException(e);
        }
        return null;
    }

    public Set<String> getFileTypes() throws DataIdInputException {
        if(magicNumbers == null)
            throw new DataIdInputException("this operation needs a magic number tree");
        try {
            InputStream stream = url.openStream();
            byte[] signature = new byte[1024];
            stream.read(signature);
            List<Integer> test = new ArrayList<>();
            for(int sig : signature)
            {
                test.add( sig < 0 ? 256 + sig : sig);
                if(!magicNumbers.startsWith(test))
                {
                    test.remove(test.size()-1);
                    break;
                }
            }
            Set<String> res = new HashSet<>();
            for(Map.Entry<String, String> m : magicNumbers.search(test))
            {
                res.add(m.getKey());
            }
            return res;
        } catch (IOException e) {
            throw new DataIdInputException(e);
        }
    }

    public static boolean matchFileSignitures(String hexString, byte[] content)
    {
        String[] sigHex = hexString.split(" ");
        if(sigHex.length > content.length)
            return false;
        for(int i =0; i < sigHex.length; i++)
        {
            if(sigHex[i].toLowerCase().equals("xx"))
                continue;
            if(content[i] != (byte)(Integer.parseInt(sigHex[i], 16)))
                return false;
        }
        return true;
    }

    public String getCompressionExtension() throws DataIdInputException {
        String compression = "";
        for(int i= formats.size()-1; i >=0 ; i--)
    {
            if(formats.get(i) != null)
                compression += "." + formats.get(i);
        }
        return compression;
    }

    public String getFileExtension() throws DataIdInputException {
        String extension = this.url.getFile().replace(getCompressionExtension(), "").trim();
        if(extension.contains("."))
            extension = extension.substring(extension.lastIndexOf('.') + 1);
    else
            return null;
        return extension;
    }

    @Override
    public Map<String, List<String>> call() throws DataIdInputException, InnerFutureException, IOException {
        if(stream == null)
            try {
                this.stream = this.url.openStream();
            } catch (IOException e) {
                throw new InnerFutureException(url.toString());
            }
        if(formats == null || formats.size() == 0)
            getCompressions();
        Map<String, List<String>> map2 = new HashMap<>();
        for(Map.Entry<String, List<String>> ent : conn.getHeaderFields().entrySet())
            if(ent.getKey() != null)
                map2.put(ent.getKey(), ent.getValue());
        map2.put("Compression", formats);
        map2.put("Extension", Arrays.asList(getFileExtension()));
        map2.put("Filename", Arrays.asList(url.getFile().substring(url.getFile().contains("/") ? url.getFile().lastIndexOf('/')+1 : 0)));


        //TODO do this better!
        if(getCompressionExtension().equals(".tar.gz")) {
            String ct = "application/x-gtar";
            String[] c = map2.get("Content-Type").get(0).split(";");
            for(String cc : c)
                if(cc.startsWith("charset"))
                    ct += ";" + cc;
            map2.put("Content-Type", Arrays.asList(ct));
        }
        return map2;
    }

    public URL getUrl() {
        return url;
    }

    public List<String> getFormats() {
        return formats;
    }

    public static InputStream getStream(InputStream bs)
    {
        InputStream comp = null;
        try {

            comp = new CompressorStreamFactory().createCompressorInputStream(bs);
            if (comp == null)
                comp = new ArchiveStreamFactory().createArchiveInputStream(bs);

        } catch (CompressorException e) {
            try {
                comp = new ArchiveStreamFactory().createArchiveInputStream(bs);
            } catch (ArchiveException e1) {
                e1.printStackTrace();
            }
        } catch (ArchiveException e) {
            try {
                comp = new CompressorStreamFactory().createCompressorInputStream(bs);
            } catch (CompressorException e1) {
                e1.printStackTrace();
            }
        }
        return comp;
    }
}
