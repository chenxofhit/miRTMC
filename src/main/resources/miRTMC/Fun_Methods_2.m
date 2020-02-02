
function [ P_ResultMat ,m_BestR_values] = Fun_Methods_2( fWrr,fWdd,fWdr,alpha,lambda,gama,tol,maxiter,maxiter_fk)

    format long
    
     %% Set parameters
    % tau = 100000;    % regularization parameter tau(10k-100k)1543123
    % delta = 1;       % step size
%    maxiter = 200;   % maximun number of iterations in SVT
%    tol = 5e-03;     % convergence threshold of SVT
%    p = 100;           % oversampling number (used in R3SVD)
%    np = 1;          % number of powers (used in R3SVD)
    percent = 0.10;   % specifying percentage of samples
    
    [bestAUCr, bestmprer, bestcombiner] = Fun_Methods_testk( fWrr,fWdd,fWdr,alpha,lambda,gama,tol,maxiter_fk);
    bestr = bestcombiner;
    m_BestR_values = bestr;
    
    Wrrn = size(fWrr);
    Wddn = size(fWdd);
    
    Wdr = fWdr;
 %   Wdr(TestIds) = 0;
    WdrOrg = Wdr;
    Wrd = Wdr';
    W = [ fWrr Wrd;Wdr fWdd];
     
    dn = size(fWdd,1);
    dr = size(fWrr,1);
    
    %% Load the original matrix

    sampling_TestIds = find(Wdr==1);

    M = W.*255;
    %M = M-diag(diag(M));  % set diag to zero, use?
    
    % resize the matrix if larger cases needed
    [n1,n2] = size(M);
    
    disp(['number of rows in adjacency matrix H (n1): ',num2str(n1)])
    disp(['number of columns in adjacency matrix H (n2): ',num2str(n2)])
%    disp(['percentage of samples (percent): ',num2str(percent)])
    % disp(['step size (delta): ',num2str(delta)])
   % disp(['convergence threshold of SVT (tol): ',num2str(tol)])
    disp(['maximun number of iterations in this step(maxiter): ',num2str(maxiter)])
   % disp(['oversampling number (p): ',num2str(p)])
   % disp(['number of powers (np): ',num2str(np)])
    
    %% generate random samples from the loaded image

    sampling_num =  length(sampling_TestIds);
    
    tot = dr*dr + dn*dn + sampling_num*2;
    
    Omega = zeros(tot, 1);
    
    drn = dr+dn;
    n = 1;
    for i = 1:dr
        for j = 1:dr
            Omega(n) = (j-1)*drn + i;
            n = n+1;
        end
    end
    
    for i = (dr+1):drn
        for j = (dr+1):drn
            Omega(n) = (j-1)*drn + i;
            n = n+1;
        end
    end
    
    for i = 1:sampling_num
        ass_id = sampling_TestIds(i);
        drug_id = ceil(ass_id/dn);
        disease_id = mod(ass_id,dn);
        if(disease_id==0)
            disease_id = dn;
        end
        pos_id_u = (disease_id + dr - 1)*drn + drug_id;
        Omega(n) = pos_id_u;
        n = n+1;
        
        pos_id_d = ( drug_id - 1)*drn + dr + disease_id;
        Omega(n) = pos_id_d;
        n = n+1;
    end
    
    Unknown = zeros(drn*drn, 1);
    n = 1;
    for i = 1:drn
        for j = 1: drn
            Unknown(n)=(j-1)*drn+i;
            n=n+1;
        end
    end
    
    Unknown = setdiff(Unknown, Omega);
    data = M(Omega);               % an array of samples
    
    Mnorm = norm(M, 'fro');
    m = length(Omega);
    %tau = Mnorm*sqrt(n1*n2/m);
	tau = Mnorm/10;
    delta = sqrt(n1*n2/m);
    %disp(['regularization parameter (tau): ',num2str(tau)])

    [X,U2,S2,V2,numiter2,out] = admm_r4svd([n1 n2],Omega,Unknown,M,maxiter, Wrrn, Wddn, bestr,alpha,lambda,gama,tol);
%     plot(out.rank, out.testRes);
%     hold on;

    toc
    
    % construct the completed matrix
%     X2 = U2*diag(S2)*V2';
    
    % Show results
    fprintf('RSVD: The recovered rank is %d\n',rank(X) );
    fprintf('RSVD: The relative error on Omega is: %d\n', norm(data-X(Omega))/norm(data))
    fprintf('RSVD: The relative recovery error is: %d\n', norm(M-X,'fro')^2/norm(M,'fro')^2)
    fprintf('RSVD: The relative recovery in the spectral norm is: %d\n', norm(M-X)/norm(M))
    
    Wdr_t = X(dr+1:dr+dn,1:dr);

% 	R_Auc = Fun_Auc(Wdr_t,WdrOrg,TestIds);
%     disp('Fun_methods.m : The Auc value result is........');
%     disp(R_Auc);
	
    P_ResultMat = Wdr_t;
end

