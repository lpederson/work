/* Acrobat 7.0 or later required
Prior to running this script for the first time, be sure the global variable global.startBatch is undefined. If necessary, execute delete global.startBatch from the console.
*/

/*
Author: Luke Pederson
Naval Postgraduate School - Dudley Knox Library
Email: lbpeders@nps.edu
*/

try{
//Trim
function trim(str) {
		str = str.replace(/\s{2,}/g," ");
		str = str.replace(/^\s+|\s+$/g,"");
		str = str.replace(/\(\s/g,"(");
		str = str.replace(/\s\)/g,")");
		return str;
}

function toTitleCase(str)
{
    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}

function fixAuthor(str){
	//Seperate authors
	str = str.replace(/,/g,";");
	str = str.replace(/(\s+);/g,";");
	str = str.replace(/;(\s+)/g,";");
	
	//Now we swap first and last names -> last, first m.
	//Multiple Authors
	if(str.search(";") != -1)
	{
		var Authors = str.split(";");
		str = "";
		len = Authors.length;
		for(var i = 0 ; i < len ; i++)
		{
			if(Authors[i].search(" ") != -1 && Authors[i].length > 0)
			{
				//Grab last name & first name
				var lastName = Authors[i].substring(Authors[i].lastIndexOf(" "),Authors[i].length);
				var firstName = Authors[i].substring(0,Authors[i].lastIndexOf(" "));
				Authors[i] = lastName + ", " + firstName;
				Authors[i] = trim(Authors[i]);
				str += Authors[i];
				if((i+1) != len)
					str += ";";
			}
			else
			{
				str += Authors[i];
				if((i+1) != len)
					str += ";";
			}
		}
	}
	//1 Author
	else
	{
		if(str.search(" ") != -1 && str.length > 0)
		{
			var lastName = str.substring(str.lastIndexOf(" "),str.length);
			var firstName = str.substring(0,str.lastIndexOf(" "));
			str = lastName + ", " + firstName;
			str = trim(str);
		}
		else
		{
			str = trim(str);
		}
	}
	return str;
}


if ( typeof global.startBatch == "undefined" ) {
	global.startBatch = true;
	
	// When we begin, we create a blank doc in the viewer to hold the
	// attachment.
	global.myContainer = app.newDoc();
	global.myContainer.addWatermarkFromText("The generated spreadsheet is an attachment of this document.\nTo locate: Open the \"View\" dropdown in the Menu Bar\nThen locate \"Navigation Panels\", and open \"Attachments\".",0,font.Helv,20,color.black);
	
	// Create an attachment and some fields separated by tabs
	global.myContainer.createDataObject({
	cName: "mySummary.xls",
	cValue: "dc.contributor.author\tdc.title\tdc.date\tdc.date.issued\tdc.publisher\tdc.description.abstract\tdc.subject.author\tpath\r\n"});
}
/*
else
{
	delete global.startBatch;
}
*/

/************************** Extract first 7 Pages *********************/
    if(this.numPages > 6)
    {
		var OLine = "";
        var dataLine = "";

        /* Spell Check a Document */
        var Word, numWords, i, j;
        for ( i = 0; i < 6; i++ )
        {
            numWords = this.getPageNumWords(i);
            for ( j = 0; j < numWords; j++)
                {
                    Word = this.getPageNthWord(i,j,false);
                        if ( Word != null )
                        {
                               dataLine += Word;
                       }
                }
        }
		
		//Remove extraneous white space
		dataLine = dataLine.replace(/\s{2,}/g," ");
		
		// Partial Dataline Pg 5 - Thesis Advisor and Second Reader
		var pDataLine = "";
		for ( i = 3; i < 6; i++ )
        {
            numWords = this.getPageNumWords(i);
            for ( j = 0; j < numWords; j++)
                {
                    Word = this.getPageNthWord(i,j,false);
                        if ( Word != null )
                        {
                               pDataLine += Word;
                       }
                }
        }
		
		// Another partial Dataline pg5 - Affiliation (Navy, Army, etc.)
		// Check below for usage...
		var apDataLine = pDataLine;
		
		//This gets set in Author
		var affAuthor = "";
		
/****************************** End Extract ************************************/
	
	/**************************** Special Chars & Variable Ini ***************************/
	//Replace special characters - Everything in brackets is the white-list [whitelist]
	dataLine = dataLine.replace(/[^\w\s\n\r\/.,\[\]{}\\~`;;"'\|!@#$%^&*()+=_-]/gi, "")
	
	var captured = [];
	var len;
	var re = "";
	
	//For checking whether to get [advisor and second reader] from [1st or 5th] page...
	var Flag = true;
	
	console.println("");
	console.println("**********Begin: Errors for " + fileName + "*************");
	/*********************************** End Special Char *********************************/
	
	
/***************** Strip New Lines ****************/	
//Get rid of all new line characters...

	//Replace newline
	dataLine = dataLine.replace(/\n/g,"");
	dataLine = dataLine.replace(/\r/g,"");
/**************************************************/	
	
/*******************************************Begin: Finding authors*********************************************/

	re = /(?:6\.?\s{0,4})(?:AUTHOR)(?:\s{0,4})(?:\(?)(?:s?)(?:\)?)(?:\s{0,4})(\D*)(?:\d\.?)/i;
	try{
		if(re.test(dataLine))
		{
			captured = re.exec(dataLine);
			len = captured.length;
			if(trim(captured[1]))
			{
				captured[1] = trim(captured[1]);
				
				//Replacing " and " with ", "
				//fixAuthor handles commas
				captured[1] = captured[1].replace(/(,?)(\s{1,4})(and)(\s{1,4})/gi,", ");
				OLine += fixAuthor(captured[1]);
				
				//Used for searching for "(author)\n(affiliation)\n"
				affAuthor = captured[1];			
			}
			else
			{
				if(trim(captured[0]))
				{
					console.println("Author: There was a problem with the match seperation, so the ENTIRE match was used...");
					OLine += trim(captured[0]);
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to capture author...");
				}
			}
			captured = [];
			re = "";
			len = 0;
		}
		else
		{
			OLine += "ERROR";
			console.println("Fail in matching author...");
		}
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";
	
/*******************************************End: Finding authors*********************************************/

/*******************************************Begin: Finding Title*********************************************/

	re = /(?:4\.?\s{0,4})(?:TITLE)(?:\s{0,4})(?:AND?)(?:\s{0,4})(?:SUBTITLE?)(?:[:]{0,3})(?:\s{0,4})(.*)(?:5a?)(?:\.?\s{0,4})/i;
	try
	{
		if(re.test(dataLine))
			{
			captured = [];
			captured = re.exec(dataLine);
			len = captured.length;
			if(trim(captured[1]))
			{
				captured[1] = captured[1].replace(/(6\.?\s{0,4})(Author)(.*)/i,"");
				captured[1] = captured[1].replace("AND SUBTITLE", "");
				captured[1] = captured[1].replace("Title (Mix case letters)", "");
				OLine += trim(captured[1]);
			}
			else
			{
				if(trim(captured[0]))
				{
					console.println("Title: There was a problem with the match seperation, so the ENTIRE match was used...");
					OLine += trim(captured[0]);
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to capture title...");
				}
			}
		}
		else
		{
			re = /(?:4\.?\s{0,4})(?:TITLE)(?:[:]{0,3})(?:\s{0,4})(.*)(?:6\.?\s{0,4}AUTH)/i;
			if(re.test(dataLine))
			{
				captured = [];
				captured = re.exec(dataLine);
				len = captured.length;
				if(trim(captured[1]))
				{
					captured[1] = captured[1].replace(/(5\.?\s{0,4})(Fund)(.*)/i,"");
					captured[1] = captured[1].replace("AND SUBTITLE", "");
					captured[1] = captured[1].replace("Title (Mix case letters)", "");
					OLine += trim(captured[1]);
				}
				else
				{
					if(trim(captured[0]))
					{
						console.println("Title: There was a problem with the match seperation, so the ENTIRE match was used...");
						OLine += trim(captured[0]);
					}
					else
					{
						OLine += "ERROR";
						console.println("Unable to capture title...");
					}
				}
			}
			else
			{
				OLine += "ERROR";
				console.println("Unable to match Title...");
			}
		}
		captured = [];
		re = "";
		len = 0;
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";

/*******************************************End: Finding Title*********************************************/

/*******************************************Begin: Finding Date & Prep ISO*********************************************/
	var dateISO = "";
	re = /(?:2\.?\s{0,4})(?:REPORT)(?:\s{0,4})(?:DATE?)(?:[:]{0,3})(?:\s{0,4})(.*)(?:3\.?\s{0,4})(?:REPORT)/i;
	try
	{
		if(re.test(dataLine))
			{
			captured = [];
			captured = re.exec(dataLine);
			len = captured.length;
			if(trim(captured[1]))
			{
				captured[1] = captured[1].replace("(DD-MM-YYYY)","");
				OLine += trim(captured[1]);
				dateISO = trim(captured[1]);
			}
			else
			{
				if(trim(captured[0]))
				{
					console.println("Date: There was a problem with the match seperation, so the ENTIRE match was used...");
					OLine += trim(captured[0]);
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to capture Date...");
				}
			}
			captured = [];
			re = "";
			len = 0;
		}
		else
		{
			re = /(?:1\.?\s{0,4})(?:REPORT)(?:\s{0,4})(?:DATE?)(?:[:]{0,3})(?:\s{0,4})(.*)(?:2\.?\s{0,4})(?:REPORT)/i;
			if(re.test(dataLine))
				{
				captured = [];
				captured = re.exec(dataLine);
				len = captured.length;
				if(trim(captured[1]))
				{
					captured[1] = captured[1].replace("(DD-MM-YYYY)","");
					OLine += trim(captured[1]);
					dateISO = trim(captured[1]);
				}
				else
				{
					if(trim(captured[0]))
					{
						console.println("Date: There was a problem with the match seperation, so the ENTIRE match was used...");
						OLine += trim(captured[0]);
					}
					else
					{
						OLine += "ERROR";
						console.println("Unable to capture Date...");
					}
				}
				captured = [];
				re = "";
				len = 0;
			}
			else
			{
				OLine += "ERROR";
				console.println("Unable to match Date...");
			}
		}
	}
	catch(e)
	{
		console.println(fileName + ": Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";

/*******************************************End: Finding Date & Prep ISO*********************************************/

/*******************************************End: Finding ISO*********************************************/
	try
	{
		if(dateISO.length > 0)
		{
			//ISO
			re = /\d{4}/;
			var year = re.exec(dateISO);
			re = /[a-zA-Z]*/;
			var month = re.exec(dateISO);
			if(trim(month[0]))
			{
				if(month[0].search(/Jan/i) != -1)
					OLine += year + "-" + "01";
				if(month[0].search(/Feb/i) != -1)
					OLine += year + "-" + "02";
				if(month[0].search(/Mar/i) != -1)
					OLine += year + "-" + "03";
				if(month[0].search(/Apr/i) != -1)
					OLine += year + "-" + "04";
				if(month[0].search(/May/i) != -1)
					OLine += year + "-" + "05";
				if(month[0].search(/Jun/i) != -1)
					OLine += year + "-" + "06";
				if(month[0].search(/Jul/i) != -1)
					OLine += year + "-" + "07";
				if(month[0].search(/Aug/i) != -1)
					OLine += year + "-" + "08";
				if(month[0].search(/Sep/i) != -1)
					OLine += year + "-" + "09";	
				if(month[0].search(/Oct/i) != -1)
					OLine += year + "-" + "10";
				if(month[0].search(/Nov/i) != -1)
					OLine += year + "-" + "11";
				if(month[0].search(/Dec/i) != -1)
					OLine += year + "-" + "12";
				//END ISO
			}
			else
			{
				OLine += "ERROR";
				console.println("Unable to convert to ISO...");
			}
		}
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";
	dateISO = "";
	re = "";
/*******************************************End: Finding ISO*********************************************/

/******************************************* Begin: Publisher ********************************************/
//This is going to be a blanket statement...
	OLine += "Monterey, California: Naval Postgraduate School";
	OLine += "\t";
/******************************************* End: Publisher ********************************************/

/*******************************************Begin: Finding Abstract*********************************************/
	//(?:(?:(?:\(?)(?:\s{0,2}?)(?:maximum?)(?:\s+?)(?:200?)(?:\s+?)(?:words?)(?:\s{0,2}?)(?:\)?)){0,1})
	re = /(?:13\.?\s{0,4})(?:ABSTRACT)(?:[:]{0,3})(?:\s{0,4})(.*)(?:14\.?\s{0,4})(?:Subj)/i;
	try
	{
		if(re.test(dataLine))
		{
			captured = [];
			captured = re.exec(dataLine);
			len = captured.length;
			if(trim(captured[1]))
			{
				captured[1] = captured[1].replace(/(\()(\s{0,4})(maximum)(\s{0,4})(\d{3})(\s{0,4})(words)(\s{0,4})(\))(\s{0,4})/i,"");
				captured[1] = captured[1].replace(/(\()(\s{0,4})(maximum)(\s{0,4})(\d{3})(\s{0,4})(words)(\s{0,4})(\.\))(\s{0,4})/i,"");
				captured[1] = captured[1].replace(/(14)(\.?)(\s{0,4})(Subject)(\s{0,4})(Term)(.*)/i,"");
				OLine += trim(captured[1]);
			}
			else
			{
				if(trim(captured[0]))
				{
					console.println("Abstract: There was a problem with the match seperation, so the ENTIRE match was used...");
					OLine += trim(captured[0]);
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to capture Abstract...");
				}
			}
			captured = [];
			re = "";
			len = 0;
		}
		else
		{
			re = /(?:14\.?\s{0,4})(?:ABSTRACT)(?:[:]{0,3})(?:\s{0,4})(.*)(?:15\.?\s{0,4})(?:Subj)/i;
			if(re.test(dataLine))
			{
				captured = [];
				captured = re.exec(dataLine);
				len = captured.length;
				if(trim(captured[1]))
				{
					captured[1] = captured[1].replace(/(\()(\s{0,4})(maximum)(\s{0,4})(\d{3})(\s{0,4})(words)(\s{0,4})(\))(\s{0,4})/i,"");
					captured[1] = captured[1].replace(/(\()(\s{0,4})(maximum)(\s{0,4})(\d{3})(\s{0,4})(words)(\s{0,4})(\.\))(\s{0,4})/i,"");
					captured[1] = captured[1].replace(/(15)(\.?)(\s{0,4})(Subject)(\s{0,4})(Term)(.*)/i,"");
					OLine += trim(captured[1]);
				}
				else
				{
					if(trim(captured[0]))
					{
						console.println("Abstract: There was a problem with the match seperation, so the ENTIRE match was used...");
						OLine += trim(captured[0]);
					}
					else
					{
						OLine += "ERROR";
						console.println("Unable to capture Abstract...");
					}
				}
				captured = [];
				re = "";
				len = 0;
			}
			else
			{
				OLine += "ERROR";
				console.println("Unable to match Abstract...");
			}
		}
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";

/*******************************************End: Finding Abstract*********************************************/

/*******************************************Begin: Finding Subject Terms*********************************************/

	re = /(?:1\d\.?\s{0,4})(?:SUBJECT)(?:\s{0,4})(?:TERMS)(?:[:]{0,3})(?:\s{0,4})(.*)(?:1\d.?\s{0,4})(?:Number)/i;
	try
	{
		if(re.test(dataLine))
		{
			captured = [];
			captured = re.exec(dataLine);
			len = captured.length;
			if(trim(captured[1]))
			{
				captured[1] = captured[1].replace(/,/g,";");
				captured[1] = captured[1].replace(/(\s+);/g,";");
				captured[1] = captured[1].replace(/;(\s+)/g,";");
				captured[1] = captured[1].replace(/(15\.?)(\s+?)(.*)/i,"");
				OLine += trim(captured[1]);
			}
			else
			{
				if(trim(captured[0]))
				{
					console.println("Subject Terms: There was a problem with the match seperation, so the ENTIRE match was used...");
					OLine += trim(captured[0]);
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to capture Subject Terms...");
				}
			}
			captured = [];
			re = "";
			len = 0;
		}
		else
		{
			re = /(?:1\d\.?\s{0,4})(?:SUBJECT)(?:\s{0,4})(?:TERMS)(?:[:]{0,3})(?:\s{0,4})(.*)(?:1\d.?\s{0,4})(?:Sec)/i;
			if(re.test(dataLine))
			{
				captured = [];
				captured = re.exec(dataLine);
				len = captured.length;
				if(trim(captured[1]))
				{
					captured[1] = captured[1].replace(/,/g,";");
					captured[1] = captured[1].replace(/(\s+);/g,";");
					captured[1] = captured[1].replace(/;(\s+)/g,";");
					captured[1] = captured[1].replace(/(15\.?)(\s+?)(.*)/i,"");
					OLine += trim(captured[1]);
				}
				else
				{
					if(trim(captured[0]))
					{
						console.println("Subject Terms: There was a problem with the match seperation, so the ENTIRE match was used...");
						OLine += trim(captured[0]);
					}
					else
					{
						OLine += "ERROR";
						console.println("Unable to capture Subject Terms...");
					}
				}
				captured = [];
				re = "";
				len = 0;
			}
			else
			{
				re = /(?:1\d\.?\s{0,4})(?:SUBJECT)(?:\s{0,4})(?:TERMS)(?:[:]{0,3})(?:\s{0,4})(.*)(?:1\d.?\s{0,4})(?:Price)/i;
				if(re.test(dataLine))
				{
					captured = [];
					captured = re.exec(dataLine);
					len = captured.length;
					if(trim(captured[1]))
					{
						captured[1] = captured[1].replace(/,/g,";");
						captured[1] = captured[1].replace(/(\s+);/g,";");
						captured[1] = captured[1].replace(/;(\s+)/g,";");
						captured[1] = captured[1].replace(/(15\.?)(\s+?)(.*)/i,"");
						OLine += trim(captured[1]);
					}
					else
					{
						if(trim(captured[0]))
						{
							console.println("Subject Terms: There was a problem with the match seperation, so the ENTIRE match was used...");
							OLine += trim(captured[0]);
						}
						else
						{
							OLine += "ERROR";
							console.println("Unable to capture Subject Terms...");
						}
					}
					captured = [];
					re = "";
					len = 0;
				}
				else
				{
					OLine += "ERROR";
					console.println("Unable to match Subject Terms...");
				}
			}
		}
	}
	catch(e)
	{
		console.println("Error on line " + e.lineNumber + ": " + e);
	}
	OLine += "\t";


/*******************************************End: Finding Subject Terms*********************************************/


	//Insert file name is last tab
	var fileName = this.documentFileName;
	OLine += fileName;


	console.println("********* End of Errors for " + fileName + "**********");
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
    else
    {console.println("Error: " + this.documentFileName + " has less than 7 pages");}
} 
catch(e) 
{
    console.println("Error on line " + e.lineNumber + ": " + e); 
	delete typeof global.startBatch;
    event.rc = false; // abort batch
}