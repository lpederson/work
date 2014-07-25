<?php
function show_contents($src,$f_out){
	if ($handle = opendir($src)) {
		while (false !== ($file = readdir($handle))) {
			if ($file != "." && $file != "..") {
				fputs($f_out,$file."\n");
			}
		}
	closedir($handle);
	}
}
?>
	