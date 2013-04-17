SIPR <- function(){
	require(RCurl)
	require(XML)
	require(tcltk)
	
	get.metadata <- function(){
		readline("Press ENTER to select the .csv containing metadata and files paths (path) or URLs (url)")
		metadata <- read.csv(file.choose(),header=T)
		return(metadata)
	}

	get.SIPname <- function(){
		SIPname <- readline(prompt="Enter a name for your SIP: ")
		return(SIPname)
	}

	get.batch_directory <- function(){
		readline("Press ENTER to select a directory to put the SIP when it is created")
		batch_directory <- tk_choose.dir()
		return(batch_directory)
	}

	metadata <- get.metadata()
	SIPname <- get.SIPname()
	batch_directory <- get.batch_directory()

	#Array to store log results
	SIPR.log <- array(dim=c(nrow(metadata),1))

	#Some variables to use later
	SIP.directory <- paste(batch_directory,"/",SIPname,sep="")

	#Change data from factors to strings
	if(class(metadata[ ,ncol(metadata)])=="factor") metadata <- data.frame(lapply(metadata, as.character),stringsAsFactors=FALSE)

	#Create SIP folder structure and contents files and copy PDFs/files
	dir.create(SIP.directory)
	
	file_found <- NULL
	#Flag for skipping rows where PDF is missing
	for(i in 1:length(metadata)){
		file_found[i] <- FALSE
	}
	
	for(i in 1:length(metadata[ ,ncol(metadata)])){
		#Create folders		
		slash ="/"
		item <- unlist(strsplit(metadata[i, ncol(metadata)],"/"))
		item.name <- unlist(strsplit(item[length(item)],"\\."))
		name <- item.name[1]
		if(length(name) < 1){
			file_found[i] <- FALSE
			next
		}
		while(isTRUE(grep(" ",name) == 1)) name <- sub(" ","_",name)
		dir.create(paste(SIP.directory,"/",name,sep=""))

		#Copy PDFs/files into folders
		mode <- names(metadata)
		mode <- mode[length(mode)]
		if(mode == "path"){
			tryCatch({
				file.copy(metadata[i, ncol(metadata)],paste(SIP.directory,slash,name,slash,name,".pdf",sep=""))
				file_found[i] <- TRUE
			},error=function(e){
				file_found[i] <- FALSE
			})
		}else{
			tryCatch({
				download.file(metadata[i, ncol(metadata)],paste(SIP.directory,slash,name,slash,name,".pdf",sep=""))
				file_found[i] <- TRUE
			},error=function(e){
				file_found[i] <- FALSE
			})
		}
		if(file_found[i] == TRUE){
			#Create contents files
			contents.name <- "contents"
			contents <- file(paste(SIP.directory,slash,name,slash,contents.name,sep=""))
			writeLines(paste(name,".pdf",sep=""),contents)
			close(contents)
		}else{
			#PDF isn't found, remove previously allocated dir
			unlink(paste(SIP.directory,"/",name,sep=""))
		}
	}

	#Create XML files for metadata
	metadata.names <- strsplit(names(metadata),"\\.")
	metadata.names[[length(metadata.names)]] <- NULL
	
	#Get namespaces from metadata element prefixes -- "dc" is considered default
	namespaces <- NULL
	for(i in 1:length(metadata.names)){
		namespaces[1] <- "dc"
		element <- unlist(metadata.names[[i]])
		if(element[1] == namespaces[1]) NULL else namespaces[length(namespaces)+1] <- element[1]
	}
			
	#Put values into XML
	metadata.nopath <- metadata[,-(ncol(metadata))]
	for(i in 1:nrow(metadata.nopath)){
		#If PDF was missing, don't create XML files
		if(file_found[i] == FALSE)
			next
		
		#Create XML nodes
		dc <- newXMLNode("dublin_core",attrs=c(schema="dc"))
		cat(saveXML(dc))	
		
		if(length(namespaces) > 1){
			alt <- newXMLNode("dublin_core",attrs=c(schema=namespaces[2])) 
			cat(saveXML(alt))
		}
		
		for(j in 1:ncol(metadata.nopath)){
			
			prefix.tag <- unlist(strsplit(unlist(names(metadata.nopath[j])),"\\."))
			prefix <- prefix.tag[1]
			tag <- prefix.tag[2]
			subtag <- prefix.tag[3]
		
			repeated <- grep("\\|\\|", metadata.nopath[i,j])
			
			if(length(repeated) == 1){
		
				delimited <- unlist(strsplit(metadata.nopath[i,j],split="\\|\\|"))
		
				for(r in 1:length(delimited)){
					
					if(as.name(prefix)=="dc") addChildren(dc,newXMLNode("dcvalue",delimited[r],attrs=c(element=tag,qualifier=if(is.na(subtag)==FALSE) subtag else "none"))) else addChildren(alt,newXMLNode("dcvalue",delimited[r],attrs=c(element=tag,qualifier=if(is.na(subtag)==FALSE) subtag else "none")))
				}
			}
			else
				if(as.name(prefix) == "dc") addChildren(dc,newXMLNode("dcvalue",metadata.nopath[i,j],attrs=c(element=tag, qualifier=if(is.na(subtag)==FALSE) subtag else "none"))) else addChildren(alt,newXMLNode("dcvalue",metadata.nopath[i,j], attrs=c(element=tag, qualifier=if(is.na(subtag)==FALSE) subtag else "none")))
		}
		
		#Get names again
		item <- unlist(strsplit(metadata[i, ncol(metadata)],"/"))
		item.name <- unlist(strsplit(item[length(item)],"\\."))
		name <- item.name[1]
		while(isTRUE(grep(" ",name) == 1)) name <- sub(" ","_",name)		
		
		
		if(length(namespaces) > 1){try(xmlParse(alt))}
		
		#Save XML
		saveXML(dc,paste(SIP.directory,slash,name,slash,"dublin_core.xml",sep=""))	
		
		if(length(namespaces) > 1){
			saveXML(alt,paste(SIP.directory,slash,name,slash,"metadata_",namespaces[2],".xml",sep=""))	
		}		
	}	
	print("Process completed.")	
}
