function method = fmethod(name,varargin)

% function method = fmethod(name,varargin)
%
% Create a structure of type 'method' as required to form a Data object
%
% INPUT
% -name :
% [baggingSVM|MKL1class|baggingMKL]
%
% OPTIONAL ARGUMENTS
% - C : the 'C' regularization parameter of the SVM. Be careful that for
%   MKL1class SVM, this corresponds in reality to parameter nu
%   (default: 1)
% - r : the ratio C+/C- if you wish to balance C between positive and
%   negative examples
%   (default : 0, means that standard ratio=N-/N+ is used)
% - kernelType : type of kernel used,
% ['linear'|'rbf'|'poly'|'precomputed'|'diffusion']
%   (default : gaussian 'rbf')
% - kernelParam : the kernel parameters
%   o if a gaussian kernel is chosen, this is the 'sigma' parameter
%   o it can be the diffusion kernel parameter
%   o otherwise it should be the degree and constant coefficient for a
%     polynomial kernel
%   (default: as a rbf kernel is chosen by default, a heuristic is 
%   used to adapt the sigma to the data, only valid when data are
%   normalized)
% - Kmult : a parameter of the bagging methods. If specified, size of the
%   bootstrap subsamples, given as a multiple of the number of positive
%   examples.
%   (default : 1)
% - K : a parameter of the bagging methods. If specified, the size of the
%   bootstrap subsamples. If K is specified, it prevails over Kmult.
%   (default : NaN)
% - NbScore : number of bootstrap iterations for bagging methods
%   (default : 10)
% - replace : bootstrap with ou without replacement
%   (default : 0, without replacement)
%
% OUPUT
% a structure variable with the following fields:
%       > name
%       > param : a parameter structure with fields:
%         - C 
%         - r
%         - kernelType
%         - kernelParam
%         - K
%         - NbScore
%         - replace

param = struct('C', 1, 'r', 0, 'kernelType', 'rbf', ...
    'Kmult', 1, 'K', NaN, 'NbScore', 10, 'replace',1);
method = struct('name', name, 'param', param);


% Parse optional arguments
l=1;
while (l<length(varargin))
   method.param.(varargin{l}) = varargin{l+1};
   l = l+2;
end

% default kernel parameters
if isfield(method.param,'kernelType') && ~isfield(method.param,'kernelParam') 
    switch lower(method.param.kernelType)
        case 'rbf'
            % putting -1 will cause a heuristic sigma to be computed
            method.param.kernelParam=-1;
        case 'diffusion' 
            % default diffusion kernel parameter is 1
            method.param.kernelParam=1;
        case 'poly'
           method.param.kernelParam=[3 0]; 
    end    
end

switch lower(method.name)
    case {'mkl1class'}
        if method.param.C >1 || method.param.C < 0
            error('For one-class MKL, parameter C corresponds to parameter nu and must be between 0 and 1')
        end
        method.param=rmfield(method.param,{'K','Kmult','NbScore', 'replace'});
        
    case {'multisvm', 'multimkl'}
        % default parameters for both data types
        if ~isfield(method.param,'kernelType1')
            method.param.kernelType1='rbf';
            method.param.kernelParam1=-1;
        else
            if ~isfield(method.param,'kernelParam1')
                switch lower(method.param.kernelType1)
                    case 'diffusion'
                        method.param.kernelParam1=1;
                    case 'rbf'
                        method.param.kernelParam1=-1;
                    case 'poly'
                        method.param.kernelParam1=[3 0]; 
                end
            end
        end
        if ~isfield(method.param,'kernelType2')
            method.param.kernelType2='rbf';
            method.param.kernelParam2=-1;
        else
            if ~isfield(method.param,'kernelParam2')
                switch lower(method.param.kernelType2)
                    case 'diffusion'
                        method.param.kernelParam2=1;
                    case 'rbf'
                        method.param.kernelParam2=-1;
                    case 'poly'
                        method.param.kernelParam2=[3 0]; 
                end
            end
        end
        method.param=rmfield(method.param,{'kernelParam','kernelType'});
        
end

if ~ (strcmp(method.name,'MultiSVM') || strcmp(method.name,'MultiMKL'))
    if isfield(method.param,'kernelType1') || isfield(method.param,'kernelType2')
        warning(mess,'Parameters kernelType1 and kernelType2 are reserved for multitask PUL, using default parameters for the kernel (rbf+heuristic sigma)')
    end
    if isfield(method.param,'kernelType1')
        method.param=rmfield(method.param,'kernelType1');
    end
    if isfield(method.param,'kernelParam1')
        method.param=rmfield(method.param,'kernelParam1');
    end
    if isfield(method.param,'kernelType2')
        method.param=rmfield(method.param,'kernelType2');
    end
    if isfield(method.param,'kernelParam2')
        method.param=rmfield(method.param,'kernelParam2');
    end
end
