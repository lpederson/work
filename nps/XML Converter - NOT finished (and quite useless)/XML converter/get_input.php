<?php
function get_user_input($f_in){

	
	$simple = file_get_contents($f_in);
	$p = xml_parser_create();
	xml_parse_into_struct($p, $simple, $vals, $index);
	xml_parser_free($p);

	$in_counter = 0;
	$out_counter = 0;
	
	$input = array();
	$output= array();
	
	$final_string = "</dublin_core>";
	$end_of_input = false;
	
	//Load array of User Tag criteria
	while(!$end_of_input){
		if(isset($input[$in_counter])){
			$input[$in_counter]=$tag_.$in_counter._in;
			$in_counter = $in_counter + 1;
		
			output[$out_counter]=$tag_.$out_counter._in;
			$out_counter = $out_counter + 1;
		} else {
		$end_of_input = true;
		}
	}
	if($in_counter != $out_counter){
		echo "Number of old tags doesn't equal number of new tags";
		exit;
	}
	
	foreach ($vals as $top){

		if(!empty($top['value'])){
			$content = htmlspecialchars($top['value']);
		}
		$counter = 0;
		while($counter < $in_counter){
			if($top["tag"]==$input[$counter]){
				$in_tag_levels = explode(".",$input[$counter]);
				$out_tag_levels = explode(".",$output[$counter]);
				if(isset($tag_levels[1])){
					$final_string .= "<dcvalue element=\"".$out_tag_level[0]."\" qualifier=\"".$out_tag_level[1]."\">".$top["value"]."</dcvalue>\n";
				} else {
				$final_string .= "<dcvalue element=\"".$out_tag_level[0]."\" qualifier=\"none\">".$top["value"]."</dcvalue>\n";
				}
			}
		}
	}
	$final_string .= "</dublin_core>";
	
	$out2 = fopen($f_in,"w+");
	fwrite($out2,$final_string);
	fclose($out2);


}
?>
