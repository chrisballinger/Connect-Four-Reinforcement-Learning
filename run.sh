#!/bin/bash

java MLGame 10 11 4 &
(sleep 2 && java MLPlayerAlpha 1) &
(sleep 3 && java MLPlayerAlpha 2) &
