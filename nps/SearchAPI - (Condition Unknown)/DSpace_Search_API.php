<HTML>
<head>
</head>
<body>
<?php
echo "You searhced for: <strong>" . $_POST['term'] . "</strong><br />";
$term = $_POST['term'];
include_once("simple_html_dom.php");  
$url = "http://dspacedev/public/search?scope=%2F&query=" .  urlencode($term) . "&rpp=10000&sort_by=0&order=DESC&submit=Go";
$html = file_get_html($url);
$div = $html->find('div[id=aspect_artifactbrowser_SimpleSearch_div_search-results]', 0)->innertext;
echo $div;
?>
</body>
</HTML>