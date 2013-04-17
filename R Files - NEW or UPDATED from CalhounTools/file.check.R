file.check <- function(){
	require(RCurl)
	require(tcltk)

	readline("Press ENTER to load your metadata in .csv format and verify file paths (path) or URLs (url)") 
	metadata <- read.csv(file.choose(),header=T)

	get.directory <- function(){
	  readline("Press ENTER to select a directory to put your results")
	  directory <- tk_choose.dir()
	  return(directory)
	}
	get.name <- function(){
	  name <- readline(prompt="Enter a filename for your .csv results (e.g. results.csv) ")
	  return(name)
	}
	
	directory <- get.directory()
	name <- get.name()
	slash <- ""
	if(.Platform$OS.type == "unix") slash <- "\\" else slash <- "/"
	filename <- paste(directory,slash,name,sep="")
	
	file.check.log <- array(dim=c(nrow(metadata),1))

	#Change data from factors to strings
	if(class(metadata[ ,ncol(metadata)])=="factor") metadata <- data.frame(lapply(metadata, as.character),stringsAsFactors=FALSE)

	mode <- names(metadata)
	mode <- mode[length(mode)]

	for(i in 1:length(metadata[ ,ncol(metadata)])){
		FileNotFound <- "Cannot locate file"
		FileFound <- "Found"
		
		if(mode == "path") exists <- file.exists(metadata[i,ncol(metadata)]) else exists <- url.exists(metadata[i,ncol(metadata)]) 
		
		if(exists == FALSE) file.check.log[i] <- "File not found.  Incorrect file path specified in metadata" else file.check.log[i] <- "File found."
	}
	results <- cbind(metadata[ncol(metadata)],file.check.log)
	write.csv(results,filename)
	print(filename)
}
