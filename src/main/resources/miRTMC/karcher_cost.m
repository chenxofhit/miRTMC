function y=karcher_cost(X,matrices)
% karcher_cost: Evaluation of the Karcher cost function
%   karcher_cost(X,mat) returns the value of the Karcher cost function, 
%   determined by the matrices given in mat, evaluated in the point X.

y=0;
sqrtX=sqrtm(X);
for i=1:length(matrices)
    arg=sqrtX\matrices{i}/sqrtX;
    if (norm(imag(eig(arg)),'fro')>1e-15)
        y=Inf;
        break;
    elseif (any(real(eig(arg))<0))
        y=Inf;
        break;
    end
    y=y+norm(logm(arg),'fro')^2;
end