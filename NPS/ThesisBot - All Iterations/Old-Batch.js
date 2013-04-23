/* Copy comments to a tab-delimited file */
/* Acrobat 7.0 or later required
** Prior to running this script for the first time, be sure the global
** variable global.startBatch is undefined. If necessary, execute
** delete global.startBatch from the console.
*/
try{
//Trim
function trim(str) {
		return str.replace(/^\s+|\s+$/g,"");
}
//Regex Stuff
function generateREArray()
{
	var searchTerms = [];
	
	//This is just a spacer more readable numbering in the array
	searchTerms[0] = "";
	
	//[cell in array] Actual Document term
	//[1] 1 Agency Use
	var re1='(1)';	// Any Single Character 1
	var re2='(\\.)';
	var re3='(\\s+)';	// White Space 1
	var re4='(AGENCY)';	// Word 1
	var re5='(\\s+)';	// White Space 2
	var re6='(USE)';	// Word 2
	var re7='(\\s+)';	// White Space 3
	var re8='(ONLY)';	// Word 3
	var p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8,["i"]);
	searchTerms[1] = p;
	p = "";
	
	
	//[2] 2 Report Date
	re1='(2)';	// Any Single Character 1
	re2='(\\.)';
	re3='(\\s+)';	// White Space 1
	re4='(REPORT)';	// Word 1
	re5='(\\s+)';	// White Space 2
	re6='(DATE)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6,["i"]);
	searchTerms[2] = p;
	p = "";

	//[3] 3 Report Type
	re1='(3)';	// Any Single Character 1
	re2='(\\.)';
	re3='(\\s+)';	// White Space 1
	re4='(REPORT)';	// Word 1
	re5='(\\s+)';	// White Space 2
	re6='(TYPE)';	// Word 2
	re7='(\\s+)';	// White Space 3
	re8='(AND)';	// Word 3
	re9='(\\s+)';	// White Space 4
	re10='(DATES)';	// Word 4
	re11='(\\s+)';	// White Space 5
	re12='(COVERED)';	// Word 5
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12,["i"]);
	searchTerms[3] = p;
	p = "";
	
	//[4] 4 Title
	re1='(4)';	// Any Single Character 1
	re2='(\\.)';
	re4='(\\s+)';	// White Space 1
	re5='(TITLE)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(AND)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(SUBTITLE)';	// Word 3
	p = new RegExp(re1+re2+re4+re5+re6+re7+re8+re9,["i"]);
	searchTerms[4] = p;
	p = "";
	
	//[5] 5 Funding Number
	re1='(5)';	// Any Single Character 1
	re2='(\\.)';
	re3='(\\s+)';	// White Space 1
	re4='(FUNDING)';	// Word 1
	re5='(\\s+)';	// White Space 2
	re6='(NUMBERS)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6,["i"]);
	searchTerms[5] = p;
	p = "";
	
	//[6] 6 Authors
	re1='(6)';	// Any Single Character 1
	re2='(\\.)';
	re4='(\\s+)';	// White Space 1
	re5='(AUTHOR)';	// Word 1
	p = new RegExp(re1+re2+re4+re5,["i"]);
	searchTerms[6] = p;
	p = "";
	
	//[7] 7 Performing Orgs
	re1='(7)';	// Any Single Character 1
	re2='(\\.)';
	re3='(\\s+)';	// White Space 1
	re4='(PERFORMING)';	// Word 1
	re5='(\\s+)';	// White Space 2
	re6='(ORGANIZATION)';	// Word 2
	re7='(\\s+)';	// White Space 3
	re8='(NAME)';	// Word 3
	re9='(\\s+)';	// White Space 4
	re11='(AND)';	// Word 4
	re12='(\\s+)';	// White Space 5
	re13='(ADDRESS)';	// Word 5
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9+re11+re12+re13,["i"]);
	searchTerms[7] = p;
	p = "";
	
	//[8] 8 Performing Orgs Report Number
	re2='(8)';	// Any Single Character 1
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(PERFORMING)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(ORGANIZATION)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(REPORT)';	// Word 3
	re10='(\\s+)';	// White Space 4
	re11='(NUMBER)';	// Word 4
	p = new RegExp(re2+re3+re4+re5+re6+re7+re8+re9+re10+re11,["i"]);
	searchTerms[8] = p;
	p = "";
	
	//[9] 9 Sponsor Agency
	re1='(9)';	// Any Single Character 1
	re2='(\\.)';	// Non-greedy match on filler
	re3='(\\s+)';	// White Space 1
	re4='(SPONSORING)';	// Word 1
	re5='([\\s+/.])';	// White Space 2
	re6='(MONITORING)';	// Word 2
	re7='(\\s+)';	// White Space 3
	re8='(AGENCY)';	// Word 3
	re9='(\\s+)';	// White Space 4
	re10='(NAME)';	// Word 4
	re11='(\\s+)';	// White Space 5
	re13='(\\s+)';	// White Space 6
	re14='(AND)';	// Word 5
	re15='(\\s+)';	// White Space 7
	re16='(ADDRESS)';	// Word 6
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re13+re14+re15+re16,["i"]);
	searchTerms[9] = p;
	p = "";
	
	//[10] 10 Sponsor Agency Report #
	re2='(1)';	// Any Single Character 1
	re3='(0)';	// Any Single Character 2
	re4='(\\.)';	// Non-greedy match on filler
	re5='(\\s+)';	// White Space 1
	re6='(SPONSORING)';	// Word 1
	re7='([\\s+/.])';	// White Space 2
	re8='(MONITORING)';	// Word 2
	re9='(\\s+)';	// White Space 3
	re10='(AGENCY)';	// Word 3
	re11='(\\s+)';	// White Space 4
	re12='(REPORT)';	// Word 4
	re13='(\\s+)';	// White Space 5
	re14='(NUMBER)';	// Word 5
	p = new RegExp(re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12+re13+re14,["i"]);
	searchTerms[10] = p;
	p = "";
	
	//[11] 11 Supp. Notes
	re1='(1)';	// Any Single Character 1
	re2='(1)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(SUPPLEMENTARY)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(NOTES)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7,["i"]);
	searchTerms[11] = p;
	p = "";
	
	//[12] 12a Rights
	re1='(1)';	// Any Single Character 1
	re2='(2)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(DISTRIBUTION)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(AVAILABILITY)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(STATEMENT)';	// Word 3
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9,["i"]);
	searchTerms[12] = p;
	p = "";
	
	//[13] 12b Rights Code
	re1='(1)';	// Any Single Character 1
	re2='(2)';	// Any Single Character 2
	re3='(.)';	// Any Single Character 3
	re4='(\\.)';	// Non-greedy match on filler
	re5='(\\s+)';	// White Space 1
	re6='(DISTRIBUTION)';	// Word 1
	re7='(\\s+)';	// White Space 2
	re8='(CODE)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8,["i"]);
	searchTerms[13] = p;
	p = "";
	
	//[14] 13 Abstract
	re1='(1)';	// Any Single Character 1
	re2='(3)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(ABSTRACT)';	// Word 1
	p = new RegExp(re1+re2+re3+re4+re5,["i"]);
	searchTerms[14] = p;
	p = "";
	
	//[15] 14 Subject terms
	re2='(1)';	// Any Single Character 1
	re3='(4)';	// Any Single Character 2
	re4='(\\.)';	// Non-greedy match on filler
	re5='(\\s+)';	// White Space 1
	re6='(SUBJECT)';	// Word 1
	re7='(\\s+)';	// White Space 2
	re8='(TERMS)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8,["i"]);
	searchTerms[15] = p;
	p = "";
	
	//[16] 15 Pages
	re1='(1)';	// Any Single Character 1
	re2='(5)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(NUMBER)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(OF)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(PAGES)';	// Word 3
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9,["i"]);
	searchTerms[16] = p;
	p = "";
	
	//[17] 16 Price Code
	re1='(1)';	// Any Single Character 1
	re2='(6)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(PRICE)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(CODE)';	// Word 2
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7,["i"]);
	searchTerms[17] = p;
	p = "";
	
	//[18] 17 Security of Report
	re1='(1)';	// Any Single Character 1
	re2='(7)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(SECURITY)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(CLASSIFICATION)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(OF)';	// Word 3
	re10='(\\s+)';	// White Space 4
	re11='(REPORT)';	// Word 4
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11,["i"]);
	searchTerms[18] = p;
	p = "";
	
	//[19] 18 Security of Page
	re1='(1)';	// Any Single Character 1
	re2='(8)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(SECURITY)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(CLASSIFICATION)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(OF)';	// Word 3
	re10='(\\s+)';	// White Space 4
	re11='(THIS)';	// Word 4
	re12='(\\s+)';	// White Space 5
	re13='(PAGE)';	// Word 5
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12+re13,["i"]);
	searchTerms[18] = p;
	p = "";
	
	//[20] 19 Security of Abstract
	re1='(2)';	// Any Single Character 1
	re2='(0)';	// Any Single Character 2
	re3='(\\.)';	// Non-greedy match on filler
	re4='(\\s+)';	// White Space 1
	re5='(LIMITATION)';	// Word 1
	re6='(\\s+)';	// White Space 2
	re7='(OF)';	// Word 2
	re8='(\\s+)';	// White Space 3
	re9='(ABSTRACT)';	// Word 3
	p = new RegExp(re1+re2+re3+re4+re5+re6+re7+re8+re9,["i"]);
	searchTerms[19] = p;
	p = "";
	
	return searchTerms
}
//Finding Stuff
function locateItem(InStr,OutStr,LoTag,HiTag)
{
	var found = "";
	if(InStr.search(searchTags[LoTag]) != -1 && InStr.search(searchTags[HiTag]) != -1)
	{
		if( (InStr.search(searchTags[HiTag]) - InStr.search(searchTags[LoTag]) ) > 0)
		{
			found = InStr.substring(InStr.search(searchTags[LoTag]),InStr.search(searchTags[HiTag]));
			found = found.replace(searchTags[LoTag],"");
			if(trim(found).length > 0){
				OutStr += trim(found);
				found = "";
			}
		}
	}		
	OutStr+="\t";
	return OutStr;
}



