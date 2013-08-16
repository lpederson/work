<?php
require_once HELPERS;

//This is a really ugly chunk... Optimize later
//include_once() would not work though.
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
# /ExhibitBuilder/views/public/exhibits/summary.php

class DspaceRecentItemsPlugin extends Omeka_Plugin_Abstract
{       
	const ELEMENT_SET_NAME = 'Dspace Recent Items';
    const ELEMENT_NAME = 'Text';
    
    protected $_hooks = array('install','uninstall','config_form','config'); 
    
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
		<label for="recent-items">Recent Items</label>
		<div class="inputs">
			<form action="<?php echo $PHP_SELF;?>" method="post">
				Base URL (default: "http://dspace.mlml.calstate.edu"): <input type="text" name="domain" />
				<br>
				Number of items (default: 5, max: 10): <input type="text" name="numItems" />
				<br>
				Master Theses Collection (default: "11028/21"): <input type="text" name="masterThesesCollection" />
			<input type="submit" />
			</form>
		</div>
	</div>
	<?php
    }
               
    public function hookConfig()
    {
    	if(!$_POST['domain'])
    		set_option('domain',false);
    	else
    		set_option('domain',$_POST['domain']);
	
    	if(!$_POST['numItems'])
    		set_option('numItems',false);
    	else
    		set_option('numItems',$_POST['numItems']);
    		
    	if(!$_POST['masterThesesCollection'])
    		set_option('masterThesesCollection',false);
    	else
    		set_option('masterThesesCollection',$_POST['masterThesesCollection']);
    	
    }     
    
    public function displayDspaceRecentItems()
    {		
		$query = '';
		
		$dspaceDomain = get_option('domain');
		if($dspaceDomain != '' && $dspaceDomain !== false){
			$query .= $dspaceDomain;
		} else {
			$query .= 'http://dspace.mlml.calstate.edu';
			$dspaceDomain = 'http://dspace.mlml.calstate.edu';
		}
		if(substr($query,-1) == '/')
			$query = substr($query,0,strlen($query)-1);
			
		$query .= '/discover?scope=';
		
		//$query .= 'http://dspace.mlml.calstate.edu/discover?scope=11028/20&sort_by=dc.date.issued_dt&order=DESC&submit_sort=Apply&rpp=100&query=');
				
		$numItems = get_option('numItems');
		if(is_numeric($numItems) && $numItems != '' && $numItems !== false){
			$numItems = (int) $numItems;
			if($numItems < 0 || $numItems > 10){
				$numItems = 5;
			}
		} else {
			$numItems = 5;
		}

		DspaceRecentItemsPlugin::displayRecentMasterTheses($dspaceDomain,$query,$numItems);
		DspaceRecentItemsPlugin::displayRecentArticles($dspaceDomain,$query,$numItems);
		
		return true;
    }  
    function checkLink($handle)
	{
		//set_time_limit(2);
		$ctx=stream_context_create(array('http'=> array('timeout' => 1 /*1sec*/)));
		if(@file_get_contents($handle,false,$ctx))
		{
			return true;
		}
		
		return false;
			
	}  
	function displayRecentMasterTheses($dspaceDomain,$query,$numItems)
	{
		$masterThesesQuery = strip_tags(get_theme_option("dspace_master_theses_query"));
		if($masterThesesQuery == '' || !$masterThesesQuery){
			return false;
		}
	
		$masterThesesCollection = get_option('masterThesesCollection');
		if($masterThesesCollection != '' && $masterThesesCollection !== false){
			$query .= $masterThesesCollection;
		} else {
			$query .= '11028/21';
		}
		
		$query .= '&sort_by=dc.date.issued_dt&order=DESC&submit_sort=Apply&rpp=100&query=';
		
		$masterThesesQuery_original = $masterThesesQuery;
		$masterThesesQuery = str_replace(',','',$masterThesesQuery);
		$masterThesesQuery = str_replace(' ','+',$masterThesesQuery);
		
		$query .= $masterThesesQuery;

		$scrapedhtml = null;
		if(DspaceRecentItemsPlugin::checkLink($query)){
			$scrapedhtml = @file_get_html($query);
		} else {
			return false;
		}
		
		# If failed to open stream, break function
		if($scrapedhtml === false || $scrapedhtml == null)
		{
			return false; #empty	
		}	
		
		$html = '<div id="dspace-recent-masterTheses">
					<h2>Recent Master Theses</h2>';
		$html .= '<ul>';
		
		$counter = 0;

		# Find all links
		foreach($scrapedhtml->find('div.artifact-description') as $element)
		{	
			if($counter >= $numItems){
				break;
			}
			$handleElement = $element->find('div.artifact-title a');
				$handle = $dspaceDomain.$handleElement[0]->href;
				if(!DspaceRecentItemsPlugin::getAdvisor($handle,$masterThesesQuery))
					continue;
				$title = $handleElement[0]->plaintext;
			
			$metadataElement = $element->find('div.artifact-info');	
				$author = $metadataElement[0]->find('span.author');
				$author = $author[0]->plaintext;
				$date = $metadataElement[0]->find('span.publisher-date');
				$date = $date[0]->plaintext;
			
				$html .= 
				'<li>
					<p style="font-size: inherit"><a href='.$handle.'>'.$title.'</a><br>'.$author.' '.$date.'</p>
				</li>';
				$counter++;
		}
		
		$html .= '</ul></div>';
		
		if($counter > 0)
			echo $html;
		
		return true;
	}
	function displayRecentArticles($dspaceDomain,$query,$numItems)
	{
		$articlesQuery = strip_tags(get_theme_option("dspace_articles_query"));
		if($articlesQuery == '' || !$articlesQuery){
			return false;
		}
	
		$articlesCollection = get_theme_option('dspace_articles_collection');
		if($articlesCollection != '' && $articlesCollection !== false){
			$query .= $articlesCollection;
		} else {
			return false;
		}
		
		$query .= '&sort_by=dc.date.issued_dt&order=DESC&submit_sort=Apply&rpp=100&query=';
		
		$articlesQuery_original = $articlesQuery;
		$articlesQuery = str_replace(',','',$articlesQuery);
		$articlesQuery = str_replace(' ','+',$articlesQuery);
		
		$query .= $articlesQuery;
		
		$scrapedhtml = null;
		if(DspaceRecentItemsPlugin::checkLink($query)){
			$scrapedhtml = @file_get_html($query);
		} else {
			return false;
		}
		
		# If failed to open stream, break function
		if($scrapedhtml === false || $scrapedhtml == null)
		{
			return false; #empty	
		}	
		
		$html = '<div id="dspace-recent-articles">
					<h2>Recent Articles</h2>';
		$html .= '<ul>';
		
		$counter = 0;

		# Find all links
		foreach($scrapedhtml->find('div.artifact-description') as $element)
		{	
			if($counter >= $numItems){
				break;
			}
			$handleElement = $element->find('div.artifact-title a');
				$handle = $dspaceDomain.$handleElement[0]->href;
				if(!DspaceRecentItemsPlugin::getAuthor($handle,$articlesQuery))
					continue;
				$title = $handleElement[0]->plaintext;
			
			$metadataElement = $element->find('div.artifact-info');	
				$author = $metadataElement[0]->find('span.author');
				$author = $author[0]->plaintext;
				$date = $metadataElement[0]->find('span.publisher-date');
				$date = $date[0]->plaintext;

				$html .= 
				'<li>
					<p style="font-size: inherit"><a href='.$handle.'>'.$title.'</a><br>'.$author.' '.$date.'</p>
				</li>';
				$counter++;
		}
		
		$html .= '</ul></div>';
		
		if($counter > 0)
			echo $html;
		
		return true;
	}
	function getAdvisor($handle)
	{
		$handle .= '?show=full';
		$scrapedhtml = null;
		if(DspaceRecentItemsPlugin::checkLink($handle)){
			$scrapedhtml = @file_get_html($handle);
		} else {
			return false;
		}
		# If failed to open stream, break function
		if($scrapedhtml === false || $scrapedhtml == null)
		{
			return false; #empty	
		}
		foreach($scrapedhtml->find('table.ds-includeSet-table tr') as $element)
		{
			foreach($element->find('td.label-cell') as $key)
			{
				$metadataKey = $key->plaintext;
				$metadataValue = $key->next_sibling()->plaintext;
				
				if($metadataKey == 'dc.contributor.advisor')
					if(DspaceRecentItemsPlugin::compareAdvisor($advisor,$advisorQuery))
						return true;
			}	
		}
		return false;
	}
	function compareAdvisor($advisor,$advisorQuery)
	{
		$advisor = str_replace(',',' ',$advisor);
		$advisor = str_replace('/',' ',$advisor);
		$advisor = strtolower($advisor);
		$advisorQuery = strtolower($advisorQuery);
		
		$advisor = explode(' ',$advisor);
		$advisorQuery = explode('+',$advisorQuery);
		foreach($advisorQuery as $element)
		{
			foreach($advisor as $subelement)
			{
				if($subelement == $element)
					return true;
			}
		}
		return false;
	}
	
	function getAuthor($handle,$authorQuery)
	{
		$handle .= '?show=full';
		$scrapedhtml = null;
		if(DspaceRecentItemsPlugin::checkLink($handle)){
			$scrapedhtml = @file_get_html($handle);
		} else {
			return false;
		}
		# If failed to open stream, break function
		if($scrapedhtml === false || $scrapedhtml == null)
		{
			return false; #empty	
		}
		foreach($scrapedhtml->find('table.ds-includeSet-table tr') as $element)
		{
			foreach($element->find('td.label-cell') as $key)
			{
				$metadataKey = $key->plaintext;
				$metadataValue = $key->next_sibling()->plaintext;
				
				if($metadataKey == 'dc.creator' || $metadataKey == 'dc.contributor.author')
				{
					if(DspaceRecentItemsPlugin::compareAuthor($metadataValue,$authorQuery))
						return true;
				}
			}	
		}
		return false;
	}
	function compareAuthor($advisor,$advisorQuery)
	{
		$advisor = str_replace(',',' ',$advisor);
		$advisor = str_replace('/',' ',$advisor);
		$advisor = strtolower($advisor);
		$advisorQuery = strtolower($advisorQuery);
		
		$advisor = explode(' ',$advisor);
		$advisorQuery = explode('+',$advisorQuery);
		foreach($advisorQuery as $element)
		{
			foreach($advisor as $subelement)
			{
				if($subelement == $element)
					return true;
			}
		}
		return false;
	}
}    

$plugin = new DspaceRecentItemsPlugin;
$plugin->setUp();
?>