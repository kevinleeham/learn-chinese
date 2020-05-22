#!/bin/bash

# NOTE: For shuffling the entries, user needs to pipe their file contents to sort -R 
# when calling this script; no need to add randomization in the script itself

entriesFile=$1
outputFilePrefix=$2

tempDir=temp
mkdir -p $tempDir

outputFileDir=output
mkdir -p $outputFileDir

beepFile=beepHigh3.aiff
cp $beepFile $tempDir/.

# voices
langA="Mei-Jia"
langB="Daniel"
introOutroVoice="Daniel"

# voice rates
langAVoiceRate=130
langBVoiceRate=160
introOutroVoiceRate="160"

# delay between entries 
delayTimeA=2000
delayTimeB=2000
delayTimeAB=2000
delayTimeBA=2000

delayFileA=delayA.aiff
say -o $tempDir/$delayFileA "[[slnc $delayTimeA]]" 

delayFileB=delayB.aiff
say -o $tempDir/$delayFileB "[[slnc $delayTimeB]]" 

delayFileAB=delayAB.aiff
say -o $tempDir/$delayFileAB "[[slnc $delayTimeAB]]" 

delayFileBA=delayBA.aiff
say -o $tempDir/$delayFileBA "[[slnc $delayTimeBA]]" 

endOfFile=endOfFile.aiff
say -v $langB -o $tempDir/$endOfFile -r $langBVoiceRate "End of file."

# max number of entries per combined output files
maxEntriesPerFile=50

summaryText=""

# the type (new or mastered, maybe default to new if none specified)
type=$2

# output files that contain the audio files to combine
langABFileList=$tempDir/fileListAB.txt
langBAFileList=$tempDir/fileListBA.txt
langAFileList=$tempDir/fileListA.txt
langBFileList=$tempDir/fileListB.txt

# create the list files (and overwrite any previous ones)
echo > $langABFileList
echo > $langBAFileList
echo > $langAFileList
echo > $langBFileList

# count the number of entries (lines in the file); helpful to know when
# we've reached the end
numEntriesInFile=`cat $1 | wc -l | awk '{$1=$1};1'`
numRemainingEntries=numEntriesInFile
echo "The input file has $numEntriesInFile entries."

# calculate the number of output audio files needed (we use this in the intro audio)
((numOutputFilesNeeded = $numEntriesInFile / $maxEntriesPerFile))
if (( $numEntriesInFile % $maxEntriesPerFile > 0)) 
then
    ((numOutputFilesNeeded++))
fi

echo "Max number of entries per output audio file is configured to $maxEntriesPerFile."
echo "Number of output audio file(s) is $numOutputFilesNeeded."
echo "Processing entries..."

entryNum=1
curOutputFileEntryNum=1
outputFileNum=1

