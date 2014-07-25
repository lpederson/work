import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.io.*;
import java.nio.channels.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//import org.apache.commons.lang.StringEscapeUtils;

public class GenericDataImport
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

			String ErrorReport = "";

	    	//To continue the SIP generation till end of records in cEJ excel sheet
		 	while( rs.next() )
		 	{
		 		try
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
						System.out.println("Please check the empty Resource Location value of Digital Resource : " + File_Name + "\n");
						ErrorReport += File_Name + ": empty resouce...;";
					}
					if (Flag==true)
					{
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

						//Appending Dublin core to Dublin core.xml file)
						outDC.write("<dublin_core>\n");

						/* Title is the first user defined column heading of cEJ excel sheet that represent document titles. The
						if..else condition checks for empty metadata value and append contents to Dublin core file. This is followed
						for the elements Contributor, Description, Date, Rights,and Type. This section can be customized as per
						individual repository requirements specifying appropriate element and qualifiers.
						*/
						/*
						Here is the list of field this program looks for in the cEJ.xls file. Case sensitive

						<dublin core value>         cEJ Header				Split by ";"	Comments
						---------------------------------------------------------------------------------
						dc.title					Title					no
						dc.title.alternative		AlternativeTitle		no
						dc.contributor.author		ContributorAuthor		yes
						dc.contributor.advisor		ContributorAdvisor		yes
						dc.contributor.secondreader	SecondReader			yes
						dc.contributor.corporate	ContributorCorporate	yes
						dc.contributor.school		ContributorSchool		no
						dc.contributor.department	ContributorDepartment	no
						dc.date						Date					no
						dc.date.issued				DateIssued				no
						dc.date.copyright			DateCopyright			no
						dc.description.abstract		DescriptionAbstract		no
						dc.description				Description				no
						dc.description.funder		DescriptionFunder		no
						dc.rights					Rights					no
						dc.type						Type					yes
						dc.publisher				Publisher				yes
						dc.language.iso				Language				no
						dc.identifier.oclc			Oclc					no
						dc.subject					Subject					yes
						dc.subject.author			SubjectAuthor			yes
						dc.format.extent			FormatExtent			no

						*/

						// dc.title
						try
						{
							String titleDC = rs.getString("Title");
							if (titleDC == null || titleDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Title dublin core element of "+ File_Name);}
							else
							{
								titleDC = escapeXML(titleDC);
								outDC.write(" <dcvalue element=\"title\" qualifier=\"none\">");
								outDC.write(titleDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Title failure...\n" + e);
						}

						// dc.title.alternative
						try
						{
							String altTitleDC = rs.getString("AlternativeTitle");
							if (altTitleDC == null || altTitleDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the AlternativeTitle dublin core element of " + File_Name);}
							else
							{
								altTitleDC = escapeXML(altTitleDC);
								outDC.write("<dcvalue element=\"title\" qualifier=\"alternative\">");
								outDC.write(altTitleDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("AlternativeTitle failure...\n" + e);
						}

						// dc.contributor.author
						try
						{
							String contributorAuthorDC = rs.getString("ContributorAuthor");
							if (contributorAuthorDC == null || contributorAuthorDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Contributor Author dublin core element of " + File_Name);}
							else
							{
								String[] contributor_authors;
								contributor_authors = contributorAuthorDC.split(";");
								for (String i : contributor_authors)
								{
									i = escapeXML(i);
									outDC.write(" <dcvalue element=\"contributor\" qualifier=\"author\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("ContributorAuthor failure...\n" + e);
						}

						// dc.contributor.advisor
						try
						{
							String contributorAdvisorDC = rs.getString("ContributorAdvisor");
							if (contributorAdvisorDC == null || contributorAdvisorDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Contributor Advisor dublin core element of " + File_Name);}
							else
							{
								String[] contributor_advisors;
								contributor_advisors = contributorAdvisorDC.split(";");
								for (String i : contributor_advisors)
								{
									i = escapeXML(i);
									outDC.write(" <dcvalue element=\"contributor\" qualifier=\"advisor\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("ContributorAdvisor failure...\n" + e);
						}

						// dc.description.abstract
						try
						{
					  		String descriptionAbstractDC = rs.getString("DescriptionAbstract");
							if (descriptionAbstractDC == null || descriptionAbstractDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Description Abstract dublin core element of " + File_Name);}
							else
							{
								descriptionAbstractDC = escapeXML(descriptionAbstractDC);
								outDC.write("<dcvalue element=\"description\" qualifier=\"abstract\">");
								outDC.write(descriptionAbstractDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("DescriptionAbstract failure...\n" + e);
						}


						// dc.contributor.secondreader
						try
						{
							String secondReaderDC = rs.getString("SecondReader");
							if (secondReaderDC == null || secondReaderDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the SecondReader dublin core element of " + File_Name);}
							else
							{
								String[] secondReadersDC;
								secondReadersDC = secondReaderDC.split(";");
								for (String i : secondReadersDC)
								{
									i = escapeXML(i);
									outDC.write(" <dcvalue element=\"contributor\" qualifier=\"secondreader\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("SecondReader failure...\n" + e);
						}

						// dc.contributor.corporate
						try
						{
							String corporateDC = rs.getString("ContributorCorporate");
							if (corporateDC == null || corporateDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the CorporateAuthor dublin core element of " + File_Name);}
							else
							{
								String[] corporates_DC;
								corporates_DC = corporateDC.split(";");
								for (String i : corporates_DC)
								{
									i = escapeXML(i);
									outDC.write("<dcvalue element=\"contributor\" qualifier=\"corporate\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("ContributorCorporate failure...\n" + e);
						}


						// dc.contributor.school
						try
						{
							String schoolDC = rs.getString("ContributorSchool");
							if (schoolDC == null || schoolDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the School dublin core element of " + File_Name);}
							else
							{
								schoolDC = escapeXML(schoolDC);
								outDC.write("<dcvalue element=\"contributor\" qualifier=\"school\">");
								outDC.write(schoolDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("ContributorSchool failure...\n" + e);
						}

						// dc.date.copyright
						try
						{
							String dateCopyrightDC = rs.getString("DateCopyright");
					 		if (dateCopyrightDC == null || dateCopyrightDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Date Copyright dublin core element of " + File_Name);
							}
							else
							{
								dateCopyrightDC = escapeXML(dateCopyrightDC);
								outDC.write("<dcvalue element=\"date\" qualifier=\"copyright\">");
								outDC.write(dateCopyrightDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("DateCopyright failure...\n" + e);
						}

						// dc.date - NOT ISO format
						try
						{
							String dateISO = rs.getString("Date");
							if (dateISO == null || dateISO.trim().length () == 0)
							{
								System.out.println("You have given null value for the Date Issued dublin core element of " + File_Name);}
							else
							{
								dateISO = escapeXML(dateISO);
								outDC.write("<dcvalue element=\"date\" qualifier=\"none\">");
								outDC.write(dateISO);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Date failure...\n" + e);
						}

						// dc.date.issued
						try
						{
							String dateIssuedDC = rs.getString("DateIssued");
							if (dateIssuedDC == null || dateIssuedDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Date dublin core element of " + File_Name);}
							else
							{
								dateIssuedDC = escapeXML(dateIssuedDC);
								outDC.write("<dcvalue element=\"date\" qualifier=\"issued\">");
								outDC.write(dateIssuedDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("DateIssued failure...\n" + e);
						}

						// dc.description
						try
						{
							String descriptionDC = rs.getString("Description");
							if (descriptionDC == null || descriptionDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Description dublin core element of " + File_Name);
							}
							else
							{
								descriptionDC = escapeXML(descriptionDC);
								outDC.write("<dcvalue element=\"description\" qualifier=\"none\">");
								outDC.write(descriptionDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Description failure...\n" + e);
						}

						// dc.description.funder
						try
						{
							String descriptionFunderDC = rs.getString("DescriptionFunder");
							if (descriptionFunderDC == null || descriptionFunderDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the DescriptionFunder dublin core element of " + File_Name);}
							else
							{
								descriptionFunderDC = escapeXML(descriptionFunderDC);
								outDC.write("<dcvalue element=\"description\" qualifier=\"funder\">");
								outDC.write(descriptionFunderDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("DescriptionFunder failure...\n" + e);
						}

						// dc.rights
						try
						{
							String rightsDC = rs.getString("Rights");
							if (rightsDC == null || rightsDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Rights dublin core element of " + File_Name);}
							else
							{
								rightsDC = escapeXML(rightsDC);
								outDC.write("<dcvalue element=\"rights\" qualifier=\"none\">");
								outDC.write(rightsDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Rights failure...\n" + e);
						}

						// dc.contributor.department
						try
						{
							String ContributorDepartmentDC = rs.getString("ContributorDepartment");
					 		if (ContributorDepartmentDC == null || ContributorDepartmentDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Contributor Department dublin core element of " +	File_Name);}
							else
							{
								ContributorDepartmentDC = escapeXML(ContributorDepartmentDC);
								outDC.write("<dcvalue element=\"contributor\" qualifier=\"department\">");
								outDC.write(ContributorDepartmentDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("ContributorDepartment failure...\n" + e);
						}

						// dc.type
						try
						{
							String typeDC = rs.getString("Type");
							if (typeDC == null || typeDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Type dublin core element of " + File_Name);}
							else
							{
								String[] typesDC;
								typesDC = typeDC.split(";");
								for(String i : typesDC)
								{
									i = escapeXML(i);
									outDC.write("<dcvalue element=\"type\" qualifier=\"none\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("Type failure...\n" + e);
						}

						// dc.publisher
						try
						{
							String publisherDC = rs.getString("Publisher");
							if (publisherDC == null || publisherDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Publisher dublin core element of " + File_Name);}
							else
							{
								String[] publishers;
								publishers = publisherDC.split(";");
								for (String i : publishers)
								{
									i = escapeXML(i);
									outDC.write("<dcvalue element=\"publisher\" qualifier=\"none\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("Type failure...\n" + e);
						}

						// dc.language.iso
						try
						{
							String langDC = rs.getString("Language");
							if (langDC == null || langDC.trim().length () == 0)
							{
							System.out.println("You have given null value for the Language dublin core element of " + File_Name);}
							else
							{
								langDC = escapeXML(langDC);
								outDC.write("<dcvalue element=\"language\" qualifier=\"iso\">");
								outDC.write(langDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Language failure...\n" + e);
						}

						// dc.identifier.oclc
						try
						{
							String oclcDC = rs.getString("Oclc");
							if (oclcDC == null || oclcDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Oclc dublin core element of " + File_Name);}
							else
							{
								oclcDC = escapeXML(oclcDC);
								outDC.write("<dcvalue element=\"identifier\" qualifier=\"oclc\">");
								outDC.write(oclcDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("Oclc failure...\n" + e);
						}

						// dc.subject
						try
						{
							String subjectDC = rs.getString("Subject");
							if (subjectDC == null || subjectDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the Subject dublin core element of " + File_Name);}
							else
							{
								String[] subjects_DC;
								subjects_DC = subjectDC.split(";");
								for (String i : subjects_DC)
								{
									i = escapeXML(i);
									outDC.write(" <dcvalue element=\"subject\" qualifier=\"none\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("Subject failure...\n" + e);
						}

						// dc.subject.author
						try
						{
							String subjectAuthorDC = rs.getString("SubjectAuthor");
							if (subjectAuthorDC == null || subjectAuthorDC.trim().length () == 0)
							{
								System.out.println("You have given null value for the SubjectAuthor dublin core element of " + File_Name);
							}
							else
							{
								String[] subjectAuthors;
								subjectAuthors = subjectAuthorDC.split(";");
								for (String i : subjectAuthors)
								{
									i = escapeXML(i);
									outDC.write(" <dcvalue element=\"subject\" qualifier=\"author\">");
									outDC.write(i);
									outDC.write("</dcvalue>\n");
								}
							}
						}
						catch(Exception e)
						{
							System.err.println("SubjectAuthor failure...\n" + e);
						}

						// dc.format.extent
						try
						{
							String format_extentDC = rs.getString("FormatExtent");
							if (format_extentDC == null || format_extentDC.trim().length () == 0)
							{
							System.out.println("You have given null value for the FormatExtent dublin core element of " + File_Name);}
							else
							{
								String newformat_extentDC = escapeXML(format_extentDC);
								outDC.write("<dcvalue element=\"format\" qualifier=\"extent\">");
								outDC.write(newformat_extentDC);
								outDC.write("</dcvalue>\n");
							}
						}
						catch(Exception e)
						{
							System.err.println("FormatExtent failure...\n" + e);
						}

						//Finishing appending to Dublin core file
						outDC.write("</dublin_core>");

				 		//Incrementing the sequence no for the SIP folder and document
						initialDocumentNo++;
						seqDocumentNo++;
						System.out.println("-----------------------------------------------------------------------------");

						outDC.close();
						outCF.close();

						//XML Valid Check
						if(checkValidXML(itemArchiveDirectory + "\\dublin_core.xml"))
						{
							System.out.println(itemArchiveDirectory + "\\dublin_core.xml" + " is valid\n");
						}
						else
						{
							System.out.println(itemArchiveDirectory + "\\dublin_core.xml" + " is NOT valid\n");
							ErrorReport += File_Name + ";";
						}

			        }//End of "if(item_exists)"
			 	}// End item Try{}
			 	catch( Exception e )
				{
					System.err.println("Item failure");
			        System.err.println(e);
				}
	        }//End of "while(next) item"

        System.out.println("************* Final Error Report *******************");
        String[] ErrorsReport;
		ErrorsReport = ErrorReport.split(";");
		for (String i : ErrorsReport)
		{
			System.out.println(i);
		}
		System.out.println("************* End of Error Report *******************");
	}
 	catch( Exception e )
	{
		System.err.println("Batch failure...");
        System.err.println(e);
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

	//Displaying the completion time for SIP generation
    System.out.println("Ending Time is : " + new Date());

    } //End main Class

	public static void copyFile(File sourceFile, File destFile) throws IOException
	{
		FileChannel contentSource = null;
		FileChannel contentDestination = null;
		try
		{
			  contentSource = new FileInputStream(sourceFile).getChannel();
			  contentDestination = new FileOutputStream(destFile).getChannel();
			  contentDestination.transferFrom(contentSource, 0, contentSource.size());
		}
		catch( Exception e )
		{
			System.err.println("CopyFile failure...");
	        System.err.println(e);
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

	// XML escape characters
	public static String escapeXML(String inputString)
	{
		String outputString  = inputString.replace("&","&amp;");
		outputString = outputString.replace("<","&lt;");
		outputString = outputString.replace(">","&gt;");
		outputString = outputString.replace("\"","&quot;");
		outputString = outputString.replace("\'","&apos;");

		//The Hail Mary
        outputString = outputString.replace("\u2070",""); //Superscript 0
        outputString = outputString.replace("\u00B9",""); //Superscript 1
        outputString = outputString.replace("\u00B2",""); //Superscript 2
        outputString = outputString.replace("\u00B3",""); //Superscript 3
        outputString = outputString.replace("\u2074",""); //Superscript 4
        outputString = outputString.replace("\u2075",""); //Superscript 5
        outputString = outputString.replace("\u2076",""); //Superscript 6
        outputString = outputString.replace("\u2077",""); //Superscript 7
        outputString = outputString.replace("\u2078",""); //Superscript 8
        outputString = outputString.replace("\u2079",""); //Superscript 9
        outputString = outputString.replace("\u207A",""); //Superscript Plus Sign
        outputString = outputString.replace("\u207B",""); //Superscript Minus
        outputString = outputString.replace("\u207C",""); //Superscript Equals
        outputString = outputString.replace("\u207D",""); //Superscript Left parenthesis
        outputString = outputString.replace("\u207E",""); //Superscript Right parenthesis
        outputString = outputString.replace("\u2071",""); //Superscript Latin i
        outputString = outputString.replace("\u207F",""); //Superscript Latin n
        outputString = outputString.replace("\u2080",""); //Subscript zero
        outputString = outputString.replace("\u2081",""); //Subscript 1
        outputString = outputString.replace("\u2082",""); //Subscript 2
        outputString = outputString.replace("\u2083",""); //Subscript 3
        outputString = outputString.replace("\u2084",""); //Subscript 4
        outputString = outputString.replace("\u2085",""); //Subscript 5
        outputString = outputString.replace("\u2086",""); //Subscript 6
        outputString = outputString.replace("\u2087",""); //Subscript 7
        outputString = outputString.replace("\u2088",""); //Subscript 8
        outputString = outputString.replace("\u2089",""); //Subscript 9
        outputString = outputString.replace("\u208A",""); //Subscript Plus sign
        outputString = outputString.replace("\u208B",""); //Subscript minus
        outputString = outputString.replace("\u208C",""); //Subscript Equals
        outputString = outputString.replace("\u208D",""); //Subscript Left Par
        outputString = outputString.replace("\u208E",""); //Subscript Right Par
        outputString = outputString.replace("\u2080",""); //Subscript zero
        outputString = outputString.replace("\u001B",""); //Escape Char
		return outputString;
	}

	public static Boolean checkValidXML(String filePath)
	{
		File xmlFile = new File(filePath);
		if (xmlFile.exists()) {
			if (isValidXMLFile(xmlFile.getAbsolutePath().toString())) {
				System.out.println("Valid XML");
				return true;
			}
		}
		System.out.println("NOT VALID");
		return false;
	}

	private static boolean isValidXMLFile(String filename)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			File f = new File(filename);
			if (f.exists())
			{
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(f);
				return true;
			}
		} catch (SAXParseException spe) {
			System.out.println("Invalid XML");
			return false;


		} catch (SAXException sxe) {
			System.out.println("Invalid XML");
			return false;


		} catch (ParserConfigurationException pce) {
			System.out.println("Invalid XML");
			return false;


		} catch (IOException ioe) {
			System.out.println("Invalid XML");
			return false;
		}
		return true;
	}
 } //End MetaDataImport class
