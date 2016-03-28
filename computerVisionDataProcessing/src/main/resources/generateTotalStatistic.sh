#!/usr/bin/env bash

rm totalStatictic.json
find . -name 'statistic.json' -exec cat {} \; > totalStatictic.json



:%s/}{/\r/g | :%s/{//g | :%s/}//g  | :%s/"//g | :%s/://g | :%s/categoryName//g | :%s/categoryId//g | :%s/totalUpc//g | :%s/suspicious//g | :%s/amountOfSuspiciousMulti//g | :%s/amountOfColorNormalIsNoDominant//g | :%s/amountOfColorNormalIsNotInList//g


#scp ninapakhomova@11.142.8.70:/Users/ninapakhomova/codeBase/computervision/rootFolder/totalStatictic.json .

#mv totalStatictic.json totalStatictic.csv