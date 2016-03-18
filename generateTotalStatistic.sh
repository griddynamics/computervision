#!/usr/bin/env bash
find rootFolder -name 'statistic.json' -exec cat {} \; > totalStatictic.json

#:%s/},{/\r/g    vim command