function result = predict_multisvm(Data)

% USAGE
% result = predict_multisvm(Data)
%
% Predicts scores for unlabeled examples in Data using multitask
%
% REQUIRED:
%  - source directory of SeDuMi should be on the path
%
% INPUT :
%  - Data : a structure containing the features of the training
%  examples, their split into set of positive and unlabeled examples and
%  various parameters, as initialized by initializeMulti()
%
% OUTPUT:
%  a vector of predicted scores for the unlabeled examples from Data
%  object, mean score and bootstrap subsamples
%
% SEE ALSO: functions 'initializeMulti' and 'fmethod'

NbScore = Data.method.param.NbScore;

labels = -ones(Data.nexample1*Data.nexample2,1);
labels(Data.posSet)=1;

% kernel data type 1
switch Data.method.param.kernelType1
    case 'rbf'
        % don't forget to convert sigma to gamma for the Gaussian kernel
        K1 = Data.feature1*Data.feature1';
        gamma = 1/(2*Data.method.param.kernelParam1(1)^2);
        d = sum(Data.feature1.*Data.feature1,2);
        K1 = -repmat(d,[1 Data.nexample1]) - repmat(d',[Data.nexample1 1]) + 2*K1;
        K1=exp(gamma*K1);
    case 'poly'
        K1 = Data.feature1*Data.feature1';
        K1 = (K1 + Data.method.param.kernelParam1(2)).^Data.method.param.kernelParam1(1);
    case 'linear'
        K1 = Data.feature1*Data.feature1';
    case 'precomputed'
        K1 = Data.feature1;
    case 'diffusion'
        K1 = diag(sum(Data.feature1,2));
        K1 = K1-Data.feature1;
        K1 = expm(-Data.method.param.kernelParam1(1)*K1);
    otherwise
        error('Kernel type unknown');
end

% kernel data type 2
switch Data.method.param.kernelType2
    case 'rbf'
        % don't forget to convert sigma to gamma for the Gaussian kernel
        K2 = Data.feature2*Data.feature2';
        gamma = 1/(2*Data.method.param.kernelParam2(1)^2);
        d = sum(Data.feature2.*Data.feature2,2);
        K2 = -repmat(d,[1 Data.nexample2]) - repmat(d',[Data.nexample2 1]) + 2*K2;
        K2=exp(gamma*K2);
    case 'poly'
        K2 = Data.feature1*Data.feature2';
        K2 = (K2 + Data.method.param.kernelParam2(2)).^Data.method.param.kernelParam2(1);
    case 'linear'
        K2 = Data.feature2*Data.feature2';
    case 'precomputed'
        K2 = Data.feature2;
    case 'diffusion'
        K2 = diag(sum(Data.feature2,2));
        K2 = K2-Data.feature2;
        K2 = expm(-Data.method.param.kernelParam2(1)*K2);
    otherwise
        error('Kernel type unknown');
end
        
% size of bootstrap samples. If not specified, take a multiple of the size
% of the positive set
K = Data.method.param.K;
if isnan(K)
    K=ceil(Data.method.param.Kmult*length(Data.posSet));
end

result = struct('scoreBrut',[],'score',NaN(Data.nexample2,1),'Record',struct([]));

% define positive and unlabeled set
trainpos = find(labels>0);
npos = size(trainpos,1);
Utrain = find(labels<0);

% svm parameter
param = ['-q -t 4 -c ',num2str(Data.method.param.C)];
switch Data.method.param.r
    case 0
        % balanced C with theoretical weigths (default behavior)
        param=[param, ' -w1 ',num2str(K/(npos+K)), ' -w-1 ',num2str(1-K/(npos+K))];
    otherwise
        % balanced C with weights given by the user
        w1 = Data.method.param.r/(1+Data.method.param.r);
        w0 = 1/(1+Data.method.param.r);
        param=[param, ' -w1 ',num2str(w1), ' -w-1 ',num2str(w0)];
end

Record=struct('trainneg',[],'diffset',[]);

% count the number of times an unlabeled element does not fall into a 
% bootstrap subsample
count = zeros(length(Data.testSet),1);

% draw randomly subsamples of size K, make sure that each example is
% predicted at least NbScore times
i=1;
while any(count<NbScore)
    Record(i).trainneg=randsample(Utrain, K,Data.method.param.replace);
    Record(i).diffset=find(ismember(Data.testSet, Record(i).trainneg)==0);
    count(Record(i).diffset) = count(Record(i).diffset)+1;
    i=i+1;
end

scoretemp = NaN(Data.nexample2, i-1);

for i=1:length(Record)    
    alltrain = [trainpos ;Record(i).trainneg];
    
    % compute the pair-kernel for training
    [indTrain2 indTrain1]=ind2sub([Data.nexample2 Data.nexample1],alltrain);
    KernelTrain = K1(indTrain1,indTrain1).*K2(indTrain2,indTrain2);
    
    % compute the pair-kernel for test
    [indTest2 indTest1]=ind2sub([Data.nexample2 Data.nexample1],Data.testSet(Record(i).diffset));
    KernelTest = K1(indTrain1,indTest1).*K2(indTrain2,indTest2);
        
    % train SVM
    model = svmtrain(labels(alltrain), [(1:length(alltrain))',KernelTrain],param);
    % test SVM
    [p,a,v] = svmpredict(ones(1,length(indTest1))',[(1:length(indTest1))',KernelTest'],model);
    
    scoretemp(Record(i).diffset,i)=v;
   
end

result.Record=Record;
result.scoreBrut = scoretemp;

score=nanmean(scoretemp,2);
recove=labels;
jj=1;
for i=1:length(recove)
    if recove(i,1) ~=1
        recove(i,1)=score(jj,1);
        jj=jj+1;
    end
end
result.predict=reshape(recove,Data.nexample2,Data.nexample1);



    
    
    
    