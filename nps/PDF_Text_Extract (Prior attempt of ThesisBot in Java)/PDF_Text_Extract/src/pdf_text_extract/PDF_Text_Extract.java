/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf_text_extract;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
import java.lang.String.*;

/**
 *
 * @author lbpeders
 */
public class PDF_Text_Extract {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
       	//connection object and statement object for MS Excel Driver/Resultset
        Connection c = null;
        Statement stmnt = null;
        
	//variables for submission Information package (SIP) folders
	String readFolderName = null;
        String archiveFolderName = null;

	//Appending SIP folder with sequence numbers
        int initialDocumentNo=1;
    	int seqDocumentNo=2;

	//Input for user defined SIP folder name
        System.out.print("Enter Your Main Archive Folder Name: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try

        {

                readFolderName = br.readLine();

		//Display current date and time when the SIP generation started
            	System.out.println("Starting Time is : " + new Date());

		//converting user input into uppercase to maintain consistency
                archiveFolderName = readFolderName.toUpperCase();

		//SIP folder name is created at C drive. You may change as per your requirement (e.g. D: or E: drive)
	        File mainArchiveDirectory= new File("c:/"+ archiveFolderName);
               	mainArchiveDirectory.mkdir();

		//connection to MS Excel Driver/Resultset (Reference: Tony Sintes, JavaWorld.com)
 	        Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
        	c = DriverManager.getConnection("jdbc:odbc:connExcelJava","","");
	        stmnt = c.createStatement();
                String query = "select * from [cEJ$]";
                ResultSet rs = stmnt.executeQuery( query );

		//creating error logging file
		File errorFile = new File(mainArchiveDirectory, "Errors");
		errorFile.createNewFile();
 		BufferedWriter outER = new BufferedWriter(new FileWriter(errorFile));
 		outER.write("Missing Files:\n");

                
                
                
                while( rs.next() )
		{
                    
		boolean Flag = true;

		//getting the location of digital item (e.g. c:\WKWSCI\Maria.pdf)
 		String resourceLocation = rs.getString("ResourceLocation");
 		String File_Name = resourceLocation.substring(resourceLocation.lastIndexOf("\\")+1,resourceLocation.length());


		//checking for empty resource location
		boolean File_Exist = (new File(resourceLocation)).exists();
		if (resourceLocation == null || resourceLocation.trim().length () == 0 || !File_Exist)
		{
			Flag=false;
			outER.write(File_Name + "\n");
			System.out.println("Please check the empty Resource Location value of Digital Resource : "
			+ File_Name + "\n");
		}
		if (Flag==true){

  		//creating the first SIP folder
		File itemArchiveDirectory= new File(mainArchiveDirectory,File_Name.substring(0,File_Name.length()-4));
		itemArchiveDirectory.mkdir();

		//get path of digital resource
		File contentSource = new File(resourceLocation);
		String path = contentSource.getAbsolutePath();
		int findItemType = path.indexOf(".");
		File contentDestination=null;

 		//creating dublin core.xml file
		File dublinCoreFile = new File(itemArchiveDirectory, "dublin_core.xml");
		dublinCoreFile.createNewFile();

		//creating contents file and appending text
		File contentsFile = new File(itemArchiveDirectory, "contents");
		contentsFile.createNewFile();
 		BufferedWriter outDC = new BufferedWriter(new FileWriter(dublinCoreFile));
		BufferedWriter outCF = new BufferedWriter(new FileWriter(contentsFile));
  		outDC.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");

 		//checking the type of digital item (e.g. pdf)

		if (path.substring(findItemType+1 ,findItemType+4).equals("pdf"))
		 {
		contentDestination = new File(itemArchiveDirectory, File_Name);
		outDC.write("<!-- title of pdf " + File_Name + "-->\n");
		outCF.write(File_Name);
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + File_Name);
  		 }

 		//checking the type of digital item (e.g. doc)

 		if (path.substring(findItemType+1 ,findItemType+4).equals("doc"))
 		{
		contentDestination = new File(itemArchiveDirectory,File_Name);
		outDC.write("<!-- title of doc " + File_Name + ".doc-->\n");
		outCF.write(File_Name);
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + File_Name);

		}

		//checking the type of digital item (e.g. jpg)
		if (path.substring(findItemType+1 ,findItemType+4).equals("jpg"))
		{
		contentDestination = new File(itemArchiveDirectory,File_Name);
		outDC.write("<!-- title of jpg " + File_Name + ".jpg-->\n");
		outCF.write(File_Name);
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + File_Name);
		}

		//checking the type of digital item (e.g. txt)
		if (path.substring(findItemType+1 ,findItemType+4).equals("txt"))
		{
		contentDestination = new File(itemArchiveDirectory,File_Name);
		outDC.write("<!-- title of txt " + File_Name + ".txt-->\n");
		outCF.write(File_Name);
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + File_Name);
		}

