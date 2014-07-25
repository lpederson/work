<?php

//Process Titles
include("config.php");
//include("meta_process.php");
include("meta_process_etd.php");
include("meta_process_etd_2.php");
//include("find_files.php");
//include("copy_directory.php");
include("show_contents.php");
//include("zip_folder.php");

echo "Starting conversion ".$LE;

$working = opendir($IA_DATA_DIR);
$items_to_date = 0;

//array of already done titles
$titles = fopen($TITLES_DONE, "r");
$processed = array();

//Create an array of all the already loaded items from the Completed file
do{
	$in = trim(fgets($titles));
	if($in != null)
		$processed[]=$in;

}
while(!feof($titles));
fclose($titles);

//Read from directory of available data, if we hit against something in the already processed array
//don't include that item
$new_items = array();
$tiq = readdir($working);
while(($tiq != false) && ($items_to_date < $DIR_LIMIT)){

	if ($tiq == "." || $tiq == ".." || $tiq == ".svn"){
		$tiq = readdir($working);
		continue;
	}

	if(!in_array($tiq,$processed))
	{
		$new_items[]=$tiq;
		$items_to_date = $items_to_date + 1;
	}


$tiq = readdir($working);
}

//That means no new items
if ($items_to_date == 0) {
	echo "No New Data to Process!".$LE;
	exit;
}


//Create Batch Directory with timestamp and DIR_LIMIT subfolders of processed materials
$batch_time = date('Y-m-d-h-s');
$batch_dir = $DS_SUITABLE."batch_".$batch_time;

if (!mkdir($batch_dir)){
	echo "Could not create batch directory: ".$batch_dir.$LE;
	exit;
}
chmod($batch_dir,0777);


foreach($new_items as $nt){
	
	mkdir($batch_dir."/".$nt);		//These lines
	chmod($batch_dir."/".$nt,0777);
	
	//copy($IA_DATA_DIR."/".$nt."/".$nt.".pdf",$batch_dir."/".$nt."/".$nt.".pdf");
	//copy($IA_DATA_DIR."/".$nt."/".$nt."_dc.xml",$batch_dir."/".$nt."/dublin_core.xml");
	//chmod($batch_dir."/".$nt."/dublin_core.xml",0777);
	//create_mdf($batch_dir."/".$nt."/dublin_core.xml");
	
	//These three lines of code below are for creating the etd metadata file, SIMON - (all fields besides degree.*)
	copy($IA_DATA_DIR."/".$nt,$batch_dir."/".$nt."/dc.xml");
	chmod($batch_dir."/".$nt."/dc.xml",0777);
	get_user_input($batch_dir."/".$nt."/dc.xml");
	//$file_finder = find_files($batch_dir."/".$nt."/dc.xml");
	//mkdir($batch_dir."/".$nt."/".$found_2);
	//chmod($batch_dir."/".$nt."/".$found_2,0777);
	//copydir($IME_DATA_DIR."/".$found."/".$found_2."/",$batch_dir."/".$nt."/".$found_2."/");
	
	//Need to include ZIP extention
	//
	//zip($batch_dir."/".$nt."/".$found_2."/",$batch_dir."/".$nt."/".$found_2."_compressed.zip");
	//chmod($batch_dir."/".$nt."/".$found_2."_compressed.zip",0777);
	
	
	//Three lines for creating etd_2 metadata file - (degree.name,level,discipline,grantor)
	//copy($IA_DATA_DIR."/".$nt."/".$nt."_meta.xml",$batch_dir."/".$nt."/etd_meta.xml");
	//chmod($batch_dir."/".$nt."/etd_meta.xml",0777);
	//create_etd_mdf_2($batch_dir."/".$nt."/etd_meta.xml");
	
	
	$cf = fopen($batch_dir."/".$nt."/contents","a");
	//fputs($cf,$found_2."_compressed.zip\n");
	show_contents($batch_dir."/".$nt."/".$found_2,$cf);
	fclose($cf);

}



//Write out new_items to TITLES_DONE in prep for next load
$ntd = fopen($TITLES_DONE,"a");
foreach($new_items as $nt)
	if ($nt != "")
		fputs($ntd,trim($nt)."\n");
fclose($ntd);

//Write out final instructions file with the batch directory just used and other settings from config.php
$if = fopen($DS_SUITABLE."instructions_".$batch_time.".txt","w");
fputs($if,"Test with:  ".$DSPACE_BIN."  --add --eperson=".$EPERSON." --collection=".$COLLECTION." --source=".$DS_SUITABLE_FULL_PATH."batch_".$batch_time." --mapfile=".$MAP_FILE_DIR."batch_".$batch_time.".map.ingest --test\n");
fputs($if,"Ingest with:  ".$DSPACE_BIN."  --add --eperson=".$EPERSON." --collection=".$COLLECTION." --source=".$DS_SUITABLE_FULL_PATH."batch_".$batch_time." --mapfile=".$MAP_FILE_DIR."batch_".$batch_time.".map.ingest\n");
fclose($if);

echo "Conversion Complete".$LE;
echo "Data is in directory: ".$batch_dir.$LE;

?>