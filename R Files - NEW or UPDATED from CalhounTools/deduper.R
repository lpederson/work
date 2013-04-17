deduper <-
function(){
	require(RCurl)
	require(tcltk)

	readline("Press ENTER to load metadata as .csv to check for duplicate titles in Calhoun")
	data <- read.csv(file.choose(),header=T)

	get.logsave <- function(){
		readline("Press ENTER to choose a location to save the results file, deduper_log.csv")
		logsave <- tk_choose.dir()
	}
	
	get.type <- function(){
		print("What type of document would you like to scrape?")
		print("1 - Theses - column header -> 'dc.title'")
		print("2 - Technical Reports - column header -> 'identifier.npsreport'")
		type <- readline("Choice: ")
		return (type)
	}

	logsave <- get.logsave()

	if(class(data[ ,ncol(data)]) =="factor") data <- data.frame(lapply(data, as.character),stringsAsFactors=FALSE)

	query <- ""

	duplicates <- NULL
	duplicate.URI <- NULL 
	
	type <- get.type()
	#print(type)
	if(type == "1"){
		print("Theses")
		query <- "http://calhoun.nps.edu/public/advanced-search?scope=/&field1=title&num_search_field=3&query1="
	}
	else if(type == "2"){
		print("Technical Reports")
		#                  |||||
		#Change this query VVVVV
		query <- "http://calhoun.nps.edu/public/advanced-search?scope=/&field1=title&num_search_field=3&query1="
	}else{
		print("Invalid Selection")
		return(NULL)
	}
	
	if(type == "1"){
		for(i in 1:length(data$dc.title)){

			make.query <- paste(query,data$dc.title[i],sep="")
			make.query <- URLencode(make.query)
			results <- readLines(make.query)
			matches <- grep("artifact-title",results)

			if(length(matches) == 0){
				duplicates[i] <- "No duplicates detected"
				duplicate.URI[i] <- NA
			} 

			else

			if(length(matches) == 1){
				duplicates[i] <- "Duplicate title detected"

				uri <- results[matches[1]+1]
				handle <- substring(uri,regexpr("/public",uri),regexpr("\">",uri)-1)
				duplicate.URI[i] <- URLencode(paste("http://calhoun",handle,sep=""))
			}

			else
				if(length(matches) > 1){
					duplicates[i] <- "Multiple possible duplicates detected."
					duplicate.URI[i] <- make.query
				}

		}
	}
	if(type == "2"){
		for(i in 1:length(data$identifier.npsreport)){

			make.query <- paste(query,data$identifier.npsreport[i],sep="")
			make.query <- URLencode(make.query)
			results <- readLines(make.query)
			matches <- grep("artifact-title",results)

			if(length(matches) == 0){
				duplicates[i] <- "No duplicates detected"
				duplicate.URI[i] <- NA
			} 

			else

			if(length(matches) == 1){
				duplicates[i] <- "Duplicate title detected"

				uri <- results[matches[1]+1]
				handle <- substring(uri,regexpr("/public",uri),regexpr("\">",uri)-1)
				duplicate.URI[i] <- URLencode(paste("http://calhoun",handle,sep=""))
			}

			else
				if(length(matches) > 1){
					duplicates[i] <- "Multiple possible duplicates detected."
					duplicate.URI[i] <- make.query
				}

		}
	}
	dups <- cbind(data$dc.title,duplicates,duplicate.URI)
	write.csv(dups,paste(logsave,"\\deduper_log.csv",sep=""))
	readline(paste("Your log is located here: ",paste(logsave,"\\deduper_log.csv",sep=""),". Press ENTER to exit.",sep=""))
}
