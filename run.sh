#!/bin/bash

java MLGame 10 11 4 &
(sleep 1 && java MLPlayerAlphaThree 2) &
(sleep 2 && java MLPlayerAlphaBaseline 1) &

