#!/usr/bin/env bash
CV_LIB_PATH=/lib
find lib -type f -name "lib*.dylib" -print0 | while IFS="" read -r -d "" dylibpath; do
   echo install_name_tool -id "$dylibpath" "$dylibpath"
   install_name_tool -id "$dylibpath" "$dylibpath"
   otool -L $dylibpath | grep libopencv | tr -d ':' | while read -a libs ; do
       [ "${file}" != "${libs[0]}" ] && install_name_tool -change ${libs[0]} ./lib/`basename ${libs[0]}` $dylibpath
   done
done