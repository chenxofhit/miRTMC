function[M_ResultMat] = miRTMC(Wrr,Wdd,Wrd)
rng('default');
%% read parameters from file
%% para=textread('para.txt','%*s %s','delimiter','\t');
%% tsfile=para{1,1};
%% msfile=para{2,1};
%% mtifile=para{3,1};
alpha=10/10000;
lambda=10;
gama=1.618;
tol=0.0001;
maxiter=200;
maxiter_fk=20;
%% read data from files
%% Wrr = importdata(tsfile);
%% Wdd = importdata(msfile);
%% Wrd = importdata(mtifile);
Wdr = Wrd';
%% get the numbers of miRNA and gene
[dn,dr] = size(Wdr);
disp(['number of miRNA: ',num2str(dn)])
disp(['number of gene: ',num2str(dr)])
%% get the numbers of known miRNA targets
PosMat = find(Wdr==1);
NumAs = length(PosMat);
disp(['number of known miRNA targets: ',num2str(NumAs)])
%% call the matrix completion function
%% alpha=str2num(alpha);
%% lambda=str2num(lambda);
%% gama=str2num(gama);
%% tol=str2num(tol);
M_ResultMat = Fun_Methods_2(Wrr,Wdd,Wdr,alpha,lambda,gama,tol,maxiter,maxiter_fk);

%% write the result matrix to file
%% disp('write the result matrix to Result_MTmatrix.txt')
%% dlmwrite('Result_MTmatrix.txt', M_ResultMat, 'precision', '%8f', 'delimiter', '\t')
disp('prediction finished!')
