#!/bin/bash

java MLGame 11 10 4 &
(sleep 1 && java MLPlayerAlphaOne 1) &
(sleep 2 && java MLPlayerAlphaOneCopy 2) &

