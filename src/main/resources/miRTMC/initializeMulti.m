function Data = initializeMulti(featureFile,posSet,method,varargin)

% function Data = initializeMulti(featureFile,edgeFile,varargin)
%
% Read data for multitask PU learning. 
%
% INPUT
% - featureFiles : training data
%       1) If the method one wants to use is Multitask Bagging with SVM,
%       featureFiles should be a structure with fields data1 and data2 each
%       being the name of a file containing either a kernel or a feature
%       matrix. An example will be considered to be a pair (element1,
%       element2) where element1 (resp element2) has an entry in the matrix
%       of the first (resp second) field of structure 'featureFile'.
%       2) If the method to use is Multitask Bagging with MKL,
%       featureFiles should still be a structure and data1, resp data2,
%       should now contain a cell array of strings specifying filenames,
%       where different kernels are stored for the first, resp second, type
%       object in the example pairs.
%  - posSet : an index vector of the positive examples, the rest of the
%  examples is considered to be the unlabeled set.
%  The index represents a pair. If you have N elements of type 1:(x1...xN)
%  and M elements of type 2:(y1...yM), the pair (xi, yj) is indexed by
%  (i-1)*M+j, so that each index is comprised between 1 and N*M.
%  - method : a structure regarding the inference method should be built 
%   with function 'fmethod'. The 'name' field should be in
%   [MultiSVM|MultiMKL]. It should contain kernel parameters for  both
%   data types.
%
% ADDITIONAL ARGUMENTS
%  - testSet : an index vector of the examples one wishes to assign a score
%  to. If not specified, all unlabeled examples are predicted. If the
%  number of pairs is large and the positive set is small, this can be very
%  costly in time. This feature allows for instance to make predictions for a
%  single disease, while using them all for training. Pairs must be indexed
%  the same way as in argument posSet.
%  - fileType1
%  - fileType2
%  - exFile1
%  - exFile2
%  - nsplitCvOut
%  - nsplitCvIn
%  - normalizeFeatures1 (default : 0)
%  - normalizeFeatures2
%  - defaultPositiveScore
%  - rocCurveLength
% For more details, please refer to the help of function initialize.m
%  
% OUTPUT:
%  a structure variable with the following fields. An index '1' in the
%  name refers to data of the first type whereas an index '2' refers to
%  data of the second type.
%      o nexample1 : the number of train examples 
%      o nfeature1 : the number of features
%      o feature1 : it can be a nexample1 x nfeature1 matrix of train
%      features or a nexample1 x nexample1 kernel matrix if 'kernelType' is
%      'precomputed' or or a nexample1 x nexample1 adjacency matrix if
%      'kernelType1' is 'diffusion'. 
%      In the last 2 cases, field 'nfeature1' is not in the structure.
%      o nexample2 : idem, data type 2
%      o nfeature2 : idem, data type 2
%      o feature2 : idem, data type 2
%      o posSet : list of positive example pairs
%      o exFile1 : the example names file
%      o exFile2
%      o method : a structure
%          > name : the name of the inference method
%          > param : a parameter structure
%             - C : the SVM C parameter
%             - kernelType1 : the kernel type used for data of type 1
%             - kernelType2 : the kernel type used for data of type 2
%             - kernelParam1 : the kernel parameters
%             - kernelParam2 : idem
%             - nsplitPredict : the number of folds for prediction
%             - nbiter : number of iterations
%      o nsplitCvIn : the number of folds for inner cross-validation
%      o nsplitCvOut : the number of folds for outer cross-validation
%      o normalizeFeatures1 : whether or not the example features are
%      normalized
%      o normalizeFeatures2 : idem for data type 2
%      o defaultPositiveScore : the score of known positive examples 
%      o rocCurveLength : length of ROC and PR curves
%
% EXAMPLE:
% method = fmethod('MultiSVM','kernelType1','precomputed','kernelType2','linear')
% featureFile = struct('data1','../data/phenotypeKernel.mat', 'data2','../data/geneFeature.txt','normalizeFeature2',0);
% data = initializeMulti(featureFile, posSet, method, 'fileType1', 'mat', 'fileType2', 'txt')


if ~strcmp(method.name,'MultiMKL') && ~strcmp(method.name,'MultiSVM')
        error('Method name should be either ''MultiSVM'' or ''MultiMKL''')
end

Data.method = method;

% Default values for additional arguments
Data.exFile1 = '';
Data.exFile2 = '';
Data.nexample1 = [];
Data.nexample2 = [];
Data.nsplitCvOut = 3;
Data.nsplitCvInt = 3;
Data.normalizeFeatures1 = 0;
Data.normalizeFeatures2 = 0;
Data.defaultPositiveScore = 100;
Data.rocCurveLength = 1000;
Data.fileType1 ='mat';
Data.fileType2 ='mat';
Data.testSet = '';

% Parse optional arguments
l=1;
while (l<length(varargin))
    Data.(varargin{l}) = varargin{l+1};
    l = l+2;
end

