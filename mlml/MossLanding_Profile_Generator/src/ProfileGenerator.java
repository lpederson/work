import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

import javax.swing.JFileChooser;

public class ProfileGenerator 
{
	public static void main( String[] args ) 
	{
		System.out.println("Choose a file...");
		String path = promptForFile();
		String s = "";
		try
		{
			s = readFileAsString(path);
		}
		catch(Exception e)
		{
			System.out.println("Error converting file to string.");
			System.exit(1);
		}
		String [] lines = s.split("\n");
		String [] fields = lines[1].split("\\|");
		String [] values = lines[3].split("\\|");
		
		for(int i=0;i<fields.length;i++)
		{
			fields[i] = fields[i].trim();
			values[i] =  values[i].trim();
		}
		
		//Crosswalk
		for(int i=0;i<fields.length;i++)
		{
			if(fields[i].equalsIgnoreCase("mlmlid"))
			{
				fields[i] = "FIELD_NUM";
				continue;
			}
			if(fields[i].equalsIgnoreCase("common name"))
			{
				fields[i] = "commonname";
				continue;
			}
			if(fields[i].equalsIgnoreCase("examiner name"))
			{
				fields[i] = "examiner";
				continue;
			}
			if(fields[i].equalsIgnoreCase("city"))
			{
				fields[i] = "CITY";
				continue;
			}
			if(fields[i].equalsIgnoreCase("body of water"))
			{
				fields[i] = "ocean_name";
				continue;
			}
			if(fields[i].equalsIgnoreCase("locality details"))
			{
				fields[i] = "locality_detail";
				continue;
			}
			if(fields[i].equalsIgnoreCase("lattitude"))
			{
				fields[i] = "lat";
				continue;
			}
			if(fields[i].equalsIgnoreCase("longitude"))
			{
				fields[i] = "LON";
				continue;
			}
			if(fields[i].equalsIgnoreCase("location determined by"))
			{
				fields[i] = "lat_achieved";
				continue;
			}
			if(fields[i].equalsIgnoreCase("restrand"))
			{
				fields[i] = "restrand_flag";
				continue;
			}
			if(fields[i].equalsIgnoreCase("group event"))
			{
				fields[i] = "group_event_flag";
				continue;
			}
			if(fields[i].equalsIgnoreCase("ume"))
			{
				fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("human interaction"))
			{
				fields[i] = "human_interaction";
				continue;
			}
			if(fields[i].equalsIgnoreCase("other findings"))
			{
				fields[i] = "other_how_determined";
				continue;
			}
			if(fields[i].equalsIgnoreCase("first observed where"))
			{
				fields[i] = "how_observed";
				continue;
			}
			if(fields[i].equalsIgnoreCase("initial condition"))
			{
				fields[i] = "ob_status_id";
				continue;
			}
			if(fields[i].equalsIgnoreCase("not able to examine"))
			{
				fields[i] = "exam_flag";
				continue;
			}
			
			///FIXXXXX
			if(fields[i].equalsIgnoreCase("examination date"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			
			///FIXXXX
			if(fields[i].equalsIgnoreCase("condition at exam"))
			{
				fields[i] = "exam_condid";
				continue;
			}
			
			if(fields[i].equalsIgnoreCase("sex"))
			{
				fields[i] = "sex_cd";
				continue;
			}
			if(fields[i].equalsIgnoreCase("age class"))
			{
				fields[i] = "age_class";
				continue;
			}
			
			//UGHGH
			if(fields[i].equalsIgnoreCase("carcass whole"))
			{
				fields[i] = "morpho_whole_carcass";
				continue;
			}
			
			if(fields[i].equalsIgnoreCase("straight length"))
			{
				fields[i] = "length";
				continue;
			}
			if(fields[i].equalsIgnoreCase("units length"))
			{
				fields[i] = "l_units";
				continue;
			}
			//asdfasdf
			if(fields[i].equalsIgnoreCase("actual length"))
			{
				fields[i] = "l_type";
				continue;
			}
			
			if(fields[i].equalsIgnoreCase("weight"))
			{
				fields[i] = "weight";
				continue;
			}
			if(fields[i].equalsIgnoreCase("units weight"))
			{
				fields[i] = "w_units";
				continue;
			}
			if(fields[i].equalsIgnoreCase("actual weight"))
			{
				fields[i] = "w_type";
				continue;
			}
			if(fields[i].equalsIgnoreCase("photos/videos"))
			{
				fields[i] = "photo_flag";
				continue;
			}
			if(fields[i].equalsIgnoreCase("photo/video disposition"))
			{
				fields[i] = "disposition";
				continue;
			}
			if(fields[i].equalsIgnoreCase("carcass status"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("status info"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("specimen disposition"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("disposition info"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("necropsied"))
			{
				//fields[i] = "UME_FLAG";
				continue;
			}
			if(fields[i].equalsIgnoreCase("additional remarks"))
			{
				fields[i] = "comments";
				continue;
			}
		}
			
		try{
			
			Class.forName("org.sqlite.JDBC");
		    Connection conn = DriverManager.getConnection("jdbc:sqlite:formhistory.sqlite");
		    Statement stat = conn.createStatement();
		    stat.executeUpdate("drop table if exists moz_formhistory;");
		    stat.executeUpdate("CREATE TABLE moz_formhistory(id INTEGER PRIMARY KEY,fieldname TEXT NOT NULL,value TEXT NOT NULL,timesUsed INTEGER,firstUsed INTEGER,lastUsed INTEGER,guid TEXT);");
		    stat.executeUpdate("create index moz_formhistory_index ON moz_formhistory(fieldname);");
		    stat.executeUpdate("create index moz_formhistory_lastused_index ON moz_formhistory(lastUsed);");
		    stat.executeUpdate("create index moz_formhistory_guid_index ON moz_formhistory(guid);");
		   
		    PreparedStatement prep = conn.prepareStatement("insert into moz_formhistory values (?,?,?,?,?,?,?);");

		    prep.setString(1,555+"");
		    prep.setString(2,"selection_single");
		    prep.setString(3,"00001");
		    prep.addBatch();
		    /*
		    for(int i=0;i<fields.length;i++)
		    {
		    	prep.setString(1,(i+1+""));
		    	prep.setString(2,fields[i]);
		    	prep.setString(3, values[i]);
		    	prep.addBatch();
		    }
		    */
		    /*
		    prep.setString(1, "1");
		    prep.setString(2, "user_name");
		    prep.setString(3, "USERNAME!");
		    prep.addBatch();
		    prep.setString(1, "2");
		    prep.setString(2, "full_name");
		    prep.setString(3, "myName");
		    prep.addBatch();
		    */
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		    
		    /*
		    ResultSet rs = stat.executeQuery("select * from moz_formhistory;");
		    while (rs.next()) {
		      System.out.println("name = " + rs.getString("1"));
		      System.out.println("job = " + rs.getString("2"));
		    }
		    */
		    //conn.commit();
		    //rs.close();
		    conn.close();
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		
	}
	public static String promptForFile()
	{
		JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        String filename = file.getName();
        System.out.println("You have selected: " + filename);
        return file.getAbsolutePath();
	}
	private static String readFileAsString(String filePath) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}