 		//Copying file using java.nio package (Reference R.J. Lorimer, Javalobby.org)
		copyFile(contentSource, contentDestination);

                //convert pdf to string
                String parsedText = PDFTextParser.pdftoText(resourceLocation);
                //parsedText = parsedText.replace("\r","").replace("\n","");

                //Find correct page
                String pdfReportPage = parsedText.substring(parsedText.indexOf("Approved for public release; distribution is unlimited")+55,parsedText.indexOf("THIS PAGE INTENTIONALLY LEFT BLANK"));
                
                
               
               
                outDC.write("<dublin_core>\n");
                
                //Title
                int letterCounter = 0;
                char myChar = pdfReportPage.charAt(letterCounter);
                String titleDC = "";
                while(!Character.isLowerCase(myChar)){
                    titleDC += pdfReportPage.charAt(letterCounter);
                    letterCounter++;
                }
                
                if (titleDC == null || titleDC.trim().length () == 0)
		{
			System.out.println("You have given null value for the Title dublin core element of " + File_Name);}
		else
		{
			String newTitleDC = escapeXML(titleDC);
			outDC.write("<dcvalue element=\"title\" qualifier=\"none\">");
			outDC.write(newTitleDC);
			outDC.write("</dcvalue>\n");
		}
                
                
                //Second Reader
                String secondReaderDC = pdfReportPage.substring(pdfReportPage.lastIndexOf("Second Reader: "),pdfReportPage.length());
                if (secondReaderDC == null || secondReaderDC.trim().length () == 0)
		{
			System.out.println("You have given null value for the Second Reader dublin core element of " + File_Name);}
		else
		{
			String newSecondReaderDC = escapeXML(secondReaderDC);
			outDC.write("<dcvalue element=\"contributor\" qualifier=\"secondreader\">");
			outDC.write(newSecondReaderDC);
			outDC.write("</dcvalue>\n");
		}
                
                
                outDC.write("<dcvalue element=\"description\" qualifier=\"none\">");
                outDC.write(pdfReportPage);
                outDC.write("</dcvalue>\n"); 
                
                
                
           		//Finishing appending to Dublin core file
		outDC.write("</dublin_core>");

 		//Incrementing the sequence no for the SIP folder and document
		initialDocumentNo++;
		seqDocumentNo++;
		System.out.println("-----------------------------------------------------------------------------");

		outDC.close();
		outCF.close();

            

        }
        outER.close();
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

 public static void copyFile(File sourceFile, File destFile) throws IOException
		{

 		FileChannel contentSource = null;
		FileChannel contentDestination = null;
		 try {
			  contentSource = new FileInputStream(sourceFile).getChannel();
			  contentDestination = new FileOutputStream(destFile).getChannel();
			  contentDestination.transferFrom(contentSource, 0, contentSource.size());
		     }
			 finally
				{
				  if(contentSource != null)
					{
					   contentSource.close();
  					}

				  if(contentDestination != null)
					{
					   contentDestination.close();
  					}
				}

		} //End copyFile method
 
  	public static String escapeXML(String inputString){
		String outputString  = inputString.replace("&","&amp;");
		outputString = outputString.replace("<","&lt;");
		outputString = outputString.replace(">","&gt;");
		outputString = outputString.replace("\"","&quot;");
		outputString = outputString.replace("\'","&apos;");

		return outputString;
	}
}