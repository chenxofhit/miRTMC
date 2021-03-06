function y=inpro_spd(A,B,X)
% inpro_spd: Inner product associated with the manifold of positive
% definite matrices
%   inpro_spd(A,B,X) returns the inner product, defined as trace(X\A*X\B), 
%   of tangent vectors A and B at the point X.

y=sum(sum((X\A)'.*(X\B)));