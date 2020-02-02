# The [miRTMC](http://bioinformatics.csu.edu.cn/miRTMC/) WebSite

## INTRODUCTION

Long non-coding RNAs (long ncRNAs, lncRNA) are non-protein coding transcripts longer than 200 nucleotides. Recent recognition that long ncRNAs function in various aspects of cell biology has focused increasing attention on their potential to contribute towards disease etiology. The miRTMC is a free web server for lncRNA-disease association prediction


## METHOD

miRTMC took the input lncRNA sequence in fasta format, either a pasted sequence (length > 200bp) or a file. Then the sequence similarity between input lncRNA and database is calculated by using Smith-Waterman algorithm. In addition, miRTMC uses LncR_Gip for lncRNA similarity and five methods (Dis_Icod, Dis_top, Dis_gene, Dis_GO and Dis_Gip) for disease similarity measurement. Then, the karcher mean of matrixes is employed to fuse similarity matrixes of lncRNA and disease and the bagging SVM is used to identify potential lncRNA-disease interactions.

## WORKFLOW

The flowchat of miRTMC is illustrated below: 
![miRTMC workflow](http://bioinformatics.csu.edu.cn/ldap/images/lncRNA-disease.jpg)

## Note

### Keywords:

- Spring
- Matlab
- Shell

### contact

- Hui Jiang :jianghui@csu.edu.cn
- Xiang Chen:chenxofhit@gmail.com
- Jianxin Wang: jxwang@mail.csu.edu.cn
- Min Li: limin@mail.csu.edu.cn