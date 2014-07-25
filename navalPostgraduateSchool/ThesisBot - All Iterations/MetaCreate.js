/* Acrobat 7.0 or later required
Prior to running this script for the first time, be sure the global variable global.startBatch is undefined. If necessary, execute delete global.startBatch from the console.
*/

/*
Author: Luke Pederson
Naval Postgraduate School - Dudley Knox Library
Email: lbpeders@nps.edu
*/

try{
	if ( typeof global.startBatch == "undefined" ) {
		global.startBatch = true;	
	}

	var reply = "";
	do
	{
		reply = app.response("Enter: Field|Value","Custom Metadata","");
		var inputs = reply.split("|");
		if(inputs[0].length == 0 || inputs[1] == null)
		{
			//reply = "ex. Title";
			console.println("Bad input, try again...");
		}
		else
		{
			eval("this.info." + inputs[0] + "=\"" + inputs[1] + "\";");
		}
	}while(reply != "n");	
} 
catch(e) 
{
    console.println("Error on line " + e.lineNumber + ": " + e); 
	delete typeof global.startBatch;
    event.rc = false; // abort batch
}