% field=fieldnames(featureFile);
% if ~isstruct(featureFile)
%    error('Wrong type or argument ''featureFile'', this should be a structure with fields ''data1'' and ''data2'', providing names of files containing the data.') 
% elseif length(field) ~= 2
%     error('Argument ''featureFile'' must not contain more than two fields')
% end

% data type 1
Data.feature1=featureFile.data1;
Data.nexample1 = size(Data.feature1,1);
% if ischar(getfield(featureFile,field{1}))
%     % single file : fields of featureFile contain either a feature matrix 
%     % or a kernel matrix
%     if ~strcmp( Data.method.param.kernelType1, 'precomputed')
%         % Read the example features
%         if strcmp(Data.fileType1, 'txt')
%             Data.feature1 = dlmread(getfield(featureFile,field{1}));
%         else
%             load(getfield(featureFile,field{1}), 'feature');
%             Data.feature1 = feature;
%             clear feature
%         end
%         [Data.nexample1,Data.nfeature1] = size(Data.feature1);
%         if (Data.normalizeFeatures1)
%             Data.feature1 = (Data.feature1 - mean(Data.feature1,2)*ones(1,Data.nfeature1)) ./ (std(Data.feature1')'*ones(1,Data.nfeature1)); %#ok<UDIM>
%         end
%         %Data.feature1 = sparse(Data.feature1);
%         
%         % default parameter of rbf kernel
%         if (strcmp(Data.method.param.kernelType1 , 'rbf') && Data.method.param.kernelParam1 <0)
%             Data.method.param.kernelParam1  = sqrt(2*Data.nfeature1)/4;
%             % a heuristic that does not make much sense if data are not normalized
%         end
%     else
%         % read the precomputed kernel
%         if strcmp(Data.fileType1, 'txt')
%             Data.feature1 = dlmread(getfield(featureFile,field{1}));
%         else
%             load(getfield(featureFile,field{1}),'diffusion_Kx')
%             Data.feature1=diffusion_Kx;
%             clear diffusion_Kx
%         end
%         Data.nexample1 = size(Data.feature1,1);
%     end
% 
%     Data.feature1=double(Data.feature1);
%     
% elseif iscell(getfield(featureFile,field{1}))
%     % fields of featureFile are cells which contain the files where the 
%     % different kernels for MKL optimization are stored
%     if isempty(Data.nexample1)
%         error('Missing field in data structure : ''nexample1''. For multitask MKL, the number of examples should be given by the user.')
%     end
%     Data.feature1=getfield(featureFile,field{1});
% else
%     error('Wrong type of argument : both fields or ''featureFile'' must be either a character string or a cell array of strings')
% end

% data type 2
 Data.feature2=featureFile.data2;
 Data.nexample2 = size(Data.feature2,1);
% if ischar(getfield(featureFile,field{2}))
    % single file : fields of featureFile contain either a feature matrix 
    % or a kernel matrix
%     if ~strcmp( Data.method.param.kernelType2, 'precomputed')
%         % Read the example features
%         if strcmp(Data.fileType2, 'txt')
%             Data.feature2 = dlmread(getfield(featureFile,field{2}));
%         else
%             load(getfield(featureFile,field{2}), 'feature');
%             Data.feature2 = feature;
%             clear feature
%         end
%         [Data.nexample2,Data.nfeature2] = size(Data.feature2);
%         if (Data.normalizeFeatures2)
%             Data.feature2 = (Data.feature2 - mean(Data.feature2,2)*ones(1,Data.nfeature2)) ./ (std(Data.feature2')'*ones(1,Data.nfeature2)); %#ok<UDIM>
%         end
%         %Data.feature2 = sparse(Data.feature2);
%         
%         % default parameter of rbf kernel
%         if (strcmp(Data.method.param.kernelType2 , 'rbf') && Data.method.param.kernelParam2 <0)
%             Data.method.param.kernelParam2  = sqrt(2*Data.nfeature2)/4;
%             % a heuristic that does not make much sense if data are not normalized
%         end
%     else
%         % read the precomputed kernel
%         if strcmp(Data.fileType2, 'txt')
%             Data.feature2 = dlmread(getfield(featureFile,field{2}));
%         else
%             load(getfield(featureFile,field{2}),'diffusion_kz')
%             Data.feature2=diffusion_kz;
%             clear diffusion_kz
%         end
%         Data.nexample2 = size(Data.feature2,1);
%     end
%             
%     Data.feature2=double(Data.feature2);
%     
% elseif iscell(getfield(featureFile,field{2}))
%     % fields of featureFile are cells which contain the files where the
%     % different kernels for MKL optimization are stored
%     if isempty(Data.nexample2)
%         error('Missing field in data structure : ''nexample2''. For multitask MKL, the number of examples should be given by the user.')
%     end
%     Data.feature2=getfield(featureFile,field{2});
% else
%     error('Wrong type of argument : both fields or ''featureFile'' must be either a character string or a cell array of strings')
% end

% Add positive examples
Data.posSet=posSet;

% Test examples
if isempty(Data.testSet)
   Data.testSet = setdiff(1:Data.nexample1*Data.nexample2, Data.posSet); 
end