if ( typeof global.startBatch == "undefined" ) {
	global.startBatch = true;
	
	// When we begin, we create a blank doc in the viewer to hold the
	// attachment.
	global.myContainer = app.newDoc();
	
	// Create an attachment and some fields separated by tabs
	global.myContainer.createDataObject({
	cName: "mySummary.xls",
	cValue: "FileName\tDate\tType\tTitle\tFunder\tAuthors\tPerformingOrgs\tPerformingReportNumber\tSponsoringAgency\tSponsoringReportNumber\tSupplementaryNots\tDistStatment\tDistCode\tAbstract\tSubjectTerms\tFormat\tPrice\tSecurityReport\tSecurityPage\tSecurityAbstract\tLimitOfAbstract\r\n"});
}// else 
//{
//	delete global.startBatch;
//}

    if(this.numPages > 7)
    {
		var OLine = "";
        var dataLine = "";

        /* Spell Check a Document */
        var Word, numWords, i, j;
        for ( i = 0; i < 7; i++ )
        {
            numWords = this.getPageNumWords(i);
            for ( j = 0; j < numWords; j++)
                {
                    Word = this.getPageNthWord(i,j,false);
                        if ( Word != null )
                        {
                               dataLine += Word;
                               dataLine += " ";
                       }
                }
        }
	//Replace special characters
	dataLine = dataLine.replace(/[^\w\s.,-_'"!=+()*~`@#$%]/gi, '')
	
	//Replace newline
	dataLine = dataLine.replace(/\n/g,"");
	dataLine = dataLine.replace(/\r/g,"");
	
	console.println(dataLine);
	
	var searchTags = []
	searchTags = generateREArray();
	
	var found = "";

	var FileName = this.documentFileName;
	OLine += FileName + "\t";	
	
	//[2] Report date
	//OLine = locateItem(dataLine,OLine,2,3);
	var len = searchTags.length;
	for(var i=2;i<len-1;i++){
	
		//This is for the numbers that go in order
		if(i != 4 && i != 5 && i != 6 && i != 14 && i != 15 && i !=16)
		{
			OLine = locateItem(dataLine,OLine,i,i+1);
		}
		
		//All the special cases where the numbers go out of order
		if(i == 4)
		{
			OLine = locateItem(dataLine,OLine,4,6);
		}
		if(i == 5)
		{
			OLine = locateItem(dataLine,OLine,5,7);
		}
		if(i == 6)
		{
			OLine = locateItem(dataLine,OLine,6,5);
		}
		if(i == 14)
		{
			var found = "";
				if(dataLine.search(searchTags[i]) != -1 && dataLine.search(searchTags[i+2]) != -1)
				{
					if( (dataLine.search(searchTags[i+2]) - dataLine.search(searchTags[i]) ) > 0)
					{
						found = dataLine.substring(dataLine.search(searchTags[i]),dataLine.search(searchTags[i+2]));
						found = found.replace(searchTags[i],"");
						
						if(trim(found).length > 0){
							var tempString = "(maximum 200 words)";
							if(found.length > tempString.length && found.search(tempString) != -1)
							{
								found = found.substring(tempString.length,found.length);
							}
							tempString = "maximum 200 words"
							if(found.length > tempString.length && found.search(tempString) != -1)
							{
								found = found.substring(tempString.length,found.length);
							}
							OLine += trim(found);
							found = "";
					}
				}
			}		
			OLine+="\t";
		}
		if(i == 15)
		{
			OLine = locateItem(dataLine,OLine,i,i+2);
		}
	}
	
	
	//No Regex for these yet
	/*
	if(dataLine.search("Thesis Advisor") != -1)
	{
		found = dataLine.substring((dataLine.search("Approved by") + "Approved by".length),dataLine.search("Thesis Advisor"));
		OLine += trim(found);
		OLine += "\t";
		found = "";
		
		if(dataLine.search("Second Reader") != -1)
		{
			found = dataLine.substring(dataLine.search("Thesis Advisor") + "Thesis Advisor".length,dataLine.search("Second Reader"));
			OLine += trim(found);
			OLine += "\t";
			found = "";
		}
	}
	*/
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
} catch(e) {
    console.println("Error on line " + e.lineNumber + ": " + e); delete typeof global.startBatch
    event.rc = false; // abort batch
}