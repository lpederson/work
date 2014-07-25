/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf_curl;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author lbpeders
 */
public class PDF_Curl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException,IOException {
        // TODO code application logic here
        String inputURL = "";
        String inputFileType = "";
        URL url = null;
        ArrayList<String> hrefs = new ArrayList();
        ArrayList<String> hrefsFiles = new ArrayList();
        JFileChooser saveDir = new JFileChooser();
        
        inputURL = getInput("Enter the URL: ");
        if(!inputURL.substring(inputURL.length() - 1).equals("/"))
            inputURL += "/";
        try{
            url = new URL(inputURL);
        } finally {}
        inputFileType = getInput("Enter a file type - ex \"txt\"\npdf by default (leave blank to skip)\nType: ");
        if(inputFileType.equals(""))
            inputFileType = "pdf";
        hrefs = getFirstHrefPerLine(url);
        if(hrefs.isEmpty()){
            System.out.println("No PDFS found.");
            return;
        }
        hrefsFiles = getFileHrefs(hrefs,"."+ inputFileType);
        System.out.println(inputFileType + "s (" + hrefsFiles.size() + "):");
        printList(hrefsFiles);
        System.out.println(inputFileType + "s (" + hrefsFiles.size() + ")");
        saveDir = chooseDirectory();
        saveFiles(hrefsFiles,saveDir.getSelectedFile().toPath()+"\\",url);
        System.out.println("Output Directory: " +saveDir.getSelectedFile().toPath());
    }
    public static String getInput(String prompt){
        String s = "";
        System.out.print(prompt);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            s = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read input!\n" + ioe);
            System.exit(1);
        }
        return s;
    }
    public static JFileChooser chooseDirectory(){
        JFileChooser chooser = new JFileChooser();
        int retval = JFileChooser.CANCEL_OPTION;
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
        try{
            retval = chooser.showDialog(null, "Select"); 
        }finally{}
        return chooser;
    }
    public static ArrayList<String> getFirstHrefPerLine(URL url) throws IOException{
        ArrayList<String> hrefs = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            
            for (String line; (line = reader.readLine()) != null;) {
                String open = "<a href=\"";
                String close = ".pdf\">";
                int end = line.indexOf(close);
                if(end > 0){
                    String temp = line.substring(0,end);
                    int start = temp.lastIndexOf(open);
                    if(start >= 0 && end > start){
                        String href = line.substring(start+open.length(),end+close.length()-2);
                        hrefs.add(href);
                    }
                }
            }
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignore) {System.out.println("Selection Cancelled");}
        }
        return hrefs;
    }
    public static ArrayList<String> getFileHrefs(ArrayList<String> hrefs,String fileExt){
        ArrayList<String> hrefs_files = new ArrayList<String>();
        for (int i = 0; i < hrefs.size(); i++) {
            String line = hrefs.get(i).toString();
            if(line.indexOf(fileExt) >= 0){
                hrefs_files.add(hrefs.get(i).toString());
            }
        }
        return hrefs_files;
    }
    public static void printList(ArrayList<String> list){
        for (int i = 0; i < list.size(); i++)
            System.out.println(list.get(i).toString());
        System.out.println();
    }
    public static void saveUrl(String filename, String urlString) throws MalformedURLException, IOException
    {
        System.out.println("Downloading " + urlString);
        HttpURLConnection c;

        //save file    	
        URL url = new URL(urlString);
        c = (HttpURLConnection)url.openConnection();
        c.setRequestProperty("User-Agent", "Mozilla/5.0");
        //c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        //set cache and request method settings
        c.setDoOutput(true);
        c.setDoInput(true);
        c.setInstanceFollowRedirects(true); 
        //c.setRequestMethod("POST"); 
        c.setRequestProperty("Content-Type", "application/pdf"); 
        c.setRequestProperty("charset", "utf-8");
        //c.setRequestProperty("Content-Length", "0");
        c.setUseCaches (false);

        //set other headers
        //c.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
        System.setProperty("http.agent", ""); 
        //connect
        c.connect();
        
        int responseCode = c.getResponseCode();
        if(responseCode != 200)
            System.out.println("Error - Response Code: " + responseCode);
        BufferedInputStream in = new BufferedInputStream(c.getInputStream());

        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filename)));
        byte[] buf = new byte[1024];
        int n = 0;
        while ((n=in.read(buf))>=0) {
            out.write(buf, 0, n);
        }
        out.flush();
        out.close();
    }
    public static void saveFiles(ArrayList<String> files,String dir,URL url) throws MalformedURLException{
        for(int i=0;i < files.size(); i++){
            try {
                saveUrl(dir+files.get(i).toString(),url.toString()+files.get(i));
            } catch (IOException ex) {
                System.out.println("Exception: Failed downloading " + url.toString() + files.get(i));
                System.out.println(ex);
            }
        }
    }
}