function [bestAUCr, bestmprer, bestcombiner] = Fun_Methods_testk( fWrr,fWdd,fWdr,alpha,lambda,gama,tol,maxiter_fk)
    rng('default')
    format long  
   %% Set parameters
    % tau = 100000;    % regularization parameter tau(10k-100k)1543123
    % delta = 1;       % step size
    maxiter = maxiter_fk;   % maximun number of iterations in SVT
   % tol = 5e-03;     % convergence threshold of SVT
   % p = 100;           % oversampling number (used in R3SVD)
   % np = 1;          % number of powers (used in R3SVD)
    percent = 0.10;   % specifying percentage of samples
    
%     Wrr = fWrr;
    Wrrn = size(fWrr);
%     Wdd = fWdd;
    Wddn = size(fWdd);
    
    Wdr = fWdr;
    % set up validation matrix
    wdrnz = nnz(fWdr);
    wdridx = randperm(wdrnz);
    PosMat = find(fWdr==1);
    valSize = floor(wdrnz*percent);
    ValIdx = PosMat(wdridx(1:valSize));
    Wdr(ValIdx) = 0;
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
    disp(['percentage of samples (percent): ',num2str(percent)])
    % disp(['step size (delta): ',num2str(delta)])
    %disp(['convergence threshold of SVT (tol): ',num2str(tol)])
    disp(['maximun number of iterations for find best rank (maxiter): ',num2str(maxiter)])
    %disp(['oversampling number (p): ',num2str(p)])
    %disp(['number of powers (np): ',num2str(np)])
    
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
    % data = M(Omega);               % an array of samples
    
    
%     Mnorm = norm(M, 'fro')
% 	disp('Mnorm.......');
% 	disp(Mnorm);
%     m = length(Omega);
%     tau = Mnorm*sqrt(n1*n2/m);
%     delta = sqrt(n1*n2/m);
%     disp(['regularization parameter (tau): ',num2str(tau)])
    
    
    %% The modified SVT algorithm based on R3SVD
    rng('default')
    format long
    
    fprintf('\nSolving by ADMM using R4SVD...\n');
    [bestAUCr, bestmprer, bestcombiner, out] = admm_r4svd_k([n1 n2],Omega,Unknown, M,maxiter,WdrOrg,ValIdx, Wrrn, Wddn,alpha,lambda,gama,tol);
%     figure(1);
%     plot(out.rank, out.testRes);
%     hold on;
%     figure(2);
%     plot(out.rank, out.maxpre);
%     hold on;
end

