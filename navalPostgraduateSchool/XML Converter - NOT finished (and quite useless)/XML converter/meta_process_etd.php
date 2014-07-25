<?php


//When passed the name of a meta xml file will convert to DS appropriate file
function create_etd_mdf($f_in){


	$simple = file_get_contents($f_in);
	$p = xml_parser_create();
	xml_parse_into_struct($p, $simple, $vals, $index);
	xml_parser_free($p);

//Make sure you put the right escape characters in
	//$final_string2 = "<dublin_core schema=\"etd\">\n";
	$final_string = "<dublin_core>\n";
	
	
	foreach ($vals as $top){

		if(!empty($top['value'])){
		$content = htmlspecialchars($top['value']);
		}
		
		switch ($top["tag"]){		
			
			case("CREATOR"):
			{
				$creators = explode(";",$content);
				foreach ($creators as $creator) {
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"author\">".$creator."</dcvalue>\n";}
				break;
			}
			case("TITLE"):
			{
				$final_string .= "<dcvalue element=\"title\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}
			case("PUBLISHER"):
			{
				$publishers = explode(";",$content);
				foreach ($publishers as $publisher) {
				$final_string .= "<dcvalue element=\"publisher\" qualifier=\"none\">".$publisher."</dcvalue>\n";}
				break;
			}
			case("DATE"):
			{
				$final_string .= "<dcvalue element=\"date\" qualifier=\"issued\">".$content."</dcvalue>\n";
				break;
			}
			case("LANGUAGE"):
			{
				$final_string .= "<dcvalue element=\"language\" qualifier=\"iso\">"."en_US"."</dcvalue>\n";
				break;
			}
			case("IDENTIFIER.OCLC"):
			{
				$final_string .= "<dcvalue element=\"identifier\" qualifier=\"oclc\">".$content."</dcvalue>\n";
				break;
			}
			case("TYPE"):
			{
				$final_string .= "<dcvalue element=\"type\" qualifier=\"none\">".$content."</dcvalue>\n";
				break;
			}	
			
			case("degree.name"):
			{
				$degrees = explode(";",$content);
				foreach ($degrees as $degree){				
				$final_string2 .= "<dcvalue element=\"degree\" qualifier=\"name\">".$degree."</dcvalue>\n";}
				break;
			}	
			case("degree.level"):
			{
				$levels = explode(";",$content);
				foreach($levels as $level){
				$final_string2 .= "<dcvalue element=\"degree\" qualifier=\"level\">".$level."</dcvalue>\n";}
				break;
			}
			case("degree.discipline"):
			{
				$levels = explode(";",$content);
				foreach($levels as $level){
				$final_string2 .= "<dcvalue element=\"degree\" qualifier=\"discipline\">".$level."</dcvalue>\n";}
				break;
			}	
			case("degree.grantor"):
			{
				$grantors = explode(";",$content);
				foreach($grantors as $grantor){
				$final_string2 .= "<dcvalue element=\"degree\" qualifier=\"grantor\">".$grantor."</dcvalue>\n";}
				break;
			}	
			
			case("CONTRIBUTOR.ADVISOR"):
			{
				$advisors = explode(";",$content);
				foreach($advisors as $advisor){
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"advisor\">".$advisor."</dcvalue>\n";}
				break;
			}
			case("CONTRIBUTOR.SECONDREADER"):
			{
				$s_readers = explode(";",$content);
				foreach($s_readers as $s_reader){
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"secondreader\">".$s_reader."</dcvalue>\n";}
				break;
			}
			case("CONTRIBUTOR.CORPORATE"):
			{
				$corporates = explode(";",$content);
				foreach($corporates as $corporate){
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"corporate\">".$corporate."</dcvalue>\n";}
				break;
			}
			case("CONTRIBUTOR.SCHOOL"):
			{
				$schools = explode(";",$content);
				foreach($schools as $school){
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"school\">".$school."</dcvalue>\n";}
				break;
			}
			case("CONTRIBUTOR.DEPARTMENT"):
			{
				$departments = explode(";",$content);
				foreach($departments as $department){
				$final_string .= "<dcvalue element=\"contributor\" qualifier=\"department\">".$department."</dcvalue>\n";}
				break;
			}
			case("TITLE.ALTERNATIVE"):
			{
				$title_alts = explode(";",$content);
				foreach($title_alts as $title_alt){
				$final_string .= "<dcvalue element=\"title\" qualifier=\"alternative\">".$title_alt."</dcvalue>\n";}
				break;
			}
			case("SUBJECT.AUTHOR"):
			{
				$subject_authors = explode(";",$content);
				foreach($subject_authors as $subject_author){
				$final_string .= "<dcvalue element=\"subject\" qualifier=\"author\">".$subject_author."</dcvalue>\n";}
				break;
			}
			case("SUBJECT"):
			{
				$subject_lcsh_s = explode(";",$content);
				foreach($subject_lcsh_s as $subject_lcsh){
				$final_string .= "<dcvalue element=\"subject\" qualifier=\"lcsh\">".$subject_lcsh."</dcvalue>\n";}
				break;
			}
			case("DESCRIPTION"):                     //On meta.xml from IA, <description> encompasses all of the following description.* case entries
			{
				$descript_abstracts = explode(";",$content);
				foreach($descript_abstracts as $descript_abstract){
				if(strlen($descript_abstract) > 300){  
				$final_string .= "<dcvalue element=\"description\" qualifier=\"abstract\">".$descript_abstract."</dcvalue>\n";}
				}
				break;
			}
			case("DESCRIPTION.FUNDER"):
			{
				$descript_funders = explode(";",$content);
				foreach($descript_funders as $descript_funder){
				$final_string .= "<dcvalue element=\"description\" qualifier=\"funder\">".$descript_funder."</dcvalue>\n";}
				break;
			}
			case("DESCRIPTION.RECOGNITION"):
			{
				$descript_recogs = explode(";",$content);
				foreach($descript_recogs as $descript_recog){
				$final_string .= "<dcvalue element=\"description\" qualifier=\"recognition\">".$descript_recog."</dcvalue>\n";}
				break;
			}
			case("DESCRIPTION.SERVICE"):
			{
				$descript_services = explode(";",$content);
				foreach($descript_services as $descript_service){
				$final_string .= "<dcvalue element=\"description\" qualifier=\"service\">".$descript_service."</dcvalue>\n";}
				break;
			}
			case("DESCRIPTION.URI"):
			{
				$descript_uris = explode(";",$content);
				foreach($descript_uris as $descript_uri){
				$final_string .= "<dcvalue element=\"description\" qualifier=\"uri\">".$descript_uri."</dcvalue>\n";}
				break;
			}
			case("RIGHTS"):
			{	
				$final_string .= "<dcvalue element=\"rights\" qualifier=\"none\">"."Approved for public release, distribution unlimited"."</dcvalue>\n";
				break;
			}
			case("IDENTIFIER-ACCESS"):
			{
				$final_string .= "<dcvalue element=\"description\" qualifier=\"uri\">".$content."</dcvalue>\n";
				break;			
			}
			case("FORMAT.EXTENT"):
			{
				$final_string .= "<dcvalue element=\"format\" qualifier=\"extent\">".$content."</dcvalue>\n";
				break;			
			}
		}
			}
	}
	
	$final_string .= "</dublin_core>";
	//$final_string2 .= "</dublin_core>";
	
	//$out = fopen($f_in,"w+");
	//fwrite($out,$final_string);
	//fclose($out);
	
	$out2 = fopen($f_in,"w+");
	fwrite($out2,$final_string);
	fclose($out2);
}

?>