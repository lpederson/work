<?php
//Not sure if this is needed
require_once HELPERS;

//This function gets called in /omeka/applications/views/scripts/common/header.php
//It's a core file, be careful

class SlideshowPlugin extends Omeka_Plugin_Abstract
{       
	const ELEMENT_SET_NAME = 'Slideshow';
    const ELEMENT_NAME = 'Slideshow';
    
    public function displaySlideshow()
    {
		$storage = Zend_Registry::get('storage');
     	$imageCounter = 1;
     	$images = array();
     	$imageDescriptions = array();
     	$html = '';
		
		$image = get_theme_option("Slideshow Image ".$imageCounter);
		
		while($image != '')
     	{	
     		$image = $storage->getUri($storage->getPathByType($image, 'theme_uploads'));
     		array_push($images,$image);
     		$imageCounter++;
     		$image = get_theme_option("Slideshow Image ".$imageCounter);
     	}

     	for($i=1;$i<$imageCounter;$i++)
     	{
     		$imageDescription = get_theme_option('Slideshow Image '.$i.' Description');
     		if($imageDescription != '')
     		{
     			array_push($imageDescriptions,$imageDescription);
     		}
     		else
     		{
     			array_push($imageDescriptions,'');
     		}
     	}
     	
     	if(strtolower(exhibit_builder_public_theme_name('title')) == 'emiglio')
     	{
     		$html .= '<div id="header-image" style="margin-bottom: 0px"></div>';
     	}
		
    	$html .=
    		'<link rel="stylesheet" href="/plugins/SlideshowPlugin/css/style.css" type="text/css">
    		<ul id="slideshow">';
    	for($i=0;$i<($imageCounter-1);$i++)
    	{
    		$html .= 
				'<li>
					<h3></h3>
					<span>'.$images[$i].'</span>
					<p>'.$imageDescriptions[$i].'</p>
					<img src="'.$images[$i].'" style="height: 55px"/>
				</li>';
    	}
    	
	$html .= '</ul>
	<div id="slideshow_wrapper">
		<div id="slideshow_thumbnails">
			<div id="slideshow_slideleft" title="Slide Left"></div>
			<div id="slideshow_slidearea">
				<div id="slideshow_slider"></div>
			</div>
			<div id="slideshow_slideright" title="Slide Right"></div>
		</div>
		<div id="slideshow_fullsize">
			<div id="slideshow_imgprev" class="slideshow_imgnav" title="Previous Image"></div>
			<div id="slideshow_imglink"></div>
			<div id="slideshow_imgnext" class="slideshow_imgnav" title="Next Image"></div>
			<div id="slideshow_image"></div>
			<div id="slideshow_information">
				<h3></h3>
				<p></p>
			</div>
		</div>
	</div>
<script type="text/javascript" src="/plugins/SlideshowPlugin/js/compressed.js"></script>
<script type="text/javascript">
	$("slideshow").style.display="none";
	$("slideshow_wrapper").style.display="block";
	var slideshow=new TINY.slideshow("slideshow");
	window.onload=function(){
		slideshow.auto=true;
		slideshow.speed=5;
		slideshow.link="slideshow_linkhover";
		slideshow.info="slideshow_information";
		slideshow.thumbs="slideshow_slider";
		slideshow.left="slideshow_slideleft";
		slideshow.right="slideshow_slideright";
		slideshow.scrollSpeed=4;
		slideshow.spacing=5;
		slideshow.active="#fff";
		slideshow.init("slideshow","slideshow_image","slideshow_imgprev","slideshow_imgnext","slideshow_imglink");
	}
	</script>';
	
	if(strtolower(exhibit_builder_public_theme_name('title')) == 'berlin')
	{
		$html = '<div id="content">'.$html.'</div>';
	}
	
	return $html;
    }   
}    

//$plugin = new SlideshowPlugin;
//$plugin->setUp();
?>