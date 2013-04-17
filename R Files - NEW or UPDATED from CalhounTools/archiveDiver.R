archiveDiver <- function(){
	require(RCurl)
	require(XML)
	require(tcltk)

	get.type <- function(){
		print("What type of document would you like to scrape?")
		print("1 - Theses (default)")
		print("2 - Technical Reports")
		type <- readline("Type: ")
	}

	get.ID <- function(){
	  readline("Press ENTER to select the .csv containing Internet Archive IDs of items you wish to scrape")
	  ID <- read.csv(file.choose())
	  return(ID)
	}

	get.name <- function(){
	  name <- readline(prompt="Enter a filename for your .csv results (e.g. results.csv) ")
	  return(name)
	}

	get.directory <- function(){
	  readline("Press ENTER to select a directory to put your results")
	  directory <- tk_choose.dir()
	  return(directory)
	}

	type <- get.type()
	#print(type)
	if(type == "1")
		print("Theses")
	else if(type == "2")
		print("Technical Reports")
	ID <- get.ID()
	name <- get.name()
	directory <- get.directory()
	slash <- ""

	if(.Platform$OS.type == "unix") slash <- "\\" else slash <- "/"
	filename <- paste(directory,slash,name,sep="")

	urls <- as.factor(ID[,1])
	urls <- as.character(urls)

	links <- NA
	links.exist <- NA
	for(k in 1:length(urls)){
	  links[k] <- paste("http://archive.org/download/",urls[k],"/",urls[k],"_meta.xml",sep="")
	  links.exist[k] <- url.exists(links[k])
	}

	good.links <- links[links.exist == TRUE]
	bad.links <- links[links.exist == FALSE]
	results <- NULL

	#Fork here - Thesis or Techincal Report
	if(type == "1"){ #Thesis

		meta <- c("dc.title","dc.title.alternative","dc.contributor.author","dc.description.service","dc.contributor.advisor","dc.contributor.secondreader","dc.contributor.corporate","dc.contributor.school","dc.contributor.department","dc.description.funder","dc.description.recognition","dc.description.uri","dc.subject.lcsh","dc.subject.author","dc.publisher","dc.date.issued","dc.language.iso","dc.identifier.oclc","dc.type","etd.thesisdegree.name","etd.thesisdegree.level","etd.thesisdegree.discipline","etd.thesisdegree.grantor","dc.format.extent","dc.description.abstract","url")  
		results <- array(dim=c(length(good.links),26))
		colnames(results) <- meta

		for(i in 1:length(good.links)){  
			url <- good.links[i]
			file_check <- FALSE
			
			tryCatch({
				doc <- xmlTreeParse(url,isURL=TRUE, useInternal=TRUE)
				top <- xmlRoot(doc)
				file_check <- TRUE
			},error=function(e){file_check <- FALSE})
			
			if(file_check == FALSE)
				next
		
			names(top)
			results[i,1] <- xmlValue(top[["title"]])
			results[i,2] <- xmlValue(top[["title.alternative"]])
			results[i,3] <- xmlValue(top[["creator"]])
			results[i,4] <- xmlValue(top[["description.service"]])
			results[i,5] <- xmlValue(top[["contributor.advisor"]])
			results[i,6] <- xmlValue(top[["contributor.secondreader"]])
			results[i,7] <- xmlValue(top[["contributor.corporate"]])
			results[i,8] <- xmlValue(top[["contributor.school"]])
			results[i,9] <- xmlValue(top[["contributor.department"]])
			results[i,10] <- xmlValue(top[["description.funder"]])
			results[i,11] <- xmlValue(top[["description.recognition"]])
			results[i,12] <- xmlValue(top[["identifier-access"]])
			results[i,13] <- xmlValue(top[["subject"]])
			results[i,14] <- xmlValue(top[["subject.author"]])
			results[i,15] <- xmlValue(top[["publisher"]])
			results[i,16] <- xmlValue(top[["date"]])
			results[i,17] <- xmlValue(top[["language"]])
			results[i,18] <- xmlValue(top[["identifier.oclc"]])
			results[i,19] <- xmlValue(top[["type"]])
			results[i,20] <- xmlValue(top[["degree.name"]])
			results[i,21] <- xmlValue(top[["degree.level"]])
			results[i,22] <- xmlValue(top[["degree.discipline"]])
			results[i,23] <- xmlValue(top[["degree.grantor"]])
			results[i,24] <- xmlValue(top[["format.extent"]])

			#abstract is tricky, but we want the description that is >300 characters
			#ind <- grep("description",names(top))
			choices <- xmlSApply(top,xmlValue)
			ans <- which(nchar(choices) > 300)
			if(length(ans) > 0) results[i,25] <- xmlValue(top[[ans]]) else results[i,25] <- NA

			#add url
			results[i,26] <- sub("_meta.xml",".pdf",good.links[i])
		}
	}

	if(type == "2"){ #Technical Reports
	
		meta <- c("dc.title","dc.title.alternative","dc.contributor.author","identifier.npsreport","dc.contributor.corporate","dc.description.funder","dc.description.recognition","dc.description.uri","dc.subject.lcsh","dc.subject.author","dc.publisher","dc.date.issued","dc.language.iso","dc.identifier.oclc","dc.type","dc.format.extent","description.sponsorship","description.conference","description","url")  
		results <- array(dim=c(length(good.links),20))
		colnames(results) <- meta

		for(i in 1:length(good.links)){  
			url <- good.links[i]
			file_check <- FALSE
			
			tryCatch({
				doc <- xmlTreeParse(url,isURL=TRUE, useInternal=TRUE)
				top <- xmlRoot(doc)
				file_check <- TRUE
			},error=function(e){file_check <- FALSE})
			
			if(file_check == FALSE)
				next
				
			results[i,1] <- xmlValue(top[["title"]])
			results[i,2] <- xmlValue(top[["title.alternative"]])
			results[i,3] <- xmlValue(top[["creator"]])
			results[i,4] <- xmlValue(top[["identifier.npsreport"]])
			results[i,5] <- xmlValue(top[["contributor.corporate"]])
			results[i,6] <- xmlValue(top[["description.funder"]])
			results[i,7] <- xmlValue(top[["description.recognition"]])
			results[i,8] <- xmlValue(top[["identifier-access"]])
			results[i,9] <- xmlValue(top[["subject"]])
			results[i,10] <- xmlValue(top[["subject.author"]])
			results[i,11] <- xmlValue(top[["publisher"]])
			results[i,12] <- xmlValue(top[["date"]])
			results[i,13] <- xmlValue(top[["language"]])
			results[i,14] <- xmlValue(top[["identifier.oclc"]])
			results[i,15] <- xmlValue(top[["type"]])
			results[i,16] <- xmlValue(top[["format.extent"]])
			results[i,17] <- xmlValue(top[["description.sponsorship"]])
			results[i,18] <- xmlValue(top[["description.conference"]])
			
			#abstract is tricky, but we want the description that is >300 characters
			#ind <- grep("description",names(top))
			choices <- xmlSApply(top,xmlValue)
			ans <- which(nchar(choices) > 300)
			if(length(ans) > 0) results[i,19] <- xmlValue(top[[ans]]) else results[i,19] <- NA

			#add url
			results[i,20] <- sub("_meta.xml",".pdf",good.links[i])
		}
	}
	write.csv(results,filename)
	print("Listed below are bad Internet Archive IDs that did not contain valid files and were excluded from your results:")
	return(bad.links)
}


