#!/usr/bin/env bash
echo 'deploy ... '
if [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_e3157f88fb5c_key -iv $encrypted_e3157f88fb5c_iv -in codesigning.asc.enc -out codesigning.asc -d
    gpg --fast-import ./cd/codesigning.asc
    ./gradlew uploadArchives -s
    echo "Finished uploading archives"
else
    echo "Nothing to deploy on branch ${TRAVIS_BRANCH}.."
fi
