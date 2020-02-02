function [X,U1,S1,V1,numiter,out]  = admm_r4svd(n,trainingIndex,reIndex,M,maxiter,wrrn,wddn,bestr,alpha,lambda,gama,tol)
time1 = cputime;
%% set parameters
n1 = n(1);
n2 = n(2);
m = length(trainingIndex);

s_index=zeros(n1,n2);
s_index(trainingIndex)=1;
s_rindex=zeros(n1,n2);
s_rindex(reIndex)=1;

T=s_index.*M;

mu=lambda;rou=alpha;
fprintf('lambda= %4d, gama= %e, alpha= %e, tol= %e\n',mu, gama, rou, tol);
% mu=10;gama=1.618;rou=0.0001;tol=0.0001;
tao=mu/rou;
Z=rand(n1,n2);
X=rand(n1,n2);
Y=zeros(n1,n2);
preU=[];

for k = 1:maxiter

    E=Z-1/rou*Y;
    %the process of getting X
%     gd_tr=1/(1+rou)*proj(T+rou*E, trainingIndex);
    tmp=s_index.*(T+rou*E);
    gd_tr=1/(1+rou)*tmp;
    gd_tr(gd_tr<0)=0;
    X_tr=gd_tr;

%     gd_re=proj(E, reIndex);
    gd_re=s_rindex.*E;
    gd_re(gd_re<0)=0;
    X_re=gd_re;

    X=X_tr+X_re;

    %the process of getting Z
    XX = X + 1/rou*Y;
    [U1,S1,V1, r]=r4svd(XX, tao, preU);
    preU = U1;
    s1 = length(S1);
    S1 = S1 - tao;
    Z=U1*diag(S1)*V1';

    %the process of getting Y
    Y=Y+gama*rou*(X-Z);
    
    % keep track of err, time, rank, nuclearNorm, and oversampling number
    eTime = cputime - time1;
    relRes = norm(M(trainingIndex) - X(trainingIndex))/sqrt(m);
    fprintf('iteration %4d, rank is %2d, rel. residual: %.2e\n',k, r, relRes);
    R_Wdr = X(wrrn+1:(wrrn+wddn), 1:wrrn);
%     R_Auc = Fun_Auc(R_Wdr,WdrOrg,TestIds);
%     disp('The Auc value result is');
%     disp(R_Auc);
    out.resRes(k) = relRes;
%     out.testRes(k) = R_Auc;
    out.time(k) = eTime;
    out.rank(k) = r;
    % check convergence
    if (relRes < tol)
        break
    end
    if (relRes > 1e5)
        disp('Divergence!');
        break
    end
    if (r >= bestr) 
        break
    end
end

% figure
% plot(out.rank, out.testRes)
% xlabel('rank')
% ylabel('AUC')
numiter = k;
% out.resRes = out.resRes(1:k,:);
% out.testRes = out.testRes(1:k,:);
% out.time = out.time(1:k,:);
% out.rank = out.rank(1:k,:);
% out.nuclearNorm= out.nuclearNorm(1:k,:);
% out.p = out.p(1:k,:);
end