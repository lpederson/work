<?php


//When passed the name of a dublin core xml file will convert to DS appropriate file
function create_mdf($f_in){


	$simple = file_get_contents($f_in);
	$p = xml_parser_create();
	xml_parse_into_struct($p, $simple, $vals, $index);
	xml_parser_free($p);


	$final_string = "<dublin_core>\n";
	

	foreach ($vals as $top){
	
		
		$content = htmlspecialchars($top["value"]);
		
		switch ($top["tag"]){

			case("DC:TITLE"):
			{
				$final_string .= "<dcvalue element=\"title\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:CREATOR"):
			{
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"author\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:TYPE"):
			{
				$final_string .= "<dcvalue element=\"type\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:PUBLISHER"):
			{
				$final_string .= "<dcvalue element=\"publisher\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:DATE"):
			{
				$final_string .= "<dcvalue element=\"date\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:LANGUAGE"):
			{
				$final_string .= "<dcvalue element=\"language\" qualifier=\"iso\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:DESCRIPTION"):
			{
				$final_string .= "<dcvalue element=\"description\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("DC:SUBJECT"):
			{
				$final_string .= "<dcvalue element=\"subject\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}	

		}
	
	}
	
	$final_string .= "</dublin_core>";
	$out = fopen($f_in,"w+");
	fwrite($out,$final_string);
	fclose($out);


}

?>