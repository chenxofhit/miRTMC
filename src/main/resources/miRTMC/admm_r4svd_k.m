function [bestAUCr, bestmprer, bestcombiner, out]  = admm_r4svd_k(n,trainingIndex,reIndex,M,maxiter,WdrOrg,TestIds,wrrn,wddn,alpha,lambda,gama,tol)
time1 = cputime;
%% set parameters
n1 = n(1);
n2 = n(2);
m = length(trainingIndex);
%%%%
s_index=zeros(n1,n2);
s_index(trainingIndex)=1;
s_rindex=zeros(n1,n2);
s_rindex(reIndex)=1;

T=s_index.*M;
%%  set the parameters for ADMM test
mu=lambda;rou=alpha;
fprintf('lambda= %4d, gama= %e, alpha= %e, tol= %e\n',mu, gama, rou, tol);
% mu=10;gama=1.618;rou=0.0001;tol=0.0001;
tao=mu/rou;
Z=rand(n1,n2);
X=rand(n1,n2);
Y=zeros(n1,n2);
preU=[];
%%%%%
out.residual = zeros(maxiter,1);
out.rank= zeros(maxiter,1);
out.time = zeros(maxiter,1);
out.nuclearNorm = zeros(maxiter,1);
out.p = zeros(maxiter,1);


percent = 0.10; % 50% energy
minrelRes = 1000000000.0; %resdu
stagnatecount = 0;

bestAUC = 0;
bestmpre = 0;
bestcombine = 0;
bestAUCr = 0;
bestmprer = 0;
bestcombiner = 0;
diff = zeros(size(M));
for k = 1:maxiter
    tic

    E=Z-1/rou*Y;
    %the process of getting X
    
    %gd_tr=1/(1+rou)*proj(T+rou*E, trainingIndex);
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
   % r = size(S1, 1);
    s1 = length(S1);
    S1 = S1 - tao;
    Z=U1*diag(S1)*V1';

    %the process of getting Y
    Y=Y+gama*rou*(X-Z);
    %%%%%%
    % keep track of err, time, rank, nuclearNorm, and oversampling number
    eTime = cputime - time1;
    diff(trainingIndex) = M(trainingIndex) - X(trainingIndex); 
    relRes = norm(diff(trainingIndex))/sqrt(m);

    R_Wdr = X(wrrn+1:(wrrn+wddn), 1:wrrn);
    [R_Auc, R_max_pre] = Fun_Auc2(R_Wdr,WdrOrg,TestIds);
    
    if (R_Auc > bestAUC) 
        bestAUC = R_Auc;
        bestAUCr = r;
    end
    
    if (R_max_pre > bestmpre) 
        bestmpre = R_max_pre;
        bestmprer = r;
    end
    
    if (R_max_pre + R_Auc > bestcombine) 
        bestcombine = R_max_pre + R_Auc;
        bestcombiner = r;
    end
    
    fprintf('%4d, rank: %2d res: %.2e percent: %3.2f bestAUC: %6.5f bestAUCrank: %3d, bestmaxpre: %6.5f, bestmaxprerank: %3d, bestcombinerank: %3d, AUC: %8.5f, maxpre: %6.5f\n',k, r, relRes, percent, bestAUC, bestAUCr, bestmpre, bestmprer, bestcombiner, R_Auc, R_max_pre);
    out.resRes(k) = relRes;
    out.testRes(k) = R_Auc;
    out.maxpre(k) = R_max_pre;
    out.time(k) = eTime;
    out.rank(k) = r;
%     out.nuclearNorm(k) = sum(Sigma);
    out.nuclearNorm(k) = sum(S1);
     
    if relRes < minrelRes
        minrelRes = relRes;
        stagnatecount = 0;
    else
        stagnatecount = stagnatecount + 1;
    end
    
    if stagnatecount >= 5
        percent = percent + (1 - percent)*0.1;
        stagnatecount = 0;
    end

    % check convergence
    if (relRes < tol)
        break
    end
    if (relRes > 1e5)
        disp('Divergence!');
        break
    end
    
%     if (r > bestr + 50)
%         break
%     end

    toc
end

end