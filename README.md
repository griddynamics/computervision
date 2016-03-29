How to run and customize job:


Job can be executed locally without any king of infrastructure. Just run SqlQueryDataCollectionJob
(don't forget to add -Djava.library.path=computervisionSource/lib, don't forget to add libs to /usr/lib)

enum Categories -> enum of categories =) just comment/uncomment/add category that will be indexed


SqlQueryDataCollectionJob properties:

processedRowPerCategory - amount of upc for given category, that will be processed. Please keep in mind, that the same picture could be assigned for several upc, so if in statistic you see total<1000 - that's fine.
debugMode - persist intermediate result for job execution. usefull for debug.
JobOutput:
    After job execution will be generated folders: rootFolder/{allCategories}. Each folder has following structure:
        downloadedImages (downloaded and processed images)
        result.json
        statistic.json
    in case of debug mode:
        joinOnProductsReturn
        imageRecognitionResults
        hierarchicalOutput


Before running job and looking into index.html page please check that you have connection to following resources:
    http://11.120.149.228:8888 - attribute service (backup version http://11.120.105.97:8888/StarsAttributeService/about)
    jdbc:oracle:thin:@//mdc2vr8245.federated.fds:1521/starsdev (backup version jdbc:oracle:thin:@//mdc2vr9079.federated.fds:1521/starsdev)
    https://stars.macys.com/preview - preview service.

This DB is production 100% copy, but this is NOT production db. so data may be different