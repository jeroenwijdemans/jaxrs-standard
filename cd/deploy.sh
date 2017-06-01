#!/usr/bin/env bash
echo 'deploy ... '
if [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    # add openssl cmd here ...
    gpg --fast-import ./cd/codesigning.asc
    ./gradlew uploadArchives -s
    echo "Finished uploading archives"
else
    echo "Nothing to deploy on branch ${TRAVIS_BRANCH}.."
fi
