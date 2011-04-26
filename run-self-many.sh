#!/bin/bash

COUNTER=0
while [  $COUNTER -lt 100000 ]; do
    java MLGame 11 10 4 &
	(sleep 0.2 && java MLPlayerAlphaTwo 1) &
	(sleep 0.2 && java MLPlayerAlphaTwoCopy 2) 
	echo Finished run number $COUNTER
    let COUNTER=COUNTER+1

    java MLGame 11 10 4 &
	(sleep 0.2 && java MLPlayerAlphaTwo 2) &
	(sleep 0.2 && java MLPlayerAlphaTwoCopy 1)
	echo Finished run number $COUNTER
    let COUNTER=COUNTER+1 
done


