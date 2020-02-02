#!/bin/bash
if [ $# -ne 4 ] 
then
    echo "usage: run.sh <fa1 abs path> <fa2 abs path> <output maxtrix abs path> <temp dir>"
    exit
fi
cd $4
# split first file
cat $1|tr -d "\r" > _tmp_file
rm -rf _tmp_splits_1
rm -rf _tmp_splits_2
rm -f order1
rm -f order2
mkdir _tmp_splits_1
cd _tmp_splits_1
awk '/^>/ {id=$1;gsub("^>","",id);filename=id ".fa";print ">" id>>filename; print id>>"../order1"} /^[^>]/ {print $0>>filename}' ../_tmp_file
cd ..
# split second file
cat $2|tr -d "\r" > _tmp_file
mkdir _tmp_splits_2
cd _tmp_splits_2
awk '/^>/ {id=$1;gsub("^>","",id);filename=id ".fa";print ">" id>>filename; print id>>"../order2"} /^[^>]/ {print $0>>filename}' ../_tmp_file
cd ..
rm -f _tmp_out
for file in `ls -tr _tmp_splits_1/*.fa`;do
    for file2 in `ls -tr _tmp_splits_2/*.fa`;do
        rm -f tmp.stretcher
        stretcher $file $file2 tmp.stretcher
        cat tmp.stretcher| sed -n -e "s/^# 1:[[:blank:]]*//gp" -e "s/^# 2:[[:blank:]]*//gp" -e "s/^# Similarity:[[:blank:]]*//gp" >> _tmp_out
    done
done
echo ""
sed 's/.*(\(.*\)%).*/\1/g' _tmp_out|awk 'FNR%3==0 {print $0} FNR%3!=0 {printf $0 "\t"}' > _score_file
awk 'BEGIN {file=0;N=0;M=0;} {if(FNR==1) file++} file==1 {s[$1,$2]=$3;} file==2 {A[N]=$1;++N} file==3 {B[M]=$1;++M} END {for(i=0;i<N;++i){for(j=0;j<M;++j) printf("%f\t",s[A[i],B[j]]/100.0); printf("\n")}}' _score_file order1 order2 >> test_out4
mv test_out4 $3
rm -rf _tmp_splits_1
rm -rf _tmp_splits_2
rm -f _score_file
rm -f order1
rm -f order2
rm -r _tmp_out
rm -f tmp.stretcher
rm -f _tmp_file
echo "finished"
