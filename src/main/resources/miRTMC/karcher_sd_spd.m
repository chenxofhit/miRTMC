function [karch,iter,cflag]=karcher_sd_spd(X0,retraction,matrices)
% karcher_sd_spd: Steepest descent method for the Karcher mean
%   [karch,iter,cflag]=karcher_sd_spd(X0,retraction,mat) 
%   returns the Karcher mean of the matrices in the cell array mat and the 
%   number of iterations until convergence by using the steepest descent
%   method. The algorithm starts from the initial point X0 and uses the 
%   given retraction (given as a string, as in the function retr). The 
%   computations are performed using inner product inpro_spd and its 
%   associated structures. If the algorithm converges, cflag will be 
%   asigned the value 0, and -1 if this is not the case.
%
%   Reference: Ben Jeuris, Raf Vandebril and Bart Vandereycken, A survey
%       and comparison of contemporary algorithms for computing the matrix
%       geometric mean, submitted to ETNA.
%
%   Version 0.1, March 15, 2012

maxiter=1000;
tol=1.d-14;
rtol=1.d-14;
mmax=75;
alpha=1;
beta=0.5;
sigma=0.5;
cflag=0;

iter=1;
while iter<=maxiter

    grad=karcher_grad_spd(X0,matrices);
    
    [X1,m]=armijo_ls_spd(X0,grad,-grad,alpha,beta,sigma,mmax,retraction,matrices);
    
    if m==(mmax+1)
        karch=X0;
        break;
    end
    
    ni=intr_dist_spd(X0,X1);
    if (ni<tol)
        karch=X1;
        break;
    end
    
    rni=ni/norm(X0,'fro');
    if (rni<rtol)
        karch=X1;
        break;
    end

    X0=X1;
    iter=iter+1;

end

if iter==maxiter+1
    disp('Karcher sd: Max number of iterations reached');
    karch=X1;
    cflag=-1;
end
