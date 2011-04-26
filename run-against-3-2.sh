#!/bin/bash

java MLGame 10 11 4 &
(sleep 1 && java MLPlayerAlphaTwo 2) &
(sleep 2 && java MLPlayerAlphaOne 1)

