package pdf_cover_page;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
import java.lang.String.*;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.BaseFont;

public class PDF_Cover_Page {
    public static void main(String[] args) {
        
       System.out.print("Enter output director: ");
       BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
       String oDir = null;
       try {
         oDir = br.readLine();
       } catch (IOException e) {
         System.out.println("Error!");
         System.exit(1);
       }

         
       	//connection object and statement object for MS Excel Driver/Resultset
        Connection c = null;
        Statement stmnt = null;
       
        try
        {
		//Display current date and time when the SIP generation started
            	System.out.println("Starting Time is : " + new Date());
               
		//connection to MS Excel Driver/Resultset (Reference: Tony Sintes, JavaWorld.com)
 	        Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
        	c = DriverManager.getConnection("jdbc:odbc:connExcelJava","","");
	        stmnt = c.createStatement();
                String query = "select * from [cEJ$]";
                ResultSet rs = stmnt.executeQuery( query );
                
                
                
                File myFile = new File(oDir);
                myFile.mkdir();
                
                System.out.println(oDir + "\n");
                
                while( rs.next() )
		{
                    
		boolean Flag = true;

		//getting the location of digital item (e.g. c:\WKWSCI\Maria.pdf)
 		String resourceLocation = rs.getString("RL");
 		String File_Name = resourceLocation.substring(resourceLocation.lastIndexOf("\\")+1,resourceLocation.length());
                
                String masterPDF = rs.getString("ML");
                System.out.println(masterPDF);
                
		//checking for empty resource location
		boolean File_Exist = (new File(resourceLocation)).exists();
		if (resourceLocation == null || resourceLocation.trim().length () == 0 || !File_Exist)
		{
			Flag=false;
			//outER.write(File_Name + "\n");
			System.out.println("Please check the empty Resource Location value of Digital Resource : "
			+ File_Name + "\n");
		}
		if (Flag==true){
                    
                //getting URL Handle
                String dspaceHandle = rs.getString("DSH");
    
                String mergedLocation = resourceLocation.substring(0,resourceLocation.lastIndexOf("\\"));   
                System.out.println("Merge Location: " + mergedLocation + "\n");
                String filesToMerge[] = new String[] {masterPDF,resourceLocation};
                mergeMyFiles(filesToMerge,mergedLocation,File_Name,dspaceHandle,oDir);
                
		System.out.println("-----------------------------------------------------------------------------");
                
        }
        //outER.close();
        }       
    }     
 	catch( Exception e )
        	{
	            System.err.println( e );
        	}
         finally
 	       {
        	    try
            		{
	                stmnt.close();
        	        c.close();
            		}
		            catch( Exception e )
            			{
		                System.err.println( e );
            			}
 	       }

        //Dsiplaying the completion time for SIP generation
        System.out.println("Ending Time is : " + new Date());
    } //End main Class


public static void mergeMyFiles(String filesToBeMerged[],
String mergedFileLocation, String File_Name, String dspaceHandle,String oDir) {
 
    System.out.println("Starting To Merge Files...");
    try {
    int fileIndex = 0;
    String outFile = mergedFileLocation;
    Document document = null;
    PdfCopy writer = null;
    PdfReader reader = null;
    
    PdfReader masterReader = null;
    PdfStamper masterStamp = null;
    
    masterReader = new PdfReader(filesToBeMerged[0]);
    masterStamp =  new PdfStamper(masterReader, new FileOutputStream(mergedFileLocation + "\\workingMaster.pdf"));
    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.WINANSI,BaseFont.EMBEDDED);
    PdfContentByte over;
    over = masterStamp.getOverContent(1);
    over.beginText();
    over.moveText(100, 500);
    over.setFontAndSize(bf, 12);
    over.showText(dspaceHandle);
    over.endText();
    masterStamp.close();
    
    filesToBeMerged[0] = mergedFileLocation + "\\workingMaster.pdf";
    System.out.println("WorkingMaster.pdf Location: " + filesToBeMerged[0]);
    for (fileIndex = 0; fileIndex < filesToBeMerged.length; fileIndex++) {
        /*
        * Create a reader for the file that we are reading
        */
        reader = new PdfReader(filesToBeMerged[fileIndex]);
        System.out.println("Reading File -"+ filesToBeMerged[fileIndex]);
        /*
        * Replace all the local named links with the actual destinations.
        */
        reader.consolidateNamedDestinations();
        /*
        * Retrieve the total number of pages for this document
        */
        int totalPages = reader.getNumberOfPages();

        /*
        * Merging the files to the first file.
        * If we are passing file1, file2 and file3,
        * we will merge file2 and file3 to file1.
        */
        if (fileIndex == 0) {
        /*
        * Create the document object from the reader
        */
        document = new Document(reader.getPageSizeWithRotation(1));
        /*
        * Create a pdf write that listens to this document.
        * Any changes to this document will be written the file
        * 
        * outFile is a location where the final merged document
        * will be written to.
        */
        System.out.println("Creating an empty PDF...");   
        writer = new PdfCopy(document,
        new FileOutputStream(outFile + "/0" + File_Name));
        /*
        * Open this document
        */
        document.open();
    }
    /*
    * Add the conent of the file into this document (writer).
    * Loop through multiple Pages
    */
       
    //System.out.println("Merging File: "+filesToBeMerged[fileIndex]);
    PdfImportedPage page;
    for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
        page = writer.getImportedPage(reader, currentPage);
        writer.addPage(page);
    }

    }
    // Finally Close the main document, which will trigger the pdfcopy
    // to write back to the filesystem.

    document.close();

    System.out.println("File has been merged and written to-" + mergedFileLocation);
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}

