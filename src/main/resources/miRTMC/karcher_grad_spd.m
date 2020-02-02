function [grad,logsum]=karcher_grad_spd(X,matrices)
% karcher_grad_spd: The gradient of the Karcher cost function
%   [grad,logsum]=karcher_grad_spd(X,mat)
%   grad is the gradient of the Karcher costfunction, determined by the 
%   matrices in mat, at the point X when the manifold is endowed with inner 
%   product inpro_spd. The output logsum contains the sum of all terms 
%   logm(matrices{i}\X), which can be used in further constructions.

logsum=zeros(size(X,1));

for i=1:length(matrices)
    KK=logm(inv(matrices{i})*X);
    logsum=logsum + KK;
end

grad=2*X*logsum;

grad=(grad+grad')/2;