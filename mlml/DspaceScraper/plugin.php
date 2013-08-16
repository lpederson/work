<?php	

$includedFiles = get_included_files();
$previouslyIncluded = false;

foreach($includedFiles as $file)
{
	if(strpos($file,'/simple_html_dom.php') !== false)
		$previouslyIncluded = true;
}

if(!$previouslyIncluded)
{
	# Include HTML DOM Parser
	include_once('simple_html_dom.php');
}
	
# Locations of files that call these functions - Please update accordingly
# /var/www/omeka/application/views/scripts/items/item-metadata.php

class DspaceScraperPlugin extends Omeka_Plugin_Abstract
{  
	const ELEMENT_SET_NAME = 'Dspace Scraper';
    const ELEMENT_NAME = 'Text';
    
	protected $_hooks = array('install', 'uninstall','config_form','config'); 
                   
    public function hookInstall()
    {
    	set_option('activePlugin', true);
    }
    
    public function hookUninstall()
    {
    	remove_option('activePlugin');
    }
    
    public function hookConfigForm()
    {
	?>
	<div class="field">
		<label for="admin_view">Show in admin view</label>
		<div class="inputs">
			<?php echo __v()->formCheckbox('show_admin_view'); ?>
		</div>
	</div>
	<div class="field">
		<label for="public_view">Show in public view</label>
		<div class="inputs">
			<?php echo __v()->formCheckbox('show_public_view'); ?>
		</div>
	</div>
	<?php
    }
               
    public function hookConfig()
    {
        if ($_POST['show_admin_view']) {
            set_option('showAdminView', true);
        } else {
        	set_option('showAdminView', false);
        }
        
        if ($_POST['show_public_view']) {
            set_option('showPublicView', true);
        } else {
        	set_option('showPublicView', false);
        }
    }           
      
	function showBitstreamLinks($handle)
	{
		if(!get_option('activePlugin'))
			return false;
		if(is_admin_theme() && !get_option('showAdminView'))
			return false;
		if(!is_admin_theme() && !get_option('showPublicView'))
			return false;
			
		$domain = "http://dspace.mlml.calstate.edu";
		$bitstreams = array();
		$size = 0;
		$newBitstream = true;
		$html = null;
		
		if(DspaceScraperPlugin::checkLink($handle) === true)
		{
			$html = @file_get_html($handle); #active link
		}
		
		# If failed to open stream, break function
		if($html === false || $html == null)
		{
			return false; #empty	
		}
		
		# Find all links
		foreach($html->find('a') as $element)
		{
			$newBitstream = true;
			
			# If link contains "bitstream"
			if(strstr($element->href,"/bitstream/"))
			{
				# Check if link is already stored.
				for($i=0;$i<$size;$i++)
				{
					if($element->href == $bitstreams[$i] || $domain.$element->href == $bitstreams[$i])
					{
						$newBitstream = false;
					}
				}
				if($newBitstream)
				{
					array_push($bitstreams,$domain.$element->href);
					$size += 1;
				}
			}
		}
		echo '<div id="itemfiles" class="element">';
		echo '<h2>External Files</h2>';
		echo '<div class="element-text">';
		#Display
		for($i=0;$i<$size;$i++)
		{
			echo __("<a href=$bitstreams[$i]>$bitstreams[$i]</a><br>");
		}
		echo '</div>';
		echo '</div>';
	}
	
	function showNonDCMetadata($handle)
	{
		$domain = "http://dspace.mlml.calstate.edu/handle";
		$handle = str_replace('http://hdl.handle.net',$domain,$handle);
		
		#Scrape from Dspace page that shows ALL metadata
		$handle .= "?show=full";
		$html = null;
		
		if(DspaceScraperPlugin::checkLink($handle) === true)
		{
			$html = @file_get_html($handle); #active link
		}
		else
		{
			return false;
		}
		
		# If failed to open stream, break function
		if($html === false || $html == null)
		{
			return false; #empty	
		}
		
		$metadata = array();
		$metadataKey = '';
		$metadataValue = '';
		$length = 0;
		$newKeyValuePair;
		
		foreach($html->find('table.ds-includeSet-table tr') as $element)
		{
			$newKeyValuePair = true;
			foreach($element->find('td.label-cell') as $key)
			{
				$metadataKey = $key->plaintext;
				$metadataValue = $key->next_sibling()->plaintext;
			}
			
			$split = explode(".",$metadataKey);
			if(count($split) != 0)
			{
				if($split[0] != "dc")
				{
					#Check if key-value pair is already in stored
					#Duplicate keys are OKAY if value is different
					for($i=0;$i<$length;$i++)
					{
						if($metadata[$i] == $metadataKey."||".$metadataValue)
							$newKeyValuePair = false;
					}
					if($newKeyValuePair)
					{
						array_push($metadata,$metadataKey."||".$metadataValue);
						$length += 1;
					}
				}
			}		
		}
		#Display
		$length = count($metadata);
		
		if($length <= 0)
		{
			return false;
		}
		echo '<div id="element-set">';
		echo "<h2>Darwin Core</h2>";
		for($i=0;$i<$length;$i++)
		{
			$split = explode("||",$metadata[$i]);
			$id = str_replace(".","-",$split[0]);
			echo "<div id=\"$id\" class=\"element\">";
       		echo "<h3>$id</h3>";
        	echo "<div class=\"element-text\">$split[1]</div>";
    		echo "</div>";
		}
		echo '</div>';
	}
	function checkLink($handle)
	{
		$ctx=stream_context_create(array('http'=> array('timeout' => 10 /*2sec*/)));
		if(@file_get_contents($handle,false,$ctx))
		{
			return true;
		}
		return false;
	} 
}

$plugin = new DspaceScraperPlugin;
$plugin->setUp();
?>
