smooth <- function(input,bw){

		size <- length(input)
		kernUp <- 0.0
		kernDown <- 0.0
		price <- 0.0
		kern <- 0.0
		output <- NULL
		
		for (i in 1:size) {
			kernUp <- 0.0
			kernDown <- 0.0		
			itr <- 1
			j <- 1
			while(itr < size){
				price <- input[[itr+1]]
				kern <- gaussianKernel(i-j,bw)
				kernUp <- (kernUp + kern*price)
				kernDown <-  (kernDown + kern)
				j <- j+1
				itr <- itr +1
			}

			output <- c(output,kernUp/kernDown)
		}
		
	return(output)
}

gaussianKernel <- function (x,bw){
		kernalVal <- ((exp(1)^((-(x*x))/(2*bw*bw)))/(bw*sqrt(2*pi)))
	return(kernalVal)
}

saveWindowPlot <- function (input,window,bw){
	for(i in 1:(length(input)-window)){
		saveName = paste0('plot',i,'.jpeg')
		jpeg(file=saveName)
		plot(input, type='l')
		lines(c(i:(i+window)),smooth(input[i:(i+window)],bw))
		dev.off()
	}
}