# read each line of the input file, tokenizing on the pipe to get the A and B
# portions of the entry
while IFS='|' read -r langAEntry langBEntry
do
    formattedEntryNum=$(printf "%04d" $entryNum)
    formattedOutputFileNum=$(printf "%04d" $outputFileNum)

    echo "$entryNum of $numEntriesInFile (audio file $outputFileNum): $langAEntry $langBEntry"

    if [ $curOutputFileEntryNum == 1 ]
    then
        if [ $outputFileNum -lt $numOutputFilesNeeded ]
        then
            curOutputFileTotalEntries=$maxEntriesPerFile
        else
            ((curOutputFileTotalEntries = numEntriesInFile - ((numOutputFilesNeeded - 1) * maxEntriesPerFile)))
        fi

        if [ $curOutputFileTotalEntries -gt 1 ]
        then
            entrySingPlural="entries"
        else
            entrySingPlural="entry"
        fi         

        say -v $langB -o $tempDir/introOutroA -r $langBVoiceRate "Mandarin speed quiz. This is file $outputFileNum of $numOutputFilesNeeded, and contains $curOutputFileTotalEntries $entrySingPlural. Let's begin."
        echo "file 'introOutroA.aiff'" >> $langAFileList
        echo "file '$delayFileA'" >> $langAFileList

        say -v $langB -o $tempDir/introOutroB -r $langBVoiceRate "English speed quiz. This is file $outputFileNum of $numOutputFilesNeeded, and contains $curOutputFileTotalEntries $entrySingPlural. Let's begin."
        echo "file 'introOutroB.aiff'" >> $langBFileList
        echo "file '$delayFileB'" >> $langBFileList

        say -v $langB -o $tempDir/introOutroAB -r $langBVoiceRate "Mandarin to English quiz. This is file $outputFileNum of $numOutputFilesNeeded, and contains $curOutputFileTotalEntries $entrySingPlural. Let's begin."
        echo "file 'introOutroAB.aiff'" >> $langABFileList
        echo "file '$delayFileAB'" >> $langABFileList

        say -v $langB -o $tempDir/introOutroBA -r $langBVoiceRate "Mandarin to English quiz. This is file $outputFileNum of $numOutputFilesNeeded, and contains $curOutputFileTotalEntries $entrySingPlural. Let's begin."
        echo "file 'introOutroBA.aiff'" >> $langBAFileList
        echo "file '$delayFileBA'" >> $langBAFileList

    fi

    # use the "say" command to speak the A and B entries
    say -v $langA -o $tempDir/entry${formattedEntryNum}A -r $langAVoiceRate $langAEntry
    say -v $langB -o $tempDir/entry${formattedEntryNum}B -r $langBVoiceRate $langBEntry

    # add the A entry and a delay to the A List
    echo "file 'entry${formattedEntryNum}A.aiff'" >> $langAFileList
    echo "file '$beepFile'" >> $langAFileList
    echo "file '$delayFileA'" >> $langAFileList

    # add the B entry and a delay to the B List
    echo "file 'entry${formattedEntryNum}B.aiff'" >> $langBFileList
    echo "file '$beepFile'" >> $langBFileList
    echo "file '$delayFileB'" >> $langBFileList

    # add the A and B entries along with delays to the AB List
    echo "file 'entry${formattedEntryNum}A.aiff'" >> $langABFileList
    echo "file '$beepFile'" >> $langABFileList
    echo "file '$delayFileAB'" >> $langABFileList
    echo "file 'entry${formattedEntryNum}B.aiff'" >> $langABFileList
    echo "file '$delayFileAB'" >> $langABFileList

    # add the B and A entries along with delays to the BA List
    echo "file 'entry${formattedEntryNum}B.aiff'" >> $langBAFileList
    echo "file '$beepFile'" >> $langBAFileList
    echo "file '$delayFileBA'" >> $langBAFileList
    echo "file 'entry${formattedEntryNum}A.aiff'" >> $langBAFileList
    echo "file '$delayFileBA'" >> $langBAFileList

    # decrement the count of remaining entries to see if we're done
    ((numRemainingEntries--))

    # see if we've reached the max entry capacity of our output file or if we're done
    if [ $curOutputFileEntryNum == $maxEntriesPerFile ] || [ $numRemainingEntries == 0 ]
    then
        #if [ $entryNum == $maxEntriesPerFile ]
        #then
        #    echo "Max number of entries per file reached ($maxEntriesPerFile), so create output audio files."
        #else
        #    echo "Finished processing entries, so create final audio output files."
        #fi

        echo "file '$endOfFile'" >> $langAFileList
        echo "file '$delayFileA'" >> $langAFileList

        echo "file '$endOfFile'" >> $langBFileList
        echo "file '$delayFileB'" >> $langBFileList

        echo "file '$endOfFile'" >> $langABFileList
        echo "file '$delayFileAB'" >> $langABFileList
        
        echo "file '$endOfFile'" >> $langBAFileList
        echo "file '$delayFileBA'" >> $langBAFileList

        # tell ffmpeg to use the file lists to create the audio files
        ffmpeg -loglevel error -f concat -i $langAFileList -c copy $outputFileDir/${outputFilePrefix}QuizA-${formattedOutputFileNum}.aiff
        ffmpeg -loglevel error -f concat -i $langBFileList -c copy $outputFileDir/${outputFilePrefix}QuizB-${formattedOutputFileNum}.aiff
        ffmpeg -loglevel error -f concat -i $langABFileList -c copy $outputFileDir/${outputFilePrefix}QuizAB-${formattedOutputFileNum}.aiff
        ffmpeg -loglevel error -f concat -i $langBAFileList -c copy $outputFileDir/${outputFilePrefix}QuizBA-${formattedOutputFileNum}.aiff

        # reset the current output file's entry number and increment the output file number
        curOutputFileEntryNum=1
        ((outputFileNum++))

        # empty out the file lists
        echo > $langABFileList
        echo > $langBAFileList
        echo > $langAFileList
        echo > $langBFileList

        #echo "entryNum: $entryNum"
        #echo "outputFileNum: $outputFileNum"
    else
        # we haven't reached max capacity in our output file AND we still have more entries to process
        
        # increment the current output file's entry number
        ((curOutputFileEntryNum++))
    fi

    # increment the entry number
    ((entryNum++))
    
done < "$entriesFile"

# remove files from temp directory
echo "Removing files from ${tempDir} folder"
rm $tempDir/*

echo "Done. Audio files generated:"
ls -1 ${outputFileDir}/${outputFilePrefix}Quiz*.aiff
