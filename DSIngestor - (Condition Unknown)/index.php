<HTML>
<HEAD><Title>Dspace Ingestor v.0.7</Title></HEAD>
<BODY>

<?php include("config.php"); ?>

<div style="margin-left:auto; margin-right:auto; width: 700px; border-style: solid; border-width: 2px; padding:5px;">
<H2>DSpace Ingestor v.0.7</H2>
<p>This is the sister application to IA_Scraper.  It will take the information downloaded from the Internet Archive and will do all of the necessary setup to get it ready for DSpace.</p>
<p><a href="process_titles.php">Process Another set of Titles</a></p>
<p><a href="<?php echo $TITLES_DONE ?>">View Finished Titles (CSV)</a></p>
<p><a href="<?php echo $TITLES_ERROR ?>">View Empty PDFs (CSV)</a></p>
<p><a href="view_prepped.php">View Prepped Data</a></p>
</p> 
<!-- Google Scholar -->
<form method="get" action="http://scholar.google.com/scholar">
<table bgcolor="#FFFFFF">
 <tr>
   <td align="center"><a href="http://scholar.google.com/"> <img src="http://scholar.google.com/scholar/scholar_sm.gif" alt="Google Scholar" width="105" height="40" border="0" align="absmiddle" /></a><br />
   <input type="hidden" name="hl" value="en">
   <input type="text" name="q" size="15" maxlength="255" value="" /><br />
   <input type="submit" name="btnG" value="Search" />
   </td>
 </tr>
</table>
</form>
<!-- Google Scholar -->

</div>
</BODY>
</HTML>