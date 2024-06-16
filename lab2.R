credit <- read.table("Credit.dat") #
attach(credit)

#Exercise 1

realmod <- lm(Balance~Rating) #create regression model
plot(Rating, Balance) #scatter plot of Rating and Balance
s <- summary(realmod)$sigma #Finding the empirical variance

y_values <- predict(realmod) #
beta_0 <- realmod$coefficients[1] #beta_0 value
beta_1 <- realmod$coefficients[2] #beta_1 value
equi <- seq(100, 1000, by=100) #creating an equispaced sequence by 100 up to 1000

predict_balance = 0 # starting a counter

ten_uppers <- list() # empty list to be filled with upper bound points
ten_lowers <- list() # empty list to be filled with lower  bound points
for (j in equi) {
  x_list <- rep(j, 2000) #replicate the sequence 2000 times
  for (h in 1:2000) {
    epsilon <- rnorm(2000, 0, s) #adding the random error that is normally distributed
    predict_balance <- beta_0+beta_1*x_list+epsilon #creating new values
  } 
  lower <- quantile(predict_balance, probs = 0.05) #selecting the value out of lowest 5% percentile
  upper <- quantile(predict_balance, probs = 0.95) #selecting the value out of upper 5% percentile
  ten_uppers <- c(ten_uppers, upper) # adding to the list with upper values
  ten_lowers <- c(ten_lowers, lower) # adding to the list with lower values
}

plot(equi, ten_lowers, ylim=c(-500, 2500), xlab = "Rating", ylab = "Balance") #create plot
points(equi, ten_uppers) # plotting the upper bound and lower bound points
points(Rating, Balance) 
lines(equi, ten_uppers, col = "green", lwd = 1.5) # drawing lines between the boundary points
lines(equi, ten_lowers, col = "red", lwd = 1.5)
legend(100, 2500, legend = c("Upper bound", "Lower bound"), col = c("green", "red"), lty=1:2, cex=1)

#Exercise 2)i)
 
N <- length(Rating) #finding the amount 
n <- floor(0.8*N) #
set.seed(321)
indeces <- sample.int(N, n) #Samples n integers from N
x_train <- Rating[indeces] #creating training and testing data
x_test <- Rating[-indeces]
y_train <- Balance[indeces]
y_test <- Balance[-indeces]


grad_p <- as.list(1:10) #creating a list with numbers 1 to 10

pMSE_list <- list()
#for loop which executes and stores sqrt(pMSE) values for each iteration of the pMSE computation  
for (p in 1:10) {
  polymod <- lm(y_train~poly(x_train, p, raw=TRUE))
  y_hat <- predict(polymod, newdata = data.frame(x_train=x_test))
  pMSE <- 1/(N-n)*sum((y_test-y_hat)^2)
  pMSE_root <- sqrt(pMSE)
  pMSE_list <- c(pMSE_list, pMSE_root)
}

plot(grad_p, pMSE_list , xlab = "Degree p", ylab = "sqrt(pMSE)") #plot of the pMSE points and a line between
lines(grad_p, pMSE_list, col = "blue")


#Exercise 2)ii)

polymod1 <- lm(y_train~poly(x_train, 1, raw=TRUE))
polymod5 <- lm(y_train~poly(x_train, 5, raw=TRUE))
polymod8 <- lm(y_train~poly(x_train, 8, raw=TRUE))
y_hat5 <- predict(polymod5, newdata = data.frame(x_train=1:1000))
y_hat8 <- predict(polymod8, newdata = data.frame(x_train=1:1000))

plot(x_test, y_test, xlab = "Rating", ylab = "Balance", xlim = c(0,1000)) 
abline(polymod1)
lines(1:1000, y_hat5, col = "red")
lines(1:1000, y_hat7, col = "green")
legend(900,300, legend = c("p=1", "p=5", "p=8"), col=c("black","red","green"), lty=1:2, cex=1)


#Exercise 2)iii) 
mymatrix <- matrix(NA, ncol = 10, nrow=200) #empty matrix for pMSE values
set.seed(321) 
#for loop which stores 200 different pMSE values for each degree p
for (i in 1:200) {
  #same calculations as in the previous exercise, run 200 times
  indeces <- sample.int(N, n)
  x_train <- Rating[indeces] 
  x_test <- Rating[-indeces]
  y_train <- Balance[indeces]
  y_test <- Balance[-indeces]
  

  grad_p <- as.list(1:10)
  
  pMSE_list <- list()
  for (p in 1:10) {
    polymod <- lm(y_train~poly(x_train, p, raw=TRUE))
    y_hat <- predict(polymod, newdata = data.frame(x_train=x_test))
    pMSE <- 1/(N-n)*sum((y_test-y_hat)^2)
    mymatrix[i,p] <- pMSE
  }
}

#finding and plotting median and upper bounds of the different values per degree p
upper <- apply(mymatrix, 2, quantile, 0.8)
median <- apply(mymatrix, 2, median)
lower <- apply(mymatrix, 2, quantile, 0.2)

plot(grad_p, upper, ylim = c(45000,89000), xlab = "Degree p", ylab = "sqrt(pMSE)")
lines(grad_p, upper, col="blue")
lines(grad_p, lower, col = "red")
lines(grad_p, median, col="green")
points(grad_p, median)
points(grad_p, lower)
legend(1, 90000, legend = c("Upper bound", "Lower bound", "Median"), col = c( "blue","red", "green"), lty=1:2)


#Exercise 2)iv)
#installed and using the library leaps
library(leaps)

#creating a model considering all covariates to output Balance
bigmodel <- lm(Balance~Rating+Income+Limit+Cards+Age+Education, x=TRUE)
X <- bigmodel$x #creating design matrix X
#finding wich models can be considered for a linear regression and adding them to a list
allmodels <- regsubsets(Balance~Rating+Income+Limit+Cards+Age+Education, data=credit, method =c("exhaustive"), intercept =  TRUE, nbest = 100, really.big = TRUE)
out <- summary(allmodels, matrix = TRUE)
Models <- out$which 

set.seed(321) #creating training and testing data
indeces <- sample.int(N, n)
Xtrain <- X[indeces,] 
Xtest <- X[-indeces,]
y_train <- Balance[indeces]
y_test <- Balance[-indeces]

#for loop to find pMSE values of all possible models, using the formulas
pmse_roots <- list()
for (d in 1:63){
  inters <- Xtrain[,Models[d,]] #creating the X training data design matrix
  outers <- Xtest[,Models[d,]] #creating the X test data design matrix
  beta_vec <- solve(t(inters)%*%inters) %*% t(inters) %*% y_train #finding the beta vector
  yhat <- outers %*% beta_vec #solving for the yhat vector
  pmse <- 1/(N-n)*sum((y_test-yhat)^2) #calculating the pMSE values for all possible models
  pmse_root <- sqrt(pmse)
  pmse_roots <- c(pmse_roots, pmse_root)
}

#Creating index table
tablemtrix1 <- cbind(Models, pmse_roots)
df <- data.frame(tablemtrix1)
rownames(tablemtrix1) <- c(1:63)
tablemtrix1[tablemtrix1==1] <-"yes"
tablemtrix1[tablemtrix1==0] <- ""


#plotting all the pMSE values
plot(1:63, pmse_roots, xlab = "Index", ylab = "sqrt(pMSE)")
lines(1:63, pmse_roots)
points(1:63, pmse_roots, pch=19,col = "red")


