<?php

////
/// Does most of the heavy lifting
//
function download_data($url_in){

	global $DEBUG,$DATA_DIR;

	//Need CURL to do this
	$dl_url_c = curl_init();
	curl_setopt($dl_url_c,CURLOPT_URL,$url_in);
	curl_setopt($dl_url_c,CURLOPT_RETURNTRANSFER,1);	
	$html_data = curl_exec($dl_url_c);
	//$html_data = @mb_convert_encoding($html_data, 'HTML-ENTITIES', 'utf-8');
	curl_close($dl_url_c);


	$dom = new DOMDocument();
	@$dom->loadHTML($html_data);
	$xpath = new DOMXPath($dom);


	$hrefs = $xpath->evaluate("/html/body//a");

	for ($i = 0; $i < $hrefs->length; $i++) {
		$href = $hrefs->item($i);
		$url = $href->getAttribute('href');
		if (preg_match('/^http:\/\//',$url))
			if (!preg_match('/^http:\/\/www/',$url)){
					$download_url = $url;
					break;
					}
	}
	
	if( @fopen($download_url.strrchr($url_in,"/").".pdf","r") && @fopen($download_url.strrchr($url_in,"/")."_dc.xml","r") && @fopen($download_url.strrchr($url_in,"/")."_meta.xml","r"))
	{
		$document_name = substr(strrchr($url_in, ("/")),1);
		mkdir($DATA_DIR.$document_name);
		chmod($DATA_DIR.$document_name,0777);

		$pdf_url = $download_url.strrchr($url_in,"/").".pdf";
		$pdf_file = file_get_contents($pdf_url);
		$pdf_file_local = fopen($DATA_DIR.$document_name."/".$document_name.".pdf","w+");
		fwrite($pdf_file_local, $pdf_file);
		fclose($pdf_file_local);
		chmod($DATA_DIR.$document_name."/".$document_name.".pdf",0777);
		
		$meta_url = $download_url.strrchr($url_in,"/")."_dc.xml";
		$meta_file = file_get_contents($meta_url);
		$meta_file_local = fopen($DATA_DIR.$document_name."/".$document_name."_dc.xml","w+");
		fwrite($meta_file_local, $meta_file);
		fclose($meta_file_local);
		chmod($DATA_DIR.$document_name."/".$document_name."_dc.xml",0777);
		
		//Added these lines to also grab the meta.xml file
		$meta_url = $download_url.strrchr($url_in,"/")."_meta.xml";
		$meta_file = file_get_contents($meta_url);
		$meta_file_local = fopen($DATA_DIR.$document_name."/".$document_name."_meta.xml","w+");
		fwrite($meta_file_local, $meta_file);
		fclose($meta_file_local);
		chmod($DATA_DIR.$document_name."/".$document_name."_meta.xml",0777);
	}
	else
	{
		echo "One or more of the files to be downloaded do NOT exist!\n";
	}
	
	return true;
	}

////
///  Before attempting to download the files this will check to see if it is already downloaded
//
	function check_if_dl($url_in) {

		global $DATA_DIR, $DEBUG, $LE;
	
		$document_name = substr(strrchr($url_in, ("/")),1);

		if (file_exists($DATA_DIR.$document_name) && 
		file_exists($DATA_DIR.$document_name."/".$document_name.".pdf") &&
		file_exists($DATA_DIR.$document_name."/".$document_name."_dc.xml") &&
		file_exists($DATA_DIR.$document_name."/".$document_name."_meta.xml")
		){
			if ($DEBUG)
				echo "  ***All files for: ".$document_name." already exist. ".$LE;
			return true;
		}
		
		return false;
	}	

////
///  Load Black List into memory
//
	function load_blacklist()  {
	
	global $INSTALL_DIR,$BLACK_LIST_FILE;
	
	$blist = fopen($INSTALL_DIR.$BLACK_LIST_FILE, "r");
	$b_array = array();
	do{
		$in = trim(fgets($blist));
		if($in != null)
			$b_array[]=$in;

	}
	while(!feof($blist));
	fclose($blist);
	
	return $b_array;
	}

////
///  Check url against Blacklist
//
	function on_blacklist($url_in, $blist){
	
	global $DEBUG,$LE;
	
		if(in_array($url_in,$blist)) {
			if ($DEBUG)
				echo $url_in." Found in Black List. ".$LE;
			return true;
			}
	return false;
	
	}

////
///  Simple Log file writer
//
	function write_single_log($url_in, $log_file){
	
		$lf = fopen($log_file,"a+");
		fwrite($lf,$url_in.",".date("Y M d : H:i:s")."\n");
		fclose($lf);
	}
?>
