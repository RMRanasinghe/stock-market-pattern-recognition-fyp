kalmanFilter <- function (input,q,r){

	Q <- q
	R <- r
	P <- 1
	X <- 0
	size <- length(input)
	output <- NULL
	itr <- 1
		while(itr < size) {
			K <- ((P + Q) / (P + Q + R))
			P <- (R * (P + Q) / (R + P + Q))
			currentResult <-( X +( (input[itr] - X) * K))
			X <- currentResult
			output <- c(output,currentResult)
			itr <- itr+1
			}
return(output)
}


saveWindowPlotKalman <- function (input,window){
	for(i in 1:(length(input)-window)){
		saveName = paste0('plot',i,'.jpeg')
		jpeg(file=saveName)
		plot(input, type='l')
		lines(c(i:(i+window-1)),kalmanFilter(input[i:(i+window)],0.000001,0.0001))
		dev.off()
	}
}
