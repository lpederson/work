
#Reference: http://stackoverflow.com/questions/1395528/scraping-html-tables-into-r-data-frames-using-the-xml-package
function(
{
	library(RCurl)
	library(XML)

	theurl <- "http://en.wikipedia.org/wiki/Brazil_national_football_team"
	webpage <- getURL(theurl)
	webpage <- readLines(tc <- textConnection(webpage)); close(tc)

	pagetree <- htmlTreeParse(webpage, error=function(...){}, useInternalNodes = TRUE)

	# Extract table header and contents
	tablehead <- xpathSApply(pagetree, "//*/table[@class='wikitable sortable']/tr/th", xmlValue)
	results <- xpathSApply(pagetree, "//*/table[@class='wikitable sortable']/tr/td", xmlValue)

	# Convert character vector to dataframe
	content <- as.data.frame(matrix(results, ncol = 8, byrow = TRUE))

	# Clean up the results
	content[,1] <- gsub("Â ", "", content[,1])
	tablehead <- gsub("Â ", "", tablehead)
	names(content) <- tablehead
}