<?php


//When passed the name of a meta xml file will convert to DS appropriate file
function create_etd_mdf_2($f_in){


	$simple = file_get_contents($f_in);
	//$p = preg_replace('/&(?![#]?[a-z0-9]+;)/i', "&amp;$1", $simple); 
	$p = xml_parser_create();
	xml_parse_into_struct($p, $simple, $vals, $index);
	xml_parser_free($p);
	
	//Make sure you put the right escape characters in
	$final_string2 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<dublin_core schema=\"etd\">\n";
	
	
	foreach ($vals as $top){
		
		if(!empty($top['value'])){
		$content = htmlspecialchars($top['value']);
		}
		
		switch ($top['tag']){

			case("DEGREE.NAME"):
			{
				$degrees = explode(";",$content);
				foreach ($degrees as $degree){				
				$final_string2 .= "<dcvalue element=\"thesisdegree\" qualifier=\"name\">".$degree."</dcvalue>\n";}
				break;
			}	
			case("DEGREE.LEVEL"):
			{
				$levels = explode(";",$content);
				foreach($levels as $level){
				$final_string2 .= "<dcvalue element=\"thesisdegree\" qualifier=\"level\">".$level."</dcvalue>\n";}
				break;
			}
			case("DEGREE.DISCIPLINE"):
			{
				$levels = explode(";",$content);
				foreach($levels as $level){
				$final_string2 .= "<dcvalue element=\"thesisdegree\" qualifier=\"discipline\">".$level."</dcvalue>\n";}
				break;
			}	
			case("DEGREE.GRANTOR"):
			{
				$grantors = explode(";",$content);
				foreach($grantors as $grantor){
				$final_string2 .= "<dcvalue element=\"thesisdegree\" qualifier=\"grantor\">".$grantor."</dcvalue>\n";}
				break;
			}	
		}
	}
	
	$final_string2 .= "<dcvalue element=\"verified\" qualifier=\"none\">yes</dcvalue>\n";
	$final_string2 .= "</dublin_core>";
	
	$out = fopen($f_in,"w+");
	fwrite($out,$final_string2);
	fclose($out);
}

?>