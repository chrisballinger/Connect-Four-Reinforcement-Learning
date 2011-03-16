#!/bin/bash

java MLGame 11 10 4 &
(sleep 1 && java MLPlayerAlphaOne 2) &
(sleep 2 && java MLPlayerAlphaBaseline 1) &

