/**
 * @(#)MetaDataImport.java
 *
 *
 * This project has been developed as part of Google Summer of code projects 2008
 * @Student: Blooma Mohan John,WKWSCI, Nanyang Technological University,Singapore
 * Mentor: Jayan C Kurian, WKWSCI, Nanyang Technological University,Singapore
 * Co-Mentor: Stuart Lewis,University of Wales Aberystwyth,UK
 * Co-Mentor: Richard Jones,HP Labs,UK
 * Programming Reference : Tony Sintes, JavaWorld.com 
 * Programming Reference : R.J. Lorimer, Javalobby.org
 * @version 1.00 2008/June/25
 */

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
 
 
public class MetaDataImport
{
    public static void main( String [] args )
    {
    	
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
                              
	        //To continue the SIP generation till end of records in cEJ excel sheet
		 while( rs.next() )
        
		 {
  
  		//creating the first SIP folder
		File itemArchiveDirectory= new File(mainArchiveDirectory,archiveFolderName + "_" + initialDocumentNo);
		itemArchiveDirectory.mkdir();
 
		//getting the location of digital item (e.g. c:\WKWSCI\Maria.pdf)  
 		String resourceLocation = rs.getString("ResourceLocation");

		//checking for empty resource location
		if (resourceLocation == null || resourceLocation.trim().length () == 0)
		{
		System.out.println("Please check the empty Resource Location value of Digital Resource : " 
			+ archiveFolderName + "_" + initialDocumentNo);
		System.exit(0); 
		}
 
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
		contentDestination = new File(itemArchiveDirectory,archiveFolderName + "_" + initialDocumentNo + ".pdf");
		outDC.write("<!-- title of pdf " + archiveFolderName + "_" + initialDocumentNo + ".pdf-->\n"); 
		outCF.write(archiveFolderName + "_" + initialDocumentNo + ".pdf");
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + archiveFolderName + "_" + 	

	initialDocumentNo + ".pdf");
  		 }

 		//checking the type of digital item (e.g. doc)
 
