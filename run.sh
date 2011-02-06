#!/bin/bash

java MLGame 11 10 4 &
(sleep 1 && java MLPlayerAlphaRandom 1) &
(sleep 2 && java MLPlayerAlphaBaseline 2) &

