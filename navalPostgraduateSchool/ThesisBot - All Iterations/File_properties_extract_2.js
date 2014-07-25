/* Acrobat 7.0 or later required
Prior to running this script for the first time, be sure the global variable global.startBatch is undefined. If necessary, execute delete global.startBatch from the console.
*/

/*
Author: Luke Pederson
Naval Postgraduate School - Dudley Knox Library
Email: lbpeders@nps.edu
*/

try
{

	if ( typeof global.startBatch == "undefined" ) {
		global.startBatch = true;
		
		// When we begin, we create a blank doc in the viewer to hold the
		// attachment.
		global.myContainer = app.newDoc();
		global.myContainer.addWatermarkFromText("The generated spreadsheet is an attachment of this document.\nTo locate: Open the \"View\" dropdown in the Menu Bar\nThen locate \"Navigation Panels\", and open \"Attachments\".",0,font.Helv,20,color.black);
		
		// Create an attachment and some fields separated by tabs
		global.myContainer.createDataObject({
		cName: "mySummary.xls",
		cValue: "FileName\tTitle\tAuthor\tSubject\tKeywords\tDescription\tCreationDate\tCategory\tCompany\tStatus\tModDate\tCreator\tSourceModified\tProducer\tContactEmail\r\n"});
	
			//Create array of global metadata tags
			//These are the default ones from the files that we sent over to test on...
			//You can add more if you want
			global.masterTags = [];
			global.masterTags[0]  = "Title";
			global.masterTags[1]  = "Author";
			global.masterTags[2]  = "Subject";
			global.masterTags[3]  = "Keywords";
			global.masterTags[4]  = "Description";
			global.masterTags[5]  = "CreationDate";
			global.masterTags[6]  = "Category";
			global.masterTags[7]  = "Company";
			global.masterTags[8]  = "Status";
			global.masterTags[9]  = "ModDate";
			global.masterTags[10] = "Creator";
			global.masterTags[11] = "SourceModified";
			global.masterTags[12] = "Producer";
			global.masterTags[13] = "ContactEmail";
			
			
			//Create array to store the Custom Tags
			global.customTags = [];
			
			//Length of the masterTag array
			global.masterLength = global.masterTags.length;
	}
	try
	{
		//For capturing the custom document metadata tags
		var customMetaTags = [];
		var customMetaValues = [];

		//Captures values
		var documentMetaValues = [];

		//For all the metadata tags and values... load into seperate arrays with corresponding indices
		for (var i in this.info)
		{
			//If custom tag is found
			var Flag = false;
			
			//If we find a tag that is undefined, skip it
			if(i == undefined || i == "undefined" || i == "" || i == "Authors")
				continue;
			
			//Find with tag matches masterTag[index]
			for(var j=0;j<global.masterTags.length;j++)
			{
				if(i == global.masterTags[j])
				{
					documentMetaValues[j] = this.info[i];
					Flag = true;
					break;
				}
			}
			//If  not found
			if(!Flag)
			{
				var matchCustomTag = false;
				
				//console.println("Length of custom Tag array: " + global.customTags.length);
				
				//See if we have already captured the custom tag
				for(var k=0;k<global.customTags.length;k++)
				{
					//console.println("Comparing " + i + " vs " + global.customTags[k]);
					
					//We already found the custom tag in a previous document
					if(i == global.customTags[k])
					{
						customMetaTags[k] = i;
						customMetaValues[k] = this.info[i];	
						matchCustomTag = true;
						break;
					}
				}
				//If we didn't have custom tag, add it to the customTag array
				if(!matchCustomTag)
				{
					console.println("Found new tag, adding " + i + " to custom tags.");
					global.customTags.push(i)
					customMetaTags[global.customTags.length-1] = i;
					customMetaValues[global.customTags.length-1] = this.info[i];
				}
			}
		};
		
		//The list is updated and printed after every document that is processed
		if(global.customTags.length != 0)
		{
			var tempString = "";
			for(var i=0;i<global.customTags.length;i++)
			{
				tempString += global.customTags[i] + "\t";
			}
			
			console.println("Updated Custom Tags...\n" + tempString);
		}
		
		//Load the metadata values to the spreadsheet
		var OLine = this.documentFileName + "\t";
		
		for(var i=0;i<documentMetaValues.length;i++)
		{
			if(documentMetaValues[i] != undefined)
			{
				OLine += documentMetaValues[i];
			}
			OLine += "\t";
		}	
		
		//Load the custom metadata values, after the default ones
		for(var i=0;i<customMetaTags.length;i++)
		{
			if(customMetaTags[i] != undefined)
			{
				OLine += customMetaValues[i];
			}
			OLine += "\t";
		}
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e); 
	}
	OLine += "\r\n";

        // Get the data object contents as a file stream 
       var oFile = global.myContainer.getDataObjectContents("mySummary.xls"); 
	   
	   // Convert the stream to a string
       var cFile = util.stringFromStream(oFile, "utf-8");
        
       //Concatenate the new lines.
        cFile += OLine;
        OLine = "";
     
       //Convert back to a file stream
        oFile = util.streamFromString( cFile, "utf-8" ); 
      
       //and update the file attachment
        global.myContainer.setDataObjectContents({cName: "mySummary.xls", oStream: oFile });  
}
catch(e) 
{
    console.println("Error on line " + e.lineNumber + ": " + e); 
	delete typeof global.startBatch;
    event.rc = false; // abort batch
}