 		if (path.substring(findItemType+1 ,findItemType+4).equals("doc"))
 		{
		contentDestination = new File(itemArchiveDirectory,archiveFolderName + "_" + initialDocumentNo + ".doc");
		outDC.write("<!-- title of doc " + archiveFolderName + "_" + initialDocumentNo + ".doc-->\n");
		outCF.write(archiveFolderName + "_" + initialDocumentNo + ".doc");
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + archiveFolderName + "_" + 	

	initialDocumentNo + ".doc");
 
		}
 
		//checking the type of digital item (e.g. jpg)
		if (path.substring(findItemType+1 ,findItemType+4).equals("jpg"))
		{
		contentDestination = new File(itemArchiveDirectory,archiveFolderName + "_" + initialDocumentNo + ".jpg");
		outDC.write("<!-- title of jpg " + archiveFolderName + "_" + initialDocumentNo + ".jpg-->\n"); 
		outCF.write(archiveFolderName + "_" + initialDocumentNo + ".jpg");
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + archiveFolderName + "_" + 	

	initialDocumentNo + ".jpg");
		}

		//checking the type of digital item (e.g. txt)
 
		if (path.substring(findItemType+1 ,findItemType+4).equals("txt"))
		{
		contentDestination = new File(itemArchiveDirectory,archiveFolderName + "_" + initialDocumentNo + ".txt");
		outDC.write("<!-- title of txt " + archiveFolderName + "_" + initialDocumentNo + ".txt-->\n"); 
		outCF.write(archiveFolderName + "_" + initialDocumentNo + ".txt");
		System.out.println("RNo:"+seqDocumentNo+". " + "Metadata creation started for " + archiveFolderName + "_" + 	

		initialDocumentNo + ".txt");
		}
 
 		//Copying file using java.nio package (Reference R.J. Lorimer, Javalobby.org)
		copyFile(contentSource, contentDestination);
 
		//Appending Dublin core to Dublin core.xml file)
		outDC.write("<dublin_core>\n");

		/* Title is the first user defined column heading of cEJ excel sheet that represent document titles. The 	

		if..else condition checks for empty metadata value and append contents to Dublin core file. This is followed 	

		for the elements Contributor, Description, Date, Rights,and Type. This section can be customized as per 	

		individual repository requirements specifying appropriate element and qualifiers. 
		*/

		String titleDC = rs.getString("Title");
		if (titleDC == null || titleDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Title dublin core element of "+ archiveFolderName + "_" 

		+ initialDocumentNo);}
		else 
		{
		outDC.write(" <dcvalue element=\"title\" qualifier=\"none\"> ");
		outDC.write( titleDC); 
		outDC.write("</dcvalue>\n");
		}

 

		String contributorAuthorDC = rs.getString("ContributorAuthor");
		if (contributorAuthorDC == null || contributorAuthorDC.trim().length () == 0)
		{ 
		System.out.println("You have given null value for the Contributor Author dublin core element of " + 		

		archiveFolderName + "_" + initialDocumentNo);}
		else 
		{
		outDC.write(" <dcvalue element=\"contributor\" qualifier=\"author\"> ");
		outDC.write(contributorAuthorDC); 
		outDC.write("</dcvalue>\n");
		}

  		String descriptionabstractDC = rs.getString("DescriptionAbstract");
		if (descriptionabstractDC == null || descriptionabstractDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Description Abstract dublin core element of " + 		

		archiveFolderName + "_" + initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"description\" qualifier=\"abstract\">");
		outDC.write(descriptionabstractDC); 
		outDC.write("</dcvalue>\n");
		}

		String contributorAdvisorDC = rs.getString("ContributorAdvisor");
 		if (contributorAdvisorDC == null || contributorAdvisorDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Contributor Advisor dublin core element of " + 		

		archiveFolderName + "_" + initialDocumentNo);
		}
		else 
		{
		outDC.write("<dcvalue element=\"contributor\" qualifier=\"advisor\">");
		outDC.write(contributorAdvisorDC); 
		outDC.write("</dcvalue>\n");
		}
 
		String dateCopyrightDC = rs.getString("DateCopyright");
 		if (dateCopyrightDC == null || dateCopyrightDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Date Copyright dublin core element of " + 		

		archiveFolderName + "_" + initialDocumentNo);
		}
		else 
		{
		outDC.write("<dcvalue element=\"date\" qualifier=\"copyright\"> ");
		outDC.write( dateCopyrightDC); 
		outDC.write("</dcvalue>\n");
		}

		String dateissuedDC = rs.getString("DateIssued");
		if (dateissuedDC == null || dateissuedDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Date Issued dublin core element of " + 			

	archiveFolderName + "_" + initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"date\" qualifier=\"issued\"> ");
		outDC.write( dateissuedDC); 
		outDC.write("</dcvalue>\n");
		}

		String descriptionDegreeDC = rs.getString("DescriptionDegree");
		if (descriptionDegreeDC == null || descriptionDegreeDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Description Degree dublin core element of " + 		

		archiveFolderName + "_" + initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"description\" qualifier=\"degree\">");
		outDC.write( descriptionDegreeDC); 
		outDC.write("</dcvalue>\n");
		}
 
		String descriptionDC = rs.getString("Description");
		if (descriptionDC == null || descriptionDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Description dublin core element of " + 			

archiveFolderName + "_" + initialDocumentNo);
		}
		else 
		{
		outDC.write("<dcvalue element=\"description\" qualifier=\"none\">");
		outDC.write( descriptionDC); 
		outDC.write("</dcvalue>\n");
		}
 
		String rightsDC = rs.getString("Rights");
		if (rightsDC == null || rightsDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Rights dublin core element of " + archiveFolderName + 	

	"_" + initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"rights\" qualifier=\"none\">");
		outDC.write( rightsDC); 
		outDC.write("</dcvalue>\n");
		}
 
		String ContributorDepartmentDC = rs.getString("ContributorDepartment");
 		if (ContributorDepartmentDC == null || ContributorDepartmentDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Contributor Department dublin core element of " + 	

		archiveFolderName + "_" + initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"contributor\" qualifier=\"department\">");
		outDC.write( ContributorDepartmentDC); 
		outDC.write("</dcvalue>\n");
		}
 
		String typeDC = rs.getString("Type");
		if (typeDC == null || typeDC.trim().length () == 0)
		{
		System.out.println("You have given null value for the Type dublin core element of " + archiveFolderName + "_" 

		+ initialDocumentNo);}
		else 
		{
		outDC.write("<dcvalue element=\"type\" qualifier=\"none\">");
		outDC.write( typeDC); 
		outDC.write("</dcvalue>\n");
		}
 
		//Finishing appending to Dublin core file
		outDC.write("</dublin_core>");
 
 		//Incrementing the sequence no for the SIP folder and document
		initialDocumentNo++;
		seqDocumentNo++;
		System.out.println("-----------------------------------------------------------------------------");
 
		outDC.close();
		outCF.close();
 
 
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

 } //End MetaDataImport class
