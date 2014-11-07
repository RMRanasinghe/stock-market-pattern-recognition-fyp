s = read.csv('/run/media/madhushi/Rajitha/workspace/stockPatterns/data/table.csv')
s = s$Open
plot(c(25:225),s[25:225],type='n')
lines(c(25:225),s[25:225],lty=1)
lines(ksmooth(c(25:225),s[25:225],"normal",bandwidth=5),lty=2)
lines(ksmooth(c(25:225),s[25:225],"normal",bandwidth=15),lty=3)
lines(ksmooth(c(25:225),s[25:225],"normal",bandwidth=25),lty=4)
lines(ksmooth(c(25:225),s[25:225],"normal",bandwidth=45),lty=5)
legend("topright",legend=c('data','bw=5','bw=15','bw=25','bw=45'),lty=1:5,pch=1)

,lwd=3,cex=.2

plot(c(1:273),s)
for(i in 1:(273-20)){
	lines(ksmooth(c(i:(i+50)),s[i:(i+50)],"normal",bandwidth=20),col=i)
}


plot(c(30:90),s[30:90])

plot(c(1:273),s)
for(i in 1:(273-20)){
	lines(ksmooth(c(i:(i+50)),s[i:(i+50)],"normal",bandwidth=20),col=i)
}


window = 50
bdwith = 20
saveName = paste0("plot2.jpeg")
jpeg(file=saveName, units="px", width=16000, height=9000)
plot(c(1:273),s)
lines(c(1:273),s)
for(i in 1:(273-window)){
	lines(ksmooth(c(i:(i+window)),s[i:(i+window)],"normal",bandwidth=bdwith),col=i)
}
dev.off()


window = 35
bdwith = 25
for(i in (133-window):120){
	saveName = paste0('plot',i,'.jpeg')
	jpeg(file=saveName)
	plot(c(90:160),s[90:160],lwd=3,cex=.2)
	lines(c(90:160),s[90:160],lty=2)
	lines(ksmooth(c(i:(i+window)),s[i:(i+window)],"normal",bandwidth=bdwith))
	dev.off()
}

