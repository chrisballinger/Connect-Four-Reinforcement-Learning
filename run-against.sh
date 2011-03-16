#!/bin/bash

java MLGame 11 10 4 &
(sleep 1 && java MLPlayer 1) &
(sleep 2 && java MLPlayerAlphaOne 2) &

