dticExtract <-
	function(){
	require(RCurl)
	require(tcltk)

		readline("Press ENTER to load metadata as .csv to search and extract DTIC metadata")
		data <- read.csv(file.choose(),header=T)

		get.logsave <- function(){
			readline("Press ENTER to choose a location to save the results file, dticExtract_out.csv")
			logsave <- tk_choose.dir()
		}

		logsave <- get.logsave()

		if(class(data[ ,ncol(data)]) =="factor") data <- data.frame(lapply(data, as.character),stringsAsFactors=FALSE)
		
		if(.Platform$OS.type == "unix") slash <- "/" else slash <- "\\"

		query <- "http://dsearch.dtic.mil/search?site=tr_all&q=" 
		
		record.reportnumber <- NULL
		record.title <- NULL
		record.author <- NULL
		record.date <- NULL
		record.institution <- NULL
		record.keywords <- NULL
		record.rights <- NULL
		record.abstract <- NULL
		record.format <- NULL
		record.identifer <- NULL
		record.corporate <- NULL
		
		for(i in 1:length(data$query)){
			record.reportnumber[i] <- ""
			record.title[i] <- ""
			record.author[i] <- ""
			record.date[i] <- ""
			record.institution[i] <- ""
			record.keywords[i] <- ""
			record.rights[i] <- ""
			record.abstract[i] <- ""	
			record.format[i] <- ""
			record.identifer <- ""
			record.corporate <- ""
		}
		
		record.temp <- NULL
		
		#to get: PAG, CA, ID
		for(i in 1:length(data$query)){

			make.query <- paste(query,data$query[i],"&btnG.x=0&btnG.y=0&client=dticol_frontend&proxystylesheet=dticol_frontend&proxyreload=1&filter=0&tlen=200&getfields=*",sep="")
			#make.query <- URLencode(make.query)
			pageresults <- readLines(make.query)
			hrefs <- grep("href=\"http://www.dtic.mil/docs/citations/",pageresults)
			if(length(pageresults[hrefs]) > 0){
				href <- substring(pageresults[hrefs],regexpr("href",pageresults[hrefs])+6,regexpr("\"><span",pageresults[hrefs])-1)
				if(length(href) > 0){
					record.pageresults <- readLines(href[1])
					record.temp <- grep("name=\"citation_technical_report_number\"",record.pageresults)
					record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
					if(length(record.temp) > 0){
						if(record.temp == data$query[i]){ #if we have the right record, start extract
							record.temp <- grep("<meta name=\"citation_title\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.title[i] <- record.temp
							else
								record.title[i] <- " "
								
							record.temp <- grep("<meta name=\"citation_author\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								for(j in 1:length(record.temp))
									if(j == 1)
										record.author[i] <- record.temp[j]
									else
										record.author[i] <- paste(record.author[i],"||",record.temp[j],sep="")
							else
								record.author[i] <- ""
	
							record.temp <- grep("<meta name=\"citation_date\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.date[i] <- record.temp
							else
								record.date[i] <- ""

							record.temp <- grep("<meta name=\"citation_technical_report_institution\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.institution[i] <- record.temp
							else
								record.institution[i] <- ""
								
							record.temp <- grep("<meta name=\"citation_keywords\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.keywords[i] <- record.temp
							else
								record.keywords[i] <- ""
								
							record.temp <- grep("<meta name=\"DS\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.rights[i] <- record.temp
							else
								record.rights[i] <- ""
								
							record.temp <- grep("<meta name=\"AB\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.abstract[i] <- record.temp
							else
								record.abstract[i] <- ""
							
							record.temp <- grep("<meta name=\"ID\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.identifer[i] <- record.temp
							else
								record.identifer[i] <- ""
								
							record.temp <- grep("<meta name=\"PAG\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.format[i] <- record.temp
							else
								record.format[i] <- ""
								
							record.temp <- grep("<meta name=\"CA\"",record.pageresults)
							record.temp <- substring(record.pageresults[record.temp],regexpr("content=\"",record.pageresults[record.temp])+9,regexpr("\">",record.pageresults[record.temp])-1)
							if(length(record.temp) > 0)
								record.corporate[i] <- record.temp
							else
								record.corporate[i] <- ""
						}
					}
				}
			}
			print(paste(data$query[i],record.reportnumber[i],record.title[i],record.author[i],record.date[i],record.abstract[i],record.keywords[i],record.rights[i],record.institution[i]))
		}

	out_csv <- cbind(data$query,record.title,record.author,record.date,record.abstract,record.keywords,record.rights,record.institution,record.identifer,record.format,record.corporate)
	write.csv(out_csv,paste(logsave,slash,"dticExtract_out.csv",sep=""))
	readline(paste("Your log is located here: ",paste(logsave,"/dticExtract_out.csv",sep=""),". Press ENTER to exit.",sep=""))
}
