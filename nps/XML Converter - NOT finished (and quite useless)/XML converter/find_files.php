<?php
function find_files($f_in)
{
	$simple = file_get_contents($f_in);
	$p = xml_parser_create();
	xml_parse_into_struct($p, $simple, $vals, $index);
	xml_parser_free($p);

//Make sure you put the right escape characters in
	//$final_string2 = "<dublin_core schema=\"etd\">\n";

	foreach ($vals as $top){

		if(!empty($top['value'])){
		$content = htmlspecialchars($top['value']);
		}
		
		switch ($top["tag"]){

			case("URL"):
			{
				$urls = explode("/",$content);
				$found = $urls[2];
				break;
			}
		}
	} 
	return $found;
}
?>
