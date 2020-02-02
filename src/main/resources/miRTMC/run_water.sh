#!/bin/bash
if [ $# -ne 3 ] 
then
    echo "usage: run.sh <file1> <file2> <output file>"
    exit
fi
for i in `seq 1 10`;do
    echo >> ___n___;
done
sed -e "s/^>/_TAG_/g" $1 > _tmp_file
mkdir _tmp_splits
while read linebuf
do
    firstChar=${linebuf:0:5}
    if [ "$firstChar" == '_TAG_' ]
    then
        _filename=${linebuf:5}
        filename=`echo $_filename|sed 's/[[:blank:]].*$//g'`
        echo -en ">" >_tmp_splits/$filename".fa"
        echo $filename >> _tmp_splits/$filename".fa"
    else
        echo $linebuf >> _tmp_splits/$filename".fa"
    fi
done < _tmp_file
rm _tmp_file
# exit
cd _tmp_splits
rm -f test_out
for file in `ls -tr`;do
   rm -f *.water
   water $file ../$2 < ../___n___
   cat *.water| sed -n -e "s/^# 1:[[:blank:]]*//gp" -e "s/^# 2:[[:blank:]]*//gp" -e "s/^# Similarity:[[:blank:]]*//gp" >> test_out
done
echo ""
sed 's/.*(\(.*\)%).*/\1/g' test_out > test_out2
awk 'FNR%3==0 {print $0} FNR%3!=0 {printf $0 "\t"}' test_out2 > test_out3
cp test_out3 ../$3
cd ..
exit 0
rm -rf _tmp_splits
rm -f ___n___
