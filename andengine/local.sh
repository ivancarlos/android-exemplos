#!/usr/bin/bash

for d in AndEngine*/ examples/*/; do
    if [[ -d "$d" ]]; then
        rel="../local.properties"
        [[ "$d" == examples/*/ ]] && rel="../../local.properties"
        echo ln -s "$rel" "${d}local.properties"
    fi
done

exit 0
