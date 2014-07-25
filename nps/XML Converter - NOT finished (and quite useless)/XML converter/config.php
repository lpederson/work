<?PHP

//Directory where the final preppared information is going to be held
//Should be chmod 777 to avoid problems
$DS_SUITABLE="ds_suitable/";

//This is the directory where the downloaded data
//from the Internet Archive Scrapper is being stored
//the default is an already downloaded example
//in practice you'd want to point this to the same directory that IAScraper (http://elibtronic.ca/software/ia_scraper) is pointing to
$IA_DATA_DIR=$xml_locat;
$IME_DATA_DIR="/usr/share/webapps/xml_converter/out";

//The Web address of the data directory for viewing online
//Should be chmod 0777 to avoid problems
$DISPLAY_DIR = "./ds_suitable";

//Amount of items to process at a time
$DIR_LIMIT = 100;

//This will be the log file where the DSpace added titles are tracked
$TITLES_DONE = "csv/finished_titles.csv";

//The _l_ine _e_nd character can be specified here
//$LE = "\n"; //useful for analyzing with terminal, eg lynx
$LE = "<br>";  //useful for looking with a web browser


//// The following configs relate to your DSpace installation.  The software will create a 
///  txt file in the DS_SUITABLE directory with the appropriate command line syntax to complete
//   the final step of ingesting the information into DSpace

//The location of the executable that does the import function
$DSPACE_BIN = "/dspace/bin/import";

//This should be the full path to the DSpace suitable files, this should be accessible to the DSpace software
//Another top contender may be:
//$DS_SUITABLE_FULL_PATH = "/var/www/dsingestor/ds_suitable";
$DS_SUITABLE_FULL_PATH = "/usr/local/apache2/htdocs/dsingestor/ds_suitable/";


//The eperson that has permission to deposit into the DSpace collection
$EPERSON = "eperson@repository.com";

//This tell DSpace what collection to add your data to.  XXXX is typically your prefix assigned by handle.net
//YYY is the collection id of your specific collection that you want to add data to
$COLLECTION = "XXXX/YYY";

//The bulk import process will create a map_file of all the imported records.  This is handy to keep track of to
//see where an item was ingested or if for some reason you need to do a bulk removal
$MAP_FILE_DIR = "/home/dspace/map_files/";